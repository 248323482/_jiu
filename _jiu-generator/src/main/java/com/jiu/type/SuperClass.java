package com.jiu.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SuperClass {

    SUPER_CLASS("com.jiu.base.controller.SuperController", "com.jiu.base.service.SuperService",
            "com.jiu.base.service.SuperServiceImpl", "com.jiu.base.mapper.SuperMapper"),
    SUPER_CACHE_CLASS("com.jiu.base.controller.SuperCacheController", "com.jiu.base.service.SuperCacheService",
            "com.jiu.base.service.SuperCacheServiceImpl", "com.jiu.base.mapper.SuperMapper"),
    NONE("", "", "", "");

    private String controller;
    private String service;
    private String serviceImpl;
    private String mapper;

    public SuperClass setController(String controller) {
        this.controller = controller;
        return this;
    }

    public SuperClass setService(String service) {
        this.service = service;
        return this;
    }

    public SuperClass setMapper(String mapper) {
        this.mapper = mapper;
        return this;
    }

    public SuperClass setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
        return this;
    }
}
