package mk.mdt.budgetapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.mdt.budgetapi.dto.LoginRequest;
import mk.mdt.budgetapi.dto.RegisterUserRequest;
import mk.mdt.budgetapi.dto.RegisterUserResponse;
import mk.mdt.budgetapi.dto.UserResponse;
import mk.mdt.budgetapi.entity.Role;
import mk.mdt.budgetapi.entity.User;
import mk.mdt.budgetapi.exception.EmailAlreadyExistsException;
import mk.mdt.budgetapi.mapper.UserMapper;
import mk.mdt.budgetapi.repository.UserRepository;
import mk.mdt.budgetapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import mk.mdt.budgetapi.dto.ForgotPasswordRequest;
import mk.mdt.budgetapi.dto.ResetPasswordRequest;
import mk.mdt.budgetapi.entity.PasswordResetToken;
import mk.mdt.budgetapi.exception.InvalidResetTokenException;
import mk.mdt.budgetapi.exception.UserNotFoundException;
import mk.mdt.budgetapi.repository.PasswordResetTokenRepository;
import mk.mdt.budgetapi.service.EmailService;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private static final long RESET_TOKEN_EXPIRATION_MINUTES = 30;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;

    @Override
    public RegisterUserResponse register(RegisterUserRequest registerUserRequest) {
        if (userRepository.existsByEmail(registerUserRequest.email())) {
            throw new EmailAlreadyExistsException(registerUserRequest.email());
        }

        User user = userMapper.toUser(registerUserRequest);

        user.setPassword(passwordEncoder.encode(registerUserRequest.password()));
        user.setRole(Role.user);
        user.setEnabled(true);

        return userMapper.toRegisterUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email().trim().toLowerCase())
                .filter(User::isEnabled)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid email or password."));

        user.setLastLogin(LocalDateTime.now());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .filter(User::isEnabled)
                .orElseThrow(() -> new UserNotFoundException(email));

        passwordResetTokenRepository.deleteByUser(user);
        passwordResetTokenRepository.flush();

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(
                token, user, LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRATION_MINUTES));
        passwordResetTokenRepository.save(resetToken);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new InvalidResetTokenException("Passwords do not match.");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.token())
                .orElseThrow(() -> new InvalidResetTokenException("Invalid reset token."));

        if (resetToken.isUsed()) {
            throw new InvalidResetTokenException("Reset token has already been used.");
        }
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException("Reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}