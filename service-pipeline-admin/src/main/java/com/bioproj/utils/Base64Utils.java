package com.bioproj.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;

public class Base64Utils {

    // 获取文件转换之后的base64内容
    public static String encodeBase64File(String prefix, File file) {
        try {
            if (file == null || !file.exists() || prefix == null) {
                return null;
            }
            long beginTime = System.currentTimeMillis();
            // base64文件前缀
            String base64Format = Base64FileTypeEnum.value(prefix.toLowerCase());
            if (base64Format == null || "".equals(base64Format)) {
                return null;
            }

            // 获取文件流
            InputStream in = new FileInputStream(file);
            BufferedInputStream bufInput = new BufferedInputStream(in); // 缓存流

            // 先把二进制流写入到ByteArrayOutputStream中
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byte[] bt = new byte[4096];
            int len;
            while ((len = bufInput.read(bt)) != -1) {
                byteArray.write(bt, 0, len);
            }
            byteArray.flush();

            long endTime = System.currentTimeMillis();
            System.out.println("==>encodeBase64File, 把文件转换成base64编码, 总耗时: " + (endTime - beginTime) + "ms");

            // 返回
            return base64Format + Base64.encodeBase64String(byteArray.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 把base64文件解码
    public static void decodeBase64String(String prefix, String base64String, String path) {
        try {
            if (prefix == null || base64String == null) {
            }
            long beginTime = System.currentTimeMillis();

            // 把base64前缀截取掉
            // base64文件前缀
            String value = Base64FileTypeEnum.value(prefix.toLowerCase());
            if (value == null || "".equals(value)) {
            }
            // 替换
            String tempBase64String = base64String.replace(value, "");

            // 把base64字符串转换成字节
            byte[] bytes = Base64.decodeBase64(tempBase64String);

            // 转换成字节输入流
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            File file = new File(path + prefix);
            if (!file.exists()) {
                file.createNewFile();
            }
            // 把base64编码文件还原, 并存放到指定磁盘路径中
            OutputStream out = new FileOutputStream(file);

            // 写文件
            byte[] buffer = new byte[4096];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len); // 文件写操作
            }

            long endTime = System.currentTimeMillis();
            System.out.println("==>decodeBase64String, 解析base64编码文件, 总耗时: " + (endTime - beginTime) + "ms");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
