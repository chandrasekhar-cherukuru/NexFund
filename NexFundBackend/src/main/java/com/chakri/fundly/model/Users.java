package com.chakri.fundly.model;

import jakarta.persistence.*;
import com.chakri.fundly.model.AuthProvider;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    private String providerId;

    private String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String profileImage;

    public Users() {}

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authProvider = AuthProvider.LOCAL;
    }

    public Users(String email, String name, AuthProvider authProvider, String providerId) {
        this.email = email;
        this.name = name;
        this.username = name;
        this.authProvider = authProvider;
        this.providerId = providerId;
        this.password = null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public AuthProvider getAuthProvider() { return authProvider; }
    public void setAuthProvider(AuthProvider authProvider) { this.authProvider = authProvider; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", authProvider=" + authProvider +
                ", profileImage=" + (profileImage != null ? "present" : "null") +
                '}';
    }
}
