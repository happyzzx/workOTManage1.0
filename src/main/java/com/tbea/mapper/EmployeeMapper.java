package com.tbea.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tbea.model.entity.Employee;
import org.apache.ibatis.annotations.*;



@Mapper    //注解标记 MyBatis 的 Mapper 接口，让 Spring Boot 在启动时扫描并创建相应的 Bean。
public interface EmployeeMapper extends BaseMapper<Employee> {
}



//表示 EmployeeMapper 接口继承自 BaseMapper<Employee> 接口
//BaseMapper 是 MyBatis-Plus 提供的一个接口，提供了一些常用的数据库操作方法，如插入、更新、删除、查询等，无需手动编写 SQL
//Employee 是实体类，它与数据库表映射，通常使用 @TableName 注解来指定对应的表名，见model/entity
//定义了一个用于操作 Employee 实体的 Mapper 接口，它继承了 MyBatis-Plus 提供的通用方法，无需手动编写 SQL 语句