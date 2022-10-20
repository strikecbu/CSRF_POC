package com.bondlic.bondsearch.util;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.aspectj.util.FileUtil;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class TempFileUtil {

    private static final String TEMP_FOLDER_NAME = "bondlinc";

    public static String writeFileToTemp(byte[] bytes) throws IOException {

        String randomFileName = UUID.randomUUID()
            .toString().replaceAll("-", "");
        Path todayPath = getTodayPath();
        Path path = todayPath.resolve(randomFileName);
        Files.createDirectories(path.getParent());
        Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return randomFileName;
    }

    public static Path writeFileToTemp(InputStream inputStream, String fileName) throws IOException {
        Path todayPath = getTodayPath();
        String replaceFileName = fileName.replaceAll("[/\\\\]", "");
        Path path = todayPath.resolve(replaceFileName);
        Files.createDirectories(path.getParent());

        Files.copy(inputStream, path, REPLACE_EXISTING);

        return path;
    }


    public static Optional<byte[]> readFileFromTemp(String fileName, boolean deleteAfterRead) throws IOException {
        Path todayPath = getTodayPath();
        String replaceFileName = fileName.replaceAll("[/\\\\]", "");
        Path path = todayPath.resolve(replaceFileName);
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        Optional<byte[]> bytes = Optional.of(Files.readAllBytes(path));
        if(deleteAfterRead) {
            Files.delete(path);
        }
        return bytes;
    }

    public static Path getTodayPath() {
        String tmpFolder = System.getProperty("java.io.tmpdir");
        String dateStr = LocalDate.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Paths.get(tmpFolder, TEMP_FOLDER_NAME, dateStr);
    }

}
