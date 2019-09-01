package com.jiu.config;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.jiu.datasource.rout.core.DynamicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@EnableTransactionManagement
@Slf4j
public class DataBaseConfiguration {
	@Bean
	public DynamicDataSource<?> dynamicDataSource() {
		DataSource dataSource = new DataSource();
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		dataSource.setTargetDataSources(targetDataSources);
		return dataSource;
	}

	@Bean
	public Filter xFilter() {
		StatFilter statFilter = new StatFilter();
		// 多长时间定义为慢sql，这里定义为5s
		statFilter.setSlowSqlMillis(5000);
		// 是否打印出慢日志
		statFilter.setLogSlowSql(true);
		// 是否将日志合并起来
		statFilter.setMergeSql(true);
		return statFilter;
	}

	class DataSource extends DynamicDataSource<DruidDataSource> {
		@Override
		public synchronized DruidDataSource createDataSource(String driverClassName, String url, String username,
				String password) {
			log.info("init 数据源中 ... [{}]", url);
			DruidDataSource dataSource = new DruidDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl(url);
			dataSource.setUsername(username);
			dataSource.setPassword(password);
			dataSource.setInitialSize(4);
			dataSource.setMaxActive(30000);
			dataSource.setMinIdle(0);
			dataSource.setMaxWait(60000);
			dataSource.setValidationQuery("SELECT 1");
			try {
				dataSource.setFilters("stat,wall,log4j");
				dataSource.setProxyFilters(Lists.newArrayList(xFilter()));
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			dataSource.setTestOnBorrow(false);
			dataSource.setTestWhileIdle(true);
			dataSource.setPoolPreparedStatements(false);
			dataSource.setRemoveAbandoned(true);
			try {
				DruidPooledConnection connection = dataSource.getConnection();
				log.info("init 数据源成功    [ {} ]", connection.getCatalog());
			} catch (SQLException e) {

				throw new RuntimeException("数据源初始化失败");
			}
			return dataSource;
		}

	}

}
