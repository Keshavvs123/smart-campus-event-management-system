package com.smcem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {
    
    private String uploadDir;
    private String certificateDir;
    
    // Getters and Setters
    public String getUploadDir() {
        return uploadDir;
    }
    
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
    
    public String getCertificateDir() {
        return certificateDir;
    }
    
    public void setCertificateDir(String certificateDir) {
        this.certificateDir = certificateDir;
    }
}
