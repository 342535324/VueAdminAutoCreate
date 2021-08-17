package com.rs.core.autoCreate.router;

import java.lang.annotation.Annotation;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.EditPage;
import com.rs.core.autoCreate.annotation.RouterIndex;
import com.rs.core.autoCreate.annotation.RouterModelName;
import com.rs.core.autoCreate.entity.AdminManageModel;
import com.rs.core.autoCreate.entity.RouterV;

public class VueAdminRouter {
	private RouterV router;

	public VueAdminRouter(VueAdminUtil vueAdminUtil, Class<?> c, List<AdminManageModel> adminManageModels)
			throws Exception {
		RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
		String controllerURL = requestMapping.value()[0];

		AdminManageModel adminManageModel = null;
		for (AdminManageModel IAdminManageModel : adminManageModels) {
			if (controllerURL.equals(IAdminManageModel.getUrl())) {
				adminManageModel = IAdminManageModel;
				break;
			}
		}

		if (adminManageModel == null) {
			System.err.println(controllerURL + " 获取授权失败");
			return;
		}
		Annotation[] routerModelNames = c.getAnnotationsByType(RouterModelName.class);

		RouterIndex routerIndex = (RouterIndex) c.getAnnotation(RouterIndex.class);
		if (routerModelNames != null && routerModelNames.length > 0) {
			for (Annotation annotation : routerModelNames) {
				RouterModelName modelName = (RouterModelName) annotation;
				this.router = new RouterV(vueAdminUtil, adminManageModel, c.getAnnotationsByType(BasePage.class),
						c.getMethods(), c.getAnnotationsByType(EditPage.class), modelName, controllerURL, routerIndex);

			}
		} else {
			this.router = new RouterV(vueAdminUtil, adminManageModel, c.getAnnotationsByType(BasePage.class),
					c.getMethods(), c.getAnnotationsByType(EditPage.class), null, controllerURL, routerIndex);
		}

	}

	public RouterV getRouter() {
		return router;
	}

}
