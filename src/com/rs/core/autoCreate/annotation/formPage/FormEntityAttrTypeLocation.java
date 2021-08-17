package com.rs.core.autoCreate.annotation.formPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 地图定位
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormEntityAttrTypeLocation {
	// ------------------------------------基础属性--------------------------------------
	String showTitle();// 属性对应显示的标题

	boolean addShow() default true;// 是否在新增页面显示

	boolean editShow() default true;// 是否在编辑页面显示

	boolean readOnly() default false;// 是否是只读属性,:disabled属性都会提交

	boolean addOnly() default false;// 是否是只允许初始化时编辑的属性,:disabled属性,有该属性在数据创建的时候会被提交

	boolean noEditor() default false;// 是否禁止编辑,有该属性无论是修改还是创建都不会被提交

	String submitName() default "";// 数据提交的别名
	// -------------------------------------END-------------------------------------

	String temp() default "      \r\n<el-form-item %s label=\"%s\">\r\n        <div>\r\n%s\r\n          <el-amap v-if=\"%s\"  vid=\"%s\" id=\"%s\"  :zoom=\"13\" :events=\"%s\" :amap-manager=\"%s\" %s  :plugin=\"%s\"> <el-amap-marker vid=\"%s\" :position=\"%s\" v-if=\"%s\" ></el-amap-marker></el-amap>\r\n        </div>\r\n      </el-form-item>";

	String searchTemp() default "<div class=\"search-box\" style=\"position: absolute; z-index: 5; width: %s; right: %s; top: 10px; height: 30px; \" > <input v-model=\"%s\" id=\"search_%s\" style=\" float: left; width: %s; height: %s; border: 1px solid #38f; padding: 0 8px; \" /> <button style=\" float: left; width: %s; height:%s; background: #38f; border: 1px solid #38f; color: #fff; display: none;\" @click=\"%s\" > 搜索 </button> <div style=\" width: %s; max-height: 260px; position: absolute; top: 30px; overflow-y: auto; background-color: #fff; \" id=\"searchTip_%s\" ></div> </div>";

	boolean search() default true;// 是否使用搜索模板

	String key() default "location1";// 编号

	int width() default 400;// 宽度

	int height() default 340;// 高度

	String addressKey() default "form.address";// 地址字段

	String nameKey() default "form.name";// 名称字段

	String lonKey() default "form.lon";// 维度字段

	String latKey() default "form.lat"; // 经度字段

	String searchKey() default "searchKey_"; // 搜索属性

	String onSearchHandle() default "%s_searchHandle(){%s}"; // 搜索事件

	String initPoiPicker() default "if (typeof(AMapUI) == \"undefined\" || !AMapUI) { if (confirm(\"地图组件加载失败,是否需要重新加载页面\")) {this.$router.go(0);}return;};AMapUI.loadUI([\"misc/PoiPicker\"], (PoiPicker) => { this.%s = new PoiPicker({ input: \"search_%s\", placeSearchOptions: { map: this.%s.getMap(), pageSize: 10, }, suggestContainer: \"searchTip_%s\", searchResultsContainer: \"searchTip_%s\", });this.%s.on(\"poiPicked\", (poiResult) => { console.log(\"poiResult\", poiResult); this.%s.hideSearchResults();  if (poiResult.source == \"search\") { } else { this.%s = null; }  this.%s = [ poiResult.item.location.lng, poiResult.item.location.lat]; this.%s.getMap().setCenter(this.%s);this.form.%s = this.%s; this.%s = poiResult.item.address; this.%s = poiResult.item.name; }); });";

	String transform() default "%s instanceof Array?%s.join(','):%s";

	// v-if条件
	String iftemp() default "";
}
