package com.rs.core.autoCreate.annotation.formPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表格控制,有此注解表示是表显示用到的字段,会通过接口返回的实体类的属性查找该注解,然后根据该注解生成后台用的view -该注解用在实体类的字段上
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface FormEntityAttr {
	// ------------------------------------基础属性--------------------------------------
	String showTitle();// 属性对应显示的标题

	boolean addShow() default true;// 是否在新增页面显示

	boolean editShow() default true;// 是否在编辑页面显示

	boolean readOnly() default false;// 是否是只读属性,:disabled属性都会提交

	boolean addOnly() default false;// 是否是只允许初始化时编辑的属性,:disabled属性,有该属性在数据创建的时候会被提交

	boolean noEditor() default false;// 是否禁止编辑,有该属性无论是修改还是创建都不会被提交

	String submitName() default "";// 数据提交的别名
	// -------------------------------------END-------------------------------------

	// 第一个是if条件,第二个是showTitle,第三个是属性名,第四个是扩展属性如禁止编辑只读等
	String temp() default "      <el-form-item %s label=\"%s\">\r\n    <el-input v-model=\"form.%s\"  %s />\r\n</el-form-item>";

	// v-if条件
	String iftemp() default "";
}
