package mk.mdt.budgetapi.service;

import mk.mdt.budgetapi.dto.LoginRequest;
import mk.mdt.budgetapi.dto.UserResponse;

public interface AuthService {

    UserResponse login(LoginRequest request);
}