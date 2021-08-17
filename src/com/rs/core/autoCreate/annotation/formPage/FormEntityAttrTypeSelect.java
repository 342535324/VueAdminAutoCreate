package com.rs.core.autoCreate.annotation.formPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * select选择框 联动使用方法: 1.父级定义changeFunction属性,触发子级获取数据方法 2.子级传入父级的对应数据
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormEntityAttrTypeSelect {
	// ------------------------------------基础属性--------------------------------------
	String showTitle();// 属性对应显示的标题

	boolean addShow() default true;// 是否在新增页面显示

	boolean editShow() default true;// 是否在编辑页面显示

	boolean readOnly() default false;// 是否是只读属性,:disabled属性都会提交

	boolean addOnly() default false;// 是否是只允许初始化时编辑的属性,:disabled属性,有该属性在数据创建的时候会被提交

	boolean noEditor() default false;// 是否禁止编辑,有该属性无论是修改还是创建都不会被提交

	String submitName() default "";// 数据提交的别名
	// -------------------------------------END-------------------------------------

	// 第一个是if条件,第二个是showTitle,第三个是提示"请选择"+showTitle,第四个是属性名,第五个是扩展属性如禁止编辑只读等,第六个是nulltemp,第七个是listName
	String temp()

	default "      <el-form-item %s label=\"%s\">\r\n        <el-select placeholder=\"%s\" v-model=\"form.%s\" %s filterable >\r\n          %s<el-option v-for=\"(item,index) in %s\" :key=\"index\" :label=\"item.text\" :value=\"item.key\"  />\r\n        </el-select>\r\n      </el-form-item>";

	// 选择列表数组的属性名,为空默认自动生成
	String listName()

	default "";

	// 允许为空
	boolean allowNull()

	default false;

	String nulltemp()

	default "<el-option label=\"设置为空\" value=\"\" />\r\n";

	// 获取list作为select选项的事件,会自动生成到方法
	String getListFunction();

	// 多选
	boolean multiple() default false;

	// change事件
	String changeFunction() default "";

	// v-if条件
	String iftemp() default "";

	// 提交时对参数进行转换
	String transform() default "%s.join(',')";
}
