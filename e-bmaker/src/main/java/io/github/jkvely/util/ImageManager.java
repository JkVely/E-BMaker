package io.github.jkvely.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for managing images within project folders.
 * Handles copying images to project directories and generating proper references.
 */
public class ImageManager {
    
    /**
     * Copies an image file to the project's Images folder and returns the relative reference.
     * 
     * @param sourceImageFile the source image file to copy
     * @param projectPath the project folder path
     * @return the relative path to use in markdown (e.g., "Images/image_name.jpg")
     * @throws IOException if copying fails
     */
    public static String copyImageToProject(File sourceImageFile, String projectPath) throws IOException {
        if (sourceImageFile == null || !sourceImageFile.exists()) {
            throw new IllegalArgumentException("Source image file does not exist");
        }
        
        if (projectPath == null || projectPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Project path cannot be null or empty");
        }
        
        // Get the project's Images folder
        String imagesPath = ProjectFolderUtils.getImagesPath(projectPath);
        if (imagesPath == null) {
            throw new IOException("Could not determine Images folder path");
        }
        
        // Ensure Images folder exists
        Path imagesFolderPath = Paths.get(imagesPath);
        Files.createDirectories(imagesFolderPath);
        
        // Generate unique filename to avoid conflicts
        String originalFileName = sourceImageFile.getName();
        String fileExtension = getFileExtension(originalFileName);
        String baseFileName = getFileNameWithoutExtension(originalFileName);
        
        // Sanitize filename
        String sanitizedBaseName = sanitizeFileName(baseFileName);
        
        // Create unique filename if file already exists
        String finalFileName = ensureUniqueFileName(imagesFolderPath, sanitizedBaseName, fileExtension);
        
        // Copy file to project Images folder
        Path destinationPath = imagesFolderPath.resolve(finalFileName);
        Files.copy(sourceImageFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
        
        // Return relative path for markdown
        return "Images/" + finalFileName;
    }
    
    /**
     * Ensures the filename is unique within the target directory.
     * 
     * @param targetDir the target directory
     * @param baseName the base name (without extension)
     * @param extension the file extension
     * @return a unique filename
     */
    private static String ensureUniqueFileName(Path targetDir, String baseName, String extension) {
        String fileName = baseName + extension;
        Path filePath = targetDir.resolve(fileName);
        
        int counter = 1;
        while (Files.exists(filePath)) {
            fileName = baseName + "_" + counter + extension;
            filePath = targetDir.resolve(fileName);
            counter++;
        }
        
        return fileName;
    }
    
    /**
     * Gets the file extension including the dot.
     * 
     * @param fileName the filename
     * @return the extension (e.g., ".jpg") or empty string if no extension
     */
    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex);
    }
    
    /**
     * Gets the filename without extension.
     * 
     * @param fileName the filename
     * @return the filename without extension
     */
    private static String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "image";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }
        
        return fileName.substring(0, lastDotIndex);
    }
    
    /**
     * Sanitizes a filename to be safe for file systems.
     * 
     * @param fileName the filename to sanitize
     * @return a sanitized filename
     */
    private static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return "image";
        }
        
        // Replace invalid characters with underscores
        String sanitized = fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
        
        // Remove multiple consecutive underscores
        sanitized = sanitized.replaceAll("_{2,}", "_");
        
        // Remove leading/trailing underscores
        sanitized = sanitized.replaceAll("^_+|_+$", "");
        
        // Ensure it's not empty
        if (sanitized.trim().isEmpty()) {
            sanitized = "image";
        }
        
        // Limit length to avoid issues
        if (sanitized.length() > 30) {
            sanitized = sanitized.substring(0, 30);
        }
        
        return sanitized;
    }
    
    /**
     * Checks if a file is a supported image format.
     * 
     * @param file the file to check
     * @return true if it's a supported image format
     */
    public static boolean isSupportedImageFormat(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return false;
        }
        
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || 
               fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || 
               fileName.endsWith(".gif") || 
               fileName.endsWith(".webp");
    }
    
    /**
     * Gets the full path to an image file in the project.
     * 
     * @param relativePath the relative path (e.g., "Images/image.jpg")
     * @param projectPath the project folder path
     * @return the full path to the image file or null if not found
     */
    public static String getFullImagePath(String relativePath, String projectPath) {
        if (relativePath == null || projectPath == null) {
            return null;
        }
        
        Path fullPath = Paths.get(projectPath, relativePath);
        if (Files.exists(fullPath)) {
            return fullPath.toAbsolutePath().toString();
        }
        
        return null;
    }
}
