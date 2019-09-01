package com.jiu.datasource.rout.entity.database;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @version 0.1
 */
@ConfigurationProperties(prefix = "faquir.master")
@EnableConfigurationProperties(DataBaseMaster.class)
@PropertySource("classpath:databases/database-dev.properties")
public class DataBaseMaster extends AbstractDataBase{

}
