import com.jiu.ProjectGenerator;
import com.jiu.config.CodeGeneratorConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 初始化项目 代码
 *
 * @author zuihou
 * @date 2019/05/25
 */
@Slf4j
public class TestInitProject {

    public static void main(String[] args) {
        CodeGeneratorConfig config = new CodeGeneratorConfig();
        config
                .setProjectRootPath(System.getProperty("user.dir"))
                // 项目的前缀
                .setProjectPrefix("jiu-")

                // 需要新建的 服务名      该例会生成 zuihou-mall 服务
                .setServiceName("file")

                // 生成代码的注释 @author zuihou
                .setAuthor("zuihou")
                // 项目描述
                .setDescription("商城")
                // 服务的端口号
                .setServerPort("17081")
        ;
        // 项目的业务代码 存放的包路径
        config.setPackageBase("com.jiu");

        System.out.println("项目初始化根路径：" + config.getProjectRootPath());
        ProjectGenerator pg = new ProjectGenerator(config);
        pg.build();
    }
}
