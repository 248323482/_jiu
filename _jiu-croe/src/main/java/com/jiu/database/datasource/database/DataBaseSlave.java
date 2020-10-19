package com.jiu.database.datasource.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 */
@ConfigurationProperties(prefix = "database.slave")
@PropertySource("classpath:databases/database-dev.properties")
public class DataBaseSlave extends AbstractDataBase{


}
