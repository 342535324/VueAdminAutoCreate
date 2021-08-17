package com.rs.core.autoCreate.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 根据此注解生成list.vue与form.vue
 * 可以写在controller上,也可以写在selectListApi,selectDetailsApi,addEntityApi这三个接口对应的方法上
 * 需要和@BasePage一起用
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewEntity {
	Class listEntity() default ViewEntity.class;// 列表的实体类

	Class formEntity() default ViewEntity.class;// 表单的实体类,包括新增与编辑

}
