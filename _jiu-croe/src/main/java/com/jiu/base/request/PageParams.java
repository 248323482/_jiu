package com.jiu.base.request;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jiu.base.entity.Entity;
import com.jiu.base.entity.SuperEntity;
import com.jiu.utils.AntiSqlFilter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页参数
 */
@Data
@ApiModel(value = "PageParams", description = "分页参数")
public class PageParams<T> {

    @NotNull(message = "查询对象model不能为空")
    @ApiModelProperty(value = "查询参数", required = true)
    private T model;

    @ApiModelProperty(value = "页面大小", example = "10")
    private long size = 10;

    @ApiModelProperty(value = "当前页", example = "1")
    private long current = 1;

    @ApiModelProperty(value = "排序,默认createTime", allowableValues = "id,createTime,updateTime", example = "id")
    private String sort = SuperEntity.FIELD_ID;

    @ApiModelProperty(value = "排序规则, 默认descending", allowableValues = "descending,ascending", example = "descending")
    private String order = "descending";

    @ApiModelProperty("扩展参数")
    private Map<String, String> map = new HashMap<>(1);

    @JsonIgnore
    public IPage buildPage() {
        Page page;
        if (StrUtil.isEmpty(this.getSort())) {
            page = new Page(this.getCurrent(), this.getSize());
            return page;
        } else {
            page = new Page(this.getCurrent(), this.getSize());
            List<OrderItem> orders = new ArrayList();
            String[] sortArr = StrUtil.split(this.getSort(), ",");
            String[] orderArr = StrUtil.split(this.getOrder(), ",");
            int len = sortArr.length < orderArr.length ? sortArr.length : orderArr.length;

            for(int i = 0; i < len; ++i) {
                String humpSort = sortArr[i];
                String underlineSort = StrUtil.toUnderlineCase(humpSort);
                if (!StrUtil.equalsAny(humpSort, new CharSequence[]{"createTime", "updateTime"})) {
                    underlineSort = AntiSqlFilter.getSafeValue(underlineSort);
                }

                orders.add("ascending".equals(orderArr[i]) ? OrderItem.asc(underlineSort) : OrderItem.desc(underlineSort));
            }

            page.setOrders(orders);
            return page;
        }
    }



}
