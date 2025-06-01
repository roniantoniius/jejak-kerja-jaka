package com.example.roniantonius.jejakkerja.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppUserRequest {
    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
