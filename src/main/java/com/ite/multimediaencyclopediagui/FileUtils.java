package com.ite.multimediaencyclopediagui;

import java.io.File;

public class FileUtils {
    public static int getNumberOfFilesInDirectory(String directory){
        File fileDir = new File(directory);

        File[] filesInDir = fileDir.listFiles();

        int numberOfFiles = 0;

        for (File file : filesInDir) {
            if (file.isFile()) {
                numberOfFiles++;
            }
        }
        return numberOfFiles;
    }

    /**
     * Gets the base name, without extension, of given file name.
     * e.g. getBaseName("file.txt") will return "file"
     */
    public static String getFileBaseName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    public static  String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
