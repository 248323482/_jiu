package com.jiu.service.common.impl;


import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.common.DictionaryMapper;
import com.jiu.entity.common.Dictionary;
import com.jiu.service.common.DictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 业务实现类
 * 字典类型
 * </p>
 *
 */
@Slf4j
@Service

public class DictionaryServiceImpl extends SuperServiceImpl<DictionaryMapper, Dictionary> implements DictionaryService {

}
