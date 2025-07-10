package com.swnur.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "session")
@NamedQuery(
        name = "Session.findByIdAndFetchUser",
        query = "SELECT s FROM Session s JOIN FETCH s.user WHERE s.id = :id"
)
@NamedQuery(
        name = "Session.deleteByExpired",
        query = "DELETE FROM Session s WHERE s.expiresAt <: now"
)
@NamedQuery(
        name = "Session.deleteByExpiredForUser",
        query = "DELETE FROM Session s WHERE s.user = :user AND s.expiresAt < :now"
)
@NamedQuery(
        name = "Session.deleteById",
        query = "DELETE FROM Session s WHERE s.id = :id"
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Session must be associated with user")
    private User user;

    @Column(name = "expires_at", nullable = false)
    @NotNull(message = "Date time can not be null")
    private LocalDateTime expiresAt;

    public Session(User user, LocalDateTime expiresAt) {
        this.user = user;
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return id != null && id.equals(session.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
