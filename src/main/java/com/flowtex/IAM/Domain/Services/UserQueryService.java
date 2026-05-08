package com.flowtex.IAM.Domain.Services;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Model.ValueObjects.Area;
import com.flowtex.IAM.Domain.Model.ValueObjects.Position;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {
    List<User> getAllUsers();
    Optional<User> getById(Long id);
    Optional<User> getByUsername(String username);
    Optional<User> getByEmployeeCode(String employeeCode);
    List<User> searchByText(String query);
    List<User> getByArea(Area area);
    List<User> getByAreaAndPosition(Area area, Position position);
}
