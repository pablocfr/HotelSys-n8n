package com.hotelsys.api.controller;

import com.hotelsys.api.dto.LoginRequest;
import com.hotelsys.api.dto.LoginResponse;
import com.hotelsys.api.repository.UsuarioRepository;
import com.hotelsys.api.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        var user = usuarioRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRol()))
        );

        var jwtToken = jwtService.generateToken(userDetails);
        var loginResponse = LoginResponse.builder()
                .id(user.getId())
                .nombreUsuario(user.getNombreUsuario())
                .email(user.getEmail())
                .rol(user.getRol())
                .token(jwtToken)
                .build();
        return ResponseEntity.ok(loginResponse);
    }
}
