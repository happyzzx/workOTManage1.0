package com.tbea.common.type;

public enum ResultEnum implements IResult{
    SUCCESS(0, "接口调用成功"),
    VALIDATE_FAILED(-1, "参数校验失败"),
    COMMON_FAILED(-2, "接口调用失败"),
    FORBIDDEN(-3, "没有权限访问资源");

    private Integer code;
    private String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
