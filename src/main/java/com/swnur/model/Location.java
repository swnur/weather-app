package com.swnur.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "location", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "name", "latitude", "longitude"})
})
@NamedQuery(
        name = "Location.findByUser",
        query = "SELECT l FROM Location l WHERE l.user = :user ORDER BY l.name ASC"
)
@NamedQuery(
        name = "Location.findByIdAndUser",
        query = "SELECT l FROM Location l WHERE l.id = :id AND l.user = :user"
)
@NamedQuery(
        name = "Location.findByUserAndDetails",
        query = "SELECT l FROM Location l WHERE l.user = :user AND l.name = :name AND l.latitude = :latitude AND l.longitude = :longitude"
)
@NamedQuery(
        name = "Location.deleteByIdAndUser",
        query = "DELETE FROM Location l WHERE l.id = :id AND l.user = :user"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Integer id;

    @NotNull(message = "Name should not be null")
    @Column(nullable = false)
    private String name;

    @NotNull(message = "Latitude can not be null")
    @Column(nullable = false)
    private BigDecimal latitude;

    @NotNull(message = "Longitude can not be null")
    @Column(nullable = false)
    private BigDecimal longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @NotNull(message = "Location must be associated with the user")
    private User user;

    public Location(String name, BigDecimal latitude, BigDecimal longitude, User user) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id != null && id.equals(location.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
