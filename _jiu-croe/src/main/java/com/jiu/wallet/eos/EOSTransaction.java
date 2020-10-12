package com.jiu.wallet.eos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jiu.wallet.Wallet;
import com.jiu.wallet.btc.utils.ByteUtil;
import com.jiu.wallet.btc.utils.Hash;
import com.jiu.wallet.btc.utils.NumericUtil;
import com.jiu.wallet.chain.utils.EOSSignUtil;
import io.eblock.eos4j.OfflineSign;
import io.eblock.eos4j.api.vo.SignParam;
import io.eblock.eos4j.ese.Action;
import io.eblock.eos4j.ese.DataParam;
import io.eblock.eos4j.ese.DataType;
import io.eblock.eos4j.utils.ByteUtils;
import lombok.SneakyThrows;

/**
 * @Author Administrator
 * @create 2020/9/29 15:33
 */
public class EOSTransaction {
    private byte[] txBuf;
    private List<ToSignObj> txsToSign;
    private String from;
    private String to;
    private String quantity;
    private String memo;
    private SignParam signParam;
    private String contractAccount;

    @SneakyThrows
    public String signTransaction() {
        String pubKey = "";
        String priKey = "";
        OfflineSign sign = new OfflineSign();
        return sign.transfer(signParam, priKey, contractAccount,
                from, to, quantity, memo);
    }

    public List<TxMultiSignResult> signTransactions(String chainId, String password, Wallet wallet) {
        byte[] privateKey=null;
        List<TxMultiSignResult> results = new ArrayList<>(txsToSign.size());
        for (ToSignObj toSignObj : txsToSign) {

            byte[] txBuf = NumericUtil.hexToBytes(toSignObj.txHex);
            String transactionID = NumericUtil.bytesToHex(Hash.sha256(txBuf));

            byte[] txChainIDBuf = ByteUtil.concat(NumericUtil.hexToBytes(chainId), txBuf);

            byte[] zeroBuf = new byte[32];
            Arrays.fill(zeroBuf, (byte) 0);
            byte[] fullTxBuf = ByteUtil.concat(txChainIDBuf, zeroBuf);

            byte[] hashedTx = Hash.sha256(fullTxBuf);

            List<String> signatures = new ArrayList<>(toSignObj.publicKeys.size());
            for (String pubKey : toSignObj.publicKeys) {
                String signed =EOSSignUtil.sign(hashedTx, privateKey);
                signatures.add(signed);
            }
            TxMultiSignResult signedResult = new TxMultiSignResult(transactionID, signatures);
            results.add(signedResult);
        }
        return results;
    }

    public static class ToSignObj {
        private String txHex;
        private List<String> publicKeys;

        public String getTxHex() {
            return txHex;
        }

        public void setTxHex(String txHex) {
            this.txHex = txHex;
        }

        public List<String> getPublicKeys() {
            return publicKeys;
        }

        public void setPublicKeys(List<String> publicKeys) {
            this.publicKeys = publicKeys;
        }
    }

    public class TxMultiSignResult {

        public TxMultiSignResult(String txHash, List<String> signed) {
            this.txHash = txHash;
            this.signed = signed;
        }

        String txHash;
        List<String> signed;

        public String getTxHash() {
            return txHash;
        }

        public void setTxHash(String txHash) {
            this.txHash = txHash;
        }

        public List<String> getSigned() {
            return signed;
        }

        public void setSigned(List<String> signed) {
            this.signed = signed;
        }
    }

}
