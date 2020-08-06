package com.jiu.database.datasource;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.jiu.api.UserApi;
import com.jiu.context.BaseContextHandler;
import com.jiu.database.injector.MySqlInjector;
import com.jiu.database.mybatis.WriteInterceptor;
import com.jiu.database.mybatis.auth.DataScopeInterceptor;
import com.jiu.database.mybatis.typehandler.FullLikeTypeHandler;
import com.jiu.database.mybatis.typehandler.LeftLikeTypeHandler;
import com.jiu.database.mybatis.typehandler.RightLikeTypeHandler;
import com.jiu.database.parsers.DynamicTableNameParser;
import com.jiu.database.properties.DatabaseProperties;
import com.jiu.database.servlet.TenantWebMvcConfigurer;
import com.jiu.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Mybatis 常用重用拦截器，jiu.database.multiTenantType=任意模式 都需要实例出来
 * <p>
 * 拦截器执行一定是：
 * WriteInterceptor > DataScopeInterceptor > PaginationInterceptor
 *
 */
@Slf4j
@Import({DatabaseProperties.class})
public class BaseMybatisConfiguration {
    protected final DatabaseProperties databaseProperties;

    public BaseMybatisConfiguration(DatabaseProperties databaseProperties) {
        this.databaseProperties = databaseProperties;
    }


    @Slf4j
    @Configuration
    @ConditionalOnProperty(prefix = DatabaseProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
    // @EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
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
     * 分页插件，自动识别数据库类型*
     */
    @Order(5)
    @Bean
    @ConditionalOnMissingBean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        List<ISqlParser> sqlParserList = new ArrayList<>();

        if (this.databaseProperties.getIsBlockAttack()) {
            // 攻击 SQL 阻断解析器 加入解析链
            sqlParserList.add(new BlockAttackSqlParser());
        }
        //动态表名加后缀
        //        DynamicTableNameParser dynamicTableNameParser = new DynamicTableNameParser();
        //            sqlParserList.add(dynamicTableNameParser);

        //动态字段添加字段
        //        TenantSqlParser tenantSqlParser = new TenantSqlParser();
        //        tenantSqlParser.setTenantHandler(new TenantHandler() {
        //            @Override
        //            public Expression getTenantId(boolean where) {
        //                // 该 where 条件 3.2.0 版本开始添加的，用于分区是否为在 where 条件中使用
        //                // 如果是in/between之类的多个tenantId的情况，参考下方示例
        //                return new StringValue(BaseContextHandler.getTenant());
        //            }
        //
        //            @Override
        //            public String getTenantIdColumn() {
        //                return databaseProperties.getTenantIdColumn();
        //            }
        //
        //            @Override
        //            public boolean doTableFilter(String tableName) {
        //                // 这里可以判断是否过滤表
        //                return false;
        //            }
        //        });
        //        sqlParserList.add(tenantSqlParser);

        paginationInterceptor.setSqlParserList(sqlParserList);
        return paginationInterceptor;
    }

    /**
     * Mybatis Plus 注入器
     *
     * @return
     */
    @Bean("myMetaObjectHandler")
    @ConditionalOnMissingBean
    public MetaObjectHandler getMyMetaObjectHandler() {
        DatabaseProperties.Id id = databaseProperties.getId();
        return new MyMetaObjectHandler(id.getWorkerId(), id.getDataCenterId());
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
}
