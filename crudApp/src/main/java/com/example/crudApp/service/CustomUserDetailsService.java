package com.example.crudApp.service;

import com.example.crudApp.model.User;
import com.example.crudApp.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository repo) {
        this.userRepository = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (u.getRoles() != null && !u.getRoles().isBlank()) {
            String[] parts = u.getRoles().split(",");
            for (String r : parts) {
                authorities.add(new SimpleGrantedAuthority(r.trim()));
            }
        }
        return new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
    }
}
