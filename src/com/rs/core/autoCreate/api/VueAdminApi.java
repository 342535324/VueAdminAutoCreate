package com.rs.core.autoCreate.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.EditPage;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttr;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeDate;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeFile;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeHtml;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImg;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImgs;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeLocation;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeNum;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypePassword;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeSelect;

/**
 * 填充api
 */
public class VueAdminApi {

	public VueAdminApi(VueAdminUtil vueAdminUtil, Class<?> c) {
		Annotation[] basePages = c.getAnnotationsByType(BasePage.class);
		Annotation[] editPages = c.getAnnotationsByType(EditPage.class);
		if (basePages.length > 0) {
			new VueAdminApiBase(vueAdminUtil, c, basePages);
		} else if (editPages.length > 0) {
			new VueAdminApiEdit(vueAdminUtil, c, editPages);
		}

	}

	public static StringBuilder getAttrNameAndSubmitName(Field[] fields, boolean isAddApi, boolean isModifyApi,
			boolean hasFile, StringBuilder parameters2) {
		String attrName = null;
		String submitName = null;

		for (Field field : fields) {
			boolean b = field.isAccessible();
			field.setAccessible(true);
			boolean isFileArray = false;
			if (field.isAnnotationPresent(FormEntityAttrTypeDate.class)) {
				// 时间组件
				FormEntityAttrTypeDate t = field.getAnnotation(FormEntityAttrTypeDate.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
			} else if (field.isAnnotationPresent(FormEntityAttrTypePassword.class)) {
				// 密码表单
				FormEntityAttrTypePassword t = field.getAnnotation(FormEntityAttrTypePassword.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
			} else if (field.isAnnotationPresent(FormEntityAttrTypeSelect.class)) {
				// 选择表单
				FormEntityAttrTypeSelect t = field.getAnnotation(FormEntityAttrTypeSelect.class);

				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
				// if (t.multiple()) {
				// // 多选 传数组
				// attrName = String.format(t.transform(), field.getName());
				// }
			} else if (field.isAnnotationPresent(FormEntityAttr.class)) {
				// 默认的字符串表单
				FormEntityAttr t = field.getAnnotation(FormEntityAttr.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
			} else if (field.isAnnotationPresent(FormEntityAttrTypeNum.class)) {
				FormEntityAttrTypeNum t = field.getAnnotation(FormEntityAttrTypeNum.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
			} else if (field.isAnnotationPresent(FormEntityAttrTypeFile.class)) {
				// 文件上传
				FormEntityAttrTypeFile t = field.getAnnotation(FormEntityAttrTypeFile.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
			} else if (field.isAnnotationPresent(FormEntityAttrTypeImgs.class)) {
				// 多图上传
				FormEntityAttrTypeImgs t = field.getAnnotation(FormEntityAttrTypeImgs.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
				isFileArray = true;
			} else if (field.isAnnotationPresent(FormEntityAttrTypeImg.class)) {
				// 图片上传
				FormEntityAttrTypeImg t = field.getAnnotation(FormEntityAttrTypeImg.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
			} else if (field.isAnnotationPresent(FormEntityAttrTypeHtml.class)) {
				// html字符串表单
				FormEntityAttrTypeHtml t = field.getAnnotation(FormEntityAttrTypeHtml.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				attrName = field.getName();
			} else if (field.isAnnotationPresent(FormEntityAttrTypeLocation.class)) {
				FormEntityAttrTypeLocation t = field.getAnnotation(FormEntityAttrTypeLocation.class);
				if ((isModifyApi && t.addOnly()) || t.noEditor()) {
					attrName = null;
					continue;
				} else if (StringUtils.isNotEmpty(t.submitName())) {
					submitName = t.submitName();
				} else {
					submitName = field.getName();
				}
				// 转换
				attrName = String.format(t.transform(), field.getName(), "form." + field.getName(),
						"form." + field.getName());
			} else {
				attrName = null;
			}

			if (attrName != null) {
				if (hasFile) {
					parameters2.append("\r\n      if (form.").append(attrName).append("!=null){");
					if (isFileArray) {
						parameters2.append("for (var _arrayObj of form.").append(attrName).append("){data.append('")
								.append(submitName).append("', _arrayObj);}}");
					} else {
						parameters2.append("data.append('").append(submitName).append("', form.").append(attrName)
								.append(");}");
					}
				} else {
					parameters2.append("\r\n      ").append(submitName).append(": form.").append(attrName).append(",");
				}
			}
			field.setAccessible(b);
		}

		return parameters2;
	}
}
