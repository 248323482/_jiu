package com.jiu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;
/**
 * 
 * @By        九
 * @Date   2019年7月29日
 * @Time   下午2:56:25
 * @Email budongilt@gmail.com
 */
public class Swagger2 {
	 @Bean
	    public Docket createAPI() {
	        return new Docket(DocumentationType.SWAGGER_2).forCodeGeneration(true).select().apis(RequestHandlerSelectors.basePackage("com.faquir.controller"))
	                //过滤生成链接
	                .paths(PathSelectors.any()).build().apiInfo(apiInfo()).groupName("JAVA")//
	                .directModelSubstitute(LocalDate.class, String.class)//
	                .genericModelSubstitutes(ResponseEntity.class)//
	                .useDefaultResponseMessages(false);//
	    }
	 
	    private ApiInfo apiInfo() {
	        Contact contact=new Contact("九","www.budongi.com","budongilt@gmail.com");
	        ApiInfo apiInfo = new ApiInfoBuilder().title("Jiu").license(" Version 1.0").title("Jiu").description("Jiu API").contact(contact).version("1.0").build();
	        return apiInfo;
	    }
}