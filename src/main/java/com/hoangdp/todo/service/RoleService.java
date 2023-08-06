package com.hoangdp.todo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hoangdp.todo.entity.Role;
import com.hoangdp.todo.repository.RoleRepository;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public Role create(Role role) {        
        return roleRepository.save(role);
    }

    public Role update(Role role) {
        final Role t = roleRepository.findById(role.getId()).orElse(null);
        if (t == null) {
            return null;
        }
        return roleRepository.save(role);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role findById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    public boolean existsById(Long id){
        return roleRepository.existsById(id);
    }
}
