package mk.mdt.budgetapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.mdt.budgetapi.dto.LoginRequest;
import mk.mdt.budgetapi.dto.UserResponse;
import mk.mdt.budgetapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}