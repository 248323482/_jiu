package com.jiu.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jiu.base.mapper.SuperMapper;
import com.jiu.dto.MsgsCenterInfoPageResultDTO;
import com.jiu.dto.MsgsCenterInfoQueryDTO;
import com.jiu.entity.MsgsCenterInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * 消息中心
 * </p>
 *
 */
@Repository
public interface MsgsCenterInfoMapper extends SuperMapper<MsgsCenterInfo> {
    /**
     * 查询消息中心分页数据
     *
     * @param page
     * @param param
     * @return
     */
    IPage<MsgsCenterInfoPageResultDTO> page(IPage page, @Param("data") MsgsCenterInfoQueryDTO param);
}
