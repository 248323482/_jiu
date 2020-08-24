package com.jiu;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.collect.Lists;
import com.jiu.config.CodeGeneratorConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目生成工具
 *
 */
@Slf4j
public class ProjectGenerator {
    /**
     * 项目模块名称
     */
    private final static String API = "api";
    private final static String ENTITY = "entity";
    private final static String BIZ = "biz";
    private final static String CONTROLLER = "controller";
    private final static String SERVER = "server";

    private final static String[] ALL_MODULAR_LIST = new String[]{
            API, ENTITY, BIZ, CONTROLLER, SERVER,
    };
    private final static String[] CHILD_MODULAR_LIST = new String[]{
            BIZ, CONTROLLER
    };

    /**
     * 模板路径
     */
    private static final String INIT_JAVA_FTL = "init/java/%s.java.ftl";
    private static final String INIT_RESOURCES_FTL = "init/resources/%s.java.ftl";
    private static final String INIT_FTL = "init/pom/%s.java.ftl";

    private final static String SRC_MAIN_JAVA = "src/main/java";
    private final static String SRC_MAIN_RESOURCES = "src/main/resources";
    private final static String[] MAVEN_PATH = new String[]{
            SRC_MAIN_JAVA, SRC_MAIN_RESOURCES, "src/test/java", "src/test/resources"
    };

    private Configuration configuration;
    private CodeGeneratorConfig config;

    public ProjectGenerator(CodeGeneratorConfig config) {
        this.init();
        this.config = config;
    }


    private void init() {
        this.configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        this.configuration.setDefaultEncoding(ConstVal.UTF8);
        this.configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, StringPool.SLASH);
    }

    public void build() {
        boolean isChildModule = !this.config.getServiceName().equalsIgnoreCase(this.config.getChildModuleName());

        // 创建项目跟文件夹
        File parentPath = new File(this.config.getProjectRootPath());
        if (!parentPath.exists()) {
            parentPath.mkdirs();
        }

        //创建服务文件夹
        String serviceName = this.config.getProjectPrefix() + this.config.getServiceName();
        log.info("服务名：{}", serviceName);
        String servicePath = Paths.get(this.config.getProjectRootPath(), serviceName).toString();
        Map<String, Object> objectMap = this.getObjectMap(this.config);


        String[] modularList = ALL_MODULAR_LIST;

        if (isChildModule) {
            modularList = CHILD_MODULAR_LIST;
        }
        if(config.isEnableMicroService()) {
            for (String modular : modularList) {
                //创建模块文件夹
                String modularPath = this.mkModular(servicePath, serviceName, modular, isChildModule);
                this.mkMaven(modularPath);
                this.mkBasePackage(modularPath);
                this.generatorPom(objectMap, String.format(INIT_FTL, modular), modularPath);
            }
        }

        if (!isChildModule) {
            this.writer(objectMap, String.format(INIT_FTL, "gradle"), Paths.get(servicePath, "build.gradle").toString());
            try {
                File path = new File( Paths.get(this.config.getProjectRootPath(), "settings.gradle").toString());
                String setting = "include \""+config.getProjectPrefix()+config.getServiceName()+"\"";
                List<String> settings = FileUtils.readLines(path);
                if(!settings.contains(setting)) {
                    settings.add(setting);
                }
                FileUtils.writeLines(new File( Paths.get(this.config.getProjectRootPath(), "settings.gradle").toString()), settings);
            }catch (Exception e){

            }

            // server 模块
            String serverApplicationPath = this.mkServer(servicePath, serviceName);
            this.generatorServerJava(objectMap, serverApplicationPath);
            String modularName ="";
            if(config.isEnableMicroService()) {
                modularName = serviceName + "-" + SERVER;
            }
            String serverResourcePath = Paths.get(servicePath, modularName, SRC_MAIN_RESOURCES).toString();
            this.generatorServerResources(objectMap, serverResourcePath);
        } else {
            String modulerName = this.config.getProjectPrefix() + this.config.getChildModuleName();
            System.err.println("------------------------------------------");
            System.err.println(String.format("生成完毕，请将以下配置手工加入：%s/pom.xml", serviceName));
            System.err.println(String.format(
                    "            <dependency>\n" +
                            "                <groupId>%s</groupId>\n" +
                            "                <artifactId>%s-biz</artifactId>\n" +
                            "                <version>${project.version}</version>\n" +
                            "            </dependency>", config.getGroupId(), modulerName));
            System.err.println(String.format(
                    "            <dependency>\n" +
                            "                <groupId>%s</groupId>\n" +
                            "                <artifactId>%s-controller</artifactId>\n" +
                            "                <version>${project.version}</version>\n" +
                            "            </dependency>", config.getGroupId(), modulerName));
            System.err.println("");
            System.err.println(String.format(
                    "        <module>%s-biz</module>\n" +
                            "        <module>%s-controller</module>", modulerName, modulerName));

            System.err.println("------------------------------------------");
            System.err.println(String.format("生成完毕，请将以下配置手工加入：%s/%s/pom.xml", serviceName, serviceName + "-server"));

            System.err.println(String.format(
                    "            <dependency>\n" +
                            "                <groupId>%s</groupId>\n" +
                            "                <artifactId>%s-controller</artifactId>\n" +
                            "            </dependency>", config.getGroupId(), modulerName));
        }

        System.err.println("生成完毕，但请手动完成以下操作：");
        System.err.println("------------------------------------------");
        System.err.println(String.format("将以下配置手工加入：%s/pom.xml", this.config.getProjectPrefix() + "admin-cloud"));
        System.err.println(String.format("        <module>%s</module>", serviceName));

        System.err.println("------------------------------------------");
        String projectName = serviceName + "-server";
        String nacosProject = serviceName + "-server.yml";
        System.err.println(String.format("在nacos中新建一个名为: %s 的配置文件，并将： %s/src/main/resources/%s 配置文件的内容移动过去", nacosProject, projectName, nacosProject));

        System.err.println("------------------------------------------");
        System.err.println("将下面的配置手动加入nacos中 zuihou-zuul-server.yml");
        System.err.println(String.format("    %s:\n" +
                "      path: /%s/**\n" +
                "      serviceId: %s-server", config.getServiceName(), config.getServiceName(), serviceName));

        System.err.println("------------------------------------------");
        System.err.println("将下面的配置手动加入nacos中 zuihou-gateway-server.yml");
        System.err.println(String.format("        - id: %s\n" +
                        "          uri: lb://%s-server\n" +
                        "          predicates:\n" +
                        "            - Path=/%s/**\n" +
                        "          filters:\n" +
                        "            - StripPrefix=1\n"
                , config.getServiceName(), serviceName, config.getServiceName()));

    }

    private void generatorApiModular(String serviceName, Map<String, Object> objectMap) {
        String apiName = this.config.getProjectPrefix() + "api";
        String apiModularName = serviceName + "-api";
        log.info("已经生成模块：{}", apiModularName);
        String apiPath = Paths.get(this.config.getProjectRootPath(), apiName, apiModularName).toString();
        File apiPathFile = new File(apiPath);
        if (!apiPathFile.exists()) {
            apiPathFile.mkdirs();
        }
        this.mkMaven(apiPath);
        this.generatorPom(objectMap, String.format(INIT_FTL, "api"), apiPath);
    }


    private void generatorServerResources(Map<String, Object> objectMap, String serverResourcePath) {
        this.writer(objectMap, String.format(INIT_RESOURCES_FTL, "bootstrap"), Paths.get(serverResourcePath, "bootstrap.yml").toString());
        this.writer(objectMap, String.format(INIT_RESOURCES_FTL, "logback-spring"), Paths.get(serverResourcePath, "logback-spring.xml").toString());
        this.writer(objectMap, String.format(INIT_RESOURCES_FTL, "spy"), Paths.get(serverResourcePath, "spy.properties").toString());
        this.writer(objectMap, String.format(INIT_RESOURCES_FTL, "application"), Paths.get(serverResourcePath, this.config.getProjectPrefix() + this.config.getServiceName() + ".yml").toString());
    }

    /**
     * 生成 server项目下的jar代码
     *
     * @param objectMap
     * @param serverApplicationPath
     */
    private void generatorServerJava(Map<String, Object> objectMap, String serverApplicationPath) {
        Object service = objectMap.get("service");
        String serverApplicationParentPath = StrUtil.subPre(serverApplicationPath, serverApplicationPath.lastIndexOf(File.separator));
        //启动类
        this.writer(objectMap, String.format(INIT_JAVA_FTL, "Application"), Paths.get(serverApplicationParentPath, service + "ServerApplication.java").toString());
        String serverConfigPath = Paths.get(serverApplicationPath, "config").toString();
        // 全局异常
        this.writer(objectMap, String.format(INIT_JAVA_FTL, "ExceptionConfiguration"), Paths.get(serverConfigPath, service + "ExceptionConfiguration.java").toString());
        //Web
        this.writer(objectMap, String.format(INIT_JAVA_FTL, "WebConfiguration"), Paths.get(serverConfigPath, service + "WebConfiguration.java").toString());
        //数据源
        this.writer(objectMap, String.format(INIT_JAVA_FTL, "DatabaseAutoConfiguration"), Paths.get(serverConfigPath, "datasource", service + "DatabaseAutoConfiguration.java").toString());
    }

    private String mkServer(String servicePath, String serviceName) {
        String modularName ="";
        if(this.config.isEnableMicroService()){
             modularName = serviceName + "-" + SERVER;
        }
        String packageBase = this.config.getPackageBase().replace(".", File.separator);

        String serverApplicationPath = Paths.get(servicePath, modularName, SRC_MAIN_JAVA, packageBase).toString();
        String modularConfigPath = Paths.get(serverApplicationPath, "config").toString();
        File modularConfigFile = new File(modularConfigPath);
        if (!modularConfigFile.exists()) {
            modularConfigFile.mkdirs();
        }
        return serverApplicationPath;
    }


    private Map<String, Object> getObjectMap(CodeGeneratorConfig globalConfig) {
        boolean isChildModule = !this.config.getServiceName().equalsIgnoreCase(this.config.getChildModuleName());
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("author", globalConfig.getAuthor());
        objectMap.put("serviceName", globalConfig.getServiceName());
        objectMap.put("isChildModule", isChildModule);
        objectMap.put("childModuleName", globalConfig.getChildModuleName());
        objectMap.put("packageBase", globalConfig.getPackageBase());
        objectMap.put("projectPrefix", globalConfig.getProjectPrefix());
        objectMap.put("service", StrUtil.upperFirst(globalConfig.getServiceName()));
        objectMap.put("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        objectMap.put("version", globalConfig.getVersion());
        objectMap.put("packageBaseParent", globalConfig.getPackageBaseParent());
        objectMap.put("serverPort", globalConfig.getServerPort());
        objectMap.put("groupId", globalConfig.getGroupId());
        objectMap.put("description", globalConfig.getDescription());
        return objectMap;
    }


    private void writer(Map<String, Object> objectMap, String templatePath, String outputFile) {
        File file = new File(outputFile);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            Template template = this.configuration.getTemplate(templatePath);
            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                template.process(objectMap, new OutputStreamWriter(fileOutputStream, ConstVal.UTF8));
            }
        } catch (Exception e) {
            log.error("生成失败", e);
        }
    }

    /**
     * 生成pom
     *
     * @param templatePath
     */
    private void generatorPom(Map<String, Object> objectMap, String templatePath, String outputFile) {
        this.writer(objectMap, templatePath, Paths.get(outputFile, "pom.xml").toString());
    }


    private String mkModular(String servicePath, String serviceName, String modular, boolean isChildModule) {
        String modularName = serviceName + "-" + modular;
        if (isChildModule) {
            String childModuleName = this.config.getProjectPrefix() + this.config.getChildModuleName();
            modularName = childModuleName + "-" + modular;
        }
        log.info("已经生成模块：{}", modularName);
        String modularPath = Paths.get(servicePath, modularName).toString();
        File modularPathFile = new File(modularPath);
        if (!modularPathFile.exists()) {
            modularPathFile.mkdirs();
        }
        return modularPath;
    }


    /**
     * 创建 maven 目录文件夹
     *
     * @param modularPath
     */
    private void mkMaven(String modularPath) {
        for (String maven : MAVEN_PATH) {
            String mavenPath = Paths.get(modularPath, maven).toString();
            File mavenPathFile = new File(mavenPath);
            if (!mavenPathFile.exists()) {
                mavenPathFile.mkdirs();
            }
        }
    }

    private void mkBasePackage(String modularPath) {
        String basePackage = Paths.get(modularPath, SRC_MAIN_JAVA, StrUtil.replace(config.getPackageBase(), ".", File.separator)).toString();
        File mavenPathFile = new File(basePackage);
        if (!mavenPathFile.exists()) {
            mavenPathFile.mkdirs();
        }


    }
}
