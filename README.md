# **VueAdminAutoCreate(施工中!!!)**

通过工具类配合注解,生成项目管理平台的前端页面([VueAdmin](https://github.com/PanJiaChen/vue-element-admin))源码。

想象一下,以后即使因为数据库表的字段增加,也不需要再去码HTML和JS了后端开发人员一个人也能维护整个管理平台,不再想要css,html,js!全部交给**VueAdminAutoCreate**吧,它帮你生成包含JS的HTML前端页面,你只需要在后端(Java)的项目中对实体类新增的属性加一个注解即可!

---

# **说明**

1.基于[ZA(github)](https://github.com/342535324/ZA)/[ZA(gitee)](https://gitee.com/mingyannu/ZA)。

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

---

# **3.注解介绍及使用方法

3.1.针对接口的注解

| 注解名称             | 作用对象  | 注解说明                                                                                                                                                              |
|------------------|------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [@BasePage](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/BasePage.java)        | 接口的类  | 有此注解表示是后台用到的API,会根据此接口生成后台专用的api.js 根据selectListApi的链接生成list.vue  * 根据selectDetailsApi生成form.vue 根据addEntityApi生成add-form.vue  * 需要和 [@ViewEntity](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/ViewEntity.java)一起用,与[@EditPage](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/EditPage.java)不兼容 |
| [@ControllerLog](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/ControllerLog.java)   | 接口的类  | 授权模板用的注解 没有此注解的控制器不会生成页面                                                                                                                                          |
| [@EditPage](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/EditPage.java)        | 接口的类  | 有此注解会生成对应的form页面 注意:与[@BasePage](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/BasePage.java)不兼容                                                                                                                                                                                                                                                                    |
| [@RouterIndex](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/RouterIndex.java)     | 接口的类  | 路由排序 如果没有此注解 默认按名称排序                                                                                                                                              |
| [@RouterModelName](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/RouterModelName.java) | 接口的类  | 路由模块名称,相同会纳入同一个模块下面 如果没有此注解 默认控制器名称分组                                                                                                                             |
| [@ViewEntity](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/ViewEntity.java)      | 接口的类 | 根据此注解生成list.vue与form.vue,可以写在controller上,也可以写在selectListApi,selectDetailsApi,addEntityApi这三个接口对应的方法上,需要和@BasePage一起用  |                                                                                                                          
| [@ExtendMethod](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/ExtendMethod.java)    | 接口的方法 | 拓展js方法,1个注解对应一个方法   |                                                                                                                                           
| [@ImportApi](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/ImportApi.java)       | 接口的方法 | 在页面(view)导入api(通过import函数)     
| [@ViewConf](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/ViewConf.java)    | 接口的方法 | 每个页面对应显示的标题   


3.2.针对列表页面(view/**/list.vue)的配置注解

| 注解名称                   | 作用对象   | 注解说明                                                                                                                                |
|------------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------|
| [@ListEntityAttr](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/listPage/ListEntityAttr.java)        | 实体类的属性 | 表格控制,有此注解表示是表显示用到的字段,会通过接口返回的实体类的属性查找该注解,然后根据该注解生成后台用的view -该注解用在实体类的字段上                                                            |
| [@ListViewAddressFilter](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/listPage/ListViewAddressFilter.java) | 接口的方法  | 地址过滤属性 -该注解用在获取列表数据的方法上                                                                                                             |
| [@ListViewExportExcel](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/listPage/ListViewExportExcel.java)   | 接口的方法  | 列表扩展 导出Excel -该注解用在获取列表数据的方法上 会将列表的所有参数传递到导出接口                                                                                      |
| [@ListViewFilter](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/listPage/ListViewFilter.java)        | 接口的方法  | 过滤属性 -该注解用在获取列表数据的方法上                                                                                                               |
| [@ListViewOperation](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/listPage/ListViewOperation.java)     | 接口的方法  | 默认是“编辑”,"删除",列表操作内容 -该注解用在获取列表数据的方法上,如果需要传参可用重写operation,list页面内置listQuery对象作为列表查询参数,在页面初始化的时候会把this.$route.query的内容同步到listQuery对象内 |
| [@ListViewSearch](https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/listPage/ListViewSearch.java)        | 接口的方法  | 字符串搜索                                                                                                                                                                                                                                                               |


3.3.针对表单页面(view/**/add-form.vue 或 form.vue 或 edit.vue)的配置注解

| 注解名称                        | 作用对象  | 注解说明                            |
|-----------------------------|-------|---------------------------------|
| [@FormEntityAttr]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttr.java            | 实体类属性 | 表格控制,有此注解表示是表显示用到的字段,生成el-input |
| [@FormEntityAttrTypeDate]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeDate.java     | 实体类属性 | 日期选择组件(el-date-picker)          |
| [@FormEntityAttrTypeFile]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeFile.java     | 实体类属性 | 文件上传,生成el-upload                |
| [@FormEntityAttrTypeHtml]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeHtml.java     | 实体类属性 | 富文本编辑器,生成tinymce                |
| [@FormEntityAttrTypeImg]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeImg.java      | 实体类属性 | 图片上传,生成el-upload与img组件          |
| [@FormEntityAttrTypeLocation]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeLocation.java | 实体类属性 | 地图定位,生成el-amap                  |
| [@FormEntityAttrTypeNum]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeNum.java      | 实体类属性 | 数字类型的el-input                   |
| [@FormEntityAttrTypePassword]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypePassword.java | 实体类属性 | 密码输入框                           |
| [@FormEntityAttrTypeReadOnly]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeReadOnly.java   | 实体类属性 | 备注说明,会生成到form的后面                 |
| [@FormEntityAttrTypeSelect]https://github.com/342535324/VueAdminAutoCreate/blob/main/src/com/rs/core/autoCreate/annotation/formPage/FormEntityAttrTypeSelect.java   | 实体类属性 | 选择框,生成el-select                 |

