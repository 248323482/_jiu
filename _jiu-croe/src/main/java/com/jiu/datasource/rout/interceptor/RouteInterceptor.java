package com.jiu.datasource.rout.interceptor;

import com.jiu.datasource.rout.core.IroutService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Properties;


@Intercepts({
		@Signature(method = "prepare", type = StatementHandler.class, args = { Connection.class, Integer.class }) })
@Slf4j
public class RouteInterceptor implements Interceptor {
	private IroutService routService;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if(routService==null){
			return invocation.proceed();
		}
		StatementHandler statementHandler = realTarget(invocation.getTarget());
		MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
		doTable(statementHandler, metaObject);
		return invocation.proceed();
	}

	private void doTable(StatementHandler handler, MetaObject metaStatementHandler) throws ClassNotFoundException {
		BoundSql boundSql = handler.getBoundSql();
		String originalSql = boundSql.getSql();
		if (originalSql != null && !originalSql.equals("")) {
			metaStatementHandler.setValue("delegate.boundSql.sql", routService.sqlReplace(originalSql));
			Object new_sql = metaStatementHandler.getValue("delegate.boundSql.sql");
			log.info(" 新   sql  [{}]",new_sql.toString().replace("\n", ""));
		}
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {

	}

	public static <T> T realTarget(Object target) {
		if (Proxy.isProxyClass(target.getClass())) {
			MetaObject metaObject = SystemMetaObject.forObject(target);
			return realTarget(metaObject.getValue("h.target"));
		}
		return (T) target;
	}

	public IroutService getRoutService() {
		return routService;
	}

	@Autowired
	public void setRoutService(IroutService routService) {
		this.routService = routService;
	}

}
