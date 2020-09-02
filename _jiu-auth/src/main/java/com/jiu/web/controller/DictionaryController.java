package com.jiu.web.controller;


import com.jiu.dto.DictionarySaveDTO;
import com.jiu.dto.DictionaryUpdateDTO;
import com.jiu.entity.common.Dictionary;
import com.jiu.entity.common.DictionaryItem;
import com.jiu.service.common.DictionaryItemService;
import com.jiu.service.common.DictionaryService;
import com.jiu.base.R;
import com.jiu.base.controller.SuperController;
import com.jiu.database.mybatis.conditions.Wraps;
import com.jiu.security.annotation.PreAuth;
import com.jiu.security.annotation.PreAuth;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * 字典类型
 * </p>
 *
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/dictionary")
@Api(value = "Dictionary", tags = "字典类型")
@PreAuth(replace = "dict:")
public class DictionaryController extends SuperController<DictionaryService, Long, Dictionary, Dictionary, DictionarySaveDTO, DictionaryUpdateDTO> {

    @Autowired
    private DictionaryItemService dictionaryItemService;

    @Override
    public R<Boolean> handlerDelete(List<Long> ids) {
        this.baseService.removeByIds(ids);
        this.dictionaryItemService.remove(Wraps.<DictionaryItem>lbQ().in(DictionaryItem::getDictionaryId, ids));
        return this.success(true);
    }
}
