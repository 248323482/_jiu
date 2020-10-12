package com.jiu.wallet.chain.utils;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.SneakyThrows;
import org.bitcoinj.crypto.ChildNumber;

import java.util.ArrayList;
import java.util.List;

public class BIP44Util {
    public final static String BITCOIN_MAINNET_PATH = "m/44'/0'/0'";
    public final static String BITCOIN_TESTNET_PATH = "m/44'/1'/0'";
    public final static String BITCOIN_SEGWIT_MAIN_PATH = "m/49'/0'/0'";
    public final static String BITCOIN_SEGWIT_TESTNET_PATH = "m/49'/1'/0'";
    public final static String LITECOIN_MAINNET_PATH = "m/44'/2'/0'";
    public final static String DOGECOIN_MAINNET_PATH = "m/44'/3'/0'";
    public final static String DASH_MAINNET_PATH = "m/44'/5'/0'";
    public final static String BITCOINSV_MAINNET_PATH = "m/44'/236'/0'";
    public final static String BITCOINCASH_MAINNET_PATH = "m/44'/145'/0'";
    public final static String ETHEREUM_PATH = "m/44'/60'/0'/0/0";
    public final static String EOS_PATH = "m/44'/194'";
    public final static String EOS_SLIP48 = "m/48'/4'/0'/0'/0',m/48'/4'/1'/0'/0'";
    public final static String EOS_LEDGER = "m/44'/194'/0'/0/0";
    public final static String TRON_PATH = "m/44'/195'/0'/0/0";


    public static ImmutableList<ChildNumber> generatePath(String path) {
        List<ChildNumber> list = new ArrayList<>();
        for (String p : path.split("/")) {
            if ("m".equalsIgnoreCase(p) || "".equals(p.trim())) {
                continue;
            } else if (p.charAt(p.length() - 1) == '\'') {
                list.add(new ChildNumber(Integer.parseInt(p.substring(0, p.length() - 1)), true));
            } else {
                list.add(new ChildNumber(Integer.parseInt(p), false));
            }
        }

        ImmutableList.Builder<ChildNumber> builder = ImmutableList.builder();
        return builder.addAll(list).build();
    }

    public static String getBTCMnemonicPath(String segWit, boolean isMainnet) {
        if (Metadata.P2WPKH.equalsIgnoreCase(segWit)) {
            return isMainnet ? BITCOIN_SEGWIT_MAIN_PATH : BITCOIN_SEGWIT_TESTNET_PATH;
        } else {
            return isMainnet ? BITCOIN_MAINNET_PATH : BITCOIN_TESTNET_PATH;
        }
    }

    @Data
    static
    class Metadata implements Cloneable {
        public static final String FROM_MNEMONIC = "MNEMONIC";
        public static final String FROM_KEYSTORE = "KEYSTORE";
        public static final String FROM_PRIVATE = "PRIVATE";
        public static final String FROM_WIF = "WIF";
        public static final String FROM_NEW_IDENTITY = "NEW_IDENTITY";
        public static final String FROM_RECOVERED_IDENTITY = "RECOVERED_IDENTITY";

        public static final String P2WPKH = "P2WPKH";
        public static final String NONE = "NONE";

        public static final String NORMAL = "NORMAL";

        public static final String HD = "HD";
        public static final String RANDOM = "RANDOM";
        public static final String HD_SHA256 = "HD_SHA256";
        public static final String V3 = "V3";


        private String name;
        private String passwordHint;
        private String chainType;
        private long timestamp;
        private String network;
        private List<String> backup = new ArrayList<>();
        private String source;
        private String mode = NORMAL;
        private String walletType;
        private String segWit;
        private boolean mainNet;

        @SneakyThrows
        @Override
        public Metadata clone() {

            Metadata metadata = null;
            try {
                metadata = (Metadata) super.clone();
            } catch (CloneNotSupportedException ex) {
                throw new Exception("Clone metadata filed");
            }
            metadata.backup = new ArrayList<>(backup);
            return metadata;
        }
    }

}