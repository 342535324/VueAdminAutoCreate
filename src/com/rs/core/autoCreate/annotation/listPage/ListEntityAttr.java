package com.rs.core.autoCreate.annotation.listPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表格控制,有此注解表示是表显示用到的字段,会通过接口返回的实体类的属性查找该注解,然后根据该注解生成后台用的view -该注解用在实体类的字段上
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListEntityAttr {
	String showTitle();// 属性对应显示的标题

	int level()

	default 0;// 属性等级 1表示高级属性,列表默认隐藏,点击“全部显示”才会显示,2表示精简模式也显示

	String value()

	default "";// 列表显示的值 如果为空则直接输出属性的值,否则输出设置的值,字符串的要用单引号

}
