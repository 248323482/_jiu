package com.jiu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @By 九
 * @Date 2019年7月29日
 * @Time 下午3:18:14
 * @Email budongilt@gmail.com
 */
@SpringBootApplication(exclude= DataSourceAutoConfiguration.class)
@Controller
@EnableScheduling
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @RequestMapping("/")
    public String  index(){
        return  "bird";
    }
}