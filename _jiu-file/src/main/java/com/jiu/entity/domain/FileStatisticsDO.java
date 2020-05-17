package com.jiu.entity.domain;


import com.jiu.entity.enumeration.DataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @desc 文件类型数量、
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileStatisticsDO {
    /**
     * 文件类型 IMAGE、DOC等
     */
    private DataType dataType;
    /**
     * 时间类型 （按月、周、天？）
     */
    private String dateType;
    /**
     * 文件数量
     */
    private Integer num;
    /**
     * 文件大小
     */
    private Long size;
}
