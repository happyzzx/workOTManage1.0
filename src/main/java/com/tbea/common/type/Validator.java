package com.tbea.common.type;

public interface Validator<T> {
    boolean validate(T obj1, T obj2);
}
