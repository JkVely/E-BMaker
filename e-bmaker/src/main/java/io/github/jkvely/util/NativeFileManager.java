package io.github.jkvely.util;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for using the native system file manager/browser instead of JavaFX/Swing file choosers.
 * Provides better integration with the operating system's native file dialogs.
 */
public class NativeFileManager {
    
    /**
     * Opens a native file selection dialog for selecting an EPUB file.
     * 
     * @return the selected file path or null if cancelled
     */
    public static String selectEpubFile() {
        return selectFile("Seleccionar archivo EPUB", "*.epub");
    }
    
    /**
     * Opens a native file selection dialog for selecting an image file.
     * 
     * @return the selected file path or null if cancelled
     */
    public static String selectImageFile() {
        return selectFile("Seleccionar imagen", "*.jpg;*.jpeg;*.png;*.gif;*.webp");
    }
    
    /**
     * Opens a native save file dialog for saving an EPUB file.
     * 
     * @param suggestedFileName the suggested filename
     * @return the selected file path or null if cancelled
     */
    public static String saveEpubFile(String suggestedFileName) {
        return saveFile("Guardar archivo EPUB", suggestedFileName, "*.epub");
    }
    
    /**
     * Opens a native file selection dialog.
     * 
     * @param title the dialog title
     * @param filter the file filter (e.g., "*.epub" or "*.jpg;*.png")
     * @return the selected file path or null if cancelled
     */
    private static String selectFile(String title, String filter) {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("win")) {
                return selectFileWindows(title, filter);
            } else if (os.contains("mac")) {
                return selectFileMacOS(title, filter);
            } else {
                // Linux and other Unix-like systems
                return selectFileLinux(title, filter);
            }
        } catch (Exception e) {
            System.err.println("Error al abrir selector de archivos nativo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Opens a native save file dialog.
     * 
     * @param title the dialog title
     * @param suggestedFileName the suggested filename
     * @param filter the file filter
     * @return the selected file path or null if cancelled
     */
    private static String saveFile(String title, String suggestedFileName, String filter) {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("win")) {
                return saveFileWindows(title, suggestedFileName, filter);
            } else if (os.contains("mac")) {
                return saveFileMacOS(title, suggestedFileName, filter);
            } else {
                // Linux and other Unix-like systems
                return saveFileLinux(title, suggestedFileName, filter);
            }
        } catch (Exception e) {
            System.err.println("Error al abrir diálogo de guardado nativo: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Windows file selection using PowerShell.
     */
    private static String selectFileWindows(String title, String filter) throws IOException, InterruptedException {
        // Convert filter format for Windows
        String windowsFilter = convertFilterForWindows(filter);
        
        // PowerShell script to open file dialog
        String script = String.format(
            "Add-Type -AssemblyName System.Windows.Forms; " +
            "$dialog = New-Object System.Windows.Forms.OpenFileDialog; " +
            "$dialog.Title = '%s'; " +
            "$dialog.Filter = '%s'; " +
            "$dialog.Multiselect = $false; " +
            "if ($dialog.ShowDialog() -eq 'OK') { Write-Output $dialog.FileName } else { Write-Output '' }",
            title, windowsFilter
        );
        
        ProcessBuilder pb = new ProcessBuilder("powershell", "-Command", script);
        Process process = pb.start();
        
        String result = readProcessOutput(process).trim();
        int exitCode = process.waitFor();
        
        if (exitCode == 0 && !result.isEmpty()) {
            return result;
        }
        
        return null;
    }
    
    /**
     * Windows save file dialog using PowerShell.
     */
    private static String saveFileWindows(String title, String suggestedFileName, String filter) throws IOException, InterruptedException {
        String windowsFilter = convertFilterForWindows(filter);
        
        String script = String.format(
            "Add-Type -AssemblyName System.Windows.Forms; " +
            "$dialog = New-Object System.Windows.Forms.SaveFileDialog; " +
            "$dialog.Title = '%s'; " +
            "$dialog.Filter = '%s'; " +
            "$dialog.FileName = '%s'; " +
            "if ($dialog.ShowDialog() -eq 'OK') { Write-Output $dialog.FileName } else { Write-Output '' }",
            title, windowsFilter, suggestedFileName != null ? suggestedFileName : ""
        );
        
        ProcessBuilder pb = new ProcessBuilder("powershell", "-Command", script);
        Process process = pb.start();
        
        String result = readProcessOutput(process).trim();
        int exitCode = process.waitFor();
        
        if (exitCode == 0 && !result.isEmpty()) {
            return result;
        }
        
        return null;
    }
    
    /**
     * macOS file selection using osascript.
     */
    private static String selectFileMacOS(String title, String filter) throws IOException, InterruptedException {
        String script = String.format(
            "choose file with prompt \"%s\"", title
        );
        
        ProcessBuilder pb = new ProcessBuilder("osascript", "-e", script);
        Process process = pb.start();
        
        String result = readProcessOutput(process).trim();
        int exitCode = process.waitFor();
        
        if (exitCode == 0 && !result.isEmpty()) {
            // osascript returns path in format "alias Macintosh HD:Users:..."
            // Convert to POSIX path
            if (result.startsWith("alias ")) {
                result = result.substring(6);
                result = result.replace(":", "/");
                if (!result.startsWith("/")) {
                    result = "/" + result;
                }
            }
            return result;
        }
        
        return null;
    }
    
    /**
     * macOS save file dialog using osascript.
     */
    private static String saveFileMacOS(String title, String suggestedFileName, String filter) throws IOException, InterruptedException {
        String script = String.format(
            "choose file name with prompt \"%s\" default name \"%s\"", 
            title, suggestedFileName != null ? suggestedFileName : "archivo"
        );
        
        ProcessBuilder pb = new ProcessBuilder("osascript", "-e", script);
        Process process = pb.start();
        
        String result = readProcessOutput(process).trim();
        int exitCode = process.waitFor();
        
        if (exitCode == 0 && !result.isEmpty()) {
            // Convert macOS path format
            if (result.startsWith("alias ")) {
                result = result.substring(6);
                result = result.replace(":", "/");
                if (!result.startsWith("/")) {
                    result = "/" + result;
                }
            }
            return result;
        }
        
        return null;
    }
    
    /**
     * Linux file selection using zenity or kdialog.
     */
    private static String selectFileLinux(String title, String filter) throws IOException, InterruptedException {
        // Try zenity first (GNOME)
        if (commandExists("zenity")) {
            List<String> command = new ArrayList<>();
            command.add("zenity");
            command.add("--file-selection");
            command.add("--title=" + title);
            
            if (filter.contains("epub")) {
                command.add("--file-filter=Archivos EPUB | *.epub");
            } else if (filter.contains("jpg") || filter.contains("png")) {
                command.add("--file-filter=Imágenes | *.jpg *.jpeg *.png *.gif *.webp");
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            
            String result = readProcessOutput(process).trim();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && !result.isEmpty()) {
                return result;
            }
        }
        
        // Try kdialog (KDE)
        if (commandExists("kdialog")) {
            ProcessBuilder pb = new ProcessBuilder("kdialog", "--getopenfilename", ".", title);
            Process process = pb.start();
            
            String result = readProcessOutput(process).trim();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && !result.isEmpty()) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Linux save file dialog using zenity or kdialog.
     */
    private static String saveFileLinux(String title, String suggestedFileName, String filter) throws IOException, InterruptedException {
        // Try zenity first
        if (commandExists("zenity")) {
            List<String> command = new ArrayList<>();
            command.add("zenity");
            command.add("--file-selection");
            command.add("--save");
            command.add("--title=" + title);
            
            if (suggestedFileName != null && !suggestedFileName.isEmpty()) {
                command.add("--filename=" + suggestedFileName);
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();
            
            String result = readProcessOutput(process).trim();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && !result.isEmpty()) {
                return result;
            }
        }
        
        // Try kdialog
        if (commandExists("kdialog")) {
            String filename = suggestedFileName != null ? suggestedFileName : "archivo.epub";
            ProcessBuilder pb = new ProcessBuilder("kdialog", "--getsavefilename", filename, title);
            Process process = pb.start();
            
            String result = readProcessOutput(process).trim();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && !result.isEmpty()) {
                return result;
            }
        }
        
        return null;
    }
    
    /**
     * Converts filter format for Windows file dialogs.
     */
    private static String convertFilterForWindows(String filter) {
        if (filter.contains("epub")) {
            return "Archivos EPUB|*.epub|Todos los archivos|*.*";
        } else if (filter.contains("jpg") || filter.contains("png")) {
            return "Imágenes|*.jpg;*.jpeg;*.png;*.gif;*.webp|Todos los archivos|*.*";
        }
        return "Todos los archivos|*.*";
    }
    
    /**
     * Checks if a command exists in the system PATH.
     */
    private static boolean commandExists(String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("which", command);
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Reads the output of a process.
     */
    private static String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }
    
    /**
     * Opens the project folder in the system file manager.
     * 
     * @param projectPath the path to open
     */
    public static void openInFileManager(String projectPath) {
        if (projectPath == null || projectPath.trim().isEmpty()) {
            return;
        }
        
        try {
            File folder = new File(projectPath);
            if (folder.exists() && folder.isDirectory()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(folder);
                } else {
                    // Alternative for systems without Desktop support
                    String os = System.getProperty("os.name").toLowerCase();
                    if (os.contains("win")) {
                        new ProcessBuilder("explorer.exe", projectPath).start();
                    } else if (os.contains("mac")) {
                        new ProcessBuilder("open", projectPath).start();
                    } else {
                        new ProcessBuilder("xdg-open", projectPath).start();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo abrir la carpeta en el explorador de archivos: " + e.getMessage());
        }
    }
}
