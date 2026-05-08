package com.flowtex.IAM.Domain.Model.Aggregates;

import com.flowtex.IAM.Domain.Model.Entities.Role;
import com.flowtex.IAM.Domain.Model.ValueObjects.Area;
import com.flowtex.IAM.Domain.Model.ValueObjects.Position;
import com.flowtex.IAM.Domain.Model.ValueObjects.Roles;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 80)
    private String username;

    @Column(unique = true, nullable = false, length = 160)
    private String email;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "employee_code", unique = true, nullable = false, length = 20)
    private String employeeCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Position position;

    @Column(name = "position_specialty", length = 120)
    private String positionSpecialty;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Area area;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public User() {
    }

    public User(String username, String email, String fullName, String passwordHash,
                String employeeCode, Position position, String positionSpecialty, Area area) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.passwordHash = passwordHash;
        this.employeeCode = employeeCode;
        this.position = position;
        this.positionSpecialty = positionSpecialty;
        this.area = area;
    }

    public User addRole(Role role) {
        this.roles.add(role);
        return this;
    }

    public User addRoles(List<Role> roles) {
        this.roles.addAll(roles);
        return this;
    }

    public User replaceRoles(List<Role> next) {
        this.roles.clear();
        this.roles.addAll(next);
        return this;
    }

    public boolean hasRole(Roles roleName) {
        return this.roles.stream().anyMatch(r -> r.getName() == roleName);
    }

    public List<String> getRoleNames() {
        return roles.stream().map(Role::getStringName).collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmployeeCode() { return employeeCode; }
    public Position getPosition() { return position; }
    public String getPositionSpecialty() { return positionSpecialty; }
    public Area getArea() { return area; }
    public Set<Role> getRoles() { return roles; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
