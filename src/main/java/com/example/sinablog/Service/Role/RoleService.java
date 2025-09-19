package com.example.sinablog.Service.Role;

import com.example.sinablog.model.Role;
import com.example.sinablog.model.enums.RoleName;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    Role createRole(Role role);
    Role createRole(RoleName roleName);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
    Optional<Role> getRoleById(Long id);
    Optional<Role> getRoleByName(RoleName roleName);
    List<Role> getAllRoles();
    boolean existsByName(RoleName roleName);
    void initializeDefaultRoles();
    Role findOrCreateRole(RoleName roleName);

    // متدهای جدید
    long countRoles();
    void validateRoleExists(RoleName roleName);
    boolean isRoleInUse(RoleName roleName);
    long countUsersWithRole(RoleName roleName);
}