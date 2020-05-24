package com.jiu.wallet.eth;

import com.jiu.wallet.AbstractWallet;
import com.jiu.wallet.Deposit;
import org.apache.commons.lang3.StringUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.utils.Convert;

import java.util.ArrayList;
import java.util.List;

public class EthWallet extends AbstractWallet {
    private Web3j web3j;

    @Override
    public List<Object> getTransactions(Long blockNumber, String receiveAddress) throws Exception {
        return null;
    }

    @Override
    public List<Object> getTransactions(Long networkBlockHeight) throws Exception {
        List transactions = new ArrayList();

        EthBlock block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(networkBlockHeight), true).send();

        block.getBlock().getTransactions().stream().forEach(transactionResult -> {
            EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
            Transaction transaction = transactionObject.get();
            if (StringUtils.isNotEmpty(transaction.getTo())
            ) {
                Deposit deposit = new Deposit();
                deposit.setTxid(transaction.getHash());
                deposit.setBlockHeight(transaction.getBlockNumber().longValue());
                deposit.setBlockHash(transaction.getBlockHash());
                deposit.setAmount(Convert.fromWei(transaction.getValue().toString(), Convert.Unit.ETHER));
                deposit.setAddress(transaction.getTo());
                transactions.add(deposit);
            }
        });
        return transactions;
    }
}
