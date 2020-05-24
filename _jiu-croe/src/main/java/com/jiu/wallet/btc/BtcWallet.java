package com.jiu.wallet.btc;

import com.jiu.wallet.AbstractWallet;
import com.jiu.wallet.Deposit;
import com.jiu.wallet.btc.rpc.Bitcoin;
import lombok.*;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 比特币钱包
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@Accessors(chain = true)
public class BtcWallet extends AbstractWallet {
    private Bitcoin bitcoin;
    @Override
    public List<Object> getTransactions(Long blockNumber, String receiveAddress) {
        return null;
    }

    @Override
    public List<Object> getTransactions(Long networkBlockHeight) throws  Exception{
        List transactions = new ArrayList();
        try {
            String blockHash = bitcoin.getBlockHash(networkBlockHeight);
            Bitcoin.Block block = bitcoin.getBlock(blockHash);
            List<String> txids = block.tx();
            for(String txid:txids){
                Bitcoin.RawTransaction transaction =  bitcoin.getRawTransaction(txid);
                List<Bitcoin.RawTransaction.Out> outs = transaction.vOut();
                if(outs != null) {
                    for (Bitcoin.RawTransaction.Out out : outs) {
                        if (out.scriptPubKey() != null) {
                            List<String> addresses = out.scriptPubKey().addresses();
                            if(addresses != null && addresses.size() > 0) {
                                //入金地址
                                String address_ = addresses.get(0);
                                //如今金额
                                BigDecimal amount = new BigDecimal(out.value());
                                Deposit deposit = new Deposit();
                                deposit.setTxid(transaction.txId());
                                deposit.setBlockHeight((long) block.height());
                                deposit.setBlockHash(transaction.blockHash());
                                deposit.setAmount(amount);
                                deposit.setAddress(address_);
                                deposit.setTime(transaction.time().getTime());
                                transactions.add(deposit);
                            }
                        }
                    }
                    return transactions;
                }
            }
        }catch (Exception e){

        }
        transactions.add("test");
        return transactions;
    }
}
