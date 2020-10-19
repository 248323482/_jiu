package com.jiu.database.datasource.database;

import lombok.Data;

import java.util.List;

/**
 */
@Data
public abstract class AbstractDataBase {

    private DataBase database;

    @Data
    public static class DataBase {
        String username;
        String password;
        String driver;
        String url;
    }

}
