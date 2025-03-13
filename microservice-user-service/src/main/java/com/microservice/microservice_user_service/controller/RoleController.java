package com.microservice.microservice_user_service.controller;

import com.microservice.microservice_user_service.model.Role;
import com.microservice.microservice_user_service.model.User;
import com.microservice.microservice_user_service.model.dto.api.ApiResponse;
import com.microservice.microservice_user_service.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Role>> createRole(@RequestParam String request) {
        try {
            Role response = roleService.createRole(request);
            ApiResponse<Role> apiResponse = new ApiResponse<>("Role créé avec succès", response);

            return ResponseEntity.ok(apiResponse);
        }catch (RuntimeException e){
            ApiResponse<Role> errorResponse = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        try {
            List<Role> response = roleService.getAllRoles();
            ApiResponse<List<Role>> apiResponse = new ApiResponse<>("List Role", response);
            return ResponseEntity.ok(apiResponse);
        }catch (RuntimeException e){
            ApiResponse<List<Role>> errorResponse = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @PostMapping("/assign")
    public ResponseEntity<User> assignRoleToUser(@RequestParam Long userId, @RequestParam Long roleId) {
        User updatedUser = roleService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok("Role deleted successfully");
    }
}
