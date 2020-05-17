package com.jiu.strategy;


import com.jiu.base.R;
import com.jiu.entity.File;
import com.jiu.entity.dto.chunk.FileChunksMergeDTO;

/**
 * 文件分片处理策略类
 *
 */
public interface FileChunkStrategy {

    /**
     * 根据md5检测文件
     *
     * @param md5
     * @param folderId
     * @param accountId
     * @return
     */
    File md5Check(String md5, Long folderId, Long accountId);

    /**
     * 合并文件
     *
     * @param merge
     * @return
     */
    R<File> chunksMerge(FileChunksMergeDTO merge);
}
