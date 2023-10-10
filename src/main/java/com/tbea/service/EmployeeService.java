package com.tbea.service;

import com.alibaba.fastjson.JSONObject;
import com.tbea.common.type.DataWithPage;
import com.tbea.model.entity.Employee;

public interface EmployeeService {
    Employee qryEmployeeById(Long employeeId);

    DataWithPage<Employee> qryEmployees(JSONObject request);
}
