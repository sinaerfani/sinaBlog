package com.example.sinablog.Repository;

import com.example.sinablog.model.Role;
import com.example.sinablog.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);

    boolean existsByName(RoleName name);

    // برای بررسی اینکه آیا نقش در حال استفاده است (توسط کاربران فعال)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM User u WHERE u.role.name = :roleName AND u.disableDate IS NULL")
    boolean existsByUsersWithRoleAndNotDisabled(@Param("roleName") RoleName roleName);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName AND u.disableDate IS NULL")
    long countUsersWithRoleAndNotDisabled(@Param("roleName") RoleName roleName);
}
