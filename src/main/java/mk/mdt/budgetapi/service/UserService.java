package mk.mdt.budgetapi.service;

import mk.mdt.budgetapi.dto.UserMetadataRequest;
import mk.mdt.budgetapi.dto.UserResponse;

import java.util.Optional;

public interface UserService {

    Optional<UserResponse> updateMetadata(Long id, UserMetadataRequest request);
}