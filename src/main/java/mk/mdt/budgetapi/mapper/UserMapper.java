package mk.mdt.budgetapi.mapper;

import mk.mdt.budgetapi.dto.UserResponse;
import mk.mdt.budgetapi.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);
}