package com.hoangdp.todo.service;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hoangdp.todo.entity.User;
import com.hoangdp.todo.repository.RoleRepository;
import com.hoangdp.todo.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findOneByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invaild user"));
        user.setRoles(new HashSet<>(roleRepository.findAllByUserId(user.getId())));
        return user;
    }

    public User loadUserById(Long userId) {
        return userRepository.findById(userId).get();
    }

    public User getCurrentUser() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
