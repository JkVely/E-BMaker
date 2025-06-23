package io.github.jkvely.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for managing project folders and assets.
 * Handles creation of dedicated project directories for storing images and project data.
 */
public class ProjectFolderUtils {
    
    private static final String PROJECT_BASE_NAME = "EBMaker_Project";
    private static final String IMAGES_FOLDER = "Images";
    private static final String ASSETS_FOLDER = "Assets";
    private static final String EXPORTS_FOLDER = "Exports";
    
    /**
     * Creates a new project folder with the specified project name.
     * If no name is provided, generates a default name with timestamp.
     * 
     * @param projectName the desired project name (can be null)
     * @param parentDirectory the parent directory where to create the project folder (can be null for Documents)
     * @return the absolute path to the created project folder
     * @throws IOException if folder creation fails
     */
    public static String createProjectFolder(String projectName, String parentDirectory) throws IOException {
        // Determine parent directory - default to user's Documents folder
        String baseDir = parentDirectory;
        if (baseDir == null || baseDir.trim().isEmpty()) {
            baseDir = System.getProperty("user.home") + File.separator + "Documents";
        }
        
        // Generate project folder name
        String folderName = generateProjectFolderName(projectName);
        
        // Create full path
        Path projectPath = Paths.get(baseDir, folderName);
        
        // Ensure uniqueness - if folder exists, append number
        projectPath = ensureUniquePath(projectPath);
        
        // Create the main project directory
        Files.createDirectories(projectPath);
        
        // Create subdirectories for organization
        createProjectSubdirectories(projectPath);
        
        System.out.println("📁 Proyecto creado en: " + projectPath.toAbsolutePath());
        
        return projectPath.toAbsolutePath().toString();
    }
    
    /**
     * Creates standard subdirectories within the project folder.
     * 
     * @param projectPath the main project folder path
     * @throws IOException if subdirectory creation fails
     */
    private static void createProjectSubdirectories(Path projectPath) throws IOException {
        // Create Images folder for cover and chapter images
        Path imagesPath = projectPath.resolve(IMAGES_FOLDER);
        Files.createDirectories(imagesPath);
        
        // Create Assets folder for other resources
        Path assetsPath = projectPath.resolve(ASSETS_FOLDER);
        Files.createDirectories(assetsPath);
        
        // Create Exports folder for generated EPUBs
        Path exportsPath = projectPath.resolve(EXPORTS_FOLDER);
        Files.createDirectories(exportsPath);
        
        System.out.println("📂 Subdirectorios creados: Images, Assets, Exports");
    }
    
    /**
     * Generates a project folder name based on the project title.
     * 
     * @param projectName the project name (can be null)
     * @return a sanitized folder name
     */
    private static String generateProjectFolderName(String projectName) {
        String baseName;
        
        if (projectName != null && !projectName.trim().isEmpty()) {
            // Sanitize the project name for use as folder name
            baseName = sanitizeForFileName(projectName.trim());
        } else {
            // Generate default name with timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
            baseName = PROJECT_BASE_NAME + "_" + timestamp;
        }
        
        return baseName;
    }
    
    /**
     * Ensures the path is unique by appending a number if necessary.
     * 
     * @param originalPath the desired path
     * @return a unique path
     */
    private static Path ensureUniquePath(Path originalPath) {
        Path uniquePath = originalPath;
        int counter = 1;
        
        while (Files.exists(uniquePath)) {
            String originalName = originalPath.getFileName().toString();
            String newName = originalName + "_" + counter;
            uniquePath = originalPath.getParent().resolve(newName);
            counter++;
        }
        
        return uniquePath;
    }
    
    /**
     * Sanitizes a string to be safe for use as a file/folder name.
     * 
     * @param input the input string
     * @return a sanitized string safe for file names
     */
    private static String sanitizeForFileName(String input) {
        if (input == null) return PROJECT_BASE_NAME;
        
        // Replace invalid characters with underscores
        String sanitized = input.replaceAll("[\\\\/:*?\"<>|]", "_");
        
        // Remove multiple consecutive underscores
        sanitized = sanitized.replaceAll("_{2,}", "_");
        
        // Remove leading/trailing underscores
        sanitized = sanitized.replaceAll("^_+|_+$", "");
        
        // Ensure it's not empty
        if (sanitized.trim().isEmpty()) {
            sanitized = PROJECT_BASE_NAME;
        }
        
        // Limit length to avoid issues with long paths
        if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 50);
        }
        
        return sanitized;
    }
    
    /**
     * Gets the Images subdirectory path for a project.
     * 
     * @param projectPath the main project folder path
     * @return the path to the Images folder
     */
    public static String getImagesPath(String projectPath) {
        if (projectPath == null || projectPath.trim().isEmpty()) {
            return null;
        }
        return Paths.get(projectPath, IMAGES_FOLDER).toString();
    }
    
    /**
     * Gets the Assets subdirectory path for a project.
     * 
     * @param projectPath the main project folder path
     * @return the path to the Assets folder
     */
    public static String getAssetsPath(String projectPath) {
        if (projectPath == null || projectPath.trim().isEmpty()) {
            return null;
        }
        return Paths.get(projectPath, ASSETS_FOLDER).toString();
    }
    
    /**
     * Gets the Exports subdirectory path for a project.
     * 
     * @param projectPath the main project folder path
     * @return the path to the Exports folder
     */
    public static String getExportsPath(String projectPath) {
        if (projectPath == null || projectPath.trim().isEmpty()) {
            return null;
        }
        return Paths.get(projectPath, EXPORTS_FOLDER).toString();
    }
    
    /**
     * Validates if a path is a valid project folder.
     * 
     * @param projectPath the path to validate
     * @return true if it's a valid project folder
     */
    public static boolean isValidProjectFolder(String projectPath) {
        if (projectPath == null || projectPath.trim().isEmpty()) {
            return false;
        }
        
        Path path = Paths.get(projectPath);
        if (!Files.exists(path) || !Files.isDirectory(path)) {
            return false;
        }
        
        // Check if it contains the expected subdirectories
        Path imagesPath = path.resolve(IMAGES_FOLDER);
        Path assetsPath = path.resolve(ASSETS_FOLDER);
        Path exportsPath = path.resolve(EXPORTS_FOLDER);
        
        return Files.exists(imagesPath) && Files.exists(assetsPath) && Files.exists(exportsPath);
    }
}
