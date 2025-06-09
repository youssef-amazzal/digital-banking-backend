package com.example.digitalbankingbackend.services;

import com.example.digitalbankingbackend.entities.User;
import com.example.digitalbankingbackend.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        
        // Update last login
        user.setLastLogin(new Date());
        userRepository.save(user);
        
        return UserPrincipal.create(user);
    }
    
    public static class UserPrincipal implements UserDetails {
        private Long id;
        private String username;
        private String email;
        private String password;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean enabled;
        private boolean accountNonExpired;
        private boolean accountNonLocked;
        private boolean credentialsNonExpired;
        
        public UserPrincipal(Long id, String username, String email, String password,
                            Collection<? extends GrantedAuthority> authorities, boolean enabled,
                            boolean accountNonExpired, boolean accountNonLocked, boolean credentialsNonExpired) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
            this.authorities = authorities;
            this.enabled = enabled;
            this.accountNonExpired = accountNonExpired;
            this.accountNonLocked = accountNonLocked;
            this.credentialsNonExpired = credentialsNonExpired;
        }
        
        public static UserPrincipal create(User user) {
            Collection<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
            );
            
            return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.isEnabled(),
                user.isAccountNonExpired(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired()
            );
        }
        
        // Getters
        public Long getId() { return id; }
        public String getEmail() { return email; }
        
        @Override
        public String getUsername() { return username; }
        
        @Override
        public String getPassword() { return password; }
        
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
        
        @Override
        public boolean isAccountNonExpired() { return accountNonExpired; }
        
        @Override
        public boolean isAccountNonLocked() { return accountNonLocked; }
        
        @Override
        public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
        
        @Override
        public boolean isEnabled() { return enabled; }
    }
}
