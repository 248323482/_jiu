package com.jiu.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.jiu.base.service.SuperServiceImpl;
import com.jiu.dao.GlobalUserMapper;
import com.jiu.database.mybatis.conditions.Wraps;
import com.jiu.dto.GlobalUserSaveDTO;
import com.jiu.dto.GlobalUserUpdateDTO;
import com.jiu.entity.GlobalUser;
import com.jiu.service.GlobalUserService;
import com.jiu.utils.BeanPlusUtil;
import com.jiu.utils.BizAssert;
import com.jiu.utils.StrHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jiu.utils.BizAssert.isFalse;

/**
 * <p>
 * 业务实现类
 * 全局账号
 * </p>
 *
 */
@Slf4j
@Service

public class GlobalUserServiceImpl extends SuperServiceImpl<GlobalUserMapper, GlobalUser> implements GlobalUserService {

    @Override
    public Boolean check(String account) {
        return super.count(Wraps.<GlobalUser>lbQ()
                .eq(GlobalUser::getAccount, account)) > 0;
    }

    /**
     * @param data
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GlobalUser save(GlobalUserSaveDTO data) {
        BizAssert.equals(data.getPassword(), data.getConfirmPassword(), "2次输入的密码不一致");
        isFalse(check(data.getAccount()), "账号已经存在");

        String md5Password = SecureUtil.md5(data.getPassword());

        GlobalUser globalAccount = BeanPlusUtil.toBean(data, GlobalUser.class);
        // 全局表就不存用户数据了
        globalAccount.setPassword(md5Password);
        globalAccount.setName(StrHelper.getOrDef(data.getName(), data.getAccount()));
        globalAccount.setReadonly(false);

        save(globalAccount);
        return globalAccount;
    }

    /**
     * @param data
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GlobalUser update(GlobalUserUpdateDTO data) {
        if (StrUtil.isNotBlank(data.getPassword()) || StrUtil.isNotBlank(data.getPassword())) {
            BizAssert.equals(data.getPassword(), data.getConfirmPassword(), "2次输入的密码不一致");
        }
        GlobalUser globalUser = BeanPlusUtil.toBean(data, GlobalUser.class);
        if (StrUtil.isNotBlank(data.getPassword())) {
            String md5Password = SecureUtil.md5(data.getPassword());
            globalUser.setPassword(md5Password);

        }
        updateById(globalUser);
        return globalUser;
    }
}
