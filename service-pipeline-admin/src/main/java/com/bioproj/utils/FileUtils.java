package com.bioproj.utils;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FileUtils {

    public static void copy(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public static String openFile(File file){
        FileInputStream fileInputStream=null;
        try {
            if(file.exists()){
                fileInputStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
                List<String> list = reader.lines().collect(Collectors.toList());
                String content = Joiner.on("\n").join(list);
                return content;
            }else {
                return "Page is not found!!";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "Page is not found!!";
        }finally {
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static  void saveFile(File file,String content){
        File parentFile = file.getParentFile();
        if(!parentFile.exists()){
            try {
                Files.createDirectories(parentFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        FileOutputStream fileOutputStream=null;
        try {
            fileOutputStream  = new FileOutputStream(file);

            fileOutputStream.write(content.getBytes());
            log.info("写入文件：{}",file.getPath().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void createFile(MultipartFile file, String path){
        //存储文件
        try {
            //保存文件
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path));
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 将file转换为inputStream
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream file2InputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }
    public  static boolean remove(String path) {
        File file = new File(path);
        log.info("删除文件{}!!",path);
        return remove(file);
    }
    public static boolean remove(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return false;
        }
        if (dirFile.isFile()) {
            return dirFile.delete();
        } else {
            for (File file : dirFile.listFiles()) {
                remove(file);
            }
        }
        return dirFile.delete();
    }
    public static void move(String source,String  target) {
        Path sourcePath = Path.of(source);
        Path targetPath = Path.of(target);

        try {
            Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("从{}移动{}",sourcePath,targetPath);
        } catch (IOException e) {
            System.out.println("文件移动失败: " + e.getMessage());
        }
    }
    public static void copy(String source,String  target) {
        Path sourcePath = Path.of(source);
        Path targetPath = Path.of(target);


        try {
            if(!targetPath.toFile().exists()){
                Files.createDirectories(targetPath);
            }

            copyFolder(sourcePath, targetPath);
            log.info("从{}拷贝{}",sourcePath,targetPath);
        } catch (IOException e) {
            System.out.println("文件拷贝失败: " + e.getMessage());
        }
    }
    public static void copy(String source,String  target,List<String> files,String basedir) {
        Path sourcePath = Path.of(source);
        Path targetPath = Path.of(target);


        try {
            if(!targetPath.toFile().exists()){
                Files.createDirectories(targetPath);
            }

            copyFolder(sourcePath, targetPath,files,basedir);
            log.info("从{}拷贝{}",sourcePath,targetPath);
        } catch (IOException e) {
            System.out.println("文件拷贝失败: " + e.getMessage());
        }
    }
    private static void copyFolder(Path source, Path target,List<String> files,String basedir) throws IOException {

        Files.walk(source)
                .forEach(sourcePath -> {
                    try {
                        String file = sourcePath.toString().replace(basedir, "");
                        if(inStartWith(file,files)){
                            Path targetPath = target.resolve(source.relativize(sourcePath));
                            Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to copy " + sourcePath + ": " + e.getMessage());
                    }
                });
    }
    private static Boolean inStartWith(String file,List<String> fileList){
        boolean startsWith = false;
        for (String item : fileList) {
            if (file.startsWith(item)) {
                startsWith = true;
                break;
            }
        }
        return startsWith;
    }
    private static void copyFolder(Path source, Path target) throws IOException {

        Files.walk(source)
                .forEach(sourcePath -> {
                    try {
                        Path targetPath = target.resolve(source.relativize(sourcePath));
                        Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
                    } catch (IOException e) {
                        System.out.println("Failed to copy " + sourcePath + ": " + e.getMessage());
                    }
                });
    }
}
