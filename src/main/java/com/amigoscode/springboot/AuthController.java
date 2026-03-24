package com.amigoscode.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(TokenService tokenService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(400).body("Username is already taken!");
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        // CRITICAL: Hash the password before saving!
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        String role = registerRequest.getRole() != null ? registerRequest.getRole() : "VIEWER";
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        //Fetch the user from the database
        log.info("Logging in");
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        //B-Crypt verification logic
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        //Password is correct! Generate the HS256 token
        String token = tokenService.generateToken(user);
        return ResponseEntity.ok(token);
    }


}
