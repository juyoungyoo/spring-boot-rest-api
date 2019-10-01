package com.juyoung.restapiwithspring.global.converter;

public interface Converter<T, R> {

    R convert(T t);
}
