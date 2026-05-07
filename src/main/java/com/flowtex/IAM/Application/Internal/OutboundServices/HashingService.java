package com.flowtex.IAM.Application.Internal.OutboundServices;

public interface HashingService {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
