package org.example.springboot.repository;

import java.util.Optional;
import org.example.springboot.enums.RoleName;
import org.example.springboot.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
