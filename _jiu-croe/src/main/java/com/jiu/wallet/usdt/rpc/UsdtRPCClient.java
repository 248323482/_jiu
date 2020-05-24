package com.jiu.wallet.usdt.rpc;

import com.jiu.wallet.btc.rpc.BitcoinRPCClient;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

public class UsdtRPCClient extends BitcoinRPCClient implements UsdtCoin {
    public UsdtRPCClient(String rpcUrl) throws MalformedURLException {
        super(rpcUrl);
    }

    @Override
    public List<String> omniListBlockTransactions(Long networkBlockHeight) {
        try {
            return (List<String>) query("omni_listblocktransactions", new Object[]{networkBlockHeight});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, Object> omniGetTransactions(String txid) {
        try {
            return (Map<String, Object>) query("omni_gettransaction", new Object[]{txid});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public BigDecimal omniGetBalance(String address) {
        String balance="0";
        try {
            Integer propertyid = Integer.valueOf(UsdtCoin.PROPERTYID_USDT);
            Map<String, Object> map = (Map<String, Object>) query("omni_getbalance", new Object[] { address, propertyid });
            if (map != null) {
                balance= map.get("balance").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigDecimal(balance);
    }

    @Override
    public BigDecimal getAddressBalance(String ... address) {
        BigDecimal balance = BigDecimal.ZERO;
        try {
            List<Unspent> unspents = this.listUnspent(3, 99999999, address);
            for(Unspent unspent:unspents){
                balance = balance.add(unspent.amount());
            }
            return balance;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return balance;
    }

    @Override
    public String omniSend(String fromaddress, String toaddress, BigDecimal amount) {
        try {
            return query("omni_send", new Object[]{fromaddress, toaddress, Integer.valueOf(UsdtCoin.PROPERTYID_USDT), amount.toPlainString()}).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String omniSend(String fromaddress, String toaddress, BigDecimal amount, BigDecimal bitcoinFee) {
        try {
            return query("omni_send", new Object[]{fromaddress, toaddress, Integer.valueOf(UsdtCoin.PROPERTYID_USDT), amount.toPlainString(), fromaddress, bitcoinFee.toPlainString()}).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
