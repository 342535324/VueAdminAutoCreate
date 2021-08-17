package com.rs.core.autoCreate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 路由排序 如果没有此注解 默认按名称排序
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
public @interface RouterIndex {
	/**
	 * 排序序号 越小越靠前
	 */
	int value();
}
