package com.qe.commoncore.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileUtil {

    /**
     * This method returns CSV file as object
     *
     * @param fileName
     * @return File
     */
    public static File getFile(String fileName) {
        return new File(FileUtil.class.getClassLoader().getResource(fileName).getFile());
    }
    
    
    /**
     * This method returns boolean value true, if file exist or new file created 
     *
     * @param fileName
     * @return true
     */
    public static boolean createFile(String fileName) throws Exception {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return file.createNewFile();
            } else {
                // File already exists
                return true;
            }
        } catch (Exception e) {
            throw new Exception("Unable to create file \"" + fileName + "\"", e);
        }
    }
    
    public static String readFile(String filePath) {
        String fileContent = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            fileContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
    
    public static String readFile(InputStream inputStream) {
        String fileContent = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            fileContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }
    
	 /**
     * Checks if the file size is within the specified limit.
     * 
     * @param filePath the path to the file
     * @param maxSize the maximum allowed file size in bytes
     * @return true if the file size is within the limit, false otherwise
     * @throws IOException if an I/O error occurs
     */
    public static boolean isFileSizeValid(String filePath, long maxSize) throws IOException{
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);
        return fileSize <= maxSize;
    }
}
