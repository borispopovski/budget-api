package mk.mdt.budgetapi.service;

import mk.mdt.budgetapi.dto.*;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface AuthService extends UserDetailsService {
    Optional<RegisterUserResponse> findByEmail(String email);

    RegisterUserResponse register(RegisterUserRequest registerUserRequest);

    LoginUserResponse login(LoginUserRequest request);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}