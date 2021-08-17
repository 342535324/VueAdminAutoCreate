package com.rs.core.autoCreate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 授权模板用的注解 没有此注解的控制器不会生成页面
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerLog {
	String model() default "";

	String icon() default "";
}