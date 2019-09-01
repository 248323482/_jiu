package com.jiu.datasource.rout.core;

import com.jiu.datasource.rout.entity.rout.RoutingRule;

public interface IroutService {

	/**
	 * 获取路由规则
	 * @param tableName
	 * @return
	 */
	RoutingRule getRoutingRule(String tableName);
	/**
	 * sql 修改
	 * @param sql
	 * @return
	 */
	String sqlReplace(String sql);

}
