package com.jtexplorer.entity.query;
@FunctionalInterface
public interface QueryConvertConsumer<T> {
    Object convert(T t);
}
