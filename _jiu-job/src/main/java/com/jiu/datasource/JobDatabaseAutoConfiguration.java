package com.jiu.datasource;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.jiu.database.datasource.BaseDatabaseConfiguration;
import com.jiu.database.datasource.BaseMybatisConfiguration;
import com.jiu.database.properties.DatabaseProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 配置一些拦截器
 *
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({MybatisPlusProperties.class,DatabaseProperties.class})
public class JobDatabaseAutoConfiguration extends BaseDatabaseConfiguration {

    public JobDatabaseAutoConfiguration(MybatisPlusProperties properties,
                                          DatabaseProperties databaseProperties,
                                          ObjectProvider<Interceptor[]> interceptorsProvider,
                                          ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                          ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                          ResourceLoader resourceLoader,
                                          ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                          ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                          ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider,
                                          ApplicationContext applicationContext) {
        super(properties, databaseProperties, interceptorsProvider, typeHandlersProvider,
                languageDriversProvider, resourceLoader, databaseIdProvider,
                configurationCustomizersProvider, mybatisPlusPropertiesCustomizerProvider, applicationContext);
    }


}