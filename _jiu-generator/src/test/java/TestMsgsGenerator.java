import com.jiu.CodeGenerator;
import com.jiu.config.CodeGeneratorConfig;
import com.jiu.config.FileCreateConfig;
import com.jiu.type.EntityFiledType;
import com.jiu.type.EntityType;
import com.jiu.type.GenerateType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 测试代码生成权限系统的代码
 *
 * @author zuihou
 * @date 2019/05/25
 */
public class TestMsgsGenerator {
    /***
     * 注意，想要在这里直接运行，需要手动增加 mysql 驱动
     * @param args
     */
    public static void main(String[] args) {
//        CodeGeneratorConfig build = buildSmsEntity();
        CodeGeneratorConfig build = buildMsgsEntity();

        System.out.println("输出路径：");
        System.out.println(System.getProperty("user.dir") + "/jiu-file");
        build.setProjectRootPath(System.getProperty("user.dir") + "/jiu-file");

       // FileCreateConfig fileCreateConfig = new FileCreateConfig(null);
        FileCreateConfig fileCreateConfig = new FileCreateConfig(GenerateType.OVERRIDE);
        fileCreateConfig.setGenerateEntity(GenerateType.OVERRIDE);
        fileCreateConfig.setGenerateEnum(GenerateType.OVERRIDE);
        fileCreateConfig.setGenerateDto(GenerateType.OVERRIDE);
        fileCreateConfig.setGenerateXml(GenerateType.OVERRIDE);
        fileCreateConfig.setGenerateDao(GenerateType.IGNORE);
        fileCreateConfig.setGenerateServiceImpl(GenerateType.IGNORE);
        fileCreateConfig.setGenerateService(GenerateType.IGNORE);
        fileCreateConfig.setGenerateController(GenerateType.IGNORE);
        build.setFileCreateConfig(fileCreateConfig);

        //手动指定枚举类 生成的路径
        Set<EntityFiledType> filedTypes = new HashSet<>();
        filedTypes.addAll(Arrays.asList(
                EntityFiledType.builder().name("providerType").table("sms_template")
                        .packagePath("com.github.zuihou.sms.enumeration.ProviderType").gen(GenerateType.IGNORE).build()
        ));
        build.setFiledTypes(filedTypes);
        CodeGenerator.run(build);
    }



    private static CodeGeneratorConfig buildMsgsEntity() {
        List<String> tables = Arrays.asList(
                "f_file"
        );
        CodeGeneratorConfig build = CodeGeneratorConfig.
                build("file", "", "zuihou", "", tables);
        build.setSuperEntity(EntityType.ENTITY);
        build.setChildPackageName("");
        return build;
    }
}
