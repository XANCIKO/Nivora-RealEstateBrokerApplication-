package com.capstone.realestate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // BROKER or CUSTOMER

    private String mobile;
    private String city;

    @Column(length = 120)
    private String passwordResetToken;

    private LocalDateTime passwordResetTokenExpiry;
}
