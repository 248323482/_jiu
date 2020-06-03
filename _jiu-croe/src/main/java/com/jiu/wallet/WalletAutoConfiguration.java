package com.jiu.wallet;

import com.jiu.wallet.btc.BtcWallet;
import com.jiu.wallet.btc.rpc.Bitcoin;
import com.jiu.wallet.btc.rpc.BitcoinRPCClient;
import com.jiu.wallet.event.WalletListener;
import org.springframework.context.annotation.Bean;
//@Configuration
public class WalletAutoConfiguration {

    /**
     * 初始化区块监听任务
     * @param bitcoin  比特币
     * @return
     */
    @Bean
    public Wallet wallet(){
        BtcWallet btcWallet = new BtcWallet();
        new Thread(btcWallet).start();
        return btcWallet;
    }
    @Bean
    public WalletListener walletListener(){
        return  new WalletListener((transaction)->{
            if(transaction instanceof  Deposit){
                Deposit deposit = (Deposit) transaction;

            }
            System.err.println(transaction.toString());
        });
    }

    //@Bean
    public Bitcoin bitcoin() throws  Exception{
        String rpcUrl ="127.0.0.1";
        return  new BitcoinRPCClient(rpcUrl);
    }
}
