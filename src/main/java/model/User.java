package model;

import java.time.LocalDateTime;

/**
 * Represents a system user (donor, ngo, or admin).
 */
public class User {

    private int           id;
    private String        name;
    private String        email;
    private String        password;        // BCrypt hash
    private String        role;            // "donor" | "ngo" | "admin"
    private String        phone;
    private String        address;
    private boolean       approved;        // NGO approval flag
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String name, String email, String password,
                String role, String phone, String address,
                boolean approved, LocalDateTime createdAt) {
        this.id        = id;
        this.name      = name;
        this.email     = email;
        this.password  = password;
        this.role      = role;
        this.phone     = phone;
        this.address   = address;
        this.approved  = approved;
        this.createdAt = createdAt;
    }

    // ── Getters ──────────────────────────────────────────────

    public int           getId()        { return id; }
    public String        getName()      { return name; }
    public String        getEmail()     { return email; }
    public String        getPassword()  { return password; }
    public String        getRole()      { return role; }
    public String        getPhone()     { return phone; }
    public String        getAddress()   { return address; }
    public boolean       isApproved()   { return approved; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ──────────────────────────────────────────────

    public void setId(int id)                      { this.id        = id; }
    public void setName(String name)               { this.name      = name; }
    public void setEmail(String email)             { this.email     = email; }
    public void setPassword(String password)       { this.password  = password; }
    public void setRole(String role)               { this.role      = role; }
    public void setPhone(String phone)             { this.phone     = phone; }
    public void setAddress(String address)         { this.address   = address; }
    public void setApproved(boolean approved)      { this.approved  = approved; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "', role='" + role + "'}";
    }
}
