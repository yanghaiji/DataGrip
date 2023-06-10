package com.javayh.cdc.data.grip.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * <p>
 *
 * </p>
 *
 * @author hai ji
 * @version 1.0.0
 * @since 2023-06-09
 */
@Slf4j
public class ReaderFile {

    /**
     * 根据资源理解获取数据
     *
     * @param resource 资源路径
     * @return {@link String} 字符数据
     */
    public static String getResource(String resource) {
        ClassPathResource pathResource = new ClassPathResource(resource);
        StringBuilder stringBuilder = new StringBuilder();
        try {

            InputStream inputStream = pathResource.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            log.error("资源文件读出失败 ,[{}], {}", e.getMessage(), e);
        }
        return stringBuilder.toString();
    }
}
