package com.jiu.database.mybatis.conditions.query;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.SharedString;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.jiu.base.model.RemoteData;
import com.jiu.database.mybatis.typehandler.BaseLikeTypeHandler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.baomidou.mybatisplus.core.enums.WrapperKeyword.APPLY;
import static com.jiu.database.mybatis.conditions.Wraps.replace;


/**
 * 类似 LambdaQueryWrapper 的增强 Wrapper
 * <p>
 * 相比 LambdaQueryWrapper 的增强如下：
 * 1，new QueryWrapper(T entity)时， 对entity 中的string字段 %和_ 符号进行转义，便于模糊查询
 * 2，对nested、eq、ne、gt、ge、lt、le、in、*like*、 等方法 进行条件判断，null 或 "" 字段不加入查询
 * 3，对*like*相关方法的参数 %和_ 符号进行转义，便于模糊查询
 * 4，增加 leFooter 方法， 将日期参数值，强制转换成当天 23：59：59
 * 5，增加 geHeader 方法， 将日期参数值，强制转换成当天 00：00：00
 *
 * @description 查询构造器
 */
public class LbqWrapper<T> extends AbstractLambdaWrapper<T, LbqWrapper<T>>
        implements Query<LbqWrapper<T>, T, SFunction<T, ?>> {
    private static final long serialVersionUID = -6842140106034506889L;
    /**
     * 查询字段
     */
    private SharedString sqlSelect = new SharedString();

    /**
     * 是否跳过空值（zuihou项目扩展）
     */
    private boolean skipEmpty = true;

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public LbqWrapper() {
        this((T) null);
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(entity)
     */
    public LbqWrapper(T entity) {
        super.setEntity(entity);
        super.initNeed();
        //覆盖之前的entity
        if (entity != null) {
            super.setEntity(replace(BeanUtil.toBean(entity, getEntityClass())));
        }
    }

    /**
     * 不建议直接 new 该实例，使用 Wraps.lbQ(entity)
     */
    public LbqWrapper(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    /**
     * 不建议直接 new 该实例，使用 Wrappers.lambdaQuery(...)
     */
    LbqWrapper(T entity, Class<T> entityClass, SharedString sqlSelect, AtomicInteger paramNameSeq,
               Map<String, Object> paramNameValuePairs, MergeSegments mergeSegments,
               SharedString lastSql, SharedString sqlComment, SharedString sqlFirst) {
        super.setEntity(entity);
        super.setEntityClass(entityClass);
        this.paramNameSeq = paramNameSeq;
        this.paramNameValuePairs = paramNameValuePairs;
        this.expression = mergeSegments;
        this.sqlSelect = sqlSelect;
        this.lastSql = lastSql;
        this.sqlComment = sqlComment;
        this.sqlFirst = sqlFirst;
    }


    /**
     * SELECT 部分 SQL 设置
     *
     * @param columns 查询字段
     */
    @SafeVarargs
    @Override
    public final LbqWrapper<T> select(SFunction<T, ?>... columns) {
        if (ArrayUtils.isNotEmpty(columns)) {
            this.sqlSelect.setStringValue(columnsToString(false, columns));
        }
        return typedThis;
    }

    /**
     * 过滤查询的字段信息(主键除外!)
     * <p>例1: 只要 java 字段名以 "test" 开头的             -> select(i -&gt; i.getProperty().startsWith("test"))</p>
     * <p>例2: 只要 java 字段属性是 CharSequence 类型的     -> select(TableFieldInfo::isCharSequence)</p>
     * <p>例3: 只要 java 字段没有填充策略的                 -> select(i -&gt; i.getFieldFill() == FieldFill.DEFAULT)</p>
     * <p>例4: 要全部字段                                   -> select(i -&gt; true)</p>
     * <p>例5: 只要主键字段                                 -> select(i -&gt; false)</p>
     *
     * @param predicate 过滤方式
     * @return this
     */
    @Override
    public LbqWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        super.setEntityClass(entityClass);
        this.sqlSelect.setStringValue(TableInfoHelper.getTableInfo(getEntityClass()).chooseSelect(predicate));
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        return this.sqlSelect.getStringValue();
    }


    /**
     * 用于生成嵌套 sql
     * <p>故 sqlSelect 不向下传递</p>
     */
    @Override
    protected LbqWrapper<T> instance() {
        return new LbqWrapper<>(getEntity(), getEntityClass(), null, paramNameSeq, paramNameValuePairs,
                new MergeSegments(), SharedString.emptyString(), SharedString.emptyString(), SharedString.emptyString());
    }

    @Override
    public void clear() {
        super.clear();
        sqlSelect.toNull();
    }

    @Override
    public LbqWrapper<T> nested(Consumer<LbqWrapper<T>> consumer) {
        final LbqWrapper<T> instance = instance();
        consumer.accept(instance);
        if (!instance.isEmptyOfWhere()) {
            return doIt(true, APPLY, instance);
        }
        return this;
    }

    @Override
    public LbqWrapper<T> eq(SFunction<T, ?> column, Object val) {
        return super.eq(this.checkCondition(val), column, val);
    }

    @Override
    public LbqWrapper<T> ne(SFunction<T, ?> column, Object val) {
        return super.ne(this.checkCondition(val), column, val);
    }

    @Override
    public LbqWrapper<T> gt(SFunction<T, ?> column, Object val) {
        return super.gt(this.checkCondition(val), column, val);
    }

    @Override
    public LbqWrapper<T> ge(SFunction<T, ?> column, Object val) {
        return super.ge(this.checkCondition(val), column, val);
    }

    public LbqWrapper<T> geHeader(SFunction<T, ?> column, LocalDateTime val) {
        if (val != null) {
            val = LocalDateTime.of(val.toLocalDate(), LocalTime.MIN);
        }
        return super.ge(this.checkCondition(val), column, val);
    }

    public LbqWrapper<T> geHeader(SFunction<T, ?> column, LocalDate val) {
        LocalDateTime dateTime = null;
        if (val != null) {
            dateTime = LocalDateTime.of(val, LocalTime.MIN);
        }
        return super.ge(this.checkCondition(val), column, dateTime);
    }

    @Override
    public LbqWrapper<T> lt(SFunction<T, ?> column, Object val) {
        return super.lt(this.checkCondition(val), column, val);
    }

    @Override
    public LbqWrapper<T> le(SFunction<T, ?> column, Object val) {
        return super.le(this.checkCondition(val), column, val);
    }

    public LbqWrapper<T> leFooter(SFunction<T, ?> column, LocalDateTime val) {
        if (val != null) {
            val = LocalDateTime.of(val.toLocalDate(), LocalTime.MAX);
        }
        return super.le(this.checkCondition(val), column, val);
    }

    public LbqWrapper<T> leFooter(SFunction<T, ?> column, LocalDate val) {
        LocalDateTime dateTime = null;
        if (val != null) {
            dateTime = LocalDateTime.of(val, LocalTime.MAX);
        }
        return super.le(this.checkCondition(val), column, dateTime);
    }

    @Override
    public LbqWrapper<T> in(SFunction<T, ?> column, Collection<?> coll) {
        return super.in(coll != null && !coll.isEmpty(), column, coll);
    }

    @Override
    public LbqWrapper<T> in(SFunction<T, ?> column, Object... values) {
        return super.in(values != null && values.length > 0, column, values);
    }

    @Override
    public LbqWrapper<T> like(SFunction<T, ?> column, Object val) {
        return super.like(this.checkCondition(val), column, BaseLikeTypeHandler.likeConvert(val));
    }

    @Override
    public LbqWrapper<T> likeLeft(SFunction<T, ?> column, Object val) {
        return super.likeLeft(this.checkCondition(val), column, BaseLikeTypeHandler.likeConvert(val));
    }

    @Override
    public LbqWrapper<T> likeRight(SFunction<T, ?> column, Object val) {
        return super.likeRight(this.checkCondition(val), column, BaseLikeTypeHandler.likeConvert(val));
    }

    /**
     * 忽略实体中的某些字段，实体中的字段默认是会除了null以外的全部进行等值匹配
     * 再次可以进行忽略
     *
     * @param column
     * @return
     */

    @Override
    public LbqWrapper<T> notLike(SFunction<T, ?> column, Object val) {
        return super.notLike(this.checkCondition(val), column, BaseLikeTypeHandler.likeConvert(val));
    }


    //----------------以下为自定义方法---------

    /**
     * 取消跳过空的字符串
     *
     * @return
     */
    public LbqWrapper<T> cancelSkipEmpty() {
        this.skipEmpty = false;
        return this;
    }

    /**
     * 空值校验
     * 传入空字符串("")时， 视为： 字段名 = ""
     *
     * @param val 参数值
     */
    private boolean checkCondition(Object val) {
        if (val instanceof String && this.skipEmpty) {
            return StringUtils.isNotBlank((String) val);
        }
        if (val instanceof Collection && this.skipEmpty) {
            return !((Collection) val).isEmpty();
        }
        if (val instanceof RemoteData && this.skipEmpty) {
            RemoteData value = (RemoteData) val;
            return ObjectUtil.isNotEmpty(value.getKey());
        }
        return val != null;
    }

    /**
     * 忽略实体(entity)中的某些字段，实体中的字段默认是会除了null以外的全部进行等值匹配
     * 再次可以进行忽略
     *
     * @param <A>       这个是传入的待忽略字段的set方法
     * @param setColumn
     * @return
     */
    public <A extends Object> LbqWrapper<T> ignore(BiFunction<T, A, ?> setColumn) {
        setColumn.apply(this.getEntity(), null);
        return this;
    }


}
