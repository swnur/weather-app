package com.swnur.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Login is required and can not be empty")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    private String login;

    @NotBlank(message = "Password is required and can not be empty")
    @Size(min = 3, max = 255, message = "Password must be between 3 and 255 characters")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Location> locations;

    public User(String login, String password, List<Location> locations) {
        this.login = login;
        this.password = password;
        this.locations = locations;
    }
}
