package io.github.jkvely.util;

/**
 * FileUtils provides utility methods for file operations.
 */
public class FileUtils {
    /**
     * Returns the file extension for a given file name.
     * @param fileName the file name
     * @return the extension (without dot) or empty string
     */
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
}
