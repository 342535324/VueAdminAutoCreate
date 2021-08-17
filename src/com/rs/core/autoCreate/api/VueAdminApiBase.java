package com.rs.core.autoCreate.api;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.ViewEntity;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeFile;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImg;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImgs;
import com.rs.core.za.testUtil.annotation.ZA_InterfaceNotes;
import com.rs.core.za.testUtil.annotation.ZA_InterfaceNotesParameter;
import com.rs.core.za.testUtil.entity.ZAInterfaceNotesSimpleEntity;

/**
 * 填充api
 */
public class VueAdminApiBase {

	/**
	 * 查询参数别名映射
	 */
	public static final Map<String, String> listQueryMapping = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("page", "listQuery.page-1");
			put("pageSize", "listQuery.limit");
		}
	};

	public VueAdminApiBase(VueAdminUtil vueAdminUtil, Class<?> c, Annotation[] basePages) {
		try {
			RequestMapping requestMapping = (RequestMapping) c.getAnnotation(RequestMapping.class);
			String controllerURL = requestMapping.value()[0];
			ViewEntity viewEntity = (ViewEntity) c.getAnnotation(ViewEntity.class);

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

					if (method.isAnnotationPresent(ViewEntity.class)) {
						viewEntity = method.getAnnotation(ViewEntity.class);
					}

					ZA_InterfaceNotes interfaceNotes = (ZA_InterfaceNotes) method
							.getAnnotation(ZA_InterfaceNotes.class);
					ZA_InterfaceNotesParameter[] interfaceNotesParameters = method
							.getAnnotationsByType(ZA_InterfaceNotesParameter.class);
					String apiUrl = mapping.value()[0];
					String url = controllerURL + apiUrl;

					ZAInterfaceNotesSimpleEntity zaInterfaceNotesSimpleEntity = new ZAInterfaceNotesSimpleEntity(
							interfaceNotes, interfaceNotesParameters, url);
					sb.append("// ");
					sb.append(zaInterfaceNotesSimpleEntity.getInterfaceNotes().name().trim());
					sb.append("\r\nexport function ");

					sb.append(vueAdminUtil.createApiName(apiUrl));
					sb.append("(");
					// StringBuilder parameters = new StringBuilder();
					StringBuilder parameters2 = new StringBuilder();

					List<String> addApis = new ArrayList<String>();// 所有新增接口均放入这个集合
					List<String> modifyApis = new ArrayList<String>();// 所有修改接口均放入这个集合
					List<String> selectListApis = new ArrayList<String>();// 所有列表查询均放入这个集合
					for (Annotation annotation : basePages) {
						BasePage basePage = (BasePage) annotation;
						if (StringUtils.isNotEmpty(basePage.addEntityApi())) {
							addApis.add(basePage.addEntityApi());
						}
						if (StringUtils.isNotEmpty(basePage.modifyEntityApi())) {
							modifyApis.add(basePage.modifyEntityApi());
						}
						if (StringUtils.isNotEmpty(basePage.selectListApi())) {
							selectListApis.add(basePage.selectListApi());
						}
					}

					boolean hasFile = false, isListApi = selectListApis.contains(apiUrl),
							isAddApi = addApis.contains(apiUrl), isModifyApi = modifyApis.contains(apiUrl);

					// 提交参数的别名
					// String submitName = null;
					// String attrName = null;

					if (isAddApi || isModifyApi) {
						if (viewEntity == null) {
							return;
						}
						Class<?> entity = viewEntity.formEntity();
						Field[] fields = entity.getDeclaredFields();
						for (Field field : fields) {
							if (field.isAnnotationPresent(FormEntityAttrTypeImg.class)
									|| field.isAnnotationPresent(FormEntityAttrTypeFile.class)
									|| field.isAnnotationPresent(FormEntityAttrTypeImgs.class)) {
								hasFile = true;
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
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
						// submitName = t.submitName();
						// } else {
						// submitName = field.getName();
						// }
						// attrName = field.getName();
						// } else if
						// (field.isAnnotationPresent(FormEntityAttrTypePassword.class))
						// {
						// // 密码表单
						// FormEntityAttrTypePassword t =
						// field.getAnnotation(FormEntityAttrTypePassword.class);
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
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
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
						// submitName = t.submitName();
						// } else {
						// submitName = field.getName();
						// }
						// attrName = field.getName();
						// if (t.multiple()) {
						// // 多选 传数组
						// attrName = String.format(t.transform(),
						// field.getName());
						// }
						//
						// } else if
						// (field.isAnnotationPresent(FormEntityAttr.class)) {
						// // 默认的字符串表单
						// FormEntityAttr t =
						// field.getAnnotation(FormEntityAttr.class);
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
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
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
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
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
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
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
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
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
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
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
						// submitName = t.submitName();
						// } else {
						// submitName = field.getName();
						// }
						// attrName = field.getName();
						// } else if
						// (field.isAnnotationPresent(FormEntityAttrTypeLocation.class))
						// {
						// FormEntityAttrTypeLocation t =
						// field.getAnnotation(FormEntityAttrTypeLocation.class);
						// if ((isAddApi && t.readOnly()) || (isModifyApi &&
						// (t.addOnly() || t.noEditor()))) {
						// attrName = null;
						// continue;
						// } else if (StringUtils.isNotEmpty(t.submitName())) {
						// submitName = t.submitName();
						// } else {
						// submitName = field.getName();
						// }
						// // 转换
						// attrName = String.format(t.transform(),
						// field.getName());
						// } else {
						// attrName = null;
						// }
						//
						// if (attrName != null) {
						// if (hasFile) {
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
						// }
						// } else {
						// parameters2.append("\r\n
						// ").append(submitName).append(": form.")
						// .append(attrName).append(",");
						// }
						// }
						// field.setAccessible(b);
						// }

						parameters2 = VueAdminApi.getAttrNameAndSubmitName(fields, isAddApi, isModifyApi, hasFile,
								parameters2);

						if (hasFile) {
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

					} else {
						int i = 0;
						for (ZA_InterfaceNotesParameter za_InterfaceNotesParameter : zaInterfaceNotesSimpleEntity
								.getInterfaceNotesParameters()) {

							if (i > 0) {
								// parameters.append(", ");
								parameters2.append(",");
							}

							// parameters.append(za_InterfaceNotesParameter.name());
							parameters2.append("\r\n      ").append(za_InterfaceNotesParameter.name()).append(": ")
									.append(listQueryMapping.containsKey(za_InterfaceNotesParameter.name())
											? listQueryMapping.get(za_InterfaceNotesParameter.name())
											: (isListApi ? ("listQuery." + za_InterfaceNotesParameter.name())
													// :
													// za_InterfaceNotesParameter.name()));
													: ("query." + za_InterfaceNotesParameter.name()
															+ " ==null? query:query."
															+ za_InterfaceNotesParameter.name())));
							i++;
						}

						sb.append(isListApi ? "listQuery = {}" : "query = {}")
								.append(") {\r\n  return request({\r\n    url: '").append(url.substring(1))
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
