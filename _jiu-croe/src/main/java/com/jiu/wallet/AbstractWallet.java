package com.jiu.wallet;

import com.jiu.utils.SpringUtils;
import com.jiu.wallet.event.WalletEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public abstract class AbstractWallet  implements Wallet{

	/**
	 * 钱包监听开关
	 */
	private Boolean stop =false;
	/**
	 * 检查频率
	 */
	private Long checkInterval = 10000L;

	/**
	 * 同步区块个数
	 */
	private int step = 1;
	/**
	 * 正在同步的区块高度
	 */
	private Long synchronizeBlockHeight = 1L;


	@Override
	public void run() {
		long nextCheck = 0;
		while (!(Thread.interrupted() || stop)) {
			if (nextCheck <= System.currentTimeMillis()) {
				try {
					nextCheck = System.currentTimeMillis() + checkInterval;
					//同步区块
					Synchronize();
				} catch (Exception ex) {
				}
			} else {
				try {
					Thread.sleep(Math.max(nextCheck - System.currentTimeMillis(), 1000));
				} catch (InterruptedException ex) {
				}
			}
		}
	}




	/**
	 * 同步区块
	 */
	private void Synchronize() throws  Exception{
		Long networkBlockHeight = getNetworkBlockHeight(); //最新区块高度
		Long to = networkBlockHeight>(synchronizeBlockHeight+(step-1))?step-1:networkBlockHeight-synchronizeBlockHeight;
		for (int i=0;i<=to;i++){
			this.toTransaction(networkBlockHeight);
		}
	}

	/**
	 * 获取交易
	 */
	private void toTransaction(Long networkBlockHeight) throws  Exception{
		log.error("当前区块同步高度  [{}]   最新高度为 [{}] ",synchronizeBlockHeight,networkBlockHeight);
		List<Object> transactions = getTransactions(synchronizeBlockHeight);
		synchronizeBlockHeight =transactions.size()==0?--synchronizeBlockHeight:++synchronizeBlockHeight;
		transactions.forEach(transaction -> {
			//推送成功的交易
            SpringUtils.publishEvent(new WalletEvent(transaction));
		});


	}




}