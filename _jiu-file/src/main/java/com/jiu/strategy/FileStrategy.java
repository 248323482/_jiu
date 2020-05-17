package com.jiu.strategy;

import com.jiu.entity.File;
import com.jiu.entity.domain.FileDeleteDO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件策略接口
 *
 */
public interface FileStrategy {
    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件对象
     */
    File upload(MultipartFile file);

    /**
     * 删除源文件
     *
     * @param list 列表
     * @return
     */
    boolean delete(List<FileDeleteDO> list);

}
