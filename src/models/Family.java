package models;

import java.time.LocalDate;
import javax.persistence.*;

@Entity
@Table(name = "family")
public class Family {

    @Id
    @Column(name = "family_id")
    private int familyId;

    @Column(name = "household_name")
    private String householdName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "location")
    private String location;

    @Column(name = "family_size")
    private int familySize;

    @Column(name = "national_id", unique = true)
    private String nationalId;

    @Column(name = "vulnerability_level")
    private String vulnerabilityLevel;

    @Column(name = "registration_date")
    @Convert(converter = config.LocalDateConverter.class)
    private LocalDate registrationDate;

    @Column(name = "last_aid_date")
    @Convert(converter = config.LocalDateConverter.class)
    private LocalDate lastAidDate;

    public Family() {}

    public Family(int familyId, String householdName, String phone, String location, int familySize, String nationalId, String vulnerabilityLevel, LocalDate registrationDate, LocalDate lastAidDate) {
        this.familyId = familyId;
        this.householdName = householdName;
        this.phone = phone;
        this.location = location;
        this.familySize = familySize;
        this.nationalId = nationalId;
        this.vulnerabilityLevel = vulnerabilityLevel;
        this.registrationDate = registrationDate;
        this.lastAidDate = lastAidDate;
    }

    public int getFamilyId() { 
        return familyId; 
    }
    public void setFamilyId(int familyId) { 
        this.familyId = familyId; 
    }

    public String getHouseholdName() { 
        return householdName; 
    }
    public void setHouseholdName(String householdName) {
        this.householdName = householdName; 
    }

    public String getPhone() { 
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() { 
        return location; 
    }
    public void setLocation(String location) { 
        this.location = location;
    }

    public int getFamilySize() { 
        return familySize; 
    }
    public void setFamilySize(int familySize) {
        this.familySize = familySize;
    }

    public String getNationalId() { 
        return nationalId;
    }
    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getVulnerabilityLevel() {
        return vulnerabilityLevel; 
    }
    public void setVulnerabilityLevel(String vulnerabilityLevel) { 
        this.vulnerabilityLevel = vulnerabilityLevel;
    }

    public LocalDate getRegistrationDate() { 
        return registrationDate; 
    }
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate; 
    }

    public LocalDate getLastAidDate() {
        return lastAidDate;
    }
    public void setLastAidDate(LocalDate lastAidDate) { 
        this.lastAidDate = lastAidDate; 
    }

    @Override
    public String toString() {
        return "ID: " + familyId + " | Head: " + householdName + " | Need: " + vulnerabilityLevel;
    }
}