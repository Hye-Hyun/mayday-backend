package com.mayday.global.security;


import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    public String createToken(Long userId, String email){
        return "temporary-token";
    }
}
