package com.jiu.database.datasource;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import com.baomidou.mybatisplus.autoconfigure.SpringBootVFS;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.google.common.collect.Lists;
import com.jiu.database.properties.DatabaseProperties;
import com.p6spy.engine.spy.P6DataSource;
import io.seata.rm.datasource.DataSourceProxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.Advisor;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

/**
 * 数据库& 事务& MyBatis & Mp 配置
 */
@Slf4j
@EnableConfigurationProperties({MybatisPlusProperties.class,DatabaseProperties.class})
@MapperScan(
        basePackages = {"com.jiu.dao","com.jiu.uid.dao"},
        annotationClass = Repository.class )
public  class BaseDatabaseConfiguration implements InitializingBean {
    /**
     * 测试环境
     */
    protected static final String[] DEV_PROFILES = new String[]{"dev"};
    @Value("${spring.profiles.active:dev}")
    protected String profiles;
    private static final List<Class<? extends Annotation>> AOP_POINTCUT_ANNOTATIONS = new ArrayList<>(2);
    static {
        //事务在controller层开启。
        AOP_POINTCUT_ANNOTATIONS.add(RestController.class);
        AOP_POINTCUT_ANNOTATIONS.add(Controller.class);
    }

    protected final MybatisPlusProperties properties;

    protected final DatabaseProperties databaseProperties;
    private final Interceptor[] interceptors;
    private final TypeHandler[] typeHandlers;
    private final LanguageDriver[] languageDrivers;
    private final ResourceLoader resourceLoader;
    private final DatabaseIdProvider databaseIdProvider;
    private final List<ConfigurationCustomizer> configurationCustomizers;
    private final List<MybatisPlusPropertiesCustomizer> mybatisPlusPropertiesCustomizers;
    private final ApplicationContext applicationContext;

    public BaseDatabaseConfiguration(MybatisPlusProperties properties,
                                     DatabaseProperties databaseProperties,
                                     ObjectProvider<Interceptor[]> interceptorsProvider,
                                     ObjectProvider<TypeHandler[]> typeHandlersProvider,
                                     ObjectProvider<LanguageDriver[]> languageDriversProvider,
                                     ResourceLoader resourceLoader,
                                     ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                     ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider,
                                     ObjectProvider<List<MybatisPlusPropertiesCustomizer>> mybatisPlusPropertiesCustomizerProvider,
                                     ApplicationContext applicationContext) {
        this.properties = properties;
        this.databaseProperties = databaseProperties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.typeHandlers = typeHandlersProvider.getIfAvailable();
        this.languageDrivers = languageDriversProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
        this.mybatisPlusPropertiesCustomizers = mybatisPlusPropertiesCustomizerProvider.getIfAvailable();
        this.applicationContext = applicationContext;
    }


    @Bean
    public SqlSessionTemplate getSqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
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
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        // return DruidDataSourceBuilder.create().build();
        return  new DefaultDataSource();
    }

    private class DefaultDataSource extends DynamicDataSource<DruidDataSource> implements InitializingBean{
        public void afterPropertiesSet() {
            super.verifyAndInitDataSource();
        }
        @Override
        public synchronized DruidDataSource createDataSource(String url, String username,
                                                             String password) {
            log.info("init 数据源中 ... [{}]", url);
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setInitialSize(10);
            dataSource.setMaxActive(500);
            dataSource.setMinIdle(10);
            dataSource.setMaxWait(60000);

            try {
                dataSource.setFilters("stat,wall");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            dataSource.setTestOnBorrow(false);
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnReturn(false);
            dataSource.setPoolPreparedStatements(true);
            dataSource.setTimeBetweenEvictionRunsMillis(60000);
            dataSource.setMinEvictableIdleTimeMillis(300000);
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
            dataSource.setRemoveAbandoned(true);
            dataSource.setValidationQuery("SELECT 'x'");
            try {
                DruidPooledConnection connection = dataSource.getConnection();
                log.info("init 数据源成功    [ {} ]", connection.getCatalog());
            } catch (SQLException e) {

                throw new RuntimeException("数据源初始化失败");
            }
            return dataSource;
        }


        @Override
        protected Object determineCurrentLookupKey() {
            return super.verifyAndInitDataSource();
        }
    }



    @Bean
    public DataSource dataSourceProxy( DataSource dataSource) {
        DataSource dataSourceWrapper = dataSource;
        if (ArrayUtil.contains(DEV_PROFILES, this.profiles)) {
            dataSourceWrapper = new P6DataSource(dataSource);
        }
        if (databaseProperties.getIsSeata()) {
            dataSourceWrapper = new DataSourceProxy(dataSourceWrapper);
        }
        return dataSourceWrapper;
    }

    protected TransactionAttributeSource transactionAttributeSource() {
        /*当前存在事务就使用当前事务，当前不存在事务就创建一个新的事务*/
        RuleBasedTransactionAttribute requiredTx = new RuleBasedTransactionAttribute();
        requiredTx.setRollbackRules(Collections.singletonList(new RollbackRuleAttribute(Throwable.class)));
        requiredTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        requiredTx.setTimeout(this.databaseProperties.getTxTimeout());
        Map<String, TransactionAttribute> txMap = new HashMap<>(this.databaseProperties.getTransactionAttributeList().size() + 5);
        this.databaseProperties.getTransactionAttributeList().forEach((key) -> txMap.put(key, requiredTx));

        RuleBasedTransactionAttribute readOnlyTx = new RuleBasedTransactionAttribute();
        readOnlyTx.setReadOnly(true);
        readOnlyTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
        //除了上面的事物以外，都走只读事物
        txMap.put("*", readOnlyTx);
        NameMatchTransactionAttributeSource txTransactionAttributeSource = new NameMatchTransactionAttributeSource();
        txTransactionAttributeSource.setNameMap(txMap);
        return txTransactionAttributeSource;
    }
    protected Advisor txAdviceAdvisor(TransactionInterceptor ti) {
        return new DefaultPointcutAdvisor(new Pointcut() {
            @Override
            public MethodMatcher getMethodMatcher() {
                return MethodMatcher.TRUE;
            }

            @Override
            public ClassFilter getClassFilter() {
                return (clazz) -> {
                    if (!clazz.getName().startsWith(BaseDatabaseConfiguration.this.databaseProperties.getTransactionScanPackage())) {
                        return false;
                    }
                    for (Class<? extends Annotation> aop : AOP_POINTCUT_ANNOTATIONS) {
                        if (clazz.getAnnotation(aop) == null) {
                            continue;
                        }
                        log.debug("允许带事务的类为：{}", clazz);
                        return true;
                    }
                    return false;
                };
            }
        }, ti);
    }

    @Override
    public void afterPropertiesSet() {
        if (!CollectionUtils.isEmpty(this.mybatisPlusPropertiesCustomizers)) {
            this.mybatisPlusPropertiesCustomizers.forEach(i -> i.customize(this.properties));
        }
        this.checkConfigFileExists();
    }

    private void checkConfigFileExists() {
        if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
            Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
            Assert.state(resource.exists(),
                    "Cannot find config location: " + resource + " (please add config file or check your Mybatis configuration)");
        }
    }
    @Bean
    protected SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        // 使用 MybatisSqlSessionFactoryBean 而不是 SqlSessionFactoryBean
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        applyConfiguration(factory);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (this.properties.getTypeAliasesSuperType() != null) {
            factory.setTypeAliasesSuperType(this.properties.getTypeAliasesSuperType());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }
        if (!ObjectUtils.isEmpty(this.typeHandlers)) {
            factory.setTypeHandlers(this.typeHandlers);
        }
        Resource[] mapperLocations = this.properties.resolveMapperLocations();
        if (!ObjectUtils.isEmpty(mapperLocations)) {
            factory.setMapperLocations(mapperLocations);
        }
        //  修改源码支持定义 TransactionFactory
        this.getBeanThen(TransactionFactory.class, factory::setTransactionFactory);

        //  对源码做了一定的修改(因为源码适配了老旧的mybatis版本,但我们不需要适配)
        Class<? extends LanguageDriver> defaultLanguageDriver = this.properties.getDefaultScriptingLanguageDriver();
        if (!ObjectUtils.isEmpty(this.languageDrivers)) {
            factory.setScriptingLanguageDrivers(this.languageDrivers);
        }
        Optional.ofNullable(defaultLanguageDriver).ifPresent(factory::setDefaultScriptingLanguageDriver);

        //  自定义枚举包
        if (StringUtils.hasLength(this.properties.getTypeEnumsPackage())) {
            factory.setTypeEnumsPackage(this.properties.getTypeEnumsPackage());
        }
        //  此处必为非 NULL
        GlobalConfig globalConfig = this.properties.getGlobalConfig();
        //  注入填充器
        this.getBeanThen(MetaObjectHandler.class, globalConfig::setMetaObjectHandler);
        //  注入主键生成器
        this.getBeanThen(IKeyGenerator.class, i -> globalConfig.getDbConfig().setKeyGenerator(i));
        //  注入sql注入器
        this.getBeanThen(ISqlInjector.class, globalConfig::setSqlInjector);
        //  注入ID生成器
        this.getBeanThen(IdentifierGenerator.class, globalConfig::setIdentifierGenerator);
        //  设置 GlobalConfig 到 MybatisSqlSessionFactoryBean
        factory.setGlobalConfig(globalConfig);
        return factory.getObject();
    }
    /**
     * 检查spring容器里是否有对应的bean,有则进行消费
     *
     * @param clazz    class
     * @param consumer 消费
     * @param <T>      泛型
     */
    private <T> void getBeanThen(Class<T> clazz, Consumer<T> consumer) {
        if (this.applicationContext.getBeanNamesForType(clazz, false, false).length > 0) {
            consumer.accept(this.applicationContext.getBean(clazz));
        }
    }

    private void applyConfiguration(MybatisSqlSessionFactoryBean factory) {
        // 这里一定要复制一次， 否则多数据源时，会导致拦截器等执行多次
        MybatisConfiguration newConfiguration = this.properties.getConfiguration();
        MybatisConfiguration configuration = new MybatisConfiguration();
        BeanUtil.copyProperties(newConfiguration, configuration);

        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
    }

    @Bean
    public DataSourceTransactionManager dsTransactionManager( DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


}
