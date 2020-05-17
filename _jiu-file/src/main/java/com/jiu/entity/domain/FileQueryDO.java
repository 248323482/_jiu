package com.jiu.entity.domain;


import com.jiu.entity.File;
import lombok.Data;

/**
 * 文件查询 DO
 *
 */
@Data
public class FileQueryDO extends File {
    private File parent;

}
