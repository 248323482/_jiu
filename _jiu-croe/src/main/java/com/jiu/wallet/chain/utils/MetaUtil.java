package com.jiu.wallet.chain.utils;

import com.jiu.wallet.chain.ChainType;
import com.jiu.wallet.chain.network.*;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;

/**
 */
public class MetaUtil {
    public static NetworkParameters getNetWork(BIP44Util.Metadata metadata) {
        NetworkParameters network = null;
        if (metadata.getChainType() == null) return MainNetParams.get();
        switch (metadata.getChainType()) {
            case ChainType.LITECOIN:
                network = LitecoinMainNetParams.get();
                break;
            case ChainType.BITCOIN:
                network = metadata.isMainNet() ? MainNetParams.get() : TestNet3Params.get();
                break;
            case ChainType.DASH:
                network = DashMainNetParams.get();
                break;
            case ChainType.BITCOINCASH:
                network = BitcoinCashMainNetParams.get();
                break;
            case ChainType.BITCOINSV:
                network = BitcoinSvMainNetParams.get();
                break;
            case ChainType.DOGECOIN:
                network = DogecoinMainNetParams.get();
                break;
        }
        return network;
    }
}
