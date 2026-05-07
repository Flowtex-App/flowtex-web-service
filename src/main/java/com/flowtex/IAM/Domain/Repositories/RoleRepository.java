package com.flowtex.IAM.Domain.Repositories;

import com.flowtex.IAM.Domain.Model.Entities.Role;
import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Roles name);
}
