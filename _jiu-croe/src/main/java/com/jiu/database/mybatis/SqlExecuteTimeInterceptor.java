package com.jiu.database.mybatis;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Connection;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
public class SqlExecuteTimeInterceptor implements Interceptor {


    /**
     * 打印的参数字符串的最大长度
     */
    private final static int MAX_PARAM_LENGTH = 5000;

    /**
     * 记录的最大SQL长度
     */
    private final static int MAX_SQL_LENGTH = 20000;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();

        long startTime = System.currentTimeMillis();
        StatementHandler statementHandler = (StatementHandler) PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        // 读操作 放行
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");

        try {
            return invocation.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            long timeCount = endTime - startTime;

            BoundSql boundSql = statementHandler.getBoundSql();
            Configuration configuration = mappedStatement.getConfiguration();
            Object parameterObject = boundSql.getParameterObject();
            List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();
            // 格式化Sql语句，去除换行符，替换参数
            String sql = formatSQL(boundSql, parameterObject, parameterMappingList, configuration);
            log.error("执行 SQL：[ {} ]执行耗时[ {} ms]", sql, timeCount);
        }
    }


    /**
     * 格式化/美化 SQL语句
     *
     * @param sql                  sql 语句
     * @param parameterObject      参数的Map
     * @param parameterMappingList 参数的List
     * @return 格式化之后的SQL
     */
    private String formatSQL(BoundSql boundSql, Object parameterObject, List<ParameterMapping> parameterMappingList, Configuration configuration) {
        // 输入sql字符串空判断
        String sql = boundSql.getSql();
        if (sql == null || sql.length() == 0) {
            return "";
        }

        if (parameterMappingList.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));

            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappingList) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = replaceValue(sql, getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = replaceValue(sql, getParameterValue(obj));
                    }
                }
            }
        }
        // 美化sql
        sql = beautifySql(sql);
        // 不传参数的场景，直接把sql美化一下返回出去
        if (parameterObject == null || parameterMappingList == null || parameterMappingList.size() == 0) {
            return sql;
        }
        return LimitSQLLength(sql);
    }

    private String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }


    /**
     * 返回限制长度之后的SQL语句
     *
     * @param sql 原始SQL语句
     */
    private String LimitSQLLength(String sql) {
        if (sql == null || sql.length() == 0) {
            return "";
        }
        if (sql.length() > MAX_SQL_LENGTH) {
            return sql.substring(0, MAX_SQL_LENGTH);
        } else {
            return sql;
        }
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }


    /**
     * 替换SQL 中? 所对应的值, 只保留前50个字符
     *
     * @param sql     sql语句
     * @param valueOf ?对应的值
     */
    private String replaceValue(String sql, String valueOf) {
        //超过50个字符只取前50个
        if (valueOf != null && valueOf.length() > MAX_PARAM_LENGTH) {
            valueOf = valueOf.substring(0, MAX_PARAM_LENGTH);
        }
        sql = sql.replaceFirst("\\?", valueOf);
        return sql;
    }

    /**
     * 美化sql
     *
     * @param sql sql语句
     */
    private String beautifySql(String sql) {
        sql = sql.replaceAll("[\\s\n ]+", "  ");
        return sql;
    }


}