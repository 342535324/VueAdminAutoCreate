package com.rs.core.autoCreate.entity;

import java.util.ArrayList;
import java.util.List;

public class AdminManageModel {

	// Fields

	private Integer id;
	/**
	 * 接口路径
	 */
	private String url;
	/**
	 * 描述
	 */
	private String text;
	/**
	 * 模块类型: 0表示公开模块 1表示分区模块 2表示只有超管有权限的私有模块
	 */
	private Integer type = 0;
	/**
	 * 子级对象
	 */
	private List<AdminManageModel> adminManageModels = new ArrayList<AdminManageModel>();

	// Constructors

	/** default constructor */
	public AdminManageModel() {
	}

	public AdminManageModel(Integer id, String url, String text, Integer type,
			List<AdminManageModel> adminManageModels) {
		super();
		this.id = id;
		this.url = url;
		this.text = text;
		this.type = type;
		this.adminManageModels = adminManageModels;
	}

	// Property accessors
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<AdminManageModel> getAdminManageModels() {
		return adminManageModels;
	}

	public void setAdminManageModels(List<AdminManageModel> adminManageModels) {
		this.adminManageModels = adminManageModels;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

}
