package mk.mdt.budgetapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import mk.mdt.budgetapi.dto.LoginRequest;
import mk.mdt.budgetapi.dto.UserResponse;
import mk.mdt.budgetapi.entity.User;
import mk.mdt.budgetapi.mapper.UserMapper;
import mk.mdt.budgetapi.repository.UserRepository;
import mk.mdt.budgetapi.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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
}