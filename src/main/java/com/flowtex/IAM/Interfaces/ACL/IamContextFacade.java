package com.flowtex.IAM.Interfaces.ACL;

import com.flowtex.IAM.Domain.Services.UserQueryService;
import org.springframework.stereotype.Service;

@Service
public class IamContextFacade {

    private final UserQueryService userQueryService;

    public IamContextFacade(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public Long fetchUserIdByUsername(String username) {
        return userQueryService.getByUsername(username)
                .map(user -> user.getId())
                .orElse(0L);
    }
}
