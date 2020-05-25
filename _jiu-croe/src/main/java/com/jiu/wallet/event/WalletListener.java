package com.jiu.wallet.event;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;


/**
 * 保存交易
 *
 */
@Slf4j
@AllArgsConstructor
public class WalletListener {

    private Consumer<Object> consumer;

    @Async
    @Order
    @EventListener(WalletEvent.class)
    public void saveTransacion(WalletEvent event) {
        consumer.accept(event.getSource());
    }

}
