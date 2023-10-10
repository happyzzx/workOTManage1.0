package com.tbea.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.tbea.common.type.CommonConstant;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Log4j2
@Component
public class HhMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter(CommonConstant.FIELD_DATE_BEGIN) && metaObject.getValue(CommonConstant.FIELD_DATE_BEGIN) == null) {
            setFieldValByName(CommonConstant.FIELD_DATE_BEGIN, new Timestamp(new Date().getTime()), metaObject);
        }
        if (metaObject.hasSetter(CommonConstant.FIELD_DATE_END) && metaObject.getValue(CommonConstant.FIELD_DATE_END) == null) {
            setFieldValByName(CommonConstant.FIELD_DATE_END, new Timestamp(new Date().getTime()), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(CommonConstant.FIELD_DATE_END) && metaObject.getValue(CommonConstant.FIELD_DATE_END) == null) {
            setFieldValByName(CommonConstant.FIELD_DATE_END, new Timestamp(new Date().getTime()), metaObject);
        }
    }
}
