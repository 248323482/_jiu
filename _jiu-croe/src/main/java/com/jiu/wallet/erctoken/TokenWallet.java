package com.jiu.wallet.erctoken;

import com.jiu.wallet.AbstractWallet;
import com.jiu.wallet.Deposit;
import com.jiu.wallet.eth.EthConvert;
import org.apache.commons.lang3.StringUtils;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TokenWallet extends AbstractWallet {
    private Web3j web3j;
    private Contract contract;
    @Override
    public List<Object> getTransactions(Long blockNumber, String receiveAddress) throws Exception {
        return null;
    }

    @Override
    public List<Object> getTransactions(Long networkBlockHeight) throws Exception {
        List transactions = new ArrayList();

        EthBlock block = null;
        try {
            block = web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(networkBlockHeight), true).send();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<EthBlock.TransactionResult> transactionResults = block.getBlock().getTransactions();
        for (EthBlock.TransactionResult transactionResult : transactionResults) {
            EthBlock.TransactionObject transactionObject = (EthBlock.TransactionObject) transactionResult;
            Transaction transaction = transactionObject.get();
            try {

                EthGetTransactionReceipt receipt = web3j.ethGetTransactionReceipt(transaction.getHash()).send();
                if (receipt.getTransactionReceipt().get().getStatus().equalsIgnoreCase("0x1")) {


                    String input = transaction.getInput();
                    String cAddress = transaction.getTo();
                    if (StringUtils.isNotEmpty(input) && input.length() >= 138 && contract.getAddress().equalsIgnoreCase(cAddress)) {
                        String data = input.substring(0, 9);
                        data = data + input.substring(17, input.length());
                        Function function = new Function("transfer", Arrays.asList(), Arrays.asList(new TypeReference<Address>() {
                        }, new TypeReference<Uint256>() {
                        }));

                        List<Type> params = FunctionReturnDecoder.decode(data, function.getOutputParameters());
                        // 充币地址
                        String toAddress = params.get(0).getValue().toString();
                        String amount = params.get(1).getValue().toString();
                        //当eventTopic0参数不为空时检查event_log结果，防止低版本的token假充值
                        // 获取充值信息
                        if (StringUtils.isNotEmpty(amount)) {
                            Deposit deposit = new Deposit();
                            deposit.setTxid(transaction.getHash());
                            deposit.setBlockHash(transaction.getBlockHash());
                            deposit.setAmount(EthConvert.fromWei(amount, contract.getUnit()));
                            deposit.setAddress(toAddress);
                            deposit.setTime(Calendar.getInstance().getTime().getTime());
                            deposit.setBlockHeight(transaction.getBlockNumber().longValue());
                            transactions.add(deposit);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return  transactions;
    }
}
