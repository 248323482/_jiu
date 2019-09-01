package com.jiu.datasource.rout.entity.rout;

import java.io.Serializable;
import java.util.Map;

public class RoutingRule implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final ThreadLocal<Map<String,Object>> tables= new ThreadLocal<>();
	//路由取模数
	private Integer ruleMoldNumber;
	//是否开启分库
	private Boolean isOpenLibrary;
	//路由规则
	private Rout rule;
	public Integer getRuleMoldNumber() {
		return ruleMoldNumber;
	}
	public void setRuleMoldNumber(Integer ruleMoldNumber) {
		this.rule.setNum(ruleMoldNumber);
		this.ruleMoldNumber = ruleMoldNumber;
	}
	public Boolean getIsOpenLibrary() {
		return isOpenLibrary;
	}
	public void setIsOpenLibrary(Boolean isOpenLibrary) {
		this.isOpenLibrary = isOpenLibrary;
	}
	public Rout getRule() {
		return rule;
	}
	public void setRule(Rout rule) {
		this.rule = rule;
	} 
	
	public static void setTable(Map<String, Object> s) {
		
		tables.set(s);
	}

	public static Map<String, Object> getTable() {
		return  tables.get();
	}

	public static void clearTable() {
		tables.remove();
	}


	
}
