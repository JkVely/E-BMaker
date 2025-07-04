package io.github.jkvely.model;

public class InterfaceModel {
    
    private String lastAction;
    private String projectName;
    private String projectPath;
    
    public InterfaceModel() {
        this.lastAction = "";
        this.projectName = "";
        this.projectPath = "";
    }
    
    // Getters y Setters
    public String getLastAction() {
        return lastAction;
    }
    
    public void setLastAction(String lastAction) {
        this.lastAction = lastAction;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getProjectPath() {
        return projectPath;
    }
    
    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
    
    @Override
    public String toString() {
        return "ProjectModel{" +
                "lastAction='" + lastAction + '\'' +
                ", projectName='" + projectName + '\'' +
                ", projectPath='" + projectPath + '\'' +
                '}';
    }
}