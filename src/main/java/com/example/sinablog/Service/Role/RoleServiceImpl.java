package com.example.sinablog.Service.Role;

import com.example.sinablog.Repository.RoleRepository;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Role;
import com.example.sinablog.model.enums.RoleName;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role createRole(Role role) {

        if (role.getName() == null) {
            throw new RuleException("Role name cannot be null");
        }

        if (roleRepository.existsByName(role.getName())) {
            throw new RuleException("Role with name " + role.getName() + " already exists");
        }

        return roleRepository.save(role);
    }

    @Override
    public Role createRole(RoleName roleName) {

        if (roleName == null) {
            throw new RuleException("Role name cannot be null");
        }

        if (roleRepository.existsByName(roleName)) {
            throw new RuleException("Role with name " + roleName + " already exists");
        }

        Role role = new Role();
        role.setName(roleName);

        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long id, Role role) {

        if (id == null) {
            throw new RuleException("Role ID cannot be null");
        }

        if (role == null) {
            throw new RuleException("Role object cannot be null");
        }

        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new RuleException("Role not found with ID"));

        if (role.getName() == null) {
            throw new RuleException("Role name cannot be null");
        }

        if (!existingRole.getName().equals(role.getName()) &&
                roleRepository.existsByName(role.getName())) {
            throw new RuleException("Role already exists");
        }

        existingRole.setName(role.getName());

        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRole(Long id) {

        if (id == null) {
            throw new RuleException("Role ID cannot be null");
        }

        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuleException("Role not found with ID: " + id));

        // بررسی اینکه نقش در حال استفاده نباشد (توسط کاربران فعال)
        if (isRoleInUse(role.getName())) {
            long userCount = countUsersWithRole(role.getName());
            throw new RuleException("Cannot delete role. There are " + userCount + " active users assigned to this role.");
        }

        try {
            roleRepository.delete(role);
        } catch (Exception e) {
            throw new RuleException("Failed to delete role with ID");
        }
    }

    @Override
    public Optional<Role> getRoleById(Long id) {

        if (id == null) {
            throw new RuleException("Role ID cannot be null");
        }

        return roleRepository.findById(id);
    }

    @Override
    public Optional<Role> getRoleByName(RoleName roleName) {

        if (roleName == null) {
            throw new RuleException("Role name cannot be null");
        }

        return roleRepository.findByName(roleName);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public boolean existsByName(RoleName roleName) {
        if (roleName == null) {
            throw new RuleException("Role name cannot be null");
        }

        return roleRepository.existsByName(roleName);
    }

    @Override
    public void initializeDefaultRoles() {

        try {
            for (RoleName roleName : RoleName.values()) {
                if (!roleRepository.existsByName(roleName)) {
                    Role role = new Role();
                    role.setName(roleName);
                    roleRepository.save(role);
                }
            }
        } catch (Exception e) {
            throw new RuleException("Failed to initialize default roles");
        }
    }

    @Override
    public Role findOrCreateRole(RoleName roleName) {

        if (roleName == null) {
            throw new RuleException("Role name cannot be null");
        }

        try {
            return roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(roleName);
                        return roleRepository.save(newRole);
                    });
        } catch (Exception e) {
            throw new RuleException("Failed to find or create role");
        }
    }

    @Override
    public long countRoles() {
        try {
            return roleRepository.count();
        } catch (Exception e) {
            throw new RuleException("Failed to count roles");
        }
    }

    @Override
    public void validateRoleExists(RoleName roleName) {
        if (roleName == null) {
            throw new RuleException("Role name cannot be null");
        }

        if (!roleRepository.existsByName(roleName)) {
            throw new RuleException("Role does not exist");
        }
    }

    @Override
    public boolean isRoleInUse(RoleName roleName) {
        if (roleName == null) {
            throw new RuleException("Role name cannot be null");
        }

        try {
            return roleRepository.existsByUsersWithRoleAndNotDisabled(roleName);
        } catch (Exception e) {
            throw new RuleException("Failed to check if role is in use");
        }
    }

    @Override
    public long countUsersWithRole(RoleName roleName) {
        if (roleName == null) {
            throw new RuleException("Role name cannot be null");
        }

        try {
            return roleRepository.countUsersWithRoleAndNotDisabled(roleName);
        } catch (Exception e) {
            throw new RuleException("Failed to count users with role");
        }
    }

}
