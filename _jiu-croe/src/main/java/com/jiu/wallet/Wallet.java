package com.jiu.wallet;

import java.util.List;

public interface Wallet extends  Runnable{

	/**
	 * 
	 * @param blockNumber
	 *            区块高度
	 * @param receiveAddress
	 *            收钱地址
	 * @return 交易详情
	 */
	public abstract List<Object> getTransactions(Long blockNumber, String receiveAddress) throws  Exception;

	/**
	 * 
	 * @param networkBlockHeight
	 *            区块高度
	 * @return 交易详情
	 */
	public abstract List<Object> getTransactions(Long networkBlockHeight) throws  Exception;

	/**
	 * 获取钱包区块最新高度
	 */
	default  Long getNetworkBlockHeight(){
		return  99999999L;
	}
}