package com.juyoung.restapiwithspring.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 테스트 설명용 애노테이션 만들기
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)                // 얼마나 오래 유지할 것 이냐
public @interface TestDescription {

    String value();

}