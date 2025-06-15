package com.lima.vetcare.service;

import com.lima.vetcare.model.User;
import com.lima.vetcare.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void changePassword(User user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Password saat salah");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void changeEmail(User user, String newEmail) {
        Optional<User> existingUserOptional = userRepository.findByEmail(newEmail);
        User existingUser = existingUserOptional.orElse(null);
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Email sudah digunakan");
        }

        user.setEmail(newEmail);
        userRepository.save(user);
    }

    public void deleteAccount(User user) {
        userRepository.delete(user);
    }

    public BCryptPasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}
