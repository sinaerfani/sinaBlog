package com.example.sinablog.Service.User;
import com.example.sinablog.model.User;
import com.example.sinablog.model.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(User user);
    User registerUser(User user, RoleName roleName);
    User updateUser(Long id, User user);
    void deleteUser(Long id); // Soft Delete
    Optional<User> getUserById(Long id); // فقط کاربران فعال
    Optional<User> getUserByUsername(String username); // فقط کاربران فعال
    Optional<User> getUserByEmail(String email); // فقط کاربران فعال
    List<User> getAllUsers(); // فقط کاربران فعال
    Page<User> getAllUsers(Pageable pageable); // فقط کاربران فعال
    List<User> getUsersByRole(RoleName roleName); // فقط کاربران فعال
    void changeUserStatus(Long id, boolean enabled);
    void changePassword(Long userId, String oldPassword, String newPassword);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean hasRole(Long userId, RoleName roleName);

    // متدهای جدید برای مدیریت Soft Delete
    List<User> getDeletedUsers();
    Page<User> getDeletedUsers(Pageable pageable);
    void restoreUser(Long id);
    void permanentDeleteUser(Long id); // حذف فیزیکی (فقط برای ادمین)

    // متدهای ادمین (دسترسی به همه کاربران شامل حذف شده‌ها)
    Optional<User> getAnyUserById(Long id);
    Optional<User> getAnyUserByUsername(String username);
    Optional<User> getAnyUserByEmail(String email);
    List<User> getAllUsersIncludingDeleted();
    Page<User> getAllUsersIncludingDeleted(Pageable pageable);
}