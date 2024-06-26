package com.jiu.swagger2;

import com.github.xiaoymin.knife4j.spring.filter.ProductionSecurityFilter;
import com.github.xiaoymin.knife4j.spring.filter.SecurityBasicAuthFilter;
import com.github.xiaoymin.knife4j.spring.model.MarkdownFiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@ComponentScan(
        basePackages = {
                "com.github.xiaoymin.knife4j.spring.plugin",
                "com.github.xiaoymin.knife4j.spring.web"
        }
)
@ConditionalOnProperty(prefix = SwaggerProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({BeanValidatorPluginsConfiguration.class})
public class Swagger2Configuration implements WebMvcConfigurer {
    /**
     * 这个地方要重新注入一下资源文件，不然不会注入资源的，也没有注入requestHandlerMappping,相当于xml配置的
     * <!--swagger资源配置-->
     * <mvc:resources location="classpath:/META-INF/resources/" mapping="swagger-ui.html"/>
     * <mvc:resources location="classpath:/META-INF/resources/webjars/" mapping="/webjars/**"/>
     * 不知道为什么，这也是spring boot的一个缺点（菜鸟觉得的）
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars*")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Slf4j
    public static class ZhSecurityConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(name = "jiu.swagger.production", havingValue = "true")
        public ProductionSecurityFilter productionSecurityFilter(SwaggerProperties swaggerProperties) {
            return new ProductionSecurityFilter(swaggerProperties.getProduction());
        }

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(name = "jiu.swagger.basic.enable", havingValue = "true")
        public SecurityBasicAuthFilter securityBasicAuthFilter(SwaggerProperties swaggerProperties) {
            SwaggerProperties.Basic basic = swaggerProperties.getBasic();
            return new SecurityBasicAuthFilter(basic.getEnable(), basic.getUsername(), basic.getPassword());
        }

        @Bean(initMethod = "init")
        @ConditionalOnMissingBean
        @ConditionalOnProperty(name = "jiu.swagger.markdown.enable", havingValue = "true")
        public MarkdownFiles markdownFiles(SwaggerProperties swaggerProperties) {
            return new MarkdownFiles(swaggerProperties.getMarkdown().getBasePath());
        }
    }

}
