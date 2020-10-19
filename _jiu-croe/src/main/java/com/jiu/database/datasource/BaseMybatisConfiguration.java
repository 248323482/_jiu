package com.jiu.database.datasource;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baidu.fsg.uid.UidGenerator;
import com.baidu.fsg.uid.buffer.RejectedPutBufferHandler;
import com.baidu.fsg.uid.buffer.RejectedTakeBufferHandler;
import com.baidu.fsg.uid.impl.CachedUidGenerator;
import com.baidu.fsg.uid.impl.DefaultUidGenerator;
import com.baidu.fsg.uid.impl.HutoolUidGenerator;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.*;
import com.jiu.api.UserApi;
import com.jiu.context.BaseContextHandler;
import com.jiu.database.injector.MySqlInjector;
import com.jiu.database.mybatis.WriteInterceptor;
import com.jiu.database.mybatis.auth.DataScopeInterceptor;
import com.jiu.database.mybatis.typehandler.FullLikeTypeHandler;
import com.jiu.database.mybatis.typehandler.LeftLikeTypeHandler;
import com.jiu.database.mybatis.typehandler.RightLikeTypeHandler;
import com.jiu.database.plugins.SchemaInterceptor;
import com.jiu.database.properties.DatabaseProperties;
import com.jiu.database.properties.MultiTenantType;
import com.jiu.database.servlet.TenantWebMvcConfigurer;
import com.jiu.uid.service.DisposableWorkerIdAssigner;
import com.jiu.utils.SpringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;

/**
 * Mybatis 常用重用拦截器，jiu.database.multiTenantType=任意模式 都需要实例出来
 * <p>
 * 拦截器执行一定是：
 * WriteInterceptor > DataScopeInterceptor > PaginationInterceptor
 *
 */
@Slf4j
public  class BaseMybatisConfiguration {
    protected final DatabaseProperties databaseProperties;

    public BaseMybatisConfiguration(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }


    @Slf4j
    @Configuration
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class DataSourceConfiguration {
        public DataSourceConfiguration() {
            log.warn("DataSource 已开启........");
        }
    }
    /**
     * 演示环境权限拦截器
     *
     * @return
     */
    @Bean
    @Order(15)
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "jiu.database.isNotWrite", havingValue = "true")
    public WriteInterceptor getWriteInterceptor() {
        log.warn("数据禁止写入........[{}}",databaseProperties.getIsNotWrite());
        return new WriteInterceptor();
    }


    /**
     * 数据权限插件
     *
     * @return DataScopeInterceptor
     */
    @Order(10)
    @Bean
    @ConditionalOnProperty(name = "jiu.database.isDataScope", havingValue = "true")
    public DataScopeInterceptor dataScopeInterceptor() {
        log.warn("数据权限已经开启........[{}}",databaseProperties.getIsDataScope());
        return new DataScopeInterceptor((userId) -> SpringUtils.getBean(UserApi.class).getDataScopeById(userId));
    }


    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
     * <p>
     * 注意:
     * 如果内部插件都是使用,需要注意顺序关系,建议使用如下顺序
     * 多租户插件,动态表名插件
     * 分页插件,乐观锁插件
     * sql性能规范插件,防止全表更新与删除插件
     * 总结: 对sql进行单次改造的优先放入,不对sql进行改造的最后放入
     * <p>
     * 参考：
     * https://mybatis.plus/guide/interceptor.html#%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F-%E4%BB%A5%E5%88%86%E9%A1%B5%E6%8F%92%E4%BB%B6%E4%B8%BE%E4%BE%8B
     */
    @Bean
    @Order(5)
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        if (MultiTenantType.SCHEMA.eq(this.databaseProperties.getMultiTenantType())) {
            // SCHEMA 动态表名插件
            SchemaInterceptor dtni = new SchemaInterceptor(databaseProperties.getTenantDatabasePrefix());
            interceptor.addInnerInterceptor(dtni);
            log.info("已启用 SCHEMA模式");
        } else if (MultiTenantType.COLUMN.eq(this.databaseProperties.getMultiTenantType())) {
            log.info("已启用 字段模式");
            // COLUMN 模式 多租户插件
            TenantLineInnerInterceptor tli = new TenantLineInnerInterceptor();
            tli.setTenantLineHandler(new TenantLineHandler() {
                @Override
                public String getTenantIdColumn() {
                    return databaseProperties.getTenantIdColumn();
                }

                @Override
                public boolean ignoreTable(String tableName) {
                    return false;
                }

                @Override
                public Expression getTenantId() {
                    return new StringValue(BaseContextHandler.getTenant());
                }
            });
            interceptor.addInnerInterceptor(tli);
        }

        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 单页分页条数限制
        paginationInterceptor.setMaxLimit(databaseProperties.getLimit());
        // 数据库类型
        paginationInterceptor.setDbType(databaseProperties.getDbType());
        // 溢出总页数后是否进行处理
        paginationInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInterceptor);

        //防止全表更新与删除插件
        if (databaseProperties.getIsBlockAttack()) {
            BlockAttackInnerInterceptor baii = new BlockAttackInnerInterceptor();
            interceptor.addInnerInterceptor(baii);
        }
        // sql性能规范插件
        if (databaseProperties.getIsIllegalSql()) {
            IllegalSQLInnerInterceptor isi = new IllegalSQLInnerInterceptor();
            interceptor.addInnerInterceptor(isi);
        }

        return interceptor;
    }

    /**
     * Mybatis Plus 注入器
     *
     * @return
     */
    @Bean("myMetaObjectHandler")
    @ConditionalOnMissingBean
    public MetaObjectHandler getMyMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("'DEFAULT'.equals('${jiu.database.id-type:DEFAULT}')|| 'CACHE'.equals('${jiu.database.id-type:DEFAULT}')")
    public DisposableWorkerIdAssigner disposableWorkerIdAssigner() {
        return new DisposableWorkerIdAssigner();
    }
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "id-type", havingValue = "DEFAULT", matchIfMissing = true)
    public UidGenerator getDefaultUidGenerator(DisposableWorkerIdAssigner disposableWorkerIdAssigner) {
        DefaultUidGenerator uidGenerator = new DefaultUidGenerator();
        BeanUtil.copyProperties(databaseProperties.getDefaultId(), uidGenerator);
        uidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        return uidGenerator;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "id-type", havingValue = "CACHE")
    public UidGenerator getCacheUidGenerator(DisposableWorkerIdAssigner disposableWorkerIdAssigner) {
        CachedUidGenerator uidGenerator = new CachedUidGenerator();
        DatabaseProperties.CacheId cacheId = databaseProperties.getCacheId();
        BeanUtil.copyProperties(cacheId, uidGenerator);
        if (cacheId.getRejectedPutBufferHandlerClass() != null) {
            RejectedPutBufferHandler rejectedPutBufferHandler = ReflectUtil.newInstance(cacheId.getRejectedPutBufferHandlerClass());
            uidGenerator.setRejectedPutBufferHandler(rejectedPutBufferHandler);
        }
        if (cacheId.getRejectedTakeBufferHandlerClass() != null) {
            RejectedTakeBufferHandler rejectedTakeBufferHandler = ReflectUtil.newInstance(cacheId.getRejectedTakeBufferHandlerClass());
            uidGenerator.setRejectedTakeBufferHandler(rejectedTakeBufferHandler);
        }
        uidGenerator.setWorkerIdAssigner(disposableWorkerIdAssigner);
        return uidGenerator;
    }


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "id-type", havingValue = "HUTOOL")
    public UidGenerator getHutoolUidGenerator() {
        DatabaseProperties.HutoolId id = databaseProperties.getHutoolId();
        return new HutoolUidGenerator(id.getWorkerId(), id.getDataCenterId());
    }


    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=leftLike} 类型的参数
     * 用于左模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=leftLike}
     *
     * @return
     */
    @Bean
    public LeftLikeTypeHandler getLeftLikeTypeHandler() {
        return new LeftLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=rightLike} 类型的参数
     * 用于右模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=rightLike}
     *
     * @return
     */
    @Bean
    public RightLikeTypeHandler getRightLikeTypeHandler() {
        return new RightLikeTypeHandler();
    }

    /**
     * Mybatis 自定义的类型处理器： 处理XML中  #{name,typeHandler=fullLike} 类型的参数
     * 用于全模糊查询时使用
     * <p>
     * eg：
     * and name like #{name,typeHandler=fullLike}
     *
     * @return
     */
    @Bean
    public FullLikeTypeHandler getFullLikeTypeHandler() {
        return new FullLikeTypeHandler();
    }


    @Bean
    @ConditionalOnMissingBean
    public MySqlInjector getMySqlInjector() {
        return new MySqlInjector();
    }

    @Bean
    public TenantWebMvcConfigurer getTenantWebMvcConfigurer() {
        return new TenantWebMvcConfigurer();
    }



    /**
     * 分页拦截器之前的插件
     *
     * @return
     */
    protected List<InnerInterceptor> getPaginationAfterInnerInterceptor() {
        return Collections.emptyList();
    }

    /**
     * 分页拦截器之后的插件
     *
     * @return
     */
    protected List<InnerInterceptor> getPaginationBeforeInnerInterceptor() {
        return Collections.emptyList();
    }

    /**
     * mybatis-plus 3.4.0开始采用新的分页插件,一缓和二缓遵循mybatis的规则,
     * 需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题
     * (该属性会在旧插件移除后一同移除)
     */
    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseDeprecatedExecutor(false);
    }
}
