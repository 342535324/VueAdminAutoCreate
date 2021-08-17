package com.rs.core.autoCreate.annotation.formPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 图片多选 1.如果一个页面有多个FormEntityAttrTypeImgs,其中一个需要设置objName避免冲突 2.提交的文件key是属性名
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormEntityAttrTypeImgs {
	// ------------------------------------基础属性--------------------------------------
	String showTitle();// 属性对应显示的标题

	boolean addShow() default true;// 是否在新增页面显示

	boolean editShow() default true;// 是否在编辑页面显示

	boolean readOnly() default false;// 是否是只读属性,:disabled属性都会提交

	boolean addOnly() default false;// 是否是只允许初始化时编辑的属性,:disabled属性,有该属性在数据创建的时候会被提交

	boolean noEditor() default false;// 是否禁止编辑,有该属性无论是修改还是创建都不会被提交

	String submitName() default "";// 数据提交的别名
	// -------------------------------------END-------------------------------------

	String temp() default " <el-form-item %s label='%s'> %s  \r\n        <el-upload\r\n   %s       :with-credentials=\"true\"\r\n          :multiple=\"true\"\r\n          :file-list=\"%s.fileList\"\r\n          :show-file-list=\"true\"\r\n          :http-request=\"%s\" \r\n          :before-upload='%s' \r\n      :on-remove='%s'              class=\"editor-slide-upload\"\r\n          action=\"no\"\r\n          list-type=\"picture-card\"\r\n        >\r\n          <el-button type=\"primary\">请选择图片(多选)</el-button>\r\n        </el-upload>\r\n      </el-form-item>%s";

	/**
	 * 上传触发事件
	 */
	String onUploadHandle() default "%s_uploadHandle_imgs(param){//上传触发事件 \r\nif (this.%s.file==null){this.%s.file = new Array()}  this.%s.file.push(param.file);this.form.%s = this.%s.file; }";

	/**
	 * 上传前事件 主要实现回显功能
	 */
	String onBeforeFunction() default "%s_uploadBeforeUpload_imgs(file){//上传前事件 \r\n%s}";

	/**
	 * 图片删除回调
	 * 
	 * @return
	 */
	String onRemove() default "%s_removeHandle_imgs(file, fileList){//图片删除回调 \r\n%s}";

	/**
	 * 数据封装对象的名称
	 */
	String objName() default "el_upload_imgs";

	/**
	 * 循环显示关联图片数组 数组对象的主键
	 */
	String itemImgKey() default "id";

	/**
	 * 循环显示关联图片数组 数组对象的图片路径
	 */
	String itemImgUrl() default "url";

	/**
	 * 图片点击事件
	 */
	String onItemImgClickFunction() default "%s_onItemImgClick(item,index){//图片点击事件 \r\n%s}";

	/**
	 * 关联图片显示模板
	 */
	String itemImgTemp() default "<a v-for=\"(item,index) of %s.imgs\" :key=\"item.%s\" href=\"javascript:;\"  @click=\"%s\"  target=\"_blank\"><img  :src=\"item.%s\"  style=\"width: 200px;padding: 10px;\" /></a>     ";

	/**
	 * 操作图片的弹窗模板
	 */
	String dialogTemp() default "<el-dialog title=\"操作图片\" :visible.sync=\"%s.dialogVisible\" width=\"%s\"  > <span><a v-if=\"this.%s.selectItem\" :href=\"this.%s.selectItem.%s\" target=\"_blank\"><img :src=\"this.%s.selectItem.%s\" style=\"width: %s;\" /></a></span> <span slot=\"footer\" class=\"dialog-footer\"> <el-button @click=\"%s\">取消</el-button> <el-button type=\"primary\" @click=\"%s\" >预览</el-button> <el-button type=\"primary\" @click=\"%s\">编辑</el-button> <el-button type=\"primary\" @click=\"%s\">删除</el-button> <input type=\"file\" id=\"%s\" style=\"display: none;\" @change=\"%s%s\" /></span> </el-dialog>";

	/**
	 * 修改请求,这是input的Change事件,参数是self对象本身,已经注入变量item(对象本身), 通常里面写修改请求
	 */
	String itemImgModifyAPI() default "";

	/**
	 * 删除请求,通常写删除请求,已经注入变量item(对象本身)
	 */
	String itemImgDeleteAPI() default "";

	// v-if条件
	String iftemp() default "";
}
