package com.jiu.wallet.usdt;

import com.jiu.wallet.AbstractWallet;
import com.jiu.wallet.Deposit;
import com.jiu.wallet.usdt.rpc.UsdtCoin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsdtWallet extends AbstractWallet {
    private UsdtCoin usdtCoin;

    @Override
    public List<Object> getTransactions(Long blockNumber, String receiveAddress) throws Exception {
        return null;
    }

    @Override
    public List<Object> getTransactions(Long networkBlockHeight) throws Exception {
        List  transactions =new ArrayList();
        List<String> list = usdtCoin.omniListBlockTransactions(networkBlockHeight);
        for (String txid : list) {
            Map<String,Object> map = usdtCoin.omniGetTransactions(txid);
            if(map.get("propertyid") == null)continue;
            String propertyid = map.get("propertyid").toString();
            String txId = map.get("txid").toString();
            String address = String.valueOf(map.get("referenceaddress"));
            Boolean valid =  Boolean.parseBoolean(map.get("valid").toString());
            if(propertyid.equals(UsdtCoin.PROPERTYID_USDT) && valid) {
                    Deposit deposit = new Deposit();
                    deposit.setTxid(txId);
                    deposit.setBlockHash(String.valueOf(map.get("blockhash")));
                    deposit.setAmount(new BigDecimal(map.get("amount").toString()));
                    deposit.setAddress(address);
                    deposit.setBlockHeight(Long.valueOf(String.valueOf(map.get("block"))));
                    transactions.add(deposit);
            }
        }
        return transactions;
    }
}
