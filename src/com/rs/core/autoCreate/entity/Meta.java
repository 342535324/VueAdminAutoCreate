package com.rs.core.autoCreate.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由的 Meta属性
 *
 */
public class Meta {

	public List<Integer> roles = new ArrayList<Integer>(); // 设置该路由进入的权限，支持多个权限叠加
															// *接口用url作为权限标识
	public String title;// 设置该路由在侧边栏和面包屑中展示的名字
	private String icon = "'tree'"; // 设置该路由的图标,已集成图标https://panjiachen.github.io/vue-element-admin/#/icon/index
	public boolean noCache = false; // 如果设置为true，则不会被<keep-alive>缓存(默认alse)
	public boolean breadcrumb = false; // 如果设置为false，则不会在breadcrumb面包屑中显示

	public Meta(String component, List<Integer> roles, String title, String icon, boolean noCache, boolean breadcrumb) {
		super();
		this.roles = roles;
		this.title = title;
		this.icon = new StringBuilder("'").append(icon).append("'").toString();
		this.noCache = noCache;
		this.breadcrumb = breadcrumb;
	}

	public Meta(String component, List<Integer> roles, String title, boolean noCache, boolean breadcrumb) {
		super();
		this.roles = roles;
		this.title = title;
		this.noCache = noCache;
		this.breadcrumb = breadcrumb;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = new StringBuilder("'").append(icon).append("'").toString();
	}

}
