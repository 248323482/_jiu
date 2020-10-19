package com.jiu.database.datasource.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 */
@ConfigurationProperties(prefix = "database.master")
@PropertySource("classpath:databases/database-dev.properties")
public class DataBaseMaster extends AbstractDataBase{

}
