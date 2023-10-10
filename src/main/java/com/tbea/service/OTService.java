package com.tbea.service;

import com.alibaba.fastjson.JSONObject;
import com.tbea.common.type.DataWithPage;
import com.tbea.model.entity.OTRecord;

import java.sql.Timestamp;
import java.util.List;

public interface OTService {
    long startRecord(long employeeId, String reason);

    OTRecord stopRecord(long recordId);

    OTRecord updateRecord(long recordId);

    DataWithPage<JSONObject> statistics(JSONObject request);

    DataWithPage<JSONObject> qryRecords(JSONObject request);

    List<OTRecord> qryRecordsByEmployeeId(Long employeeId, Timestamp beginDate, Timestamp endDate);

}
