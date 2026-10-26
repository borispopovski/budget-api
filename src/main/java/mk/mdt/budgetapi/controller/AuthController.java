package mk.mdt.budgetapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.mdt.budgetapi.dto.LoginRequest;
import mk.mdt.budgetapi.dto.RegisterUserRequest;
import mk.mdt.budgetapi.dto.RegisterUserResponse;
import mk.mdt.budgetapi.dto.UserResponse;
import mk.mdt.budgetapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mk.mdt.budgetapi.dto.ForgotPasswordRequest;
import mk.mdt.budgetapi.dto.MessageResponse;
import mk.mdt.budgetapi.dto.ResetPasswordRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(authService.register(registerUserRequest));
    }


    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(new MessageResponse("Password reset link has been sent to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
    }
}