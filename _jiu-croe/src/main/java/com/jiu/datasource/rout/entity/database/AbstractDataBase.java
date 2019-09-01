package com.jiu.datasource.rout.entity.database;

import lombok.Data;

import java.util.List;

/**
 * @version 0.1
 */
@Data
public abstract class AbstractDataBase {

    private DataBase[][] database;

    @Data
    public static class DataBase {
        String username;
        String password;
        String driver;
        String url;
    }

}
