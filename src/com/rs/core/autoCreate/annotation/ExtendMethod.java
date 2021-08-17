package com.rs.core.autoCreate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 拓展js方法,1个注解对应一个方法
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
@Repeatable(ExtendMethods.class)
public @interface ExtendMethod {
	String methodName();// 方法名

	String methodBody();// 方法体

	String methodParam();// 方法参数

	boolean createdRun() default false;// 是否初始化后运行并调用
}