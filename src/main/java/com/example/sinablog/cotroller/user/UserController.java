package com.example.sinablog.cotroller.user;

import com.example.sinablog.Service.Role.RoleService;
import com.example.sinablog.Service.User.UserService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.dtos.user.PasswordChangeDto;
import com.example.sinablog.dtos.user.UserResponseDto;
import com.example.sinablog.dtos.user.UserUpdateDto;
import com.example.sinablog.model.User;
import com.example.sinablog.model.enums.RoleName;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private  final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(Pageable pageable) {
        try {
            Page<User> users = userService.getAllUsers(pageable);
            Page<UserResponseDto> response = users.map(this::convertToResponseDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed to get users");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        try {

            checkUserAccess(id);

            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuleException("User not found with ID"));

            return ResponseEntity.ok(convertToResponseDto(user));
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to get user: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUserProfile() {
        try {
            Long currentUserId = getCurrentUserId();
            User user = userService.getUserById(currentUserId)
                    .orElseThrow(() -> new RuleException("User not found"));

            return ResponseEntity.ok(convertToResponseDto(user));
        } catch (RuleException e) {
            throw new RuleException("Failed to get user profile");
        } catch (Exception e) {
            throw new RuleException("Failed to get user profile");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id,
                                                      @Valid @RequestBody UserUpdateDto updateDto) {
        try {

            checkUserAccess(id);

            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuleException("User not found with ID: " + id));


            if (updateDto.getUsername() != null) {
                user.setUsername(updateDto.getUsername());
            }
            if (updateDto.getEmail() != null) {
                user.setEmail(updateDto.getEmail());
            }
            if (updateDto.getFullName() != null) {
                user.setFullName(updateDto.getFullName());
            }
            if (updateDto.getEnabled() != null && isAdmin()) {
                user.setEnabled(updateDto.getEnabled());
            }
            if (updateDto.getRole() != null && isAdmin()) {
                user.setRole(roleService.findOrCreateRole(RoleName.valueOf(updateDto.getRole())));
            }
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(convertToResponseDto(updatedUser));

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to update user");
        }
    }


    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> changeUserStatus(@PathVariable Long id,
                                                            @RequestParam boolean enabled) {
        try {
            userService.changeUserStatus(id, enabled);
            User user = userService.getUserById(id)
                    .orElseThrow(() -> new RuleException("User not found with ID"));

            return ResponseEntity.ok(convertToResponseDto(user));
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to change user status: " + e.getMessage());
        }
    }


    @PostMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id,
                                                 @Valid @RequestBody PasswordChangeDto passwordDto) {
        try {

            checkUserAccess(id);

            userService.changePassword(id, passwordDto.getCurrentPassword(), passwordDto.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to change password: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {

            checkUserAccess(id);

            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to delete user");
        }
    }


    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> restoreUser(@PathVariable Long id) {
        try {
            userService.restoreUser(id);
            User user = userService.getAnyUserById(id)
                    .orElseThrow(() -> new RuleException("User not found with ID"));

            return ResponseEntity.ok(convertToResponseDto(user));
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to restore user: " + e.getMessage());
        }
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable RoleName roleName) {
        try {
            List<User> users = userService.getUsersByRole(roleName);
            List<UserResponseDto> response = users.stream()
                    .map(this::convertToResponseDto)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed to get users by role");
        }
    }


    @GetMapping("/check-admin")
    public ResponseEntity<String> checkAdminStatus() {
        try {
            boolean isAdmin = isAdmin();
            String message = isAdmin ? "User is an administrator" : "User is not an administrator";
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            throw new RuleException("Failed to check admin status");
        }
    }


    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getDeletedUsers() {
        try {
            List<User> deletedUsers = userService.getDeletedUsers();
            List<UserResponseDto> response = deletedUsers.stream()
                    .map(this::convertToResponseDto)
                    .toList();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuleException("Failed to get deleted users");
        }
    }


    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuleException("User not authenticated");
        }

        String username = authentication.getName();
        Optional<User> user = userService.getUserByUsername(username);

        return user.map(User::getId)
                .orElseThrow(() -> new RuleException("Current user not found"));
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuleException("User not authenticated");
        }
        return authentication.getName();
    }

    // متد کمکی برای بررسی دسترسی
    private void checkUserAccess(Long targetUserId) {
        // اگر کاربر ادمین است، اجازه دسترسی دارد
        if (isAdmin()) {
            return;
        }

        // اگر کاربر عادی است، فقط به اطلاعات خودش دسترسی دارد
        Long currentUserId = getCurrentUserId();
        if (!targetUserId.equals(currentUserId)) {
            throw new RuleException("You can only access your own data.");
        }
    }

    // متد کمکی برای تبدیل User به UserResponseDto
    private UserResponseDto convertToResponseDto(User user) {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
        responseDto.setFullName(user.getFullName());
        responseDto.setRole(user.getRole().getName());
        responseDto.setCreatedAt(user.getCreatedAt());
        responseDto.setUpdatedAt(user.getUpdatedAt());
        responseDto.setEnabled(user.isEnabled());
        return responseDto;
    }
}