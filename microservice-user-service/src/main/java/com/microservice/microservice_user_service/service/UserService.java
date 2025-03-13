package com.microservice.microservice_user_service.service;

import com.microservice.microservice_user_service.conf.jwt.JwtTokenProvider;
import com.microservice.microservice_user_service.model.Role;
import com.microservice.microservice_user_service.model.User;
import com.microservice.microservice_user_service.model.dto.LoginRequest;
import com.microservice.microservice_user_service.model.dto.RegisterRequest;
import com.microservice.microservice_user_service.model.dto.user.UserLoginResponse;
import com.microservice.microservice_user_service.model.dto.user.UserRegisterResponse;
import com.microservice.microservice_user_service.repository.RoleRepository;
import com.microservice.microservice_user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Set;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    public UserService(AuthenticationManager authenticationManager,PasswordEncoder passwordEncoder,RoleRepository roleRepository,UserRepository userRepository, JwtTokenProvider jwtTokenProvider)
    {
      this.userRepository =userRepository ;
      this.roleRepository = roleRepository;
      this.passwordEncoder = passwordEncoder;
      this.jwtTokenProvider = jwtTokenProvider;
      this.authenticationManager =authenticationManager;

    }

    public UserRegisterResponse registerUser(RegisterRequest requestUser) {
        if (userRepository.existsByUsername(requestUser.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.existsByEmail(requestUser.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Créer un rôle par défaut "USER"
        Role userRole = roleRepository.findByName("user")
                .orElseGet(() -> roleRepository.save(new Role("user")));

        User user = new User();
        user.setUsername(requestUser.getUsername());
        user.setEmail(requestUser.getEmail());
        user.setPassword(passwordEncoder.encode(requestUser.getPassword()));
        user.setRoles(Set.of(userRole));

        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                savedUser.getUsername(), savedUser.getPassword());
        String token = jwtTokenProvider.generateToken(authentication,user.getRoles());

        // Mapper la réponse
        UserRegisterResponse response = new UserRegisterResponse(savedUser,token);
        response.setUser(savedUser);
        response.setToken(token);

        // Retourner la réponse
        return response;
        }

    public UserLoginResponse loginUser(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        String token = jwtTokenProvider.generateToken(authentication,userOptional.get().getRoles());


        if (userOptional.isPresent()) {
            User user = userOptional.get();

            UserLoginResponse userLoginResponse = new UserLoginResponse(user,token);
            userLoginResponse.setUser(user);
            userLoginResponse.setToken(token);

            return userLoginResponse;
        }
        throw new RuntimeException("Email is already in use!");
    }

    public UserRegisterResponse updateUser(Long userId, RegisterRequest requestUser) {
        // Vérifier si l'utilisateur existe
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found!");
        }

        User user = userOptional.get();

        // Vérifier si le nom d'utilisateur ou l'email sont déjà pris
        if (userRepository.existsByUsername(requestUser.getUsername()) && !user.getUsername().equals(requestUser.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.existsByEmail(requestUser.getEmail()) && !user.getEmail().equals(requestUser.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Mettre à jour les informations de l'utilisateur
        user.setUsername(requestUser.getUsername());
        user.setEmail(requestUser.getEmail());
        user.setPassword(passwordEncoder.encode(requestUser.getPassword()));

        // Sauvegarder l'utilisateur mis à jour
        User updatedUser = userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                updatedUser.getUsername(), updatedUser.getPassword());
        String token = jwtTokenProvider.generateToken(authentication,updatedUser.getRoles());

        // Mapper la réponse
        UserRegisterResponse response = new UserRegisterResponse(updatedUser, token);
        response.setUser(updatedUser);
        response.setToken(token);

        // Retourner la réponse
        return response;
    }

    public String deleteUser(Long userId) {
        // Vérifier si l'utilisateur existe
        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found!");
        }

        // Supprimer l'utilisateur
        userRepository.deleteById(userId);

        return "User deleted successfully!";
    }


}
