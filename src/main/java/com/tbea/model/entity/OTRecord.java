package com.tbea.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@TableName("t_ot_record")
public class OTRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long employeeId;

    @TableField(fill = FieldFill.INSERT)   //在执行插入操作时，自动填充该字段的值
    private Timestamp beginDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)  //在执行插入和更新操作时，自动填充该字段的值
    private Timestamp endDate;

    private String description;
    private String status;
}
