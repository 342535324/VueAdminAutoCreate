package com.rs.core.autoCreate.view.form;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.ExtendMethod;
import com.rs.core.autoCreate.annotation.ViewEntity;

/**
 * 填充表单新增页
 *
 */
public class VueAdminAddFormPage {
	public VueAdminAddFormPage(VueAdminUtil vueAdminUtil, Method method, ViewEntity viewEntity, String name,
			String istr, BasePage BasePage) throws Exception {

		File addform = vueAdminUtil.createFile("views/" + name + "/add-form" + istr + ".vue");

		// 新增页
		String data = "  data() {\r\n    return {\r\n%s\r\n      form: {\r\n%s\r\n},\r\n      listLoading: false\r\n    };\r\n  },";// 数据块
		String importApi = "import * as %s from '@/api/%s'\r\n%s\r\n";
		String created = "  created() {\r\n%s\r\n  },";// 创建完后调用的方法
		String methods = "  methods: {%s\r\n\r\n    onSubmit() {\r\n      this.listLoading = true;\r\n      //创建\r\n      "
				+ name + "\r\n        ." + vueAdminUtil.createApiName(BasePage.addEntityApi())
				+ "(this.form)\r\n        .then(response => {\r\n          this.listLoading = false;\r\n          this.$confirm('创建成功,是否前往列表页?', '提示', {\r\n            confirmButtonText: '确定',\r\n            cancelButtonText: '取消',\r\n            type: 'warning'\r\n          })\r\n            .then(() => {\r\n              this.$router.replace({\r\n                "
				+ vueAdminUtil.replacePath("path:'/" + name + "/list',query:this.$route.query")
				+ "\r\n              });\r\n            })\r\n            .catch(() => {\r\n              this.$message({\r\n                type: 'info',\r\n                message: '已取消页面跳转'\r\n              });\r\n            });\r\n        })\r\n        .catch(response => {\r\n          this.listLoading = false;\r\n        });\r\n    }\r\n  }";// 方法体

		StringBuilder formStr = new StringBuilder();
		Class<?> entity = viewEntity.formEntity();
		Field[] fields = entity.getDeclaredFields();

		StringBuilder getDetailsCallBack = new StringBuilder();// 不使用
		StringBuilder readOnlyTemp = new StringBuilder();// 不使用

		StringBuilder componentSB = new StringBuilder();
		StringBuilder importSB = new StringBuilder();
		StringBuilder attrSB = new StringBuilder();
		StringBuilder listNames = new StringBuilder();
		StringBuilder createdFunction = new StringBuilder();
		StringBuilder extendMethods = new StringBuilder();

		// 通过注解构建html
		for (Field field : fields) {
			vueAdminUtil.createFormHTML(vueAdminUtil, field, getDetailsCallBack, readOnlyTemp, formStr, componentSB,
					importSB, attrSB, listNames, createdFunction, extendMethods, true);
		}

		// 导入api
		vueAdminUtil.createImportApiHTML(method, importSB);

		// 拓展方法
		Annotation[] annotations = method.getAnnotationsByType(ExtendMethod.class);
		for (Annotation annotation : annotations) {
			ExtendMethod e = (ExtendMethod) annotation;
			extendMethods.append(e.methodName()).append("(").append(e.methodParam()).append(")").append(" {\r\n")
					.append(e.methodBody()).append("\r\n},\r\n");

			if (e.createdRun()) {
				createdFunction.append("\r\nthis.").append(e.methodName()).append("();");
			}
		}

		data = String.format(data, listNames.toString(), attrSB.toString());
		created = String.format(created, createdFunction.toString());
		methods = String.format(methods, extendMethods.toString());
		importApi = String.format(importApi, name, name, importSB.toString());

		String content = String
				.format("<template>\r\n  <div class='app-container'>\r\n    <el-form ref='form' :model='form' label-width='120px' v-loading='listLoading'>\r\n      %s\r\n\r\n      <el-form-item>\r\n        <el-button type='primary' @click='onSubmit'>新增</el-button>\r\n         </el-form-item>\r\n    </el-form>\r\n  </div>\r\n</template>\r\n\r\n<script>\r\n%s\r\n\r\nexport default {\r\n  %s\r\n  \r\n  %s\r\n  %s\r\n  %s\r\n};\r\n</script>\r\n\r\n<style scoped>\r\n.line {\r\n  text-align: center;\r\n}\r\n</style>",
						formStr.toString(), importApi,
						componentSB.length() > 0
								? String.format("  components: {\r\n    %s  },", componentSB.toString()) : "",
						data, created, methods);

		vueAdminUtil.write(addform, content);

	}
}
