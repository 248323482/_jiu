package com.jiu.database.datasource.defaults;

import cn.hutool.core.util.ArrayUtil;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.jiu.database.datasource.BaseDatabaseConfiguration;
import com.jiu.database.properties.DatabaseProperties;
import com.p6spy.engine.spy.P6DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.List;

/**
 * 数据库& 事务& MyBatis & Mp 配置
 * zuihou.database.multiTenantType != DATASOURCE时， 子类需要继承它，并让程序启动时加载
 * <p>
 * 注意：BaseDatabaseConfiguration 和 DynamicDataSourceAutoConfiguration 只能同时加载一个
 *
 * <p>
 * 不使用多数据源模式时，一个服务如何配置多数据源？
 * <p>
 * 1. 本类的子类的 @MapperScan(basePackages = {"com.jiu"}) 修改为  @MapperScan(basePackages = {"com.jiu.product.dao"})
 * 2. 修改本类的子类的 @ConfigurationProperties(prefix = "spring.datasource.druid") 为 @ConfigurationProperties(prefix = "spring.datasource.druid.master")
 * 3. 复制该类 重命名为 SlaveDatabaseAutoConfiguration
 * 4. 修改 DATABASE_PREFIX 为 slave
 * 5. 修改 @ConfigurationProperties(prefix = "spring.datasource.druid") 为 @ConfigurationProperties(prefix = "spring.datasource.druid.slave")
 * 6. SlaveDatabaseAutoConfiguration 中的 basePackages = {"com.jiu"}, 修改为  basePackages = {"com.jiu.order.dao"} # 这个路径根据你的情况修改
 * 7. mysql.yml 中增加2个数据源的配置 spring.database.druid.master 和 spring.database.druid.slave
 * <p>
 * 完成上述修改后， 位于com.jiu.product.dao 包下的dao 将操作  master 库， com.jiu.order.dao中的dao 将操作slave库
 *
 */
@Slf4j
public abstract class MasterDatabaseConfiguration extends BaseDatabaseConfiguration {
    /**
     * 每个数据源配置不同即可
     */
    public final static String DATABASE_PREFIX = "master";

    public MasterDatabaseConfiguration(MybatisPlusProperties properties,
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

    @Bean(DATABASE_PREFIX + "SqlSessionTemplate")
    public SqlSessionTemplate getSqlSessionTemplate(@Qualifier(DATABASE_PREFIX + "SqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        ExecutorType executorType = this.properties.getExecutorType();
        if (executorType != null) {
            return new SqlSessionTemplate(sqlSessionFactory, executorType);
        } else {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
    }

    /**
     * 数据源信息
     *
     * @return
     */
    @Primary
    @Bean(name = DATABASE_PREFIX + "DruidDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = DATABASE_PREFIX + "DataSource")
    public DataSource dataSource(@Qualifier(DATABASE_PREFIX + "DruidDataSource") DataSource dataSource) {
        if (ArrayUtil.contains(DEV_PROFILES, this.profiles)) {
            return new P6DataSource(dataSource);
        } else {
            return dataSource;
        }
    }

    /**
     * mybatis Sql Session 工厂
     *
     * @return
     * @throws Exception
     */
    @Bean(DATABASE_PREFIX + "SqlSessionFactory")
    public SqlSessionFactory getSqlSessionFactory(@Qualifier(DATABASE_PREFIX + "DataSource") DataSource dataSource) throws Exception {
        return super.sqlSessionFactory(dataSource);
    }

    /**
     * 数据源事务管理器
     *
     * @return
     */
    @Bean(name = DATABASE_PREFIX + "TransactionManager")
    public DataSourceTransactionManager dsTransactionManager(@Qualifier(DATABASE_PREFIX + "DataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}