package com.rs.core.autoCreate.annotation.formPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 富文本编辑器,有此注解表示是表显示用到的字段,会通过接口返回的实体类的属性查找该注解,然后根据该注解生成后台用的view -该注解用在实体类的字段上
 * https://panjiachen.gitee.io/vue-element-admin-site/zh/feature/component/rich-
 * editor.html#tinymce
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormEntityAttrTypeHtml {

	// ------------------------------------基础属性--------------------------------------
	String showTitle();// 属性对应显示的标题

	boolean addShow() default true;// 是否在新增页面显示

	boolean editShow() default true;// 是否在编辑页面显示

	boolean readOnly() default false;// 是否是只读属性,:disabled属性都会提交

	boolean addOnly() default false;// 是否是只允许初始化时编辑的属性,:disabled属性,有该属性在数据创建的时候会被提交

	boolean noEditor() default false;// 是否禁止编辑,有该属性无论是修改还是创建都不会被提交

	String submitName() default "";// 数据提交的别名
	// -------------------------------------END-------------------------------------

	// 第一个是showTitle,第二个是属性名,第三个是高度
	String temp() default "      <el-form-item %s label=\"%s\">\r\n     <tinymce %s menubar=\"%s\" v-model=\"form.%s\" :height=\"%s\" %s />\r\n</el-form-item>";

	int height() default 300;

	String toolbar() default "";// 工具栏控件 是个数组

	String menubar() default "file edit insert view format table";// menubar控件

	String uploadImgApiUrl() default "/common/add/updateInformationImg.app";

	String uploadImgFileKey() default "file";

	// v-if条件
	String iftemp() default "";
}
