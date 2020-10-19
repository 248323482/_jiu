package com.jiu.database.datasource;

import com.jiu.database.datasource.database.AbstractDataBase;
import com.jiu.database.datasource.database.DataBaseMaster;
import com.jiu.database.datasource.database.DataBaseSlave;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Getter
@Setter
@ConfigurationProperties("spring.datasource.druid")
public abstract class DynamicDataSource<T extends DataSource> extends AbstractRoutingDataSource {
    private static AtomicInteger a = new AtomicInteger(0);
    private Map<Object, Object> targetDataSources = new HashMap<>();
    private DataBaseMaster dataBaseMaster;
    private DataBaseSlave dataBaseSlave;
    protected volatile String username;
    protected volatile String password;
    protected volatile String url;
    protected volatile String driverClassName;

    /**
     * 创建数据源
     *
     * @param url
     * @param username
     * @param password
     * @return
     */
    public abstract T createDataSource(String url, String username, String password);

    /**
     * 获取链接数据库的配置信息
     *
     * @param databaseType
     * @return
     */
    private AbstractDataBase.DataBase switchDataBaseConifg(DatabaseType databaseType) {
        AbstractDataBase.DataBase dataBase = null;
        if (databaseType == DatabaseType.Master) {
            if (dataBaseMaster == null) {
                return dataBase;
            }
            dataBase = dataBaseMaster.getDatabase();
        } else {
            if (dataBaseSlave == null) {
                return dataBase;
            }
            dataBase = dataBaseSlave.getDatabase();
        }
        log.info("当前初始化配置为 [{}]", dataBase.toString());
        return dataBase;
    }


    protected Object verifyAndInitDataSource() {
        return verifyAndInitDataSource("DEFAULT");
    }

    /**
     * 检查并初始化数据源
     */
    @SneakyThrows
    private Object verifyAndInitDataSource(String dbkey) {
        DatabaseType databaseType = DatabaseType.Master;
        if (DatabaseType.databaseHolder.get() != null) {
            databaseType = DatabaseType.databaseHolder.get();
            DatabaseType.databaseHolder.remove();
        }
        dbkey = String.format("%s_%s", databaseType.name(), dbkey);
        Object obj = this.targetDataSources.get(dbkey);
        if (obj != null && obj instanceof DataSource) {
            if (((DataSource) obj).getConnection().isValid(1000)) {
                return dbkey;
            }
        }
        AbstractDataBase.DataBase dataBase = switchDataBaseConifg(databaseType);
        if (dataBase == null) {
            dataBase = new AbstractDataBase.DataBase();
            dataBase.setDriver(driverClassName);
            dataBase.setPassword(password);
            dataBase.setUrl(url);
            dataBase.setUsername(username);
        }
        String url = dataBase.getUrl();
        String username = dataBase.getUsername();
        String password = dataBase.getPassword();
        // 创建数据源
        T dataSource = createDataSource(url, username, password);
        this.addTargetDataSource(dbkey, dataSource);
        return dbkey;
    }


    /**
     * 添加数据源
     */
    private void addTargetDataSource(String key, T dataSource) {
        this.targetDataSources.put(key, dataSource);
        super.setTargetDataSources(this.targetDataSources);
        super.afterPropertiesSet();
    }

    /**
     * 数据库类型
     */
    public enum DatabaseType {
        Master, Slave;
        private static final ThreadLocal<DatabaseType> databaseHolder = new ThreadLocal<DatabaseType>();
    }
}