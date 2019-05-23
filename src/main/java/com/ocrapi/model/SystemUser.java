package com.ocrapi.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Document
@NoArgsConstructor
public class SystemUser implements UserDetails {

    @Id
    public String id;

    public String username;
    public String password;
    public String role;
    public boolean enabled;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public Date lastPasswordResetDate;

    private List<Authority> authorities = new ArrayList<>();

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
}
