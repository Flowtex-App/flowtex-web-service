package com.flowtex.IAM.Application.Internal.OutboundServices;

public interface TokenService {
    String generateToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
