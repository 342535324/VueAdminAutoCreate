package com.rs.core.autoCreate.annotation.listPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字符串搜索 -该注解用在获取列表数据的方法上
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListViewSearch {

	String model() default "listQuery.q";// 搜索绑定的字段

	String placeholder() default "请输入关键字";// 搜索输入框的提示

	String event() default "handleFilter";// 输入框回车/搜索按钮点击后 触发的事件

}
