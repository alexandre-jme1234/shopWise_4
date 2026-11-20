package com.shopwise.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shopwise.app.dto.request.UserRequest;
import com.shopwise.app.dto.response.UserResponse;
import com.shopwise.app.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	// Le mot de passe est ignoré ici : il doit être haché par le service avant d'être enregistré
	@Mapping(target = "password", ignore = true)
	User toEntity(UserRequest request);

	UserResponse toResponse(User user);

	List<UserResponse> toResponseList(List<User> users);

}
