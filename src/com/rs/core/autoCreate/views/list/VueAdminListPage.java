package com.rs.core.autoCreate.views.list;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.ExtendMethod;
import com.rs.core.autoCreate.annotation.ViewConf;
import com.rs.core.autoCreate.annotation.ViewEntity;
import com.rs.core.autoCreate.annotation.listPage.ListEntityAttr;
import com.rs.core.autoCreate.annotation.listPage.ListViewAddressFilter;
import com.rs.core.autoCreate.annotation.listPage.ListViewExportExcel;
import com.rs.core.autoCreate.annotation.listPage.ListViewFilter;
import com.rs.core.autoCreate.annotation.listPage.ListViewFilters;
import com.rs.core.autoCreate.annotation.listPage.ListViewOperation;
import com.rs.core.autoCreate.annotation.listPage.ListViewSearch;
import com.rs.core.za.testUtil.annotation.ZA_InterfaceNotes;

/**
 * 填充列表页
 * 
 * 内置listQuery对象 作为列表查询参数,在页面初始化的时候会把this.$route.query的内容同步到listQuery对象内
 */
public class VueAdminListPage {

	public VueAdminListPage(VueAdminUtil vueAdminUtil, Method method, ViewEntity viewEntity, String name, String istr,
			BasePage basePage) throws Exception {
		String readData = String.format("const %s = response", StringUtils.isEmpty(vueAdminUtil.getApiDataKey())
				? vueAdminUtil.getApiDataKey() : ("{ " + vueAdminUtil.getApiDataKey() + " }"));

		StringBuilder addFormButton = new StringBuilder();
		StringBuilder head = new StringBuilder();
		StringBuilder table = new StringBuilder();
		StringBuilder operation = new StringBuilder();
		StringBuilder extendData = new StringBuilder();
		StringBuilder extendMethod = new StringBuilder();
		StringBuilder created = new StringBuilder();

		File list = vueAdminUtil.createFile("views/" + name + "/list" + istr + ".vue");

		if (method.isAnnotationPresent(ListViewSearch.class)) {
			ListViewSearch textSearchSetting = method.getAnnotation(ListViewSearch.class);
			String search = "<div class='listViewSearch'><el-input\r\n  prefix-icon='el-icon-search'         v-model='%s'\r\n        placeholder='%s'\r\n       \r\n        class='filter-item'\r\n        @keyup.enter.native='%s'\r\n      @keyup.enter='%s'/></div>";
			head.append(String.format(search, textSearchSetting.model(), textSearchSetting.placeholder(),
					textSearchSetting.event(), textSearchSetting.event()));
		}

		if (method.isAnnotationPresent(ListViewFilters.class)) {
			ListViewFilters listViewFilters = method.getAnnotation(ListViewFilters.class);
			for (ListViewFilter listViewFilter : listViewFilters.value()) {
				String temp = "<div class='listViewFilter'><el-select class=\"filter-item\" v-if=\"%s && %s.length\" placeholder=\"%s\" v-model=\"%s\" %s  filterable > <el-option label=\"不过滤%s\" value=\"\" /><el-option v-for=\"(item,index) in %s\" :key=\"index\" :label=\"item.text\" :value=\"item.key\"  /></el-select></div>";

				String listName = listViewFilter.listName();
				String model = listViewFilter.model();
				if (listName.indexOf(listViewFilter.queryParamName() + ".") == -1) {
					listName = listViewFilter.queryParamName() + "." + listName;
				}

				if (model.indexOf(listViewFilter.queryParamName() + ".") == -1) {
					model = listViewFilter.queryParamName() + "." + model;
				}

				head.append(String.format(temp, listName, listName, listViewFilter.placeholder(), model,
						(StringUtils.isNotEmpty(listViewFilter.changeFunction())
								? ("@change=\"" + listViewFilter.changeFunction() + "\"") : ""),
						listViewFilter.placeholder(), listName));

				created.append("    this.").append(listViewFilter.getListFunction().substring(0,
						listViewFilter.getListFunction().indexOf(")") + 1)).append(";\r\n");
				extendMethod.append(",\r\n").append(listViewFilter.getListFunction());
			}
		}

		if (method.isAnnotationPresent(ListViewAddressFilter.class)) {
			String temp = "<div class='listViewAddressFilter'><el-select class=\"filter-item\" style=\"margin-left: 30px;\" v-if=\"addressFilterData.provinceList && addressFilterData.provinceList.length\" placeholder=\"所属省份\" v-model=\"listQuery.provinceId\" @change=\"getCityList(true);getList()\"   filterable > <el-option label=\"不过滤\" value=\"\" /><el-option v-for=\"(item,index) in addressFilterData.provinceList\" :key=\"index\" :label=\"item.text\" :value=\"item.key\"  /></el-select><el-select style=\"margin-left: 30px;\" class=\"filter-item\" v-if=\"addressFilterData.cityList && addressFilterData.cityList.length\" placeholder=\"所属市\" v-model=\"listQuery.cityId\" @change=\"getAreaList();getList()\"   filterable > <el-option label=\"不过滤\" value=\"\" /><el-option v-for=\"(item,index) in addressFilterData.cityList\" :key=\"index\" :label=\"item.text\" :value=\"item.key\"  /></el-select><el-select style=\"margin-left: 30px;\" class=\"filter-item\" v-if=\"addressFilterData.areaList && addressFilterData.areaList.length\" placeholder=\"所属县区\" v-model=\"listQuery.areaId\"  @change=\"getList()\" filterable > <el-option label=\"不过滤\" value=\"\" /><el-option v-for=\"(item,index) in addressFilterData.areaList\" :key=\"index\" :label=\"item.text\" :value=\"item.key\"  /></el-select></div>";
			head.append(temp);
			created.append("    this.getProvinceList();\r\n    this.getCityList();\r\n    this.getAreaList();\r\n");
			extendMethod.append(",\r\n").append(
					"getProvinceList() {require(\"@/api/selectList\").getProvinceList().then(response=>{this.addressFilterData.provinceList = response.data;});},getCityList() {require(\"@/api/selectList\").getCityList({id:this.listQuery.provinceId}).then(response=>{this.addressFilterData.cityList = response.data;});},getAreaList() {require(\"@/api/selectList\").getAreaList({id:this.listQuery.cityId}).then(response=>{this.addressFilterData.areaList = response.data;});}");
		}

		for (Field field : viewEntity.listEntity().getDeclaredFields()) {
			boolean b = field.isAccessible();
			field.setAccessible(true);

			if (field.isAnnotationPresent(ListEntityAttr.class)) {
				ListEntityAttr entityAttr = field.getAnnotation(ListEntityAttr.class);
				String column = "<el-table-column %s label='%s' align='center'>\r\n <template slot-scope='scope'>\r\n  <span>%s</span>\r\n </template>\r\n</el-table-column>\r\n";

				switch (entityAttr.level()) {
				case 0:
					table.append(
							String.format(column, "v-if='showReviewer==0 || showReviewer==1'", entityAttr.showTitle(),
									StringUtils.isEmpty(entityAttr.value()) ? "{{ scope.row." + field.getName() + " }}"
											: vueAdminUtil.replacePath(entityAttr.value())));
					break;
				case 1:
					table.append(String.format(column, "v-if='showReviewer==1'", entityAttr.showTitle(),
							StringUtils.isEmpty(entityAttr.value()) ? "{{ scope.row." + field.getName() + " }}"
									: vueAdminUtil.replacePath(entityAttr.value())));
					break;
				case 2:
					table.append(String.format(column, "", entityAttr.showTitle(),
							StringUtils.isEmpty(entityAttr.value()) ? "{{ scope.row." + field.getName() + " }}"
									: vueAdminUtil.replacePath(entityAttr.value())));
					break;
				}

			}

			field.setAccessible(b);
		}

		// 操作
		ListViewOperation listViewOperation = method.getAnnotation(ListViewOperation.class);

		if (listViewOperation != null) {
			operation.append(vueAdminUtil.replacePath(String.format(listViewOperation.operation(), name, istr)));
		}

		String importApi = String.format("import * as %s from '@/api/%s'", name, name);

		Annotation[] annotations = method.getAnnotationsByType(ExtendMethod.class);
		for (Annotation annotation : annotations) {
			ExtendMethod e = (ExtendMethod) annotation;
			extendMethod.append(",").append(e.methodName()).append("(").append(e.methodParam()).append(")")
					.append(" {\r\n").append(e.methodBody()).append("\r\n}\r\n");

			if (e.createdRun()) {
				created.append("\r\nthis.").append(e.methodName()).append("()");
			}
		}

		// 扩展 Excel导出
		ListViewExportExcel listViewExportExcel = method.getAnnotation(ListViewExportExcel.class);
		if (listViewExportExcel != null) {
			head.append(String.format(listViewExportExcel.temp(), listViewExportExcel.paramName(),
					listViewExportExcel.paramName(), listViewExportExcel.paramName(), listViewExportExcel.paramName()));

			extendData.append(String.format(listViewExportExcel.param(), listViewExportExcel.paramName(),
					listViewExportExcel.selectList()));
			// 参数1-3:参数名,参数4:对象属性名,参数5-10:参数名

			StringBuilder tHeader = new StringBuilder("['id'");
			StringBuilder filterVal = new StringBuilder("['id'");

			for (Field field : listViewExportExcel.entity().getDeclaredFields()) {
				boolean b = field.isAccessible();
				field.setAccessible(true);

				if (!field.getName().equals("id")) {
					if (field.isAnnotationPresent(ListEntityAttr.class)) {
						ListEntityAttr t = field.getAnnotation(ListEntityAttr.class);

						tHeader.append(",'").append(t.showTitle()).append("'");
						filterVal.append(",'").append(field.getName()).append("'");
					}
				}

				field.setAccessible(b);
			}
			tHeader.append("]");
			filterVal.append("]");
			extendMethod.append(",")
					.append(String.format(listViewExportExcel.formatJsonTemp(), listViewExportExcel.paramName()))
					.append(",")
					.append(String.format(listViewExportExcel.handleDownloadTemp(), listViewExportExcel.paramName(),
							listViewExportExcel.paramName(), tHeader, filterVal, listViewExportExcel.paramName(),
							listViewExportExcel.api(), listViewExportExcel.paramName(), listViewExportExcel.paramName(),
							listViewExportExcel.paramName(), listViewExportExcel.paramName(),
							listViewExportExcel.paramName()));
		}

		// 新增数据按钮
		if (!basePage.routerShowForm()) {
			Class controller = method.getDeclaringClass();
			for (Method m : controller.getMethods()) {
				if (m.isAnnotationPresent(RequestMapping.class)) {
					RequestMapping mapping = m.getAnnotation(RequestMapping.class);
					if (mapping.value()[0].equals(basePage.addEntityApi())) {

						String title = "新增";
						if (m.isAnnotationPresent(ViewConf.class)) {
							ViewConf vc = m.getAnnotation(ViewConf.class);
							title = vc.showTitle();
						} else if (m.isAnnotationPresent(ZA_InterfaceNotes.class)) {
							ZA_InterfaceNotes interfaceNotes = m.getAnnotation(ZA_InterfaceNotes.class);
							title = interfaceNotes.name();
						}
						addFormButton.append(vueAdminUtil.replacePath(
								String.format(basePage.formButtonTemp(), "/" + name + "/add-form", title)));
					}
				}

			}

		}

		StringBuffer operationTemp = new StringBuffer(
				"\r\n      <el-table-column label='操作' align='center' width='230' class-name='small-padding fixed-width'>\r\n        <template slot-scope='scope'>\r\n          ")
						.append(operation).append("\r\n        </template>\r\n      </el-table-column>");

		String filterList = "<div class=\"filter-item\"><el-radio v-model=\"showReviewer\" :label=\"0\" @change=\"tableKey = tableKey + 1\">默认显示列表内容</el-radio><el-radio v-model=\"showReviewer\" :label=\"1\" @change=\"tableKey = tableKey + 1\">全部显示列表内容</el-radio><el-radio v-model=\"showReviewer\" :label=\"2\" @change=\"tableKey = tableKey + 1\">精简显示列表内容</el-radio></div>";
		// 参数1:head,参数2:表,参数3:操作,参数4:导入API,参数5:自定义参数,参数6:扩展js
		StringBuilder content = new StringBuilder("<template>\r\n  <div class='app-container'>\r\n")
				.append(addFormButton).append("    <div class='filter-container'>\r\n     ").append(head)
				.append("\r\n     ").append(filterList)
				.append("\r\n    </div>\r\n\r\n    <el-table\r\n      :key='tableKey'\r\n      v-loading='listLoading'\r\n      :data='list'\r\n      border\r\n      fit\r\n      highlight-current-row\r\n      style='width: 100%;'>\r\n     ")
				.append(table).append(operation.length() > 0 ? operationTemp : "")
				.append("\r\n    </el-table>\r\n\r\n    <pagination\r\n      v-show='total>0'\r\n      :total='total'\r\n      :page.sync='listQuery.page'\r\n      :limit.sync='listQuery.limit'\r\n      @pagination='getList'\r\n    />\r\n  </div>\r\n</template>\r\n\r\n<script>\r\n")
				.append(importApi)
				.append("\r\nimport waves from '@/directive/waves'// waves directive\r\nimport Pagination from '@/components/Pagination'// secondary package based on el-pagination\r\n\r\nexport default {\r\n  name: 'ComplexTable',\r\n  components: { Pagination },\r\n  directives: { waves },\r\n  filters: {},\r\n  data() {\r\n    return {")
				.append("\r\n      ").append(extendData.length() > 0 ? (extendData + ",") : "")
				.append("\r\n      tableKey: 0,\r\n      list: null,\r\n      total: 0,\r\n      listLoading: true,\r\n      addressFilterData:{\r\nprovinceList: [],\r\n      cityList: [],\r\n      areaList: [],\r\n      \r\n},\r\n      listQuery: {\r\n        page: 1,\r\n        limit: 20,\r\n        title: null,\r\n      provinceId: null,\r\n      cityId: null,\r\n      areaId: null,\r\n      },\r\n\r\n      showReviewer: /ipad|iphone|midp|rv:1.2.3.4|ucweb|android|windows ce|windows mobile/.test(navigator.userAgent.toLowerCase())? 2: 0,\r\n      rules: {\r\n        type: [\r\n          { required: true, message: 'type is required', trigger: 'change' }\r\n        ],\r\n        timestamp: [\r\n          {\r\n            type: 'date',\r\n            required: true,\r\n            message: 'timestamp is required',\r\n            trigger: 'change'\r\n          }\r\n        ],\r\n        title: [\r\n          { required: true, message: 'title is required', trigger: 'blur' }\r\n        ]\r\n      }\r\n    };\r\n  },\r\n  created() {\r\n    if (this.$route.query) {for (let k in this.$route.query) {this.listQuery[k] = this.$route.query[k];}}\r\nthis.getList();")
				.append(created)
				.append("\r\n  },\r\n  methods: {\r\n    getList() {\r\n      this.listLoading = true;\r\n      " + name
						+ "." + vueAdminUtil.createApiName(basePage.selectListApi())
						+ "(\r\n        this.listQuery\r\n      ).then(response => {\r\n        " + readData
						+ ";\r\n        this.list = data.data;\r\n        this.total = data.length;\r\n        this.listLoading = false;\r\n      });\r\n    },\r\n    handleFilter() {\r\n      this.listQuery.page = 1;\r\n      this.getList();\r\n    },\r\n");

		if (StringUtils.isNotEmpty(basePage.deleteEntityApi())) {
			content.append(
					"		handleDelete(row) {\r\n      this.$confirm('此操作将永久删除该数据, 是否继续?', '提示', {\r\n        confirmButtonText: '确定',\r\n        cancelButtonText: '取消',\r\n        type: 'warning'\r\n      }).then(async res => {\r\n        if (res == 'confirm') {\r\n          ")
					.append(name).append(".").append(vueAdminUtil.createApiName(basePage.deleteEntityApi()))
					.append("(row).then(response => {\r\n            this.$message({\r\n              type: 'success',\r\n              message: '删除成功!'\r\n            });\r\n            this.getList();\r\n});\r\n} \r\n});\r\n}");
		} else {
			content.append("    handleDelete() {}");
		}

		vueAdminUtil.write(list, content.append(extendMethod).append("\r\n  }\r\n};\r\n</script>\r\n").toString());

	}

}
