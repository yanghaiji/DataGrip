package com.javayh.data.grip.core.util;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-11
 */
public class FileUtils {

    private static final String FILE_PATH = System.getProperty("user.dir");
    private static final String SQL_FILE_PATH = "sql";


    /**
     * 将集合数据写入
     *
     * @param list {@link List<String>元数据}
     */
    public static void writeLines(List<String> list) {
        // 将集合数据写入文件

        FileUtils.writeLines(list, FILE_PATH + File.separator + SQL_FILE_PATH);
    }


    /**
     * 根据指定路径写出文件
     *
     * @param list     {@link List<String>元数据}
     * @param fileName {@link String 文件名}
     */
    public static void writeLines(List<String> list, String fileName) {
        // 将集合数据写入文件
        FileUtil.writeLines(list, FILE_PATH + File.separator + SQL_FILE_PATH + File.separator + fileName, "UTF-8");
    }


}
