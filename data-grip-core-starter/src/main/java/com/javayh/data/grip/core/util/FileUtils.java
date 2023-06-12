package com.javayh.data.grip.core.util;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.Collections;
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
    private static final String DB_SYNC_OFFSET = "db_sync_offset";


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

    /**
     * 将出错的位置进行进来，方便后期进行根据断点进行二次补偿
     *
     * @param text {@link String} tableName:xxxx,
     */
    public static void appendUtf8Lines(String text) {
        // 将集合数据写入文件
        FileUtil.appendUtf8Lines(Collections.singletonList(text), FILE_PATH + File.separator + DB_SYNC_OFFSET + File.separator + DB_SYNC_OFFSET.concat(".log"));
    }


}
