package com.rs.core.autoCreate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 有此注解表示是后台用到的API,会根据此接口生成后台专用的api.js 根据selectListApi的链接生成list.vue
 * 根据selectDetailsApi生成form.vue 根据addEntityApi生成add-form.vue
 * 需要和 @ViewEntity一起用,与@FormPage不兼容
 */
@Documented
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
@Repeatable(BasePages.class)
public @interface BasePage {
	String selectListApi() default "/select/getByPage.app";// 列表的api,根据这个路径生成list.vue

	String selectDetailsApi() default "/select/getDetails.app";// 详情api,根据这个路径生成modify-form.vue

	String addEntityApi() default "/add/entity.app";// 新增数据api,根据这个路径生成add-form.vue

	String modifyEntityApi() default "/modify/entity.app";// 修改api

	String deleteEntityApi() default "/delete/entity.app";// 删除数据api

	/**
	 * 为false的话只会生成api文件,不会生成路由与页面
	 */
	boolean createView() default true;

	/**
	 * 排序序号 越小越靠前
	 */
	int index() default 999;

	/**
	 * 是否显示模块新增的路由,true是显示
	 */
	boolean routerShowForm() default false;

	/**
	 * 是否在路由隐藏
	 */
	boolean routerHide() default false;

	/**
	 * 模块的新增按钮, 参数1是路径,参数2是按钮名称
	 */
	String formButtonTemp() default "<router-link :to=\"{path:'%s'}\"><el-button v-waves type=\"primary\" icon=\"el-icon-edit\" style=\"float: right;margin: 30px;\">%s</el-button></router-link>";

	String icon() default "tree";
}
