package com.rs.core.autoCreate.annotation.listPage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 过滤属性 -该注解用在获取列表数据的方法上
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ListViewFilters.class)
public @interface ListViewFilter {

	String queryParamName() default "listQuery";// 内置列表查询参数对象,所有查询相关参数都放入这个对象中,列表的参数就是这个对象

	String model() default "filter";// 搜索绑定的字段,会自动拼上queryParamName的value

	String placeholder() default "请选择";// 搜索输入框的提示

	String listName() default "options";// 选项内容的属性名,会自动拼上queryParamName的value

	// 获取选项内容数组的js方法,需要给上面的listName的值赋值,例如listName是typeList,那么
	// getTypeList(){
	// this.listQuery.typeList
	// =[{key:0,text:'支付宝'},{key:1,text:'微信'}]},返回的数组对象key是key,value是text
	String getListFunction();

	String changeFunction() default "handleFilter"; // change事件

}
