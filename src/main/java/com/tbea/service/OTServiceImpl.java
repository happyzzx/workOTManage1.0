package com.tbea.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tbea.common.type.BusinessException;
import com.tbea.common.type.CommonConstant;
import com.tbea.common.type.DataWithPage;
import com.tbea.common.utils.DateTimeUtil;
import com.tbea.mapper.OTRecordMapper;
import com.tbea.model.entity.OTRecord;
import com.tbea.model.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OTServiceImpl implements OTService{
    private static final long RECORD_TIMEOUT_TIMES = 3;
    private static final long RECORD_TIMEOUT_INTERVAL = 6 * 1000;
    private final Map<Long, Long> recordTimeouts = new HashMap<>();

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private OTRecordMapper otRecordMapper;

    @Override
    public long startRecord(long employeeId, String reason) {
        if (hasOTRecording(employeeId)) {
            log.warn("has a OT recording");
            throw new BusinessException("has a OT recording");
        }

        OTRecord otRecord = new OTRecord();
        otRecord.setEmployeeId(employeeId);
        otRecord.setDescription(reason);
        otRecord.setStatus(CommonConstant.FIELD_OT_STATUS_WORKING);
        int insert = otRecordMapper.insert(otRecord);

        if (insert > 0) {
            startTimeoutCheck(otRecord);
        }

        return insert > 0 ? otRecord.getId() : -1;
    }

    private void startTimeoutCheck(OTRecord otRecord) {
        recordTimeouts.put(otRecord.getId(), RECORD_TIMEOUT_TIMES);
        Timer timer = new Timer("Timer-" + otRecord.getId());
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("timeout {}", RECORD_TIMEOUT_TIMES - recordTimeouts.get(otRecord.getId()));
                OTRecord record = otRecordMapper.selectById(otRecord.getId());

                // 已主动结束
                if (record == null || StringUtils.equals(CommonConstant.FIELD_OT_STATUS_END,record.getStatus())) {
                    log.info("record {} stop", otRecord.getId());
                    timer.cancel();
                    recordTimeouts.remove(otRecord.getId());
                } else {
                    // 更新超时次数
                    recordTimeouts.put(otRecord.getId(), recordTimeouts.get(otRecord.getId()) - 1);

                    // 超时处理
                    if (recordTimeouts.get(otRecord.getId()) <= 0) {
                        log.info("record {} timeout", otRecord.getId());
                        timer.cancel();
                        recordTimeouts.remove(otRecord.getId());

                        stopRecord(otRecord);
                    }
                }
            }
        }, RECORD_TIMEOUT_INTERVAL, RECORD_TIMEOUT_INTERVAL);
    }

    private boolean hasOTRecording(long employeeId) {
        LambdaQueryWrapper<OTRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OTRecord::getEmployeeId, employeeId);
        queryWrapper.eq(OTRecord::getStatus, CommonConstant.FIELD_OT_STATUS_WORKING);
        return otRecordMapper.exists(queryWrapper);
    }

    @Override
    public OTRecord stopRecord(long recordId) {
        OTRecord otRecord = otRecordMapper.selectById(recordId);
        if (otRecord == null) {
            log.error("can not get OT record:{}",recordId);
            throw new BusinessException("OT record is not exist");
        }

        boolean stop = stopRecord(otRecord);

        return stop ? otRecord : null;
    }

    private boolean stopRecord(OTRecord otRecord) {
        otRecord.setEndDate(new Timestamp(new Date().getTime()));
        otRecord.setStatus(CommonConstant.FIELD_OT_STATUS_END);
        int update = otRecordMapper.updateById(otRecord);
        return update > 0;
    }

    @Override
    public OTRecord updateRecord(long recordId) {
        // 重置超时次数
        recordTimeouts.put(recordId, RECORD_TIMEOUT_TIMES);

        OTRecord otRecord = otRecordMapper.selectById(recordId);
        if (otRecord == null) {
            log.error("can not get OT record:{}",recordId);
            throw new BusinessException("OT record is not exist");
        }

        if (!StringUtils.equals(CommonConstant.FIELD_OT_STATUS_WORKING,otRecord.getStatus())) {
            log.error("record {} is not recording", recordId);
            throw new BusinessException("OT record is not recording");
        }

        otRecord.setEndDate(new Timestamp(new Date().getTime()));
        int update = otRecordMapper.updateById(otRecord);
        return update > 0 ? otRecord : null;
    }

    @Override
    public DataWithPage<JSONObject> statistics(JSONObject request) {
        DataWithPage<Employee> employees = employeeService.qryEmployees(request);
        if (CollectionUtils.isEmpty(employees.getData())) {
            log.warn("employees is empty");
            return DataWithPage.instance(employees.getTotal(), employees.getPageSize(), employees.getPage(), null);
        }

        List<JSONObject> list =
                employees.getData().stream().map(employee->toJSONObject(employee,
                        request.getTimestamp(CommonConstant.FIELD_DATE_BEGIN),
                        request.getTimestamp(CommonConstant.FIELD_DATE_END))).collect(Collectors.toList());
        return DataWithPage.instance(employees.getTotal(), employees.getPageSize(), employees.getPage(), list);
    }

    private JSONObject toJSONObject(Employee employee, Timestamp beginDate, Timestamp endDate) {
        JSONObject json = (JSONObject) JSONObject.toJSON(employee);
        List<OTRecord> otRecords = qryRecordsByEmployeeId(employee.getId(), beginDate, endDate);
        if (CollectionUtils.isNotEmpty(otRecords)) {
            long sumDuration = otRecords.stream().mapToLong(
                    record -> DateTimeUtil.minutesBetween(record.getBeginDate(), record.getEndDate())).sum();
            json.put(CommonConstant.FIELD_DURATION_TOTAL, sumDuration);
        }
        return json;
    }

    @Override
    public DataWithPage<JSONObject> qryRecords(JSONObject request) {
        Long employeeId = request.getLong(CommonConstant.FIELD_OT_EMPLOYEE);
        if (employeeId == null) {
            log.error("param invalid!");
            return null;
        }

        LambdaQueryWrapper<OTRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OTRecord::getEmployeeId, employeeId);
        queryWrapper.eq(OTRecord::getStatus, CommonConstant.FIELD_OT_STATUS_END);
        queryWrapper.ge(OTRecord::getBeginDate, request.getTimestamp(CommonConstant.FIELD_DATE_BEGIN)).le(
                OTRecord::getEndDate, DateTimeUtil.dayEndTime(request.getTimestamp(CommonConstant.FIELD_DATE_END)));

        // 创建分页对象
        Page<OTRecord> page = new Page<>(request.getIntValue(CommonConstant.PAGE_CURRENT_FIELD),
                request.getIntValue(CommonConstant.PAGE_SIZE_FIELD));

        Page<OTRecord> result = otRecordMapper.selectPage(page, queryWrapper);
        return DataWithPage.instance(result.getTotal(), page.getSize(), page.getCurrent(),
                result.getRecords().stream().map(this::toJSONObject).collect(
                        Collectors.toList()));
    }

    private JSONObject toJSONObject(OTRecord record) {
        JSONObject json = (JSONObject) JSONObject.toJSON(record);
        json.put(CommonConstant.FIELD_DURATION_TOTAL, DateTimeUtil.minutesBetween(record.getBeginDate(), record.getEndDate()));
        return json;
    }

    @Override
    public List<OTRecord> qryRecordsByEmployeeId(Long employeeId, Timestamp beginDate, Timestamp endDate) {
        Employee employee = employeeService.qryEmployeeById(employeeId);
        if (employee == null) {
            log.error("can not get OT record of {}",employeeId);
            return null;
        }

        LambdaQueryWrapper<OTRecord> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OTRecord::getEmployeeId, employeeId);
        queryWrapper.eq(OTRecord::getStatus, CommonConstant.FIELD_OT_STATUS_END);
        queryWrapper.ge(OTRecord::getBeginDate, beginDate).le(OTRecord::getEndDate, endDate);
        List<OTRecord> records = otRecordMapper.selectList(queryWrapper);

        log.info("qryRecordsByEmployeeId->records: {}",JSONObject.toJSONString(records));
        return records;
    }
}
