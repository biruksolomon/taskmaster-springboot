package com.taskmaster_springboot.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmaster_springboot.model.Roles;
import com.taskmaster_springboot.model.Users;
import com.taskmaster_springboot.model.enums.AccountStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserPrincipal implements UserDetails {

    private UUID userId;
    private String username;
    private String email;
    private String firstname;
    private String lastname;

    private Set<Roles> roles = new HashSet<>();

    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    private String password;

    public static CustomUserPrincipal createCustomUserPrincipal(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Roles role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        }
        CustomUserPrincipal principal = new CustomUserPrincipal();
        principal.userId = user.getUserId();
        principal.username = user.getUsername();
        principal.email = user.getEmail();
        principal.firstname = user.getFirstName();
        principal.lastname = user.getLastName();
        principal.password = user.getPassword();
        principal.enabled = user.getStatus() == AccountStatus.ACTIVE; // Assuming you have an active status
        principal.accountNonExpired = true; // Add your own logic here
        principal.accountNonLocked = true; // Add your own logic here
        principal.credentialsNonExpired = true; // Add your own logic here
        principal.authorities = authorities;

        return principal;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
