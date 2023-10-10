package com.tbea.common.interceptor;

import com.tbea.common.type.BusinessException;
import com.tbea.common.type.ForbiddenException;
import com.tbea.common.type.ResultEnum;
import com.tbea.model.vo.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Log4j2
@RestControllerAdvice(basePackages = "com.tbea")
public class ExceptionAdvice {
    /**
     * 捕获 {@code BusinessException} 异常
     */
    @ExceptionHandler({BusinessException.class})
    public Result<?> handleBusinessException(BusinessException ex) {
        log.info("BusinessException:{}",ex.getMessage());
        ex.printStackTrace();
        return Result.failed(ex.getMessage());
    }

    /**
     * 捕获 {@code ForbiddenException} 异常
     */
    @ExceptionHandler({ForbiddenException.class})
    public Result<?> handleForbiddenException(ForbiddenException ex) {
        log.info("ForbiddenException");
        return Result.failed(ResultEnum.FORBIDDEN);
    }

    /**
     * {@code @RequestBody} 参数校验不通过时抛出的异常处理
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.info("MethodArgumentNotValidException");
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder sb = new StringBuilder("校验失败:");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append("：").append(fieldError.getDefaultMessage()).append(", ");
        }
        String msg = sb.toString();
        if (StringUtils.hasText(msg)) {
            return Result.failed(ResultEnum.VALIDATE_FAILED.getCode(), msg);
        }
        return Result.failed(ResultEnum.VALIDATE_FAILED);
    }

    /**
     * {@code @PathVariable} 和 {@code @RequestParam} 参数校验不通过时抛出的异常处理
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public Result<?> handleConstraintViolationException(ConstraintViolationException ex) {
        log.info("ConstraintViolationException");
        if (StringUtils.hasText(ex.getMessage())) {
            return Result.failed(ResultEnum.VALIDATE_FAILED.getCode(), ex.getMessage());
        }
        return Result.failed(ResultEnum.VALIDATE_FAILED);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<?> handlerException(RuntimeException re) {
        log.info("RuntimeException:" + re);
        re.printStackTrace();
        return Result.failed("系统运行时异常,请联系管理员!");
    }

    // 捕捉其他所有异常
    @ExceptionHandler(Exception.class)
    public Result globalException(Throwable ex) {
        log.info("Exception:{}",ex.getMessage());
        ex.printStackTrace();
        return Result.failed("访问出错，无法访问: " + ex.getMessage());
    }
}
