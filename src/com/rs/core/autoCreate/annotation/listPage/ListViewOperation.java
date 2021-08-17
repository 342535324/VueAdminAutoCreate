package com.rs.core.autoCreate.annotation.listPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列表操作内容 -该注解用在获取列表数据的方法上
 * 
 * @ 如果需要传参可用重写operation
 * 
 * @ list页面内置listQuery对象作为列表查询参数,在页面初始化的时候会把this.$route.query的内容同步到listQuery对象内
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListViewOperation {
	String operation() default "<router-link :to=\"{path:'/%s/form%s',query: {id: scope.row.id}}\">\r\n<el-button type=\"primary\" size=\"mini\">编辑</el-button>\r\n</router-link>\r\n<el-button size=\"mini\" type=\"danger\" @click=\"handleDelete(scope.row)\">删除</el-button>";// 搜索绑定的字段
}
