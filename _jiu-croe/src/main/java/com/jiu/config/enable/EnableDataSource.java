package com.jiu.config.enable;

import com.jiu.aspect.weave.DataSourcAspect;
import com.jiu.config.DataBaseConfiguration;
import com.jiu.datasource.rout.entity.database.DataBaseMaster;
import com.jiu.datasource.rout.entity.database.DataBaseSlave;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 动态添加数据源,以及读写分离
 * @By 九
 * @Date 2019年8月20日
 * @Time 下午5:16:55
 * @Email budongilt@gmail.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(value = {DataBaseConfiguration.class, DataSourcAspect.class, DataBaseMaster.class, DataBaseSlave.class})
public @interface EnableDataSource {
}