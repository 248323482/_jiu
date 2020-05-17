package com.jiu.entity.domain;


import com.jiu.entity.enumeration.DataType;
import lombok.Builder;
import lombok.Data;

/**
 */
@Data
@Builder
public class FileDO {
    /**
     * 原始文件名
     */
    private String submittedFileName;
    /**
     * 数据类型 IMAGE/VIDEO/AUDIO/DOC/OTHER/DIR
     */
    private DataType dataType;
    /**
     * 文件访问链接
     */
    private String url;
    /**
     * 文件大小
     */
    private Long size;
}
