package com.jiu.datasource.rout.core;

import com.jiu.datasource.rout.entity.rout.Rout;
import com.jiu.datasource.rout.entity.rout.RoutingRule;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Map.Entry;
@Slf4j
public class RoutServiceImpl implements IroutService {

	@Override
	public RoutingRule getRoutingRule(String tableName) {
		RoutingRule routingRule = new RoutingRule();
		//取模
		routingRule.setRule(Rout.Mold);
		// 5000
		routingRule.setRuleMoldNumber(5000);
		//是否分库
		routingRule.setIsOpenLibrary(false);
		return routingRule;
	}

	@Override
	public String sqlReplace(String sql) {
		log.info(" 原   sql  [{}]",sql.replace("\n", ""));
		String new_sql="";
		Map<String, Object> table = RoutingRule.getTable();
		if (table != null) {
			for (Entry<String, Object> entry : table.entrySet()) {
				new_sql = sql.replace(entry.getKey(), entry.getKey() + entry.getValue());
			}
		}
		return new_sql;
	}

}
