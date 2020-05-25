package com.jiu.wallet.btc;

import com.jiu.wallet.btc.utils.ByteUtil;
import com.jiu.wallet.btc.utils.Hash;
import com.jiu.wallet.btc.utils.NumericUtil;
import lombok.*;
import lombok.experimental.Accessors;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@Accessors(chain = true)
@Data
public class BitcoinTransaction {
    private String to;

    private long amount;

    private List<UTXO> outputs;

    private String memo;

    private long fee;

    private int changeIdx;

    private long locktime = 0;

    private Address changeAddress;

    private NetworkParameters network;

    private List<BigInteger> prvKeys;
    // 2730 sat
    private final static long DUST_THRESHOLD = 2730;

    @Data
    public static class UTXO {
        private String txHash;
        private int vout;
        private long amount;
        private String address;
        private String scriptPubKey;
        private String derivedPath;
        private long sequence = 4294967295L;
    }

    public void signTransaction(String chainID, BigInteger prvKey) throws Exception {
        Transaction tran = new Transaction(network);
        //可交易金額
        long totalAmount = 0;
        for (UTXO output : getOutputs()) {
            totalAmount += output.getAmount();
        }
        if (totalAmount < getAmount()) {
            throw new RuntimeException("余额不足");
        }
        //转出地址金额转换
        tran.addOutput(Coin.valueOf(getAmount()), Address.fromBase58(network, getTo()));
        //改变后的金额
        long changeAmount = totalAmount - (getAmount() + getFee());
        //把剩余金额转入到地址
        if (changeAmount >= DUST_THRESHOLD) {
            tran.addOutput(Coin.valueOf(changeAmount), changeAddress);
        }
        for (UTXO output : getOutputs()) {
            tran.addInput(Sha256Hash.wrap(output.getTxHash()), output.getVout(), new Script(NumericUtil.hexToBytes(output.getScriptPubKey())));
        }
        for (int i = 0; i < getOutputs().size(); i++) {
            UTXO output = getOutputs().get(i);
            BigInteger privateKey = prvKey;
            ECKey ecKey;
            if (output.getAddress().equals(ECKey.fromPrivate(privateKey).toAddress(network).toBase58())) {
                ecKey = ECKey.fromPrivate(privateKey);
            } else if (output.getAddress().equals(ECKey.fromPrivate(privateKey, false).toAddress(network).toBase58())) {
                ecKey = ECKey.fromPrivate(privateKey, false);
            } else {
                throw new Exception("私钥不存在");
            }
            TransactionInput transactionInput = tran.getInput(i);
            Script scriptPubKey = ScriptBuilder.createOutputScript(Address.fromBase58(network, output.getAddress()));
            Sha256Hash hash = tran.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, false);
            ECKey.ECDSASignature ecSig = ecKey.sign(hash);
            TransactionSignature txSig = new TransactionSignature(ecSig, Transaction.SigHash.ALL, false);
            if (scriptPubKey.isSentToRawPubKey()) {
                transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig));
            } else {
                if (!scriptPubKey.isSentToAddress()) {
                    throw new Exception("UNSUPPORT_SEND_TARGET");
                }
                transactionInput.setScriptSig(ScriptBuilder.createInputScript(txSig, ecKey));
            }
        }
        //将signedHex  广播到链上
        String signedHex = NumericUtil.bytesToHex(tran.bitcoinSerialize());
        //交易ID
        String txHash = NumericUtil.beBigEndianHex(Hash.sha256(Hash.sha256(signedHex)));
    }


    public void signSegWitTransaction(String chainId, String password) throws Exception {
        long totalAmount = 0L;
        boolean hasChange = false;
        for (UTXO output : getOutputs()) {
            totalAmount += output.getAmount();
        }
        if (totalAmount < getAmount()) {
            throw new Exception("余额不够");
        }
        long changeAmount = totalAmount - (getAmount() + getFee());
        Address toAddress = Address.fromBase58(network, to);
        byte[] targetScriptPubKey;
        if (toAddress.isP2SHAddress()) {
            targetScriptPubKey = ScriptBuilder.createP2SHOutputScript(toAddress.getHash160()).getProgram();
        } else {
            targetScriptPubKey = ScriptBuilder.createOutputScript(toAddress).getProgram();
        }
        byte[] changeScriptPubKey = ScriptBuilder.createP2SHOutputScript(changeAddress.getHash160()).getProgram();
        byte[] hashPrevouts;
        byte[] hashOutputs;
        byte[] hashSequence;
        try {
            // calc hash prevouts
            UnsafeByteArrayOutputStream stream = new UnsafeByteArrayOutputStream();
            for (UTXO utxo : getOutputs()) {
                TransactionOutPoint outPoint = new TransactionOutPoint(this.network, utxo.vout, Sha256Hash.wrap(utxo.txHash));
                outPoint.bitcoinSerialize(stream);
            }
            hashPrevouts = Sha256Hash.hashTwice(stream.toByteArray());
            // calc hash outputs
            stream = new UnsafeByteArrayOutputStream();
            TransactionOutput targetOutput = new TransactionOutput(this.network, null, Coin.valueOf(amount), toAddress);
            targetOutput.bitcoinSerialize(stream);
            if (changeAmount >= DUST_THRESHOLD) {
                hasChange = true;
                TransactionOutput changeOutput = new TransactionOutput(this.network, null, Coin.valueOf(changeAmount), changeAddress);
                changeOutput.bitcoinSerialize(stream);
            }
            Utils.uint64ToByteStreamLE(BigInteger.valueOf(amount), stream);
            stream.write(new VarInt(targetScriptPubKey.length).encode());
            stream.write(targetScriptPubKey);
            Utils.uint64ToByteStreamLE(BigInteger.valueOf(changeAmount), stream);
            stream.write(new VarInt(changeScriptPubKey.length).encode());
            stream.write(changeScriptPubKey);
            hashOutputs = Sha256Hash.hashTwice(stream.toByteArray());
            // calc hash sequence
            stream = new UnsafeByteArrayOutputStream();
            for (UTXO utxo : getOutputs()) {
                Utils.uint32ToByteStreamLE(utxo.getSequence(), stream);
            }
            hashSequence = Sha256Hash.hashTwice(stream.toByteArray());
            // calc witnesses and redemScripts
            List<byte[]> witnesses = new ArrayList<>();
            List<String> redeemScripts = new ArrayList<>();
            for (int i = 0; i < getOutputs().size(); i++) {
                UTXO utxo = getOutputs().get(i);
                // Metadata.FROM_WIF.equals(wallet.getMetadata().getSource())
                BigInteger prvKey = true ? prvKeys.get(0) : prvKeys.get(i);
                ECKey key = ECKey.fromPrivate(prvKey, true);
                String redeemScript = String.format("0014%s", NumericUtil.bytesToHex(key.getPubKeyHash()));
                redeemScripts.add(redeemScript);
                // calc outpoint
                stream = new UnsafeByteArrayOutputStream();
                TransactionOutPoint txOutPoint = new TransactionOutPoint(this.network, utxo.vout, Sha256Hash.wrap(utxo.txHash));
                txOutPoint.bitcoinSerialize(stream);
                byte[] outpoint = stream.toByteArray();
                // calc scriptCode
                byte[] scriptCode = NumericUtil.hexToBytes(String.format("0x1976a914%s88ac", NumericUtil.bytesToHex(key.getPubKeyHash())));
                // before sign
                stream = new UnsafeByteArrayOutputStream();
                Utils.uint32ToByteStreamLE(2L, stream);
                stream.write(hashPrevouts);
                stream.write(hashSequence);
                stream.write(outpoint);
                stream.write(scriptCode);
                Utils.uint64ToByteStreamLE(BigInteger.valueOf(utxo.getAmount()), stream);
                Utils.uint32ToByteStreamLE(utxo.getSequence(), stream);
                stream.write(hashOutputs);
                Utils.uint32ToByteStreamLE(locktime, stream);
                // hashType 1 = all
                Utils.uint32ToByteStreamLE(1L, stream);
                byte[] hashPreimage = stream.toByteArray();
                byte[] sigHash = Sha256Hash.hashTwice(hashPreimage);
                ECKey.ECDSASignature signature = key.sign(Sha256Hash.wrap(sigHash));
                byte hashType = 0x01;
                // witnesses
                byte[] sig = ByteUtil.concat(signature.encodeToDER(), new byte[]{hashType});
                witnesses.add(sig);
            }
            // the second stream is used to calc the traditional txhash
            UnsafeByteArrayOutputStream[] serialStreams = new UnsafeByteArrayOutputStream[]{
                    new UnsafeByteArrayOutputStream(), new UnsafeByteArrayOutputStream()
            };
            for (int idx = 0; idx < 2; idx++) {
                stream = serialStreams[idx];
                Utils.uint32ToByteStreamLE(2L, stream); // version
                if (idx == 0) {
                    stream.write(0x00); // maker
                    stream.write(0x01); // flag
                }
                // inputs
                stream.write(new VarInt(getOutputs().size()).encode());
                for (int i = 0; i < getOutputs().size(); i++) {
                    UTXO utxo = getOutputs().get(i);
                    stream.write(NumericUtil.reverseBytes(NumericUtil.hexToBytes(utxo.txHash)));
                    Utils.uint32ToByteStreamLE(utxo.getVout(), stream);
                    // the length of byte array that follows, and this length is used by OP_PUSHDATA1
                    stream.write(0x17);
                    // the length of byte array that follows, and this length is used by cutting array
                    stream.write(0x16);
                    stream.write(NumericUtil.hexToBytes(redeemScripts.get(i)));
                    Utils.uint32ToByteStreamLE(utxo.getSequence(), stream);
                }
                // outputs
                // outputs size
                int outputSize = hasChange ? 2 : 1;
                stream.write(new VarInt(outputSize).encode());
                Utils.uint64ToByteStreamLE(BigInteger.valueOf(amount), stream);
                stream.write(new VarInt(targetScriptPubKey.length).encode());
                stream.write(targetScriptPubKey);
                if (hasChange) {
                    Utils.uint64ToByteStreamLE(BigInteger.valueOf(changeAmount), stream);
                    stream.write(new VarInt(changeScriptPubKey.length).encode());
                    stream.write(changeScriptPubKey);
                }
                // the first stream is used to calc the segwit hash
                if (idx == 0) {
                    for (int i = 0; i < witnesses.size(); i++) {
                        //Metadata.FROM_WIF.equals(wallet.getMetadata().getSource())
                        BigInteger prvKey = true ? prvKeys.get(0) : prvKeys.get(i);
                        ECKey ecKey = ECKey.fromPrivate(prvKey);
                        byte[] wit = witnesses.get(i);
                        stream.write(new VarInt(2).encode());
                        stream.write(new VarInt(wit.length).encode());
                        stream.write(wit);
                        stream.write(new VarInt(ecKey.getPubKey().length).encode());
                        stream.write(ecKey.getPubKey());
                    }
                }
                Utils.uint32ToByteStreamLE(locktime, stream);
            }
            byte[] signed = serialStreams[0].toByteArray();
            String signedHex = NumericUtil.bytesToHex(signed);
            String wtxID = NumericUtil.bytesToHex(Sha256Hash.hashTwice(signed));
            wtxID = NumericUtil.beBigEndianHex(wtxID);
            String txHash = NumericUtil.bytesToHex(Sha256Hash.hashTwice(serialStreams[1].toByteArray()));
            txHash = NumericUtil.beBigEndianHex(txHash);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private void collectPrvKeysAndAddress(String segWit, String address, String privKey) {
        this.network = MainNetParams.get();
        if ("".equals("WIF")) {
            changeAddress = Address.fromBase58(network, address);
            BigInteger prvKey = DumpedPrivateKey.fromBase58(network, privKey).getKey().getPrivKey();
            prvKeys = Collections.singletonList(prvKey);
        } else {
            prvKeys = new ArrayList<>(getOutputs().size());
            String xprv = "";
            DeterministicKey xprvKey = DeterministicKey.deserializeB58(xprv, network);
            DeterministicKey changeKey = HDKeyDerivation.deriveChildKey(xprvKey, ChildNumber.ONE);
            DeterministicKey indexKey = HDKeyDerivation.deriveChildKey(changeKey, new ChildNumber(getChangeIdx(), false));
            if ("P2WPKH".equals(segWit)) {
                changeAddress = new SegWitBitcoinAddressCreator(network).fromPrivateKey(indexKey);
            } else {
                changeAddress = indexKey.toAddress(network);
            }
            for (UTXO output : getOutputs()) {
                String derivedPath = output.getDerivedPath().trim();
                String[] pathIdxs = derivedPath.replace('/', ' ').split(" ");
                int accountIdx = Integer.parseInt(pathIdxs[0]);
                int changeIdx = Integer.parseInt(pathIdxs[1]);
                DeterministicKey accountKey = HDKeyDerivation.deriveChildKey(xprvKey, new ChildNumber(accountIdx, false));
                DeterministicKey externalChangeKey = HDKeyDerivation.deriveChildKey(accountKey, new ChildNumber(changeIdx, false));
                prvKeys.add(externalChangeKey.getPrivKey());
            }
        }
    }
}
