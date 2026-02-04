package com.kvstech.tickets.utils;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public class Util {
    private Util(){}

    public static UUID parseUserId(Jwt jwt){
        return UUID.fromString(jwt.getSubject());
    }
}
