package com.example.roniantonius.jejakkerja.service;

import com.example.roniantonius.jejakkerja.domain.entity.AppUser;
import com.example.roniantonius.jejakkerja.dto.user.AppUserResponse;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface AppUserService {

    AppUserResponse findOrCreateAppUserDto(JwtAuthenticationToken principal);

    AppUser findOrCreateAppUserEntity(JwtAuthenticationToken principal);
}
