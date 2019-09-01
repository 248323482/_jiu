package com.jiu.datasource.rout.entity.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

/**
 * @version 0.1
 */
@ConfigurationProperties(prefix = "faquir.slave")
@PropertySource("classpath:databases/database-dev.properties")
@EnableConfigurationProperties(DataBaseSlave.class)
public class DataBaseSlave extends AbstractDataBase{


}
