package com.flowtex.IAM.Domain.Services;

import com.flowtex.IAM.Domain.Model.Aggregates.User;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    List<User> getAllUsers();
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
}
