package mk.mdt.budgetapi.service;

import mk.mdt.budgetapi.dto.LoginRequest;
import mk.mdt.budgetapi.dto.RegisterUserRequest;
import mk.mdt.budgetapi.dto.RegisterUserResponse;
import mk.mdt.budgetapi.dto.UserResponse;

public interface AuthService {
    RegisterUserResponse register(RegisterUserRequest registerUserRequest);

    UserResponse login(LoginRequest request);
}