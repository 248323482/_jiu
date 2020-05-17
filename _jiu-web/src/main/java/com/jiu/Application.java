package com.jiu;

import com.jiu.log.annotation.SysLog;
import com.jiu.utils.SpringUtils;
import com.jiu.validator.config.EnableFormValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @By 九
 * @Date 2019年7月29日
 * @Time 下午3:18:14
 * @Email budongilt@gmail.com
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Controller
//https://www.cnblogs.com/cjsblog/p/10548022.html
//https://www.cnblogs.com/1138720556Gary/p/11821059.html
//https://www.jianshu.com/p/13b8654a157f
//https://www.liangzl.com/get-article-detail-153506.html
@Slf4j
@Api(value = "Application", tags = "小鸟")
@EnableFormValidator
public class Application {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(Application.class, args);
        Environment env = application.getEnvironment();
        SpringUtils.setApplicationContext(application);
        log.info("\n----------------------------------------------------------\n\t" +
                        "应用 '{}' 运行成功! 访问连接:\n\t" +
                        "Swagger文档: \t\thttp://{}:{}{}{}/doc.html\n\t"+
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path", ""),
                env.getProperty("spring.mvc.servlet.path", "")
        );
    }

    @SysLog
    @ApiOperation(value = "小鸟", notes = "小鸟")
    @GetMapping("/")
    public String  index(HttpServletRequest request){
        return  "bird";
    }


}