package com.yourcompany.pos.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MeResponse {
    private String username;
    private String fullName;
    private String role;
}
