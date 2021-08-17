# **VueAdminAutoCreate(施工中!!!)**

通过工具类配合注解,生成项目管理平台的前端页面([VueAdmin](https://github.com/PanJiaChen/vue-element-admin))源码。

想象一下,以后即使因为数据库表的字段增加,也不需要再去码HTML和JS了后端开发人员一个人也能维护整个管理平台,不再想要css,html,js!全部交给**VueAdminAutoCreate**吧,它帮你生成包含JS的HTML前端页面,你只需要在后端(Java)的项目中对实体类新增的属性加一个注解即可!

---

# **说明**

1.基于[ZA](https://github.com/342535324/ZA)。

2.基于[VueAdmin](https://github.com/PanJiaChen/vue-element-admin)。

3.基于Spring的注解(仅需要注解部分,如@RequestMapping,因为我需要读取到接口的URL)。

4.对项目无侵害,可当成独立工具使用

---

# **目录**

* 1.DEMO
* 2.代码生成的工作流程
* 3.注解介绍及使用方法
* 4.工具类介绍
* 5.集成方法

---

# **1.DEMO**

DEMO施工中

---

# **2.代码生成的工作流程**

![工作流程](https://images.gitee.com/uploads/images/2021/0817/121259_2022b409_9598857.png "workflow.png")

绿色代表原本交互的工作流程,黄色是VueAdminAutoCreate的工作流程

目前支持生成的前端组件包括:el-date-picker,el-upload,tinymce,img,el-amap,el-input,el-select(含多选与联动)。

