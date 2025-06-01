package com.example.roniantonius.jejakkerja.service.impl;

import com.example.roniantonius.jejakkerja.domain.entity.AppUser;
import com.example.roniantonius.jejakkerja.domain.repository.AppUserRepository;
import com.example.roniantonius.jejakkerja.dto.user.AppUserResponse;
import com.example.roniantonius.jejakkerja.mapper.AppUserMapper;
import com.example.roniantonius.jejakkerja.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    @Override
    public AppUserResponse findOrCreateAppUserDto(JwtAuthenticationToken principal) {
        AppUser appUser = findOrCreateUserInternal(principal);
        return appUserMapper.toAppUserResponse(appUser);
    }

    @Override
    public AppUser findOrCreateAppUserEntity(JwtAuthenticationToken principal) {
        return findOrCreateUserInternal(principal);
    }

    private AppUser findOrCreateUserInternal(JwtAuthenticationToken principal) {
        Jwt jwt = principal.getToken();
        String keycloakId = jwt.getSubject();
        Optional<AppUser> existingUser = appUserRepository.findByKeycloakId(keycloakId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            AppUser newUser = AppUser.builder()
                    .keycloakId(keycloakId)
                    .username(jwt.getClaimAsString("preferred_username")) // Standard OIDC claim
                    .email(jwt.getClaimAsString("email")) // Standard OIDC claim
                    .firstName(jwt.getClaimAsString("given_name")) // Standard OIDC claim
                    .lastName(jwt.getClaimAsString("family_name")) // Standard OIDC claim
                    .build();
            log.info("Creating new AppUser for Keycloak ID: {}", keycloakId);
            return appUserRepository.save(newUser);
        }
    }
}
