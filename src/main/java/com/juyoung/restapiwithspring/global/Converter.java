package com.juyoung.restapiwithspring.global;

public interface Converter<T, R> {

    R convert(T t);
}
