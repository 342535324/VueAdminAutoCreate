package com.rs.core.autoCreate.annotation.listPage;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列表扩展 导出Excel -该注解用在获取列表数据的方法上 会将列表的所有参数传递到导出接口
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListViewExportExcel {
	Class entity();// 实体类 里面获取参数名与属性名

	String api();// 获取导出数据的api

	String paramName() default "excelPramer";// 参数名

	String selectList() default "['全部数据','上月数据','本月数据','昨日数据','本日数据']";// 选项内容
	// 参数1:参数名

	String param() default "%s:{downloadLoading: false,filename: '',autoWidth: true,bookType: 'xlsx',selectIndex:0,selectList:%s,list:[]}";

	// 参数1-4:参数名
	String temp() default "<div class='listViewExportExcel'> <div style=\"display:inline-block;\"> <el-select v-model=\"%s.selectIndex\" style=\"width:140px;\"> <el-option v-for=\"(item,index) in %s.selectList\" :key=\"item\" :label=\"item\" :value=\"index\" /> </el-select> </div> <el-button :loading=\"%s.downloadLoading\" type=\"primary\" icon=\"el-icon-document\" @click=\"%s_handleDownload\" >导出表格</el-button> </div>";

	// 参数1-2:参数名,参数3是列名,参数4:对象属性名,参数5是参数名,参数6是api名,参数7-11:参数名
	String handleDownloadTemp() default "%s_handleDownload() { this.%s.downloadLoading = true; import('@/vendor/Export2Excel').then(excel => { const tHeader = %s; const filterVal = %s;  import('@/api/exportData').then(api => { let query = this.listQuery; query.selectIndex = this.%s.selectIndex;  api.%s(query).then(response => { const data = this.%s_formatJson(filterVal, response.data); excel.export_json_to_excel({ header: tHeader, data, filename: this.%s.filename, autoWidth: this.%s.autoWidth, bookType: this.%s.bookType }); this.%s.downloadLoading = false; }); }); }); }";

	// 参数1:参数名
	String formatJsonTemp() default "%s_formatJson(filterVal, jsonData) { return jsonData.map(v => filterVal.map(j => { return v[j] })) }";

}
