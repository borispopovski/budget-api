package mk.mdt.budgetapi.service.impl;

import lombok.RequiredArgsConstructor;
import mk.mdt.budgetapi.dto.UserMetadataRequest;
import mk.mdt.budgetapi.dto.UserResponse;
import mk.mdt.budgetapi.mapper.UserMapper;
import mk.mdt.budgetapi.repository.UserRepository;
import mk.mdt.budgetapi.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import mk.mdt.budgetapi.exception.EmailAlreadyExistsException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public Optional<UserResponse> updateMetadata(Long id, UserMetadataRequest request) {
        return userRepository.findById(id).map(user -> {
            boolean emailChanged = !user.getEmail().equalsIgnoreCase(request.email());
            if (emailChanged && userRepository.existsByEmail(request.email())) {
                throw new EmailAlreadyExistsException(request.email());
            }

            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setEmail(request.email());

            return userMapper.toUserResponse(userRepository.save(user));
        });
    }
}