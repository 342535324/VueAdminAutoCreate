package com.rs.core.autoCreate.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.rs.core.autoCreate.VueAdminUtil;
import com.rs.core.autoCreate.annotation.BasePage;
import com.rs.core.autoCreate.annotation.EditPage;
import com.rs.core.autoCreate.annotation.RouterIndex;
import com.rs.core.autoCreate.annotation.RouterModelName;

/**
 * vueadmin专用 路由
 * 说明:https://panjiachen.github.io/vue-element-admin-site/zh/guide/
 * essentials/router-and-nav.html#%E9%85%8D%E7%BD%AE%E9%A1%B9
 */
public class RouterV {

	transient private String fileName;// 生成路由文件的文件名

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isEmpty() {
		return StringUtils.isEmpty(this.fileName);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			// System.out.println("匹配对象null");
			return false;
		}
		if (getClass() != obj.getClass()) {
			// System.out.println("匹配class错误");
			return false;
		}
		RouterV other = (RouterV) obj;
		if (fileName == null) {
			if (other.fileName != null) {
				// System.out.println("自身fileName为空");
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			// System.out.println("fileName错误");
			return false;
		}
		return true;
	}

	private String path;// 路径

	private String component;
	// 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
	private String redirect = "'noRedirect'";
	// 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
	private boolean hidden = false;
	// 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
	// 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
	// 若你想不管路由下面的 children 声明的个数都显示你的根路由
	// 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
	private boolean alwaysShow = true;

	private String name; // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
							// *并非实际显示的名称

	private Meta meta;

	private List<Children> children;

	private String getChildrenTitle(String title) {
		return title.substring(title.indexOf("-") + 1);
	}

	// 合并路由(将children合并,且将meta的roles合并)
	public RouterV append(RouterV newRouter) {
		this.children.addAll(newRouter.children);
		this.meta.roles.addAll(newRouter.meta.roles);
		// 自定义排序
		Collections.sort(children, new Comparator<Children>() {
			@Override
			public int compare(Children o1, Children o2) {
				boolean b1 = o1.path.indexOf("_list") > 0;
				boolean b2 = o2.path.indexOf("_list") > 0;
				if (b1 != b2) {
					if (b1) {
						return -1;
					} else if (b2) {
						return 1;
					} else {
						return 0;
					}
				}
				return o1.routerIndex >= o2.routerIndex ? 1 : -1;
			}
		});

		return this;
	}

	public String getHashCode(RouterModelName modelClass) {
		Integer hashCode = modelClass.value().hashCode();
		return hashCode.toString().replaceAll("-", "_99_");
	}

	/**
	 * 设置路由为不输出对象
	 */
	private void setNull() {
		this.path = null;
		this.fileName = null;
	}

	public RouterV(VueAdminUtil vueAdminUtil, final AdminManageModel tAdminManageModel, Annotation[] basePages,
			Method[] methods, Annotation[] formPages, RouterModelName modelClass, String controllerURL,
			RouterIndex index) {

		// 处理标记
		if (formPages != null && formPages.length < 1) {
			formPages = null;
		}
		List<BasePage> basePageList = new ArrayList<BasePage>();
		if (basePages != null && basePages.length < 1) {
			basePages = null;
		} else {
			// 排序
			basePageList = new ArrayList<BasePage>((Collection<? extends BasePage>) Arrays.asList(basePages));
			basePageList.sort((a, b) -> a.index() - b.index());
		}
		if (tAdminManageModel.getAdminManageModels().size() > 0) {
			this.children = new ArrayList<Children>();
		}
		List<Integer> roles = new ArrayList<Integer>();

		String childrenPath = "";
		String controllerName = tAdminManageModel.getUrl().substring(1);

		if (modelClass == null) {// 普通模块,按控制器分组
			this.fileName = controllerURL.replaceAll("\\W", "");
			this.path = "'" + tAdminManageModel.getUrl() + "'";
			this.component = "Layout";// component: Layout,
			this.name = "'menu-" + tAdminManageModel.getId() + "'";
			this.redirect = "'" + tAdminManageModel.getUrl() + "/list'";

			this.meta = new Meta(null, new ArrayList<Integer>(1) {
				{
					add(tAdminManageModel.getId());
				}
			}, "'" + tAdminManageModel.getText() + "'", false, false);
		} else {// 自定义分组模块
			this.fileName = "router_" + getHashCode(modelClass);
			this.path = "'/router_" + getHashCode(modelClass) + "'";
			this.component = "Layout";// component: Layout,
			this.name = "'menu-" + getHashCode(modelClass) + "'";
			childrenPath = controllerName + "/";
		}

		for (final AdminManageModel adminManageModel : tAdminManageModel.getAdminManageModels()) {
			if (formPages != null && formPages.length > 0) {
				int i = 1;
				for (Annotation f : formPages) {
					EditPage formPage = (EditPage) f;
					String icon;
					if (StringUtils.isNotEmpty(formPage.icon())) {
						icon = formPage.icon();
					} else {
						icon = "form";
					}

					String istr = i + "";
					if (StringUtils.isNotEmpty(formPage.selectDetailsApi())
							&& adminManageModel.getUrl().contains(formPage.selectDetailsApi())) {
						children.add(new Children("'" + childrenPath + "edit" + istr + "'",
								"() => import('@/views" + tAdminManageModel.getUrl() + "/edit" + istr + "')",
								"'" + tAdminManageModel.getUrl().substring(1) + "_form'",
								new Meta("() => import('@/views" + tAdminManageModel.getUrl() + "/edit" + istr + "')",
										new ArrayList<Integer>(1) {
											{
												add(adminManageModel.getId());
											}
										}, "'" + getChildrenTitle(adminManageModel.getText()) + "'", icon, false, true),
								index.value(), formPage.routerHide()));
					}
					i++;
					if (modelClass != null) {
						VueAdminUtil.routerPathMAP.put("'/" + controllerName + "/edit" + istr + "'", "'/router_"
								+ getHashCode(modelClass) + tAdminManageModel.getUrl() + "/edit" + istr + "'");
					}
				}
				roles.add(adminManageModel.getId());

			} else {
				int i = 0;

				for (BasePage apiController : basePageList) {
					if (apiController.createView() == false) {
						setNull();
						return;
					}

					String istr = i > 0 ? i + "" : "";
					i++;
					if (StringUtils.isNotEmpty(apiController.selectListApi())
							&& adminManageModel.getUrl().contains(apiController.selectListApi())) {

						String icon;
						if (StringUtils.isNotEmpty(apiController.icon())) {
							icon = apiController.icon();
						} else {
							icon = "table";
						}
						// 列表
						children.add(new Children("'" + childrenPath + "list" + istr + "'",
								"() => import('@/views" + tAdminManageModel.getUrl() + "/list" + istr + "')",
								"'" + tAdminManageModel.getUrl().substring(1) + "_list'",
								new Meta("() => import('@/views" + tAdminManageModel.getUrl() + "/list" + istr + "')",
										new ArrayList<Integer>(1) {
											{
												add(adminManageModel.getId());
											}
										}, "'" + getChildrenTitle(adminManageModel.getText()) + "'", icon, false, true),
								index.value(), apiController.routerHide()));
						roles.add(adminManageModel.getId());
						if (modelClass != null) {
							VueAdminUtil.routerPathMAP.put("'/" + controllerName + "/list" + istr + "'", "'/router_"
									+ getHashCode(modelClass) + tAdminManageModel.getUrl() + "/list" + istr + "'");
						}
					} else if (StringUtils.isNotEmpty(apiController.addEntityApi())
							&& adminManageModel.getUrl().contains(apiController.addEntityApi())) {
						String icon;
						if (StringUtils.isNotEmpty(apiController.icon())) {
							icon = apiController.icon();
						} else {
							icon = "form";
						}
						// 新增页
						children.add(
								new Children("'" + childrenPath + "add-form" + istr + "'",
										"() => import('@/views" + tAdminManageModel.getUrl() + "/add-form" + istr
												+ "')",
										"'" + tAdminManageModel.getUrl().substring(1) + "_addform'",
										new Meta("() => import('@/views" + tAdminManageModel.getUrl() + "/add-form"
												+ istr + "')", new ArrayList<Integer>(1) {
													{
														add(adminManageModel.getId());
													}
												}, "'" + getChildrenTitle(adminManageModel.getText()) + "'", icon,
												false, true),
										index.value(), !apiController.routerShowForm()));
						roles.add(adminManageModel.getId());
						if (modelClass != null) {
							VueAdminUtil.routerPathMAP.put("'/" + controllerName + "/add-form" + istr + "'", "'/router_"
									+ getHashCode(modelClass) + tAdminManageModel.getUrl() + "/add-form" + istr + "'");
						}
					} else if (StringUtils.isNotEmpty(apiController.selectDetailsApi())
							&& adminManageModel.getUrl().contains(apiController.selectDetailsApi())) {
						String icon;
						if (StringUtils.isNotEmpty(apiController.icon())) {
							icon = apiController.icon();
						} else {
							icon = "form";
						}
						// 详情页
						children.add(new Children("'" + childrenPath + "form" + istr + "'",
								"() => import('@/views" + tAdminManageModel.getUrl() + "/form" + istr + "')",
								"'" + tAdminManageModel.getUrl().substring(1) + "_form'",
								new Meta("() => import('@/views" + tAdminManageModel.getUrl() + "/form" + istr + "')",
										new ArrayList<Integer>(1) {
											{
												add(adminManageModel.getId());
											}
										}, "'" + getChildrenTitle(adminManageModel.getText()) + "'", icon, false, true),
								index.value(), true));
						roles.add(adminManageModel.getId());
						if (modelClass != null) {
							VueAdminUtil.routerPathMAP.put("'/" + controllerName + "/form" + istr + "'", "'/router_"
									+ getHashCode(modelClass) + tAdminManageModel.getUrl() + "/form" + istr + "'");
						}
					}

				}
			}
		}
		// 自定义排序
		Collections.sort(children, new Comparator<Children>() {
			@Override
			public int compare(Children o1, Children o2) {
				if (o1.path.equals("'list'")) {
					return -1;
				} else if (o1.path.equals("'form'")) {
					return 1;
				} else {
					boolean b1 = o1.path.indexOf("_list") > 0;
					boolean b2 = o2.path.indexOf("_list") > 0;
					if (b1 != b2) {
						if (b1) {
							return -1;
						} else if (b2) {
							return 1;
						} else {
							return 0;
						}
					}
				}
				return 0;
			}
		});

		if (this.children.size() > 0) {
			if (StringUtils.isEmpty(this.redirect)) {
				this.redirect = "'/router_" + getHashCode(modelClass) + "/" + this.children.get(0).path.substring(1);
			}
			if (this.meta == null) {
				this.meta = new Meta(null, roles, "'" + modelClass.value() + "'", false, false);
			}
		}

		if (formPages != null && formPages.length == 1 && this.meta != null) {
			EditPage formPage = (EditPage) formPages[0];
			this.meta.setIcon(formPage.icon());
			// } else if (basePageList != null && basePageList.size() == 1 &&
			// this.meta != null) {
			// this.meta.setIcon(basePageList.get(0).icon());
		}

	}

}
