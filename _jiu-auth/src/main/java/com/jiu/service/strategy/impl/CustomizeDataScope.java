package com.jiu.service.strategy.impl;

import com.jiu.entity.common.Org;
import com.jiu.exception.BizException;
import com.jiu.exception.code.ExceptionCode;
import com.jiu.service.common.OrgService;
import com.jiu.service.strategy.AbstractDataScopeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义模式
 *
 */
@Component("CUSTOMIZE")
public class CustomizeDataScope implements AbstractDataScopeHandler {

    @Autowired
    private OrgService orgService;

    @Override
    public List<Long> getOrgIds(List<Long> orgList, Long userId) {
        if (orgList == null || orgList.isEmpty()) {
            throw new BizException(ExceptionCode.BASE_VALID_PARAM.getCode(), "自定义数据权限类型时，组织不能为空");
        }
        List<Org> children = orgService.findChildren(orgList);
        return children.stream().mapToLong(Org::getId).boxed().collect(Collectors.toList());
    }
}
