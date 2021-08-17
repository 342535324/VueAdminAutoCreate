# **VueAdminAutoCreate(施工中!!!)**

通过工具类配合注解,生成项目管理平台的前端页面([VueAdmin](https://github.com/PanJiaChen/vue-element-admin))源码。

想象一下,以后即使因为数据库表的字段增加,也不需要再去码HTML和JS了后端开发人员一个人也能维护整个管理平台,不再想要css,html,js!全部交给**VueAdminAutoCreate**吧,它帮你生成包含JS的HTML前端页面,你只需要在后端(Java)的项目中对实体类新增的属性加一个注解即可!

目前支持生成的前端组件包括:el-date-picker,el-upload,tinymce,img,el-amap,el-input,el-select(含多选与联动)。

# **说明**

基于[ZA](https://github.com/342535324/ZA)。

基于[VueAdmin](https://github.com/PanJiaChen/vue-element-admin)。

基于Spring的注解(仅需要注解部分,如@RequestMapping,因为我需要读取到接口的URL)。

