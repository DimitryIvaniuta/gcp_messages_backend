package com.messages.engine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class User {

    /** Unique identifier for the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GP_UNIQUE_ID")
    @SequenceGenerator(name = "GP_UNIQUE_ID", sequenceName = "GP_UNIQUE_ID", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    /** Login of the user. */
    @Column(name = "login", nullable = false)
    private String login;

    /** Name of the user. */
    @Column(name = "name", nullable = false)
    private String userName;

    /** Email of the user. */
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /** Password of the user. */
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * TOTP secret in Base32 or other format
     */
    @Column(name = "totp_secret")
    private String totpSecret;

    /**
     * Indicates if user has 2FA enabled.
     */
    @Column(name = "two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled;

    public User() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor with all fields.
     *
     * @param login the login.
     * @param name the username.
     * @param email the email.
     * @param password the password.
     */
    public User(String login, String name, String email, String password) {
        this.login = login;
        this.userName = name;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

}
