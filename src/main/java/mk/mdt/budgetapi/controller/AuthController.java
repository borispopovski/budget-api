package mk.mdt.budgetapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mk.mdt.budgetapi.dto.*;
import mk.mdt.budgetapi.entity.User;
import mk.mdt.budgetapi.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/{email}")
    public ResponseEntity<RegisterUserResponse> findByEmail(@PathVariable String email) {
        return authService
                .findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<RegisterUserResponse> me(@AuthenticationPrincipal User user) {
        return authService
                .findByEmail(user.getEmail())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(authService.register(registerUserRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponse> login(@Valid @RequestBody LoginUserRequest loginUserRequest) {
        return ResponseEntity.ok(authService.login(loginUserRequest));

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