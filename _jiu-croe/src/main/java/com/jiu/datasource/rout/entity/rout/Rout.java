package com.jiu.datasource.rout.entity.rout;

public enum Rout {
	/**
	 * 取模
	 */
	Mold,
	/**
	 * 累加
	 */
	Accumulate,
	/**
	 * 二叉数
	 */
	BinaryTree;
	private Integer num;
	
	public void setNum(Integer num){
		this.num =num;
	}
	public String getValue(String v) {
		switch (this) {
		case Mold:
			return moldCaalculate(v);
		case Accumulate:
			return accumulateCalculate(v);
		case BinaryTree:
			return BinaryTreeCalculate(v);
		default:
			return moldCaalculate(v);
		}

	}

	/**
	 * 二叉树算法
	 * @param v
	 * @return
	 */
	private String BinaryTreeCalculate(String v) {
		return v;

	}

	//累加算法
	private String accumulateCalculate(String v) {
		return v;

	}
	
	//取模算法
	private String moldCaalculate(String v) {
		Integer i = (Integer.valueOf(v) / this.num) + 1;
		return String.valueOf(i);

	}

}
