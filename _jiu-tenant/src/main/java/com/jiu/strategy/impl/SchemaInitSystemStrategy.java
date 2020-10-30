package com.jiu.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.jiu.dao.InitDbMapper;
import com.jiu.database.properties.DatabaseProperties;
import com.jiu.strategy.InitSystemStrategy;
import com.jiu.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;


/**
 * 初始化系统
 * <p>
 */
@Service("SCHEMA")
@Slf4j
public class SchemaInitSystemStrategy implements InitSystemStrategy {
    /**
     * 需要初始化的sql文件在classpath中的路径
     */
    private final static String SQL_RESOURCE_PATH = "sqls/%s.sql";

    /**
     * 需要初始化的库
     * 可能不同的服务，会连接不同的库
     */
    private final static List<String> INIT_DATABASE_LIST = Arrays.asList("");

    @Autowired
    private DataSource dataSource;
    @Autowired
    private InitDbMapper initDbMapper;
    //默认数据库
    private String defaultDatabase;
    @Autowired
    private DatabaseProperties databaseProperties;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initConnect(String tenant) {
        //创建数据库
        this.initDatabases(tenant);
        //运行脚本初始化表与数据
        ScriptRunner runner = this.getScriptRunner();
        this.initTables(runner, tenant);
        this.initData(runner, tenant);
        // 切换为默认数据源
        this.resetDatabase(runner);

        return true;
    }

    @Override
    public boolean reset(String tenant) {
        ScriptRunner runner = null;
        try {
            runner = getScriptRunner();
            String tenantDatabasePrefix = databaseProperties.getTenantDatabasePrefix();
            useDb(tenant, runner, tenantDatabasePrefix);
            String dataScript = tenantDatabasePrefix + "_" + tenant;
            runner.runScript(Resources.getResourceAsReader(String.format(SQL_RESOURCE_PATH, dataScript)));
        } catch (Exception e) {
            log.error("重置数据失败", e);
            return false;
        }
        return true;
    }

    //创建数据库
    public void initDatabases(String tenant) {
        INIT_DATABASE_LIST.forEach((database) -> this.initDbMapper.createDatabase(StrUtil.join(StrUtil.UNDERLINE, database, tenant)));
    }
   //初始化表
    public void initTables(ScriptRunner runner, String tenant) {
        try {
            for (String database : INIT_DATABASE_LIST) {
                this.useDb(tenant, runner, database);
                //运行sql 建表脚本
                runner.runScript(Resources.getResourceAsReader(String.format(SQL_RESOURCE_PATH, database)));
            }
        } catch (Exception e) {
            log.error("初始化表失败", e);
            throw new RuntimeException("初始化表失败");
        }
    }

    /**
     * 角色表
     * 菜单表
     * 资源表
     *
     * @param tenant
     */
    public void initData(ScriptRunner runner, String tenant) {
        try {
            for (String database : INIT_DATABASE_LIST) {
                this.useDb(tenant, runner, database);
                String dataScript = database + "_data";
                runner.runScript(Resources.getResourceAsReader(String.format(SQL_RESOURCE_PATH, dataScript)));
            }
        } catch (Exception e) {
            log.error("初始化数据失败", e);
            throw new RuntimeException("初始化数据失败");
        }
    }

    public void resetDatabase(ScriptRunner runner) {
        try {
            runner.runScript(new StringReader(StrUtil.format("use {};", this.defaultDatabase)));
        } catch (Exception e) {
            log.error("切换为默认数据源失败", e);
            throw new RuntimeException("切换为默认数据源失败");
        }
    }
    //设置数据库
    public String useDb(String tenant, ScriptRunner runner, String database) {
        String db = StrUtil.join(StrUtil.UNDERLINE, database, tenant);
        runner.runScript(new StringReader(StrUtil.format("use {};", db)));
        return db;
    }

    @SuppressWarnings("AlibabaRemoveCommentedCode")
    public ScriptRunner getScriptRunner() {
        try {
            Connection connection = this.dataSource.getConnection();
            ScriptRunner runner = new ScriptRunner(connection);
            runner.setAutoCommit(false);
            //遇见错误是否停止
            runner.setStopOnError(true);
            //按照那种方式执行 方式一：true则获取整个脚本并执行； 方式二：false则按照自定义的分隔符每行执行；
            runner.setSendFullScript(true);
            // 设置是否输出日志，null不输出日志，不设置自动将日志输出到控制台
            // runner.setLogWriter(null);
            Resources.setCharset(Charset.forName("UTF8"));
            //设置分隔符 runner.setDelimiter(";");
            runner.setFullLineDelimiter(false);
            return runner;
        } catch (Exception ex) {
            throw new RuntimeException("获取连接失败");
        }
    }

   //删除数据库
    @Override
    public boolean delete(List<Long> ids, List<String> tenantCodeList) {
        if (tenantCodeList.isEmpty()) {
            return true;
        }

        INIT_DATABASE_LIST.forEach((prefix) -> {
            tenantCodeList.forEach((tenant) -> {
                String database = new StringBuilder().append(prefix).append(StrPool.UNDERSCORE).append(tenant).toString();
                initDbMapper.dropDatabase(database);
            });
        });

        return true;
    }
}
