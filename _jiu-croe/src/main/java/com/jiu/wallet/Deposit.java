package com.jiu.wallet;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Deposit {
    /**
     * 币种
     */
    private String coin;
    /**
     * 交易ID
     */
    private String txid;
    /**
     * 区块高度
     */
    private Long blockHeight;
    /**
     * 区块hash
     */
    private String blockHash;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 收钱地址
     */
    private String address;
    /**
     * 时间
     */
    private Long time;
}
