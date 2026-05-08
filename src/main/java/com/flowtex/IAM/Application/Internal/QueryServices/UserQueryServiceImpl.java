package com.flowtex.IAM.Application.Internal.QueryServices;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Model.ValueObjects.Area;
import com.flowtex.IAM.Domain.Model.ValueObjects.Position;
import com.flowtex.IAM.Domain.Repositories.UserRepository;
import com.flowtex.IAM.Domain.Services.UserQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getByEmployeeCode(String employeeCode) {
        return userRepository.findByEmployeeCode(employeeCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> searchByText(String query) {
        if (query == null || query.isBlank()) return userRepository.findAll();
        return userRepository.searchByText(query.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getByArea(Area area) {
        return userRepository.findByArea(area);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getByAreaAndPosition(Area area, Position position) {
        return userRepository.findByAreaAndPosition(area, position);
    }
}
