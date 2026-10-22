package mk.mdt.budgetapi.mapper;

import mk.mdt.budgetapi.dto.RegisterUserRequest;
import mk.mdt.budgetapi.dto.RegisterUserResponse;
import mk.mdt.budgetapi.dto.UserResponse;
import mk.mdt.budgetapi.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User toUser(RegisterUserRequest request);

    RegisterUserResponse toRegisterUserResponse(User user);

}