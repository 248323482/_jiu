package com.jiu.context;

import lombok.Data;

import java.io.Serializable;

/**
 * 线程变量封装的参数
 *
 */
@Data
public class ThreadLocalParam implements Serializable {
    private Boolean boot;
    private String tenant;
    private Long userid;
    private String name;
    private String account;
}
