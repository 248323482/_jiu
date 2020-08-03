package com.jiu.dao;

import com.baomidou.mybatisplus.annotation.SqlParser;
import com.jiu.base.mapper.SuperMapper;
import com.jiu.entity.GlobalUser;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 全局账号
 * </p>
 *
 */
@Repository
@SqlParser(filter = true)
public interface GlobalUserMapper extends SuperMapper<GlobalUser> {

}
