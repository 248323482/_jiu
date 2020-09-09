package com.jiu.database.properties;

public enum MultiTenantType {
    NONE("非租户模式"),
    COLUMN("字段模式"),
    SCHEMA("独立schema模式"),
    DATASOURCE("独立数据源模式");

    String describe;

    private MultiTenantType(String describe) {
        this.describe = describe;
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(MultiTenantType val) {
        return val == null ? false : this.eq(val.name());
    }

    public String getDescribe() {
        return this.describe;
    }
}