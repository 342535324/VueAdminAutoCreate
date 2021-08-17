package com.rs.core.autoCreate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 有此注解会生成对应的form页面 注意:与@BasePage不兼容
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
@Repeatable(EditPages.class)
public @interface EditPage {
	String name();// 显示模块名称

	String selectDetailsApi() default "/select/getDetails.app";// 详情api

	String modifyEntityApi() default "/modify/entity.app";// 修改api

	Class formEntity() default ViewEntity.class;// 新增与编辑的表单实体类

	/**
	 * 是否在路由隐藏
	 */
	boolean routerHide() default false;

	String icon() default "edit";
}
