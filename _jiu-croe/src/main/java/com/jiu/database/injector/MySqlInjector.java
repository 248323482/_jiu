package com.jiu.database.injector;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.jiu.base.entity.SuperEntity;
import com.jiu.database.injector.method.UpdateAllById;

import java.util.List;

/**
 * 自定义sql 注入器
 *

 */
public class MySqlInjector extends DefaultSqlInjector {
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);

        //增加自定义方法
        methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
        methodList.add(new UpdateAllById(field -> !ArrayUtil.containsAny(new String[]{
                SuperEntity.CREATE_TIME_COLUMN, SuperEntity.CREATE_USER_COLUMN
        }, field.getColumn())));
        return methodList;
    }
}
