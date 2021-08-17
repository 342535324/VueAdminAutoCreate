package com.rs.core.autoCreate.entity;

/**
 * 路由的Children属性
 *
 */
public class Children {

	public String path;// 路径
	public String component;
	public String name; // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
						// *并非实际显示的名称
	public Meta meta;
	public boolean hidden = false;

	public Integer routerIndex;

	public Children(String path, String component, String name, Meta meta, Integer routerIndex, boolean hidden) {
		super();
		this.path = path;
		this.component = component;
		this.name = name;
		this.meta = meta;
		this.hidden = hidden;
		this.routerIndex = routerIndex;
	}

	public Children(String path, String component, String name, Meta meta, Integer routerIndex) {
		this(path, component, name, meta, routerIndex, false);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Children other = (Children) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

}
