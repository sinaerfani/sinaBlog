package com.example.sinablog.cotroller;


import com.example.sinablog.Service.Role.RoleService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.Role;
import com.example.sinablog.model.enums.RoleName;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')") // تمام endpointها فقط برای ادمین
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    // ایجاد نقش جدید
    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        try {
            Role createdRole = roleService.createRole(role);
            return ResponseEntity.ok(createdRole);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to create role");
        }
    }

    // دریافت همه نقش‌ها
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            throw new RuleException("Failed to get roles");
        }
    }

    // دریافت نقش بر اساس ID
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id)
                    .orElseThrow(() -> new RuleException("Role not found with ID"));
            return ResponseEntity.ok(role);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to get role");
        }
    }

    // دریافت نقش بر اساس نام
    @GetMapping("/name/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable RoleName roleName) {
        try {
            Role role = roleService.getRoleByName(roleName)
                    .orElseThrow(() -> new RuleException("Role not found with name"));
            return ResponseEntity.ok(role);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to get role");
        }
    }

    // به‌روزرسانی نقش
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @Valid @RequestBody Role role) {
        try {
            Role updatedRole = roleService.updateRole(id, role);
            return ResponseEntity.ok(updatedRole);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to update role");
        }
    }

    // حذف نقش
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok("Role deleted successfully");
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to delete role: " + e.getMessage());
        }
    }

    // بررسی وجود نقش
    @GetMapping("/exists/{roleName}")
    public ResponseEntity<Boolean> checkRoleExists(@PathVariable RoleName roleName) {
        try {
            boolean exists = roleService.existsByName(roleName);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            throw new RuleException("Failed to check role existence");
        }
    }

    // بررسی استفاده شدن نقش
    @GetMapping("/{id}/in-use")
    public ResponseEntity<Boolean> checkRoleInUse(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id)
                    .orElseThrow(() -> new RuleException("Role not found with ID"));
            boolean inUse = roleService.isRoleInUse(role.getName());
            return ResponseEntity.ok(inUse);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to check role usage");
        }
    }

    // شمارش کاربران یک نقش
    @GetMapping("/{id}/user-count")
    public ResponseEntity<Long> countUsersWithRole(@PathVariable Long id) {
        try {
            Role role = roleService.getRoleById(id)
                    .orElseThrow(() -> new RuleException("Role not found with ID: " + id));
            long count = roleService.countUsersWithRole(role.getName());
            return ResponseEntity.ok(count);
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to count users with role");
        }
    }


}
