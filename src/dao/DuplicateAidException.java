package dao;

import java.time.LocalDate;

public class DuplicateAidException extends Exception {
    private String familyName;
    private String vulnerabilityLevel;
    private String organizationName;
    private LocalDate lastAidDate;

    public DuplicateAidException(String familyName, String vulnerabilityLevel, String organizationName, LocalDate lastAidDate) {
        super("Duplicate aid detected within 30 days.");
        this.familyName = familyName;
        this.vulnerabilityLevel = vulnerabilityLevel;
        this.organizationName = organizationName;
        this.lastAidDate = lastAidDate;
    }

    public String getFamilyName() { 
        return familyName; 
    }
    public String getVulnerabilityLevel() {
        return vulnerabilityLevel; 
    }
    public String getOrganizationName() {
        return organizationName; 
    }
    public LocalDate getLastAidDate() { 
        return lastAidDate; 
    }
}
