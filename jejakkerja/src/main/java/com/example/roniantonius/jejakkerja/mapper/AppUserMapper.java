package com.example.roniantonius.jejakkerja.mapper;

import com.example.roniantonius.jejakkerja.domain.entity.AppUser;
import com.example.roniantonius.jejakkerja.dto.user.AppUserResponse;
import com.example.roniantonius.jejakkerja.dto.user.CreateAppUserRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppUserMapper {

    AppUserResponse toAppUserResponse(AppUser appUser);

    AppUser toAppUser(CreateAppUserRequest request);

    List<AppUserResponse> toAppUserResponseList(List<AppUser> appUsers);
}
