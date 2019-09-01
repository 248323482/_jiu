package com.jiu.aspect.weave;

import com.jiu.annotation.Router;
import com.jiu.datasource.rout.core.IroutService;
import com.jiu.datasource.rout.entity.rout.RoutingRule;
import org.apache.commons.beanutils.BeanUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @By 九 水平分表Aspect
 * @Date 2019年8月20日
 * @Time 上午10:44:07
 * @Email budongilt@gmail.com
 */
@Aspect
public class TableRouterAspect {
	@Autowired
	private IroutService routService;

	@Pointcut("@annotation(com.jiu.annotation.Router)")
	public void tablePointcut() {
	}

	@Before("tablePointcut()")
	public Object router(JoinPoint point) {
		Method method = null;
		try {
			method = getMethod(point);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		Router router = method.getAnnotation(Router.class);
		// 根据路由的属性值
		String routerField = router.routerField();
		// 分表标志
		String tableStyle = router.tableStyle();
		// 分表表名
		String[] tableName = router.tableName();
		// 分表属性的值
		String routerFieldValue = getRouterFieldValue(point, routerField);
		if (StringUtils.isEmpty(routerFieldValue)) {
			//属性值不存在
			throw new RuntimeException("routerFieldValue is null");
		}
		router(tableStyle, routerFieldValue, tableName);
		return point;

	}

	/**
	 * 进行路由计算
	 */
	private void router(String tableStyle, String routerFieldValue, String[] tableNames) {
		RoutingRule routingRule = null;
		// 存放本次serivce查询所有表的坐标
		Map<String, Object> coordinate = new HashMap<>();
		// 获取路由规则
		for (String tableName : tableNames) {
			routingRule = routService.getRoutingRule(tableName);
			if (tableNames.length > 1) {
				// 分库只能单标查询,无法联合查询
				routingRule.setIsOpenLibrary(false);
			}
			// 获取表坐标
			String value = routingRule.getRule().getValue(routerFieldValue);
			coordinate.put(tableName, tableStyle + value);
			RoutingRule.setTable(coordinate);
		}

	}

	/**
	 * 获取分表属性的值
	 * 
	 * @param point
	 * @param routerField
	 * @return
	 */
	private String getRouterFieldValue(JoinPoint point, String routerField) {
		// 获取所有形参
		Object[] args = point.getArgs();
		// 所有的形參名称
		String[] parameterNames = null;
		ParameterNameDiscoverer pnd = new DefaultParameterNameDiscoverer();
		try {
			parameterNames = pnd.getParameterNames(getMethod(point));
			for (int i = 0; i < parameterNames.length; i++) {
				String paramName = parameterNames[i];
				if (paramName.equals(routerField)) {
					return String.valueOf(args[i]);
				}
			}
			for (Object arg : args) {
				try {
					return String.valueOf(BeanUtils.getProperty(arg, routerField));
				} catch (Exception e) {
					continue;
				}
			}

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取执行方法
	 * 
	 * @param point
	 * @return
	 * @throws NoSuchMethodException
	 */
	private Method getMethod(JoinPoint point) throws NoSuchMethodException {
		Signature sig = point.getSignature();
		MethodSignature msig = (MethodSignature) sig;
		return point.getTarget().getClass().getMethod(msig.getName(), msig.getParameterTypes());
	}
}