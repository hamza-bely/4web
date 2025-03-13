package com.microservice.microservice_user_service.service;

import com.microservice.microservice_user_service.model.Role;
import com.microservice.microservice_user_service.model.User;
import com.microservice.microservice_user_service.repository.RoleRepository;
import com.microservice.microservice_user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public Role createRole(String request) {
        Optional<Role> existingRole = roleRepository.findByName(request);
        if (existingRole.isPresent()) {
            throw new RuntimeException("Role existe déjà");
        }
        Role newRole = new Role(request);
        return roleRepository.save(newRole);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public User assignRoleToUser(Long userId, Long roleId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Role> roleOptional = roleRepository.findById(roleId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        if (roleOptional.isEmpty()) {
            throw new RuntimeException("Role not found");
        }

        User user = userOptional.get();
        Role role = roleOptional.get();

        user.getRoles().add(role);
        return userRepository.save(user);
    }

    // Supprimer un rôle
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RuntimeException("Role not found");
        }
        roleRepository.deleteById(roleId);
    }
}
