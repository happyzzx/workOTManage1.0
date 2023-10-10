package com.tbea.controller;

import com.alibaba.fastjson.JSONObject;
import com.tbea.common.type.BusinessException;
import com.tbea.common.type.CommonConstant;
import com.tbea.common.type.DataWithPage;
import com.tbea.common.utils.ValidatorUtil;
import com.tbea.model.entity.Employee;
import com.tbea.model.entity.OTRecord;
import com.tbea.service.EmployeeService;
import com.tbea.service.OTService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

//带有 Log4j2 日志功能的 Spring MVC 控制器类
@Log4j2
@RestController
public class OTController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private OTService otService;

    @GetMapping(path = "/client_ip")
    public String getClientIp(HttpServletRequest httpRequest) {
        return ValidatorUtil.getIpAddr(httpRequest);
    }

    @PostMapping(path = "/statistics")
    public DataWithPage<JSONObject> qryOTStatistics(@RequestBody JSONObject request) {
        log.info("qryOTStatistics->request:" + JSONObject.toJSONString(request));

        ValidatorUtil.validatePeriodDefault(request);
        ValidatorUtil.validatePage(request);

        DataWithPage<JSONObject> result = otService.statistics(request);
        log.info("qryOTStatistics->result: " + JSONObject.toJSONString(result));
        return result;
    }

    @PostMapping(path = "/list")
    public DataWithPage<JSONObject> qryOTRecords(@RequestBody JSONObject request) {
        log.info("qryOTRecords->request:" + JSONObject.toJSONString(request));

        ValidatorUtil.validatePeriodDefault(request);

        ValidatorUtil.validatePage(request);

        DataWithPage<JSONObject> projects = otService.qryRecords(request);

        log.info("qryOTRecords->projects: " + JSONObject.toJSONString(projects));
        return projects;
    }

    @PostMapping(path = "/start")
    public Long startOT(@RequestBody JSONObject request, HttpServletRequest httpRequest) {
        log.info("startOT->employeeId={}",request.getLong(CommonConstant.FIELD_OT_EMPLOYEE));

        String ipAddr = ValidatorUtil.getIpAddr(httpRequest);
        if (StringUtils.equals(CommonConstant.HTTP_HEADER_UNKNOWN, ipAddr)) {
            log.error("startOT->can not get client IP");
            throw new BusinessException("can not get client IP");
        }

        Employee user = employeeService.qryEmployeeById(request.getLong(CommonConstant.FIELD_OT_EMPLOYEE));
        if (user == null || !StringUtils.equals(user.getIp(), ipAddr)) {
            log.error("startOT->client IP not match");
            throw new BusinessException("client IP not match");
        }

        long recordId = otService.startRecord(request.getLong(CommonConstant.FIELD_OT_EMPLOYEE),
                request.getString(CommonConstant.FIELD_OT_REASON));

        log.info("startOT->result: " + JSONObject.toJSONString(recordId));
        return recordId;
    }

    @PostMapping(path = "/{recordId}/update")
    public JSONObject recordOT(@PathVariable("recordId") long recordId) {
        log.info("recordOT->recordId={}",recordId);

        OTRecord result = otService.updateRecord(recordId);

        log.info("recordOT->result: " + JSONObject.toJSONString(result));
        return (JSONObject) JSONObject.toJSON(result);
    }

    @PostMapping(path = "/{recordId}/stop")
    public JSONObject stopOT(@PathVariable("recordId") long recordId) {
        log.info("stopOT->recordId={}",recordId);

        OTRecord result = otService.stopRecord(recordId);

        log.info("stopOT->result: " + JSONObject.toJSONString(result));
        return (JSONObject) JSONObject.toJSON(result);
    }
}
