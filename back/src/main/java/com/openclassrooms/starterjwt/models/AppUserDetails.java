package com.openclassrooms.starterjwt.models;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Implémentation de {@link UserDetails} adaptée à l'entité {@link User}.
 * 
 * Cette classe est utilisée par Spring Security pour représenter un utilisateur authentifié.
 */
public class AppUserDetails implements UserDetails {

    private final User user;

    public AppUserDetails(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.isAdmin()) {
            return List.of(() -> "ROLE_ADMIN", () -> "ROLE_USER");
        } else {
            return List.of(() -> "ROLE_USER");
        }
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    public Long getId() {
        return user.getId();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return user.getUpdatedAt();
    }

    @Override
    public String toString() {
        return "AppUserDetails{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", admin=" + isAdmin() +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                '}';
    }
}
