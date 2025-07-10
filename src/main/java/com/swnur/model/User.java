package com.swnur.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NamedQuery(
        name = "User.findById",
        query = "SELECT u FROM User u LEFT JOIN FETCH u.locations WHERE u.id = :id"
)
@NamedQuery(
        name = "User.findByLogin",
        query = "SELECT u FROM User u LEFT JOIN FETCH u.locations WHERE u.login = :login"
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"locations", "password"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer id;

    @NotBlank(message = "Login is required and can not be empty")
    @Size(min = 3, max = 50, message = "Login must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String login;

    @NotBlank(message = "Password is required and can not be empty")
    @Size(min = 3, max = 255, message = "Password must be between 3 and 255 characters")
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Location> locations = new ArrayList<>();

    public void addLocation(Location location) {
        this.locations.add(location);
        location.setUser(this);
    }

    public void removeLocation(Location location) {
        this.locations.remove(location);
        location.setUser(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
