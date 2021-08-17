package com.rs.core.autoCreate;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.ControllerLog;
import com.rs.core.autoCreate.annotation.EditPage;
import com.rs.core.autoCreate.annotation.ImportApi;
import com.rs.core.autoCreate.annotation.RouterIndex;
import com.rs.core.autoCreate.annotation.ViewEntity;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttr;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeDate;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeFile;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeHtml;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImg;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeImgs;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeLocation;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeNum;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypePassword;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeReadOnly;
import com.rs.core.autoCreate.annotation.formPage.FormEntityAttrTypeSelect;
import com.rs.core.autoCreate.api.VueAdminApi;
import com.rs.core.autoCreate.entity.AdminManageModel;
import com.rs.core.autoCreate.entity.RouterV;
import com.rs.core.autoCreate.router.VueAdminRouter;
import com.rs.core.autoCreate.view.VueAdminView;
import com.rs.core.autoCreate.view.form.VueAdminAddFormPage;
import com.rs.core.autoCreate.view.form.VueAdminEditPage;
import com.rs.core.autoCreate.view.form.VueAdminFormPage;
import com.rs.core.autoCreate.views.list.VueAdminListPage;

public class VueAdminUtil {

	private String adminName;

	/**
	 * vueAdmin项目的src目录
	 */
	private String srcPath;
	/**
	 * 高德地图web服务的key
	 */
	private String amapKey;
	/**
	 * 接口后缀
	 */
	private String apiSuffix = ".app";

	/**
	 * 接口返回的数据key的字段名，如果为空表示接口直接返回数据体
	 */
	private String apiDataKey = "data";

	public VueAdminUtil(String adminName, String srcPath, String amapKey) {
		super();
		this.adminName = adminName;
		this.srcPath = srcPath;
		this.amapKey = amapKey;
	}

	public String getApiSuffix() {
		return apiSuffix;
	}

	public void setApiSuffix(String apiSuffix) {
		this.apiSuffix = apiSuffix;
	}

	public String getAdminName() {
		return adminName;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public String getAmapKey() {
		return amapKey;
	}

	public String getApiDataKey() {
		return apiDataKey;
	}

	public void setApiDataKey(String apiDataKey) {
		this.apiDataKey = apiDataKey;
	}

	/**
	 * 路由模板
	 */
	private String routerTemp = "import Vue from 'vue'\r\nimport Router from 'vue-router'\r\n\r\nVue.use(Router)\r\n\r\nimport Layout from '@/layout'\r\n\r\n%s\r\nexport const constantRoutes = [\r\n  {\r\n    path: '/login',\r\n    component: () => import('@/views/login/index'),\r\n    hidden: true\r\n  },\r\n  {\r\n    path: '/',\r\n    component: Layout,\r\n    redirect: '/dashboard',\r\n    children: [{\r\n      path: 'dashboard',\r\n      name: 'dashboard',\r\n      component: () => import('@/views/dashboard/index'),\r\n      meta: { title: '%s', icon: 'dashboard' }\r\n    }]\r\n  },\r\n\r\n  {\r\n    path: '/404',\r\n    component: () => import('@/views/404'),\r\n    hidden: true\r\n  }\r\n]\r\n\r\nexport const asyncRoutes = [%s{ path: '*', redirect: '/404', hidden: true }]\r\nconst createRouter = () => new Router({\r\n  scrollBehavior: () => ({ y: 0 }),\r\n  routes: constantRoutes\r\n})\r\nconst router = createRouter()\r\n\r\n// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465\r\nexport function resetRouter() {\r\n  const newRouter = createRouter()\r\n  router.matcher = newRouter.matcher // reset router\r\n}\r\n\r\nexport default router\r\n";

	/**
	 * 路由path转换后的映射关系
	 */
	public static Map<String, String> routerPathMAP = new HashMap<String, String>();

	/**
	 * 处理path属性,如果controller指定了路由模块名称(@RouterModelName),要替换path才能访问正确的路由
	 */
	public String replacePath(final String temp) {
		String newTemp = temp;
		String regEx = "path\\s*:\\s*\'(.*?)\'";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(temp);
		while (m.find()) {
			String pathStr = m.group();
			for (Entry<String, String> entry : routerPathMAP.entrySet()) {
				System.out.println(entry.getKey() + " -> " + entry.getValue());
				if (pathStr.indexOf(entry.getKey()) > -1) {
					String newPath = pathStr.replaceAll(entry.getKey(), entry.getValue());
					newTemp = newTemp.replaceAll(pathStr, newPath);
				}
			}
		}
		return newTemp;
	}

	public void createVueAdminFile(File classFolder, List<AdminManageModel> adminManageModels) throws Exception {
		File[] classFiles = classFolder.listFiles();

		// 路由可排序
		TreeMap<Integer, String> routesMap = new TreeMap<Integer, String>();

		// 路由相关
		StringBuilder routerModules = new StringBuilder();
		StringBuilder routes = new StringBuilder();
		// ------------------------------------------------------

		List<RouterV> routerList = new ArrayList<RouterV>();
		Map<String, Class<?>> importRouter = new HashMap<>();
		for (File file : classFiles) {
			if (this.checkClassName(file)) {
				Class<?> c = Class
						.forName("com.rs.core.controller." + file.getName().substring(0, file.getName().indexOf(".")));

				if (c.isAnnotationPresent(com.rs.core.autoCreate.annotation.BasePage.class)
						|| c.isAnnotationPresent(com.rs.core.autoCreate.annotation.BasePages.class)
						|| c.isAnnotationPresent(com.rs.core.autoCreate.annotation.EditPage.class)
						|| c.isAnnotationPresent(com.rs.core.autoCreate.annotation.EditPages.class)) {
					this.createApi(c);

					RouterV router = this.createRouter(c, adminManageModels);
					if (router == null) {
						continue;
					}

					importRouter.put(router.getFileName(), c);
					// System.out.println("校验:" + router.getFileName());
					if (routerList.contains(router)) {
						// 如果已经生成过路由文件则将children合并,且将meta的roles合并
						int i = routerList.indexOf(router);
						RouterV v = routerList.get(i);
						routerList.set(i, v.append(router));
					} else {
						routerList.add(router);
						System.out.println("r");
					}
					if (c.isAnnotationPresent(ControllerLog.class)) {
						this.createView(c);
					}
				}
			}
		}

		// 生成文件
		for (RouterV router : routerList) {
			if (router != null && router.isEmpty() == false) {
				File file = createFile("router/modules/" + router.getFileName() + ".js");
				// 创建自动生成标记
				createFile("router/modules/_autoCreate");

				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				String json_routes = gson.toJson(router).replaceAll("\"", "").replaceAll(",", ",\r\n");

				StringBuilder content = new StringBuilder("import Layout from '@/layout'\r\nexport default ")
						.append(json_routes);
				write(file, content.toString());

				System.out.println(file.getPath() + "已生成(router)");
			} else {
				System.out.println("跳过:" + router.getFileName());
			}
		}

		for (Entry<String, Class<?>> entry : importRouter.entrySet()) {
			if (entry.getKey() != null) {
				routerModules.append(
						String.format("import %sRouter from './modules/%s'\r\n", entry.getKey(), entry.getKey()));

				String routerName = entry.getKey();
				Integer key = routerName.hashCode();

				if (entry.getValue().isAnnotationPresent(RouterIndex.class)) {
					RouterIndex routerIndex = (RouterIndex) entry.getValue().getAnnotation(RouterIndex.class);
					key = Integer.valueOf(routerIndex.value());
				}
				while (routesMap.containsKey(key)) {
					key += 1;
				}
				routesMap.put(key, routerName);

			}
		}

		for (String str : routesMap.values()) {
			routes.append(str).append("Router, ");
		}
		StringBuilder content = new StringBuilder(
				String.format(routerTemp, routerModules.toString(), getAdminName(), routes.toString()));
		this.write(this.createFile("router/index.js"), content.toString());
		// 创建自动生成标记
		this.createFile("router/_autoCreate");
	}

	/**
	 * 创建文件夹与内容
	 * 
	 * @param c
	 * @throws Exception
	 */
	public void createView(Class<?> c) throws Exception {
		new VueAdminView(this, c);
	}

	/**
	 * 生成list.vue
	 * 
	 * @param istr
	 * @param basePage
	 * 
	 * @throws Exception
	 */
	public void createView_list(Method method, ViewEntity viewEntity, String name, String istr, BasePage basePage)
			throws Exception {
		new VueAdminListPage(this, method, viewEntity, name, istr, basePage);
	}

	/**
	 * 生成form.vue
	 * 
	 * @param istr
	 * @param basePage
	 */
	public void createView_form(Method method, ViewEntity viewEntity, String name, String istr, BasePage basePage)
			throws Exception {
		new VueAdminFormPage(this, method, viewEntity, name, istr, basePage);
	}

	/**
	 * 生成add-form.vue
	 * 
	 * @param basePage
	 */
	public void createView_addform(Method method, ViewEntity viewEntity, String name, String istr, BasePage basePage)
			throws Exception {
		new VueAdminAddFormPage(this, method, viewEntity, name, istr, basePage);
	}

	/**
	 * 生成edit.vue
	 */
	public void createView_edit(Method method, EditPage viewEntity, String name, String istr) throws Exception {
		new VueAdminEditPage(this, method, viewEntity, name, istr);
	}

	/**
	 * 创建路由文件
	 * 
	 * @param c
	 * @param adminManageModels
	 * @return
	 * @throws Exception
	 */
	public RouterV createRouter(Class<?> c, List<AdminManageModel> adminManageModels) throws Exception {
		return new VueAdminRouter(this, c, adminManageModels).getRouter();
	}

	/**
	 * 创建api文件
	 * 
	 * @param c
	 * @throws Exception
	 */
	public void createApi(Class<?> c) throws Exception {
		new VueAdminApi(this, c);
	}

	/**
	 * 写入
	 * 
	 * @param f
	 * @param content
	 * @throws Exception
	 */
	public final void write(File f, final String content) throws Exception {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(f);
			bos = new BufferedOutputStream(fos);
			bos.write(content.getBytes());
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}

	public final File createFile(final String path) throws Exception {
		File f = new File(srcPath, path);
		if (!f.exists()) {
			f.createNewFile();
		}
		return f;
	}

	public final File createFolder(final String path) throws Exception {
		File f = new File(srcPath, path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f;
	}

	// 首字母转小写
	public final String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	// 首字母转大写
	public final String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}

	public final boolean checkClassName(File file) {
		return file.getName().indexOf("$") < 0 && file.exists() && file.isFile();
	}

	public String createApiName(String apiUrl) {
		if (StringUtils.isEmpty(apiUrl)) {
			return "";
		}
		apiUrl = apiUrl.substring(0, apiUrl.indexOf("."));
		if (apiUrl.charAt(0) == '/') {
			apiUrl = apiUrl.substring(1);
		}
		String[] as = apiUrl.toString().split("/");
		StringBuilder apiName = new StringBuilder();
		for (int i = 0; i < as.length; i++) {
			String str = as[i];
			if (i > 0) {
				// 首字母大写
				apiName.append(toUpperCaseFirstOne(str));
			} else {
				apiName.append(str);
			}

		}
		return apiName.toString();
	}

	public void createFormHTML(VueAdminUtil vueAdminUtil, java.lang.reflect.Field field,
			StringBuilder getDetailsCallBack, StringBuilder readOnlyTemp, StringBuilder formStr,
			StringBuilder componentSB, StringBuilder importSB, StringBuilder attrSB, StringBuilder listNames,
			StringBuilder createdFunction, StringBuilder extendMethods, boolean isAddForm) {

		// System.out.println(field.getName());
		boolean b = field.isAccessible();
		field.setAccessible(true);
		String elementAttr = "";
		if (field.isAnnotationPresent(FormEntityAttrTypeDate.class)) {
			// 时间组件
			FormEntityAttrTypeDate t = field.getAnnotation(FormEntityAttrTypeDate.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";

			}

			formStr.append(
					String.format(t.temp(), StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "",
							t.showTitle(), field.getName(), "请选择" + t.showTitle(), elementAttr, "width: 100%;"));
			attrSB.append("            ").append(field.getName()).append(":null,\r\n");
		} else if (field.isAnnotationPresent(FormEntityAttrTypePassword.class)) {
			// 密码表单
			FormEntityAttrTypePassword t = field.getAnnotation(FormEntityAttrTypePassword.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			String id = UUID.randomUUID().toString().replaceAll("\\W", "").substring(0, 6);
			String cTemp = String.format("<el-input v-model=\"%s_c\" type=\"password\" @blur=\"%s\"  />",
					field.getName(), "checkPassword" + id);
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
				cTemp = "";
			}
			extendMethods.append("    ").append("checkPassword").append(id)
					.append(String.format(
							"(){if (this.form.%s != this.%s_c){this.$message.error('密码输入不一致!'); this.form.%s='';this.%s_c='' }",
							field.getName(), field.getName(), field.getName(), field.getName()))
					.append("},\r\n");
			listNames.append("        ").append(field.getName()).append("_c:null").append(",\r\n");
			formStr.append(
					String.format(t.temp(), StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "",
							t.showTitle(), field.getName(), elementAttr, cTemp));
			attrSB.append("            ").append(field.getName()).append(":null,\r\n");

		} else if (field.isAnnotationPresent(FormEntityAttrTypeSelect.class)) {
			// 选择表单
			FormEntityAttrTypeSelect t = field.getAnnotation(FormEntityAttrTypeSelect.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			String id = "select_" + UUID.randomUUID().toString().replaceAll("\\W", "").substring(0, 8);
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
			}

			formStr.append(
					String.format(t.temp(), StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "",
							t.showTitle(), "请选择" + t.showTitle(), field.getName(),
							elementAttr + (t.multiple() ? " multiple " : "")
									+ (StringUtils.isNotEmpty(t.changeFunction())
											? ("@change=\"" + t.changeFunction() + "\"") : ""),
							t.nulltemp(), t.listName() + ".data"));
			attrSB.append("            ").append(field.getName()).append((t.multiple() ? ":[]" : ":null") + ",\r\n");
			listNames.append("        ").append(t.listName()).append(":{data:[],param:null}").append(",\r\n");

			createdFunction.append("    this.")
					.append(t.getListFunction().substring(0, t.getListFunction().indexOf(")") + 1)).append(";\r\n");
			extendMethods.append("    ").append(t.getListFunction()).append(",\r\n");

		} else if (field.isAnnotationPresent(FormEntityAttrTypeNum.class)) {
			// 生成数字输入表单
			FormEntityAttrTypeNum t = field.getAnnotation(FormEntityAttrTypeNum.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
			}

			formStr.append(
					String.format(t.temp(), StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "",
							t.showTitle(), field.getName(), elementAttr, t.isFloat() ? "parseFloat" : "parseInt"));
			attrSB.append("            ").append(field.getName()).append(":null,\r\n");
		} else if (field.isAnnotationPresent(FormEntityAttr.class)) {
			// 生成默认的字符串表单
			FormEntityAttr t = field.getAnnotation(FormEntityAttr.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
			}

			formStr.append(
					String.format(t.temp(), StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "",
							t.showTitle(), field.getName(), elementAttr));
			attrSB.append("            ").append(field.getName()).append(":null,\r\n");

		} else if (field.isAnnotationPresent(FormEntityAttrTypeImgs.class)) {
			FormEntityAttrTypeImgs t = field.getAnnotation(FormEntityAttrTypeImgs.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
			}

			String uploadHandle = String.format(t.onUploadHandle(), t.objName(), t.objName(), t.objName(), t.objName(),
					field.getName(), t.objName());
			String beforeFunction = String.format(t.onBeforeFunction(), t.objName(), "");
			String remove = String.format(t.onRemove(), t.objName(), "");

			String itemImgClickFunction = String.format(t.onItemImgClickFunction(), t.objName(),
					String.format("this.%s.dialogVisible = true;this.%s.selectItem = item;this.%s.selectIndex = index",
							t.objName(), t.objName(), t.objName()));

			String imgTemp = String.format(t.itemImgTemp(), t.objName(), t.itemImgKey(),
					itemImgClickFunction.substring(0, itemImgClickFunction.indexOf(")") + 1), t.itemImgUrl());

			// 弹窗相关
			String itemCancelFunction = String.format(
					"%s_itemCancel(){//图片列表-操作图片弹窗-取消事件 \r\n this.%s.dialogVisible = false;}", t.objName(),
					t.objName());
			String itemPreviewFunction = String.format(
					"%s_itemPreview(){//图片列表-操作图片弹窗-预览事件 \r\n window.open(this.%s.selectItem.%s)}", t.objName(),
					t.objName(), t.itemImgUrl());

			String itemModifyFunction = String.format("%s_itemModify%s", t.objName(),
					"(){//图片列表-操作图片弹窗-编辑 触发input的click事件\r\n %s}");
			String itemDeleteFunction = String.format("%s_itemDelete%s", t.objName(), "(){//图片列表-操作图片弹窗-删除事件\r\n %s}");

			// 修改图片api
			String itemFileInputId = "";
			String itemFileChangeFunction = String
					.format("%s_itemFileChange(self){//图片列表-操作图片弹窗-编辑 input的Change事件\r\n}", t.objName());
			if (StringUtils.isNotEmpty(t.itemImgModifyAPI())) {
				itemFileInputId = String.format("%s_itemFileInput", t.objName());
				itemFileChangeFunction = String.format(
						"%s_itemFileChange(self){//图片列表-操作图片弹窗-编辑 input的Change事件\r\n var item = this.%s.selectItem;%s}) .catch(response => { this.listLoading = false; }); }",
						t.objName(), t.objName(), t.itemImgModifyAPI());
				itemModifyFunction = String.format(itemModifyFunction,
						String.format(
								"this.$confirm( \"确定要重写上传吗?(重新上传后旧的图片文件将会被删除)\", \"提示\", { confirmButtonText: \"确定\", cancelButtonText: \"取消\", type: \"warning\" } ).then(() => { document.getElementById(\"%s\").click(); }).catch(response => {});",
								itemFileInputId));
			} else {
				itemModifyFunction = String.format(itemModifyFunction, "");
			}

			if (StringUtils.isNotEmpty(t.itemImgDeleteAPI())) {
				itemDeleteFunction = String.format(itemDeleteFunction,
						String.format(
								"var item = this.%s.selectItem;this.$confirm(\"确定要删除这张图片吗?\", \"提示\", { confirmButtonText: \"确定\", cancelButtonText: \"取消\", type: \"warning\" }).then(() => { %s}) .catch(response => { this.listLoading = false; }); }).catch(response => {});",
								t.objName(), t.itemImgDeleteAPI()));
			} else {
				itemDeleteFunction = String.format(itemDeleteFunction, "");
			}

			String dialogTemp = String.format(t.dialogTemp(), t.objName(), "30%", t.objName(), t.objName(),
					field.getName(), t.objName(), t.itemImgUrl(), "100%",
					itemCancelFunction.substring(0, itemCancelFunction.indexOf("(")),
					itemPreviewFunction.substring(0, itemPreviewFunction.indexOf("(")),
					itemModifyFunction.substring(0, itemModifyFunction.indexOf("(")),
					itemDeleteFunction.substring(0, itemDeleteFunction.indexOf("(")), itemFileInputId,
					itemFileChangeFunction.substring(0, itemFileChangeFunction.indexOf("(")), "($event)");

			formStr.append(String.format(t.temp(),
					StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "", t.showTitle(), imgTemp,
					elementAttr, t.objName(), uploadHandle.substring(0, uploadHandle.indexOf("(")),
					beforeFunction.substring(0, beforeFunction.indexOf("(")), remove.substring(0, remove.indexOf("(")),
					dialogTemp));

			attrSB.append("").append(field.getName()).append(":null,\r\n");
			listNames.append("").append(t.objName())
					.append(":{imgs:[], fileList:[],dialogVisible:false,selectItem:null,selectIndex:null}")
					.append(",\r\n");

			getDetailsCallBack.append("    this.").append(t.objName()).append(".imgs = this.form.")
					.append(field.getName()).append(";\r\n").append("this.")
					.append(itemCancelFunction.substring(0, itemCancelFunction.indexOf(")") + 1)).append(";\r\n");
			extendMethods.append(uploadHandle).append(",\r\n").append(beforeFunction).append(",\r\n").append(remove)
					.append(",\r\n").append(itemImgClickFunction).append(",\r\n").append(itemCancelFunction)
					.append(",\r\n").append(itemPreviewFunction).append(",\r\n").append(itemModifyFunction)
					.append(",\r\n").append(itemDeleteFunction).append(",\r\n").append(itemFileChangeFunction)
					.append(",\r\n");
		} else if (field.isAnnotationPresent(FormEntityAttrTypeImg.class)) {
			FormEntityAttrTypeImg t = field.getAnnotation(FormEntityAttrTypeImg.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
			}

			String uploadHandle = String.format(t.onUploadHandle(), t.objName(), t.objName(), field.getName());
			String beforeFunction = String.format(t.onBeforeFunction(), t.objName(), t.objName());

			formStr.append(String.format(t.temp(),
					StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "", t.showTitle(), // 1
					String.format("<img v-if=\"%s.fileUrl == null\" :src=\"form.%s\" style=\"width: 200px;\" /> ",
							t.objName(), field.getName()), // 2
					t.objName(), // 3
					t.objName(), // 4
					t.objName(), // 5
					elementAttr, // 6
					t.objName(), // 7
					t.objName(), // 8
					uploadHandle.substring(0, uploadHandle.indexOf("(")), // 9
					beforeFunction.substring(0, beforeFunction.indexOf("(")), t.objName()));// 10;

			attrSB.append("            ").append(field.getName()).append(":null,\r\n");
			listNames.append("        ").append(t.objName()).append(":{file:null,fileUrl:null,fileList:[], limit: ")
					.append(t.limit()).append("}").append(",\r\n");

			extendMethods.append(uploadHandle).append(",\r\n").append(beforeFunction).append(",\r\n");

		} else if (field.isAnnotationPresent(FormEntityAttrTypeFile.class)) {
			FormEntityAttrTypeFile t = field.getAnnotation(FormEntityAttrTypeFile.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
			}
			String uploadHandle = String.format(t.onUploadHandle(), t.objName(), t.objName(), field.getName());
			String beforeFunction = String.format(t.onBeforeFunction(), t.objName(), t.objName());

			formStr.append(String.format(t.temp(),
					StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "", t.showTitle(), // 1
					String.format("<span v-if=\"%s.file\">{{%s.file.name}}</span>", t.objName(), t.objName()), // 2
					elementAttr, // 3
					t.objName(), // 4
					t.objName(), // 5
					uploadHandle.substring(0, uploadHandle.indexOf("(")), // 6
					beforeFunction.substring(0, beforeFunction.indexOf("(")), // 7
					t.objName() // 8
			));

			attrSB.append("            ").append(field.getName()).append(":null,\r\n");
			listNames.append("        ").append(t.objName()).append(":{ file:null,fileUrl:null,fileList:[], limit: ")
					.append(t.limit()).append("}").append(",\r\n");

			extendMethods.append(uploadHandle).append(",\r\n").append(beforeFunction).append(",\r\n");

		} else if (field.isAnnotationPresent(FormEntityAttrTypeHtml.class)) {
			// 富文本编辑器
			FormEntityAttrTypeHtml t = field.getAnnotation(FormEntityAttrTypeHtml.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				elementAttr = ":disabled='true'";
			}
			StringBuilder toolbar = new StringBuilder("");

			if (StringUtils.isNotEmpty(t.toolbar())) {
				toolbar = new StringBuilder(" :toolbar=\"").append(t.toolbar()).append("\"");
			}

			formStr.append(
					String.format(t.temp(), StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "",
							t.showTitle(), toolbar.toString(), t.menubar(), field.getName(), t.height(),
							elementAttr + String.format(" :uploadImgApi=\"{url:'%s',fileKey:'%s'}\"",
									t.uploadImgApiUrl(), t.uploadImgFileKey())));
			attrSB.append("            ").append(field.getName()).append(":null,\r\n");
			if (importSB.indexOf("import Tinymce from '@/components/Tinymce") < 0) {
				componentSB.append("Tinymce");
				importSB.append("import Tinymce from '@/components/Tinymce';\r\n");
			}
		} else if (field.isAnnotationPresent(FormEntityAttrTypeReadOnly.class)) {
			FormEntityAttrTypeReadOnly t = field.getAnnotation(FormEntityAttrTypeReadOnly.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			readOnlyTemp.append(String.format(t.temp(), t.showTitle(), field.getName())).append("\r\n");

		} else if (field.isAnnotationPresent(FormEntityAttrTypeLocation.class)) {
			// 经纬度拾取
			FormEntityAttrTypeLocation t = field.getAnnotation(FormEntityAttrTypeLocation.class);
			if ((isAddForm && t.addShow() == false) || (isAddForm == false && t.editShow() == false)) {
				return;
			}
			String amapManager = "amapManager_" + t.key();
			String searchKey = t.searchKey() + t.key();
			String poiPicker = "poiPicker_" + t.key();

			String searchHandle = String.format(t.onSearchHandle(), t.key(), String.format(
					" if(this.%s){ this.%s.searchByKeyword(this.%s);}", searchKey, "poiPicker_" + t.key(), searchKey));

			String searchTemp = String.format(t.searchTemp(), "35%", "1%", searchKey, t.key(), "80%", "100%", "20%",
					"100%", searchHandle.substring(0, searchHandle.indexOf("(")), "100%", t.key());

			String initPoiPicker = String.format(t.initPoiPicker(), poiPicker, t.key(), amapManager, t.key(), t.key(),
					poiPicker, poiPicker, searchKey, "amapLocation_" + t.key(), amapManager, "amapLocation_" + t.key(),
					field.getName(), "amapLocation_" + t.key(), t.addressKey(), t.nameKey());
			boolean search = t.search();
			if (t.readOnly() || t.noEditor() || t.addOnly()) {
				search = false;
				initPoiPicker = "";
			}

			formStr.append(String.format(t.temp(),
					StringUtils.isNotEmpty(t.iftemp()) ? (" v-if=\"" + t.iftemp() + "\" ") : "", t.showTitle(),
					search ? searchTemp : "", "amapEvent_" + t.key(), t.key(), t.key(), "amapEvent_" + t.key(),
					amapManager, ":center=\"amapLocation_" + t.key() + "\"", "plugins_" + t.key(), "marker_" + t.key(),
					"amapLocation_" + t.key(), "amapLocation_" + t.key()));
			listNames.append("          ").append("amapLocation_" + t.key()).append(":[0,0],\r\n");
			listNames.append("            ").append(searchKey).append(":null,\r\n");
			listNames.append("            ").append(amapManager).append(":new VueAMap.AMapManager(),\r\n");
			listNames
					.append("            ").append(
							"plugins_"
									+ t.key())
					.append(":[{buttonPosition: \"RB\",  zoomToAccuracy: true, extensions: \"all\",pName: \"Geolocation\",events: {init(o) { "
							+ (isAddForm
									? " o.getCurrentPosition((status, result) => {if (result && result.position) {self.amapLocation_location1 = [result.position.lng,result.position.lat];}}); "
									: "")
							+ " }},}, {pName: \"ToolBar\"}, {pName: \"Scale\"},],\r\n");

			String events = "={init: (o) => {var map = document.getElementById(\"" + t.key()
					+ "\"); map.setAttribute(\"style\", \"height:\" + " + t.height() + " + \"px;\");" + initPoiPicker
					+ " },moveend: () => {}, zoomchange: () => {},click: (e) => {this.amapLocation_" + t.key()
					+ "=[e.lnglat.lng,e.lnglat.lat];this." + amapManager + ".getMap().setCenter(this.amapLocation_"
					+ t.key() + ");this.form." + field.getName() + "=this.amapLocation_" + t.key() + ";}}\r\n";

			attrSB.append("").append(field.getName()).append(":null,\r\n");
			listNames.append("            ").append("amapEvent_" + t.key()).append(":null,\r\n");
			listNames.append("            ").append(poiPicker).append(":null,\r\n");
			importSB.append(
					"let self;import VueAMap from \"vue-amap\";import Vue from \"vue\";import { lazyAMapApiLoaderInstance } from 'vue-amap';Vue.use(VueAMap);VueAMap.initAMapApiLoader({ key: \""
							+ vueAdminUtil.getAmapKey()
							+ "\", plugin: [ \"AMap.Scale\", \"AMap.ToolBar\", \"AMap.Geolocation\", \"AMap.Autocomplete\", \"AMap.PlaceSearch\", ], v: \"1.4.4\", uiVersion: \"1.0.11\", });");
			extendMethods.append(searchHandle).append(",");

			createdFunction.append("\r\nself = this;\r\nif (this.form.").append(field.getName())
					.append(") {this.amapLocation_").append(t.key()).append("=this.form.").append(field.getName())
					.append("};");
			createdFunction.append("\r\nlazyAMapApiLoaderInstance.load().then(() => {setTimeout(() => {")
					.append("this.amapEvent_" + t.key()).append(events).append(" }, 300);});");
		}

		field.setAccessible(b);
	}

	public void createImportApiHTML(Method method, StringBuilder importSB) {
		// 导入api
		Annotation[] importApis = method.getAnnotationsByType(ImportApi.class);
		for (Annotation annotation : importApis) {
			ImportApi api = (ImportApi) annotation;
			importSB.append(String.format("import * as %s from \"@/api/%s\";\r\n", api.apiName(), api.apiName()));
		}
	}
}
