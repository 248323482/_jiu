package com.jiu.wallet.usdt;

import com.jiu.wallet.btc.BitcoinTransaction;
import com.jiu.wallet.btc.utils.Hash;
import com.jiu.wallet.btc.utils.NumericUtil;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@Accessors(chain = true)
@Data
@Slf4j
public class UsdtTransaction  extends BitcoinTransaction {

    public void signUsdtTransaction() throws Exception {

        Transaction tran = new Transaction(getNetwork());
        long totalAmount = 0L;

        long needAmount = 546L;

        for (UTXO output : getOutputs()) {
            totalAmount += output.getAmount();
        }

        if (totalAmount < needAmount) {
            throw new Exception("");
        }
        //add change output
        long changeAmount = totalAmount - (needAmount + getFee());
        if (changeAmount >= BitcoinTransaction.DUST_THRESHOLD) {
            tran.addOutput(Coin.valueOf(changeAmount), getChangeAddress());
        }

        String usdtHex = "6a146f6d6e69" + String.format("%016x", 31) + String.format("%016x", getAmount());
        tran.addOutput(Coin.valueOf(0L), new Script(Utils.HEX.decode(usdtHex)));

        //add send to output
        tran.addOutput(Coin.valueOf(needAmount), Address.fromBase58(getNetwork(), getTo()));

        for (UTXO output : getOutputs()) {
            tran.addInput(Sha256Hash.wrap(output.getTxHash()), output.getVout(), new Script(NumericUtil.hexToBytes(output.getScriptPubKey())));
        }

        for (int i = 0; i < getOutputs().size(); i++) {
            UTXO output = getOutputs().get(i);


            ECKey ecKey;
            if (output.getAddress().equals(ECKey.fromPrivate(getPrvKey()).toAddress(getNetwork()).toBase58())) {
                ecKey = ECKey.fromPrivate(getPrvKey());
            } else if (output.getAddress().equals(ECKey.fromPrivate(getPrvKey(), false).toAddress(getNetwork()).toBase58())) {
                ecKey = ECKey.fromPrivate(getPrvKey(), false);
            } else {
                throw new Exception("签名失败");
            }
            TransactionInput transactionInput = tran.getInput(i);
            Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(getNetwork(), output.getAddress()));
            Sha256Hash hash = tran.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
            if (scriptPubKey.isSentToRawPubKey()) {
                transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig));
            } else {
                if (!scriptPubKey.isSentToAddress()) {
                    throw new Exception();
                }
                transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
            }
        }

        String signedHex = NumericUtil.bytesToHex(tran.bitcoinSerialize());
        String txHash = NumericUtil.beBigEndianHex(Hash.sha256(Hash.sha256(signedHex)));
    }
}
