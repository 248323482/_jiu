package com.jiu.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiu.base.R;
import com.jiu.base.request.PageParams;
import com.jiu.database.mybatis.conditions.Wraps;
import com.jiu.database.mybatis.conditions.query.QueryWrap;
import com.jiu.log.annotation.SysLog;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * 查询Controller
 *
 * @param <Entity>  实体
 * @param <Id>      主键
 * @param <PageDTO> 分页参数
 */
public interface QueryController<Entity, Id extends Serializable, PageDTO> extends PageController<Entity, PageDTO> {

    /**
     * 查询
     *
     * @param id 主键id
     * @return 查询结果
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键", dataType = "long", paramType = "query"),
    })
    @ApiOperation(value = "查询", notes = "查询")
    @GetMapping("/{id}")
    @SysLog("'查询:' + #id")
    default R<Entity> get(@PathVariable Id id) {
        return success(getBaseService().getById(id));
    }

    /**
     * 分页查询
     *
     * @param params
     * @return
     */
    @ApiOperation(value = "分页列表查询")
    @PostMapping(value = "/page")
    @SysLog(value = "'分页列表查询:第' + #params?.current + '页, 显示' + #params?.size + '行'", response = false)
    default R<IPage<Entity>> page(@RequestBody @Validated PageParams<PageDTO> params) {
        // 处理参数
        IPage<Entity> page = params.buildPage();
        query(params, page, null);
        return success(page);
    }


    /**
     * 批量查询
     *
     * @param data 批量查询
     * @return 查询结果
     */
    @ApiOperation(value = "批量查询", notes = "批量查询")
    @PostMapping("/query")
    @SysLog("批量查询")
    default R<List<Entity>> query(@RequestBody Entity data) {
        QueryWrap<Entity> wrapper = Wraps.q(data);
        return success(getBaseService().list(wrapper));
    }

}
