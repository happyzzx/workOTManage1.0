package com.tbea.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                     // Lombok注解，自动为类生成 toString、equals、hashCode 等数据处理方法
@NoArgsConstructor        //Lombok注解，自动生成一个无参构造函数
@TableName("t_employee")  // MyBatis Plus注解，指定对应的数据库表名为 "t_employee"


public class Employee {
    @TableId(type = IdType.AUTO)    //指定 id 字段为自动生成的主键
    private Long id;
    private String name;
    private String ip;
}
