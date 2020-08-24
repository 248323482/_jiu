package com.jiu.config;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.jiu.model.GenTableColumn;
import com.jiu.type.EntityFiledType;
import com.jiu.type.EntityType;
import com.jiu.type.SuperClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 代码生成配置
 * <p>
 * <p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class CodeGeneratorConfig {

    /**
     * 项目跟路径
     */
    String projectRootPath = System.getProperty("user.dir");
    /**
     * 0 pom
     * 1 gradle
     */
    Integer generatorJavaType =1;



    /**
     * 服务名
     */
    String serviceName = "";
    /**
     * 子模块名称
     * 如消息服务(cloud-msgs-new)包含消息、短信、邮件3个模块
     */
    String childModuleName = "";
    /**
     * 基础包   所有的代码都放置在这个包之下
     */
    String packageBase = "";
    /**
     * 子包名称
     * 会在api、controller、service、serviceImpl、dao、entity等包下面创建子包
     */
    String childPackageName = "";
    /**
     * 作者
     */
    String author = "Jiu";
    /**
     * 项目统一前缀  比如：  cloud-
     */
    private String projectPrefix = "";

    /**
     * 模块后缀
     */
    private String apiSuffix = "";
    private String entitySuffix = "";
    private String serviceSuffix = "";
    private String controllerSuffix = "";
    private String serverSuffix = "";
    /**
     * 版本
     */
    String version = "1.0-SNAPSHOT";
    /**
     * 端口号
     */
    String serverPort = "8080";
    String groupId = "com.jiu";
    String description = "服务";


    public String getPackageBaseParent() {
        return StrUtil.subPre(this.packageBase, this.packageBase.lastIndexOf("."));
    }

    /**
     * entity的父类
     */
    private EntityType superEntity = EntityType.ENTITY;
    /**
     * controller的父类
     */
    private String superControllerClass = SuperClass.SUPER_CLASS.getController();

    /**
     * 自定义继承的Mapper类全称，带包名
     */
    private String superMapperClass = SuperClass.SUPER_CLASS.getMapper();
    /**
     * 自定义继承的Service类全称，带包名
     */
    private String superServiceClass = SuperClass.SUPER_CLASS.getService();
    /**
     * 自定义继承的ServiceImpl类全称，带包名
     */
    private String superServiceImplClass = SuperClass.SUPER_CLASS.getServiceImpl();
    /**
     * 表前缀
     */
    private String tablePrefix = "";
    /**
     * 字段前缀
     */
    private String fieldPrefix = "";
    /**
     * 需要包含的表名，允许正则表达式；用来自动生成代码
     */
    private String[] tableInclude = {};
    /**
     * 排除那些表
     */
    private String[] tableExclude = {};

    /**
     * 包含表名
     *
     * @since 3.3.0
     */
    private LikeTable likeTable;
    /**
     * 不包含表名
     *
     * @since 3.3.0
     */
    private LikeTable notLikeTable;

    /**
     * 驱动连接的URL
     */
    private String url = "jdbc:mysql://budongi.club:3306/jiu?serverTimezone=CTT&characterEncoding=utf8&useUnicode=true&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull";
    /**
     * 驱动名称
     */
    private String driverName = "com.mysql.cj.jdbc.Driver";
    /**
     * 数据库连接用户名
     */
    private String username = "root";
    /**
     * 数据库连接密码
     */
    private String password = "88888888";
    /**
     * 仅仅在微服务架构下面才进行分包
     */
    private boolean enableMicroService = false;


    private FileCreateConfig fileCreateConfig = new FileCreateConfig();
    /**
     * 需要制定生成路径的枚举类列表
     */
    private Set<EntityFiledType> filedTypes = new HashSet<>();

    private Vue vue = new Vue();


    @Data
    public static class Vue {
        private String viewsPath = "views";

        /**
         * 表名 - <字段名 - 字段信息>
         */
        private Map<String, Map<String, GenTableColumn>> tableFieldMap = new HashMap<>();
    }

    /**
     * 必填项 构造器
     *
     * @param serviceName     服务名
     *                        eg： msgs
     * @param childModuleName 子模块名
     *                        eg: sms、emial
     * @param author          作者
     * @param tablePrefix     表前缀
     * @param tableInclude    生成的表 支持通配符
     *                        eg： msgs_.* 会生成msgs_开头的所有表
     * @return
     */
    public static CodeGeneratorConfig build(String serviceName, String childModuleName, String author, String tablePrefix, List<String> tableInclude) {
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        config.setServiceName(serviceName).setAuthor(author).setTablePrefix(tablePrefix)
                .setTableInclude(tableInclude.stream().toArray(String[]::new))
                .setChildModuleName(childModuleName == null ? "" : childModuleName);
        config.setPackageBase("com.jiu");
        return config;
    }


    public static CodeGeneratorConfig buildVue(String serviceName, String tablePrefix, List<String> tableInclude) {
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        config.setServiceName(serviceName).setTablePrefix(tablePrefix)
                .setTableInclude(tableInclude.stream().toArray(String[]::new))
                .setChildModuleName("");
        config.setPackageBase("com.jiu");
        return config;
    }

    public String getChildModuleName() {
        if (StringUtils.isBlank(this.childModuleName)) {
            this.childModuleName = this.serviceName;
        }
        return this.childModuleName;
    }
}
