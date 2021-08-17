package com.rs.core.autoCreate.annotation.formPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图片 注意 如果一个实体类有多个FormEntityAttrTypeImg注解,objName不能是一样的!
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormEntityAttrTypeImg {
	// ------------------------------------基础属性--------------------------------------
	String showTitle();// 属性对应显示的标题

	boolean addShow() default true;// 是否在新增页面显示

	boolean editShow() default true;// 是否在编辑页面显示

	boolean readOnly() default false;// 是否是只读属性,:disabled属性都会提交

	boolean addOnly() default false;// 是否是只允许初始化时编辑的属性,:disabled属性,有该属性在数据创建的时候会被提交

	boolean noEditor() default false;// 是否禁止编辑,有该属性无论是修改还是创建都不会被提交

	String submitName() default "";// 数据提交的别名
	// -------------------------------------END-------------------------------------

	// 第一个是showTitle,第二个是回显组件,第三个是objName,第四个是objName，第五个是objName,第六个是禁用属性,第七个是objName,第八个是objName,第九个是http-request,第十个是before-upload,第十一个是objName
	String temp() default "      <el-form-item %s label='%s'>\r\n        %s<img v-if='%s.limit<=1 && %s.fileUrl' :src='%s.fileUrl'  style=\"width: 200px;\" />\r\n        <el-upload\r\n   %s       :with-credentials=\"true\"\r\n          :multiple=\"true\"\r\n          :file-list=\"%s.fileList\"\r\n          :show-file-list=\"%s.limit>1\"\r\n          :http-request=\"%s\" \r\n          :before-upload='%s' \r\n          :limit=\"%s.limit\"\r\n          class=\"editor-slide-upload\"\r\n          action=\"no\"\r\n          list-type=\"picture-card\"\r\n        >\r\n          <el-button type=\"primary\">请选择图片</el-button>\r\n        </el-upload>\r\n      </el-form-item>";

	/**
	 * 上传触发事件
	 */
	String onUploadHandle() default "%s_uploadHandle(param){  this.%s.file = param.file;this.form.%s = param.file;  }";

	/**
	 * 上传前事件 主要实现回显功能
	 */
	String onBeforeFunction() default "%s_uploadBeforeUpload(file){ var self = this,reader = new FileReader();reader.readAsDataURL(file);reader.onload = function(e) {self.%s.fileUrl = e.target.result;};  }";

	int limit() default 0;

	/**
	 * 数据封装对象的名称
	 */
	String objName() default "el_upload";

	// v-if条件
	String iftemp() default "";
}
