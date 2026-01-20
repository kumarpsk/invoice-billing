package com.yourcompany.pos.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(authentication.getName())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String token = jwtService.generateToken(user.getUsername());
        return new LoginResponse(token, user.getRole().getName(), user.getFullName());
    }

    public MeResponse me(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return new MeResponse(user.getUsername(), user.getFullName(), user.getRole().getName());
    }

    public void ensureAdminUser() {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }
        Role role = roleRepository.findByName("ADMIN")
            .orElseThrow(() -> new IllegalArgumentException("Role ADMIN missing"));
        User user = new User();
        user.setUsername("admin");
        user.setFullName("Administrator");
        user.setPasswordHash(passwordEncoder.encode("admin123"));
        user.setRole(role);
        userRepository.save(user);
    }
}
