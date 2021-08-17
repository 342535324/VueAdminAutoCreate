package com.rs.core.autoCreate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 路由模块名称,相同会纳入同一个模块下面 如果没有此注解 默认控制器名称分组
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
@Repeatable(RouterModelNames.class)
public @interface RouterModelName {
	/**
	 * 模块名称,相同会纳入同一个模块下面
	 */
	String value();
}
