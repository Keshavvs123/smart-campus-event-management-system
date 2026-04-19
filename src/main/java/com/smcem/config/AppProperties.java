package com.smcem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    
    private String name;
    private String url;
    private MailProperties mail = new MailProperties();
    private CertificateProperties certificate = new CertificateProperties();
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public MailProperties getMail() {
        return mail;
    }
    
    public void setMail(MailProperties mail) {
        this.mail = mail;
    }
    
    public CertificateProperties getCertificate() {
        return certificate;
    }
    
    public void setCertificate(CertificateProperties certificate) {
        this.certificate = certificate;
    }
    
    // Inner class for mail properties
    public static class MailProperties {
        private String from;
        
        public String getFrom() {
            return from;
        }
        
        public void setFrom(String from) {
            this.from = from;
        }
    }
    
    // Inner class for certificate properties
    public static class CertificateProperties {
        private String issuer;
        
        public String getIssuer() {
            return issuer;
        }
        
        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }
    }
}
