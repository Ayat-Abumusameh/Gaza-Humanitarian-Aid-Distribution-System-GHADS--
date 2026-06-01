package models;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @ManyToOne
    @JoinColumn(name = "org_id")
    private Organization organization;

    public User() {}

    public User(String username, String password, String fullName, String email, String role, Organization organization) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.organization = organization;
    }

    public int getUserId() { 
        return userId; 
    }
    public void setUserId(int userId) {
        this.userId = userId; 
    }

    public String getUsername() { 
        return username; 
    }
    public void setUsername(String username) { 
        this.username = username; 
    }

    public String getPassword() { 
        return password; 
    }
    public void setPassword(String password) { 
        this.password = password; 
    }

    public String getFullName() { 
        return fullName; 
    }
    public void setFullName(String fullName) { 
        this.fullName = fullName; 
    }

    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getRole() { 
        return role; 
    }
    public void setRole(String role) {
        this.role = role; 
    }

    public Organization getOrganization() { 
        return organization; 
    }
    public void setOrganization(Organization organization) { 
        this.organization = organization; 
    }

    @Override
    public String toString() {
        return "User: " + fullName + " (" + role + ")";
    }
}