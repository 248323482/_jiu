package com.jiu.wallet.event;


import org.springframework.context.ApplicationEvent;

/**
 * 钱包接受交易时间
 *
 */
public class WalletEvent extends ApplicationEvent {

    public WalletEvent(Object source) {
        super(source);
    }
}
