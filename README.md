# README



## 1、模块结构

src/main/java/com.tbea

│Application				  应用程序的入口点、Spring MVC、MyBatis的配置

├─controller                  处理客户端的请求，根据请求类型和参数调用service中相应的业务方法，并返回处理结果  

├─service                       方法的具体实现：开始和结束记录、数据库查询和更新等

├─handler                      元对象处理器，设置实时时间戳

├─mapper                      定义操作实体的 Mapper 接口，它继承了 MyBatis-Plus 的通用方法，无需手写 SQL 语句

├─model

​	 ├─entity								定义实体类及其和数据库表的映射关系

​     └─vo                                      定义结果对象：结果状态码、文本消息、所携数据

├─common

​     ├─interceptor             异常处理类 ExceptionAdvice、统一接口的响应格式类 ResponseAdvice

​	 ├─type                         定义类型：常量、返回类型DataWithPage、自定义异常、IResult接口的枚举类型...

​	 └─utils                         操作日期时间和Spring 上下文、验证时间段和ip地址的工具类                          

​     └─CorsConfig              配置跨域资源共享



## 2、代码注释

详见代码



## 3、后端API

### 1、分页查询所有员工加班时长

**功能**：分页查询所有员工加班时长

**协议**：POST

**URL**：/statistics

**入参**：

| **字段名** | **类型** | **M/O** | **说明**         |
| ---------- | -------- | ------- | ---------------- |
| beginDate  | string   | O       | 加班统计开始日期 |
| endDate    | string   | O       | 加班统计结束日期 |
| page       | Int      | O       | 当前页           |
| pageSize   | Int      | O       | 每页条数         |

```
示例：
{
  "beginDate": "2023-9-25",
  "endDate": "2023-9-25",
  "page": 1,
  "pageSize": 10
}

返回值：
{
  "code": 0,
  "message": "接口调用成功",
  "data": {
     "data": [
       {
         "total": 640000,
         "ip": "127.0.0.1",
         "name": "李志刚",
         "id": 374165
       }
     ],
     "total": 1,
     "pageSize": 100,
     "page": 1
  }
}
```

 

### 2、分页查询员工的加班详情

**功能**：分页查询员工的加班详情

**协议**：POST

**URL**：/list

**入参**：

| **字段名** | **类型** | **M/O** | **说明**         |
| ---------- | -------- | ------- | ---------------- |
| employee   | Long     | M       | 员工工号         |
| beginDate  | string   | O       | 加班统计开始日期 |
| endDate    | string   | O       | 加班统计结束日期 |
| page       | Int      | O       | 当前页           |
| pageSize   | Int      | O       | 每页条数         |

```
示例：
{
  "employee": 374165,
  "beginDate": "2023-9-24",
  "endDate": "2023-9-25",
  "page": 1,
  "pageSize": 10
}

返回值：
{
  "code": 0,
  "message": "接口调用成功",
  "data": {
     "data": [
       {
         "beginDate": 1695610896000,
         "endDate": 1695611536000,
         "employeeId": 374165,
         "id": 1,
         "status": "end"
       }
     ],
     "total": 1,
     "pageSize": 10,
     "page": 1
  }
}
```

 

### 3、开始加班登记

**功能**：开始加班登记

**协议**：POST

**URL**：/start

**入参**：

| **字段名** | **类型** | **M/O** | **说明** |
| ---------- | -------- | ------- | -------- |
| employee   | Long     | M       | 员工工号 |
| reason     | string   | O       | 加班原因 |

```
示例：
{
  "employee": 374165,
  "reason": "month"
} 

返回值：
{
  "code": 0,
  "message": "接口调用成功",
  "data": 4 //加班记录ID
}
```

 

### 4、加班登记

**功能**：加班登记

**协议**：POST

**URL**：/{recordId}/update

**入参**：

| **字段名** | **类型** | **M/O** | **说明**   |
| ---------- | -------- | ------- | ---------- |
| recordId   | Long     | M       | 加班记录ID |

```
示例：
{

}
 
返回值：

{
  "code": 0,
  "message": "接口调用成功",
  "data": {
     "beginDate": 1695624349000,
     "endDate": 1695629765908,
     "description": "month",
     "employeeId": 374165,
     "id": 2,
     "status": "working"
  }
}
```



### 5、结束加班登记

**功能**：结束加班登记

**协议**：POST

**URL**：/{recordId}/stop

**入参**：

| **字段名** | **类型** | **M/O** | **说明**   |
| ---------- | -------- | ------- | ---------- |
| recordId   | Long     | M       | 加班记录ID |

```
示例：
{

}

返回值：
{
  "code": 0,
  "message": "接口调用成功",
  "data": {
     "beginDate": 1695624349000,
     "endDate": 1695629766000,
     "description": "month",
     "employeeId": 374165,
     "id": 2,
     "status": "end"
  }
}
```



### 6、获取客户端IP

**功能**：获取客户端IP

**协议**：GET

**URL**：/client_ip

**入参**：

| **字段名** | **类型** | **M/O** | **说明** |
| ---------- | -------- | ------- | -------- |
|            |          |         |          |

```
示例：
{

}

返回值：
{
  "code": 0,
  "message": "接口调用成功",
  "data": "192.168.1.11"
}
```







