package com.jiu.aspect.weave;

import com.jiu.datasource.rout.core.DynamicDataSource;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;


/**
 * 数据源的切入面
 *
 */
@Aspect
public class DataSourcAspect {

    @Before("(@annotation(com.jiu.annotation.Master) || execution(* com.jiu.service..*.insert*(..)) || " +
            "execution(* com.jiu.service..*.update*(..)) || execution(* com.jiu.service..*.delete*(..)) || " +
            "execution(* com.jiu.service..*.add*(..))) && !@annotation(com.jiu.annotation.Slave) -")
    public void setWriteDataSourceType() {
        DynamicDataSource.master();
    }

    @Before("(@annotation(com.jiu.annotation.Slave) || execution(* com.jiu.service..*.select*(..)) ||"
    		+ " execution(* com.jiu.service..*.get*(..))) && !@annotation(com.jiu.annotation.Master)")
    public void setReadDataSourceType() {
        DynamicDataSource.slave();
    }


}
