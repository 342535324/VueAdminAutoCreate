package com.rs.core.autoCreate.view.form;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.ExtendMethod;
import com.rs.core.autoCreate.annotation.ViewEntity;

/**
 * 填充form表单页面 详情/修改资料页
 */
public class VueAdminFormPage {
	public VueAdminFormPage(VueAdminUtil vueAdminUtil, Method method, ViewEntity viewEntity, String name, String istr,
			BasePage basePage) throws Exception {

		File form = vueAdminUtil.createFile("views/" + name + "/form" + istr + ".vue");

		// 设置页
		String data = "  data() {\r\n    return {\r\n%s\r\n      form: {\r\n%s\r\n},\r\n      listLoading: true\r\n    };\r\n  },";// 数据块
		String importApi = "import * as %s from '@/api/%s'\r\n%s\r\n";
		String created = "  async created() {\r\n    if (this.$route.query) {\r\n      await this.getDetails();\r\n    } else {\r\nthis.$message({ type: 'warning',message: '未找到页面'});\r\nthis.$router.replace({"
				+ vueAdminUtil.replacePath("path:'/" + name + "/list',query:this.$route.query")
				+ "});\r\n    }\r\n%s\r\n  },";// 创建完后调用的方法
		String methods = "  methods: {%s\r\n\r\n    async getDetails() {await this.getAsyncDetails();},getAsyncDetails() {\r\n      this.listLoading = true;\r\n\r\n      return new Promise((resolve, reject) => {"
				+ name + "." + vueAdminUtil.createApiName(basePage.selectDetailsApi())
				+ "(this.$route.query)\r\n        .then(response => {\r\n          this.form = response.data;\r\n          this.listLoading = false;\r\n%s\r\n resolve(response);\r\n       })\r\n        .catch(response => {\r\n          this.listLoading = false;\r\n        });\r\n}).catch((res) => {console.log('error', res);});\r\n    },\r\n    onSubmit() {\r\n ";
		String modifyEntityApi = vueAdminUtil.createApiName(basePage.modifyEntityApi());
		if (StringUtils.isNotEmpty(modifyEntityApi)) {
			methods = methods + "     this.listLoading = true;\r\n      " + name + "."
					+ vueAdminUtil.createApiName(basePage.modifyEntityApi())
					+ "(this.form)\r\n        .then(response => {\r\n          this.listLoading = false;\r\n        this.$message({message: '修改成功!',type: 'success'});\r\nthis.getDetails();\r\n          })\r\n        .catch(response => {\r\n          this.listLoading = false;\r\n        });\r\n ";
		}
		methods = methods
				+ "},\r\n    onCancel() {\r\n      this.getDetails();\r\n      this.$message({\r\n        message: '取消修改!',\r\n        type: 'info'\r\n      });\r\n    }\r\n  }";// 方法体

		StringBuilder readOnlyTemp = new StringBuilder();

		StringBuilder getDetailsCallBack = new StringBuilder();
		StringBuilder formStr = new StringBuilder();
		Class entity = viewEntity.formEntity();
		Field[] fields = entity.getDeclaredFields();

		StringBuilder componentSB = new StringBuilder();
		StringBuilder importSB = new StringBuilder();
		StringBuilder attrSB = new StringBuilder();
		StringBuilder listNames = new StringBuilder();
		StringBuilder createdFunction = new StringBuilder();
		StringBuilder extendMethods = new StringBuilder();

		// 通过注解构建html
		for (Field field : fields) {
			vueAdminUtil.createFormHTML(vueAdminUtil, field, getDetailsCallBack, readOnlyTemp, formStr, componentSB,
					importSB, attrSB, listNames, createdFunction, extendMethods, false);
		}

		// 导入api
		vueAdminUtil.createImportApiHTML(method, importSB);

		// 拓展方法
		Annotation[] annotations = method.getAnnotationsByType(ExtendMethod.class);
		System.out.println(method.getName());
		for (Annotation annotation : annotations) {
			ExtendMethod e = (ExtendMethod) annotation;
			extendMethods.append(e.methodName()).append("(").append(e.methodParam()).append(")").append(" {\r\n")
					.append(e.methodBody()).append("\r\n},\r\n");

			if (e.createdRun()) {
				createdFunction.append("\r\nthis.").append(e.methodName()).append("();");
			}
		}

		importApi = String.format(importApi, name, name, importSB.toString());
		data = String.format(data, listNames.toString(), attrSB.toString());
		created = String.format(created, createdFunction.toString());
		methods = String.format(methods, extendMethods.toString(), getDetailsCallBack.toString());

		String content = String
				.format("<template>\r\n  <div class='app-container'>\r\n    <el-form ref='form' :model='form' label-width='120px' v-loading='listLoading'>\r\n      %s\r\n\r\n      <el-form-item>\r\n        <el-button type='primary' @click='onSubmit'>修改</el-button>\r\n        <el-button v-if='form.id' @click='onCancel'>取消修改</el-button>\r\n      </el-form-item>\r\n    </el-form>%s\r\n  </div>\r\n</template>\r\n\r\n<script>\r\n%s\r\n\r\nexport default {\r\n  %s\r\n  \r\n  %s\r\n  %s\r\n  %s\r\n};\r\n</script>\r\n\r\n<style scoped>\r\n.line {\r\n  text-align: center;\r\n}\r\n</style>",
						formStr.toString(), readOnlyTemp, importApi,
						componentSB.length() > 0
								? String.format("  components: {\r\n    %s  },", componentSB.toString()) : "",
						data, created, methods);
		vueAdminUtil.write(form, content);

	}
}
