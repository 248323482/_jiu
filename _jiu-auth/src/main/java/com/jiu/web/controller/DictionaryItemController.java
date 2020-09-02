package com.jiu.web.controller;


import com.jiu.base.controller.SuperCacheController;
import com.jiu.base.request.PageParams;
import com.jiu.database.mybatis.conditions.query.QueryWrap;
import com.jiu.dto.DictionaryItemSaveDTO;
import com.jiu.dto.DictionaryItemUpdateDTO;
import com.jiu.entity.common.DictionaryItem;
import com.jiu.security.annotation.PreAuth;
import com.jiu.service.common.DictionaryItemService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * 字典项
 * </p>
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dictionaryItem")
@Api(value = "DictionaryItem", tags = "字典项")
@PreAuth(replace = "dict:")
public class DictionaryItemController extends SuperCacheController<DictionaryItemService, Long, DictionaryItem, DictionaryItem, DictionaryItemSaveDTO, DictionaryItemUpdateDTO> {
    @Override
    public QueryWrap<DictionaryItem> handlerWrapper(DictionaryItem model, PageParams<DictionaryItem> params) {
        QueryWrap<DictionaryItem> wrapper = super.handlerWrapper(model, params);
        wrapper.lambda().ignore(DictionaryItem::setDictionaryType)
                .eq(DictionaryItem::getDictionaryType, model.getDictionaryType());
        return wrapper;
    }

}
