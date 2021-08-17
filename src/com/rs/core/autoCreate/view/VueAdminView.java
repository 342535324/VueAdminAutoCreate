package com.rs.core.autoCreate.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.RequestMapping;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.EditPage;
import com.rs.core.autoCreate.annotation.ViewEntity;

/**
 * 内容生成与填充
 */
public class VueAdminView {

	public VueAdminView(VueAdminUtil vueAdminUtil, Class c) throws Exception {
		String name = ((RequestMapping) c.getAnnotation(RequestMapping.class)).value()[0]
				.replaceAll(vueAdminUtil.getApiSuffix(), "");
		if (name.indexOf("/") == 0) {
			name = name.substring(1);
		}
		ViewEntity viewEntity = (ViewEntity) c.getAnnotation(ViewEntity.class);
		Annotation[] basePages = c.getAnnotationsByType(BasePage.class);
		Annotation[] editPages = c.getAnnotationsByType(EditPage.class);
		if (basePages.length < 1 && editPages.length < 1) {
			return;
		}
		vueAdminUtil.createFolder("views/" + name);
		// 创建自动生成标记
		vueAdminUtil.createFile("views/" + name + "/_autoCreate");

		// 根据接口匹配规则 生成list.vue,form.vue.add-form.vue三个页面
		Method[] ms = c.getMethods();
		for (Method method : ms) {
			if (method.isAnnotationPresent(RequestMapping.class)) {
				RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
				String apiURL = requestMapping.value()[0];
				if (viewEntity == null || method.isAnnotationPresent(ViewEntity.class)) {
					viewEntity = method.getAnnotation(ViewEntity.class);
				}
				int i = 0;
				for (Annotation annotation : editPages) {
					EditPage formPage = (EditPage) annotation;
					i++;
					vueAdminUtil.createView_edit(method, formPage, name, i > 0 ? i + "" : "");
				}

				i = 0;
				for (Annotation annotation : basePages) {
					BasePage apiContoller = (BasePage) annotation;
					String istr = i > 0 ? i + "" : "";
					i++;
					if (apiContoller.createView() == false) {
						return;
					} else {
						if (apiURL.equals(apiContoller.selectListApi())) {
							vueAdminUtil.createView_list(method, viewEntity, name, istr, apiContoller);
						} else if (apiURL.equals(apiContoller.selectDetailsApi())) {
							vueAdminUtil.createView_form(method, viewEntity, name, istr, apiContoller);
						} else if (apiURL.equals(apiContoller.addEntityApi())) {
							vueAdminUtil.createView_addform(method, viewEntity, name, istr, apiContoller);
						}
					}
				}
			}
		}
	}

}
