package models;

import javax.persistence.*;

@Entity
@Table(name = "organization")
public class Organization {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "org_id")
    private int orgId;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "contact_info")
    private String contactInfo;

    public Organization() {}

    public Organization(String name, String type, String contactInfo) {
        this.name = name;
        this.type = type;
        this.contactInfo = contactInfo;
    }

    public Organization(int orgId, String name, String type, String contactInfo) {
        this.orgId = orgId;
        this.name = name;
        this.type = type;
        this.contactInfo = contactInfo;
    }

    public int getOrgId() { 
        return orgId; 
    }
    public void setOrgId(int orgId) { 
        this.orgId = orgId; 
    }

    public String getName() { 
        return name; 
    }
    public void setName(String name) {
        this.name = name; 
    }

    public String getType() { 
        return type; 
    }
    public void setType(String type) {
        this.type = type; 
    }

    public String getContactInfo() {
        return contactInfo; 
    }
    public void setContactInfo(String contactInfo) { 
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return "Org ID: " + orgId + " | Name: " + name + " (" + type + ")";
    }
}