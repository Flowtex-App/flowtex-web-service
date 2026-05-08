package com.flowtex.IAM.Domain.Repositories;

import com.flowtex.IAM.Domain.Model.Aggregates.User;
import com.flowtex.IAM.Domain.Model.ValueObjects.Area;
import com.flowtex.IAM.Domain.Model.ValueObjects.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmployeeCode(String employeeCode);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByEmployeeCode(String employeeCode);

    List<User> findByArea(Area area);
    List<User> findByAreaAndPosition(Area area, Position position);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(u.employeeCode) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<User> searchByText(@Param("q") String query);
}
