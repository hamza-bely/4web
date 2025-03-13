package com.microservice.microservice_user_service.controller;

import com.microservice.microservice_user_service.model.dto.LoginRequest;
import com.microservice.microservice_user_service.model.dto.RegisterRequest;
import com.microservice.microservice_user_service.model.dto.api.ApiResponse;
import com.microservice.microservice_user_service.model.dto.user.UserLoginResponse;
import com.microservice.microservice_user_service.model.dto.user.UserRegisterResponse;
import com.microservice.microservice_user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserRegisterResponse>> register(@RequestBody RegisterRequest request) {
        try {
            UserRegisterResponse response = userService.registerUser(request);
            ApiResponse<UserRegisterResponse> apiResponse = new ApiResponse<>("User registered successfully", response);
            return ResponseEntity.ok(apiResponse);
        } catch (RuntimeException e) {
            ApiResponse<UserRegisterResponse> errorResponse = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            UserLoginResponse response = userService.loginUser(request);
            ApiResponse<UserLoginResponse> apiResponse = new ApiResponse<>("Login successful", response);
            return ResponseEntity.ok(apiResponse);
        } catch (RuntimeException e) {
            ApiResponse<UserLoginResponse> errorResponse = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);

        }
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<ApiResponse<UserRegisterResponse>> updateUser(@PathVariable Long userId, @RequestBody RegisterRequest request) {
        try {
            UserRegisterResponse response = userService.updateUser(userId, request);
            ApiResponse<UserRegisterResponse> apiResponse = new ApiResponse<>("User updated successfully", response);
            return ResponseEntity.ok(apiResponse);
        } catch (RuntimeException e) {
            ApiResponse<UserRegisterResponse> errorResponse = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        try {
            String message = userService.deleteUser(userId);
            ApiResponse<String> apiResponse = new ApiResponse<>(message, null);
            return ResponseEntity.ok(apiResponse);
        } catch (RuntimeException e) {
            ApiResponse<String> errorResponse = new ApiResponse<>(e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
