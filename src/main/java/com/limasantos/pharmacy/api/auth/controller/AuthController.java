package com.limasantos.pharmacy.api.auth.controller;



import com.limasantos.pharmacy.api.auth.dto.AuthenticationDTO;
import com.limasantos.pharmacy.api.auth.dto.LoginResponseDTO;
import com.limasantos.pharmacy.api.auth.dto.RegisterDTO;
import com.limasantos.pharmacy.api.infra.service.TokenService;
import com.limasantos.pharmacy.api.user.models.User;
import com.limasantos.pharmacy.api.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private final AuthenticationManager authenticationManager;


    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;



    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO authenticationDTO) {

        var usernamePassword = new UsernamePasswordAuthenticationToken(
                authenticationDTO.username(),
                authenticationDTO.password()
        );

        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));

    }


    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO registerDTO) {

        if (this.userRepository.findByUsername(registerDTO.username()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");

        } else {
           String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());

            User NewUser = new User();
            NewUser.setUsername(registerDTO.username());
            NewUser.setPassword(encryptedPassword);
            NewUser.setEmail(registerDTO.email());
            NewUser.setRole(registerDTO.role());

            this.userRepository.save(NewUser);

            return ResponseEntity.ok().build();


        }
    }
}



