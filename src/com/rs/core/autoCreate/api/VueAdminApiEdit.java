package com.rs.core.autoCreate.api;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.RequestMapping;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.EditPage;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeFile;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImg;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImgs;
import com.rs.core.za.testUtil.annotation.ZA_InterfaceNotes;
import com.rs.core.za.testUtil.annotation.ZA_InterfaceNotesParameter;
import com.rs.core.za.testUtil.entity.ZAInterfaceNotesSimpleEntity;

/**
 * 填充api
 */
public class VueAdminApiEdit {

	public VueAdminApiEdit(VueAdminUtil vueAdminUtil, Class<?> c, Annotation[] editPages) {
		try {
			RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
			String controllerURL = requestMapping.value()[0];

			File api = vueAdminUtil.createFile("api" + controllerURL + ".js");
			// 创建自动生成标记
			vueAdminUtil.createFile("api/_autoCreate");
			StringBuilder sb = new StringBuilder(
					"import request from '@/utils/request'\r\nimport qs from 'qs'\r\n%s\r\n");

			boolean improtRequestFile = false;
			// 搜方法
			Method[] ms = c.getMethods();
			for (Method method : ms) {
				RequestMapping mapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
				if (mapping != null && method.isAnnotationPresent(ZA_InterfaceNotes.class)) {
					boolean skip = false;

					ZA_InterfaceNotes interfaceNotes = (ZA_InterfaceNotes) method
							.getAnnotation(ZA_InterfaceNotes.class);
					ZA_InterfaceNotesParameter[] interfaceNotesParameters = method
							.getAnnotationsByType(ZA_InterfaceNotesParameter.class);

					String url = controllerURL + mapping.value()[0];

					ZAInterfaceNotesSimpleEntity zaInterfaceNotesSimpleEntity = new ZAInterfaceNotesSimpleEntity(
							interfaceNotes, interfaceNotesParameters, url);
					sb.append("// ");
					sb.append(zaInterfaceNotesSimpleEntity.getInterfaceNotes().name().trim());
					sb.append("\r\nexport function ");

					sb.append(vueAdminUtil.createApiName(mapping.value()[0]));
					sb.append("(");
					StringBuilder parameters2 = new StringBuilder();

					for (Annotation annotation : editPages) {
						EditPage editPage = (EditPage) annotation;
						boolean f = false;
						if (mapping.value()[0].equals(editPage.modifyEntityApi())) {
							skip = true;
							// 提交参数的别名
							String submitName = null;
							String attrName = null;

							Class<?> entity = editPage.formEntity();
							Field[] fields = entity.getDeclaredFields();
							for (Field field : fields) {
								if (field.isAnnotationPresent(FormEntityAttrTypeImg.class)
										|| field.isAnnotationPresent(FormEntityAttrTypeFile.class)
										|| field.isAnnotationPresent(FormEntityAttrTypeImgs.class)) {
									f = true;
									improtRequestFile = true;
								}
							}

							// for (Field field : fields) {
							// boolean b = field.isAccessible();
							// field.setAccessible(true);
							// boolean isFileArray = false;
							// if
							// (field.isAnnotationPresent(FormEntityAttrTypeDate.class))
							// {
							// // 时间组件
							// FormEntityAttrTypeDate t =
							// field.getAnnotation(FormEntityAttrTypeDate.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							//
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypePassword.class))
							// {
							// // 密码表单
							// FormEntityAttrTypePassword t = field
							// .getAnnotation(FormEntityAttrTypePassword.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypeSelect.class))
							// {
							// // 选择表单
							// FormEntityAttrTypeSelect t =
							// field.getAnnotation(FormEntityAttrTypeSelect.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// } else if
							// (field.isAnnotationPresent(FormEntityAttr.class))
							// {
							// // 默认的字符串表单
							// FormEntityAttr t =
							// field.getAnnotation(FormEntityAttr.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypeNum.class))
							// {
							// FormEntityAttrTypeNum t =
							// field.getAnnotation(FormEntityAttrTypeNum.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypeFile.class))
							// {
							// // 文件上传
							// FormEntityAttrTypeFile t =
							// field.getAnnotation(FormEntityAttrTypeFile.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypeImgs.class))
							// {
							// // 多图上传
							// FormEntityAttrTypeImgs t =
							// field.getAnnotation(FormEntityAttrTypeImgs.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// isFileArray = true;
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypeImg.class))
							// {
							// // 图片上传
							// FormEntityAttrTypeImg t =
							// field.getAnnotation(FormEntityAttrTypeImg.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypeHtml.class))
							// {
							// // html字符串表单
							// FormEntityAttrTypeHtml t =
							// field.getAnnotation(FormEntityAttrTypeHtml.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = field.getName();
							// } else if
							// (field.isAnnotationPresent(FormEntityAttrTypeLocation.class))
							// {
							// // html字符串表单
							// FormEntityAttrTypeLocation t = field
							// .getAnnotation(FormEntityAttrTypeLocation.class);
							// if
							// (mapping.value()[0].equals(editPage.modifyEntityApi())
							// && t.noEditor()) {
							// attrName = null;
							// continue;
							// } else if
							// (StringUtils.isNotEmpty(t.submitName())) {
							// submitName = t.submitName();
							// } else {
							// submitName = field.getName();
							// }
							// attrName = String.format(t.transform(),
							// field.getName());
							// } else {
							// attrName = null;
							// }
							//
							// if (attrName != null) {
							// if (f) {
							// parameters2.append("\r\n if
							// (form.").append(attrName).append("!=null){");
							// if (isFileArray) {
							// parameters2.append("for (var _arrayObj of
							// form.").append(attrName)
							// .append("){data.append('").append(submitName)
							// .append("', _arrayObj);}}");
							// } else {
							// parameters2.append("data.append('").append(submitName).append("',
							// form.")
							// .append(attrName).append(");}");
							//
							// }
							//
							// } else {
							// parameters2.append("\r\n
							// ").append(submitName).append(": form.")
							// .append(attrName).append(",");
							// }
							// }
							// field.setAccessible(b);
							//
							// }

							parameters2 = VueAdminApi.getAttrNameAndSubmitName(fields, false,
									mapping.value()[0].equals(editPage.modifyEntityApi()), f, parameters2);

							if (f) {
								sb.append("form) {\r\n let data = new FormData();").append(parameters2)
										.append("\r\n return requestFile({\r\n    url: '").append(url.substring(1))
										.append("',\r\n    method: 'post'")
										.append(",\r\n    data: data\r\n    })\r\n}\r\n");
							} else {
								sb.append("form) {\r\n  return request({\r\n    url: '").append(url.substring(1))
										.append("',\r\n    method: 'post'");
								if (parameters2.length() > 0) {
									sb.append(",\r\n    data: qs.stringify({").append(parameters2)
											.append("\r\n    })\r\n  })\r\n}\r\n");
								} else {
									sb.append("\r\n  })\r\n}\r\n");
								}
							}

						}
					}

					if (!skip) {
						int i = 0;
						for (ZA_InterfaceNotesParameter za_InterfaceNotesParameter : zaInterfaceNotesSimpleEntity
								.getInterfaceNotesParameters()) {

							if (i > 0) {
								parameters2.append(",");
							}

							parameters2.append("\r\n      ").append(za_InterfaceNotesParameter.name()).append(": form.")
									.append(za_InterfaceNotesParameter.name());
							i++;
						}

						sb.append("form) {\r\n  return request({\r\n    url: '").append(url.substring(1))
								.append("',\r\n    method: 'post'");
						if (parameters2.length() > 0) {
							sb.append(",\r\n    data: qs.stringify({").append(parameters2)
									.append("\r\n    })\r\n  })\r\n}\r\n");
						} else {
							sb.append("\r\n  })\r\n}\r\n");
						}

					}

				}
			}

			vueAdminUtil.write(api, String.format(sb.toString(),
					improtRequestFile ? "import requestFile from '@/utils/request-file'" : ""));

			System.out.println(api.getPath() + "已生成(api)");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
