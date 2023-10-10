package com.tbea.common.utils;


import com.alibaba.fastjson.JSONObject;
import com.tbea.common.type.BusinessException;
import com.tbea.common.type.CommonConstant;
import com.tbea.common.type.Validator;
import com.tbea.model.entity.OTRecord;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.UNKNOWN;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Log4j2
public final class ValidatorUtil {
    public static void validatePeriod(JSONObject request) {
        Timestamp beginTime = request.getTimestamp(CommonConstant.FIELD_DATE_BEGIN);
        Timestamp endTime = request.getTimestamp(CommonConstant.FIELD_DATE_END);
        if (beginTime == null || endTime == null || beginTime.after(endTime)) {
            throw new BusinessException("query params is invalid");
        }
    }

    public static void validatePeriodDefault(JSONObject request) {
        Timestamp beginDate = DateTimeUtil.monthBeginTime(new Timestamp(System.currentTimeMillis()));
        String beginDateStr = request.getString(CommonConstant.FIELD_DATE_BEGIN);
        if (StringUtils.isNotEmpty(beginDateStr)) {
            beginDate = DateTimeUtil.dayBeginTime(DateTimeUtil.stringToTimestamp(beginDateStr, CommonConstant.DATE_FORMAT_DEFAULT));
        }
        request.put(CommonConstant.FIELD_DATE_BEGIN, beginDate);

        Timestamp endDate = DateTimeUtil.dayEndTime(new Timestamp(System.currentTimeMillis()));
        String endDateStr = request.getString(CommonConstant.FIELD_DATE_END);
        if (StringUtils.isNotEmpty(endDateStr)) {
            endDate = DateTimeUtil.dayEndTime(DateTimeUtil.stringToTimestamp(endDateStr,
                    CommonConstant.DATE_FORMAT_DEFAULT));
        }
        request.put(CommonConstant.FIELD_DATE_END, endDate);

        if (beginDate.after(endDate)) {
            throw new BusinessException("query params is invalid");
        }
    }

    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return CommonConstant.HTTP_HEADER_UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || CommonConstant.HTTP_HEADER_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || CommonConstant.HTTP_HEADER_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || CommonConstant.HTTP_HEADER_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || CommonConstant.HTTP_HEADER_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || CommonConstant.HTTP_HEADER_UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

    public static void validatePage(JSONObject request) {
        if (request.getIntValue(CommonConstant.PAGE_CURRENT_FIELD) <= 0) {
            request.put(CommonConstant.PAGE_CURRENT_FIELD,CommonConstant.PAGE_CURRENT_DEFAULT);
        }
        if (request.getIntValue(CommonConstant.PAGE_SIZE_FIELD) <= 0) {
            request.put(CommonConstant.PAGE_SIZE_FIELD,CommonConstant.PAGE_SIZE_DEFAULT);
        }
    }

    public static Validator getValidatorByType(Class cls) {
        log.info("getValidatorByType->cls:" + cls.getName());
        Validator validator = null;
        Map<String, Validator> validators = SpringUtil.getBeans(Validator.class);
        for (Map.Entry<String, Validator> entry : validators.entrySet()) {
            Type[] interfaces = entry.getValue().getClass().getGenericInterfaces();
            if (interfaces.length == 0) {
                log.info("getValidatorByType->has no GenericInterfaces");
                continue;
            }
            Optional<Type> first = Arrays.stream(interfaces).filter((Type type) -> {
                Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
                return typeArguments.length > 0?typeArguments[0].getTypeName().equals(cls.getName()): false;
            }).findFirst();
            if (first.isPresent()) {
                validator = entry.getValue();
                break;
            }
        }
        log.info("getValidatorByType->validator:" + validator);
        return validator;
    }
}
