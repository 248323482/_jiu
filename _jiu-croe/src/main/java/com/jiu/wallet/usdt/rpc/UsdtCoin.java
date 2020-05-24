package com.jiu.wallet.usdt.rpc;

import com.jiu.wallet.btc.rpc.Bitcoin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UsdtCoin  extends Bitcoin {
    static  String PROPERTYID_USDT="31";
    List<String> omniListBlockTransactions(Long networkBlockHeight);

    Map<String, Object> omniGetTransactions(String txid);

    BigDecimal omniGetBalance(String address);

    BigDecimal getAddressBalance(String ... address);

    public String omniSend(String fromaddress,String toaddress,BigDecimal amount);

    public String omniSend(String fromaddress, String toaddress, BigDecimal amount, BigDecimal bitcoinFee);

}
