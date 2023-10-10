package com.tbea.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tbea.common.type.CommonConstant;
import com.tbea.common.type.DataWithPage;
import com.tbea.mapper.EmployeeMapper;
import com.tbea.model.entity.Employee;
import com.tbea.model.entity.OTRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee qryEmployeeById(Long employeeId) {
        return employeeMapper.selectById(employeeId);
    }

    @Override
    public DataWithPage<Employee> qryEmployees(JSONObject request) {
        LambdaQueryWrapper<Employee> queryWrapper = Wrappers.lambdaQuery();
        // 创建分页对象
        Page<Employee> page = new Page<>(request.getIntValue(CommonConstant.PAGE_CURRENT_FIELD),
                request.getIntValue(CommonConstant.PAGE_SIZE_FIELD));

        Page<Employee> result = employeeMapper.selectPage(page, queryWrapper);
        return DataWithPage.instance(result.getTotal(),page.getSize(),page.getCurrent(), result.getRecords());
    }
}
