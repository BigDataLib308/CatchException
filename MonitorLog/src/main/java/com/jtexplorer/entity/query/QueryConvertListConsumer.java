package com.jtexplorer.entity.query;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

@FunctionalInterface
public interface QueryConvertListConsumer<T> {
    List convert(IPage<T> t);
}
