package com.jiu.wallet.chain.creator;

import com.jiu.wallet.btc.utils.NumericUtil;
import lombok.Data;
import org.bitcoinj.core.*;

@Data
public class BitcoinAddressCreator {
    private NetworkParameters networkParameters;

    public BitcoinAddressCreator(NetworkParameters network) {
        this.networkParameters=network;
    }

    public String fromPrivateKey(String prvKeyHex) {
        ECKey key;
        if (prvKeyHex.length() == 51 || prvKeyHex.length() == 52) {
            DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameters, prvKeyHex);
            key = dumpedPrivateKey.getKey();
            if (!key.isCompressed()) {
            }
        } else {
            key = ECKey.fromPrivate(NumericUtil.hexToBytes(prvKeyHex), true);
        }
        return calcSegWitAddress(key.getPubKeyHash());
    }

    public String fromPrivateKey(byte[] prvKeyBytes) {
        ECKey key = ECKey.fromPrivate(prvKeyBytes, true);
        return calcSegWitAddress(key.getPubKeyHash());
    }

    private String calcSegWitAddress(byte[] pubKeyHash) {
        String redeemScript = String.format("0x0014%s", NumericUtil.bytesToHex(pubKeyHash));
        return Address.fromP2SHHash(networkParameters, Utils.sha256hash160(NumericUtil.hexToBytes(redeemScript))).toBase58();
    }

    public Address fromPrivateKey(ECKey ecKey) {
        String redeemScript = String.format("0x0014%s", NumericUtil.bytesToHex(ecKey.getPubKeyHash()));
        return Address.fromP2SHHash(networkParameters, Utils.sha256hash160(NumericUtil.hexToBytes(redeemScript)));
    }
}