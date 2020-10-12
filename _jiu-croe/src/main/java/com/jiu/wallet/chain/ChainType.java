package com.jiu.wallet.chain;

public class ChainType {
    /**
     * 以太坊
     */
    public final static String ETHEREUM = "ETHEREUM";
    /**
     * 比特币
     */
    public final static String BITCOIN = "BITCOIN";
    /**
     * EOS
     */
    public final static String EOS = "EOS";
    /**
     * 莱特币
     */
    public final static String LITECOIN = "LITECOIN";
    public final static String DASH = "DASH";
    public final static String BITCOINCASH = "BITCOINCASH";
    public final static String BITCOINSV = "BITCOINSV";
    public final static String DOGECOIN = "DOGECOIN";
    public final static String TRON = "TRON";


    public static void validate(String type) throws Exception {
        if (!ETHEREUM.equals(type) &&
                !BITCOIN.equals(type) &&
                !EOS.equals(type) &&
                !LITECOIN.equals(type) &&
                !DASH.equals(type) &&
                !BITCOINSV.equals(type) &&
                !BITCOINCASH.equals(type) &&
                !DOGECOIN.equals(type) &&
                !TRON.equals(type)) {
            throw new Exception("类型不存在");
        }
    }
}