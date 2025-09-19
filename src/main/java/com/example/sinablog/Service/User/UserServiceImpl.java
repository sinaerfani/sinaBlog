package com.example.sinablog.Service.User;

import com.example.sinablog.Repository.UserRepository;
import com.example.sinablog.Service.Role.RoleService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.model.User;
import com.example.sinablog.model.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(User user) {
        return registerUser(user, RoleName.ROLE_USER);
    }

    @Override
    public User registerUser(User user, RoleName roleName) {
        if (existsByUsername(user.getUsername())) {
            throw new RuleException("Username already exists");
        }

        if (existsByEmail(user.getEmail())) {
            throw new RuleException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(roleService.findOrCreateRole(roleName));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id)
                .orElseThrow(() -> new RuleException("User not found with ID"));

        if (!existingUser.getUsername().equals(user.getUsername()) &&
                existsByUsername(user.getUsername())) {
            throw new RuleException("Username already exists");
        }

        if (!existingUser.getEmail().equals(user.getEmail()) &&
                existsByEmail(user.getEmail())) {
            throw new RuleException("Email already exists");
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFullName(user.getFullName());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id)
                .orElseThrow(() -> new RuleException("User not found with ID"));

        user.setDisableDate(LocalDateTime.now());
        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findByIdAndDisableDateIsNull(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsernameAndDisableDateIsNull(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmailAndDisableDateIsNull(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAllByDisableDateIsNull();
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAllByDisableDateIsNull(pageable);
    }

    @Override
    public List<User> getUsersByRole(RoleName roleName) {
        return userRepository.findByRole_NameAndDisableDateIsNull(roleName);
    }

    @Override
    public void changeUserStatus(Long id, boolean enabled) {
        User user = getUserById(id)
                .orElseThrow(() -> new RuleException("User not found with ID"));

        user.setEnabled(enabled);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId)
                .orElseThrow(() -> new RuleException("User not found with ID"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuleException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsernameAndDisableDateIsNull(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailAndDisableDateIsNull(email);
    }

    @Override
    public boolean hasRole(Long userId, RoleName roleName) {
        return userRepository.existsByIdAndRole_NameAndDisableDateIsNull(userId, roleName);
    }

    @Override
    public List<User> getDeletedUsers() {
        return userRepository.findByDisableDateIsNotNull();
    }

    @Override
    public Page<User> getDeletedUsers(Pageable pageable) {
        return userRepository.findByDisableDateIsNotNull(pageable);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findByIdAndDisableDateIsNotNull(id)
                .orElseThrow(() -> new RuleException("Deleted user not found with ID"));

        user.setDisableDate(null);
        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void permanentDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuleException("User not found with ID: " + id));

        userRepository.delete(user);
    }

    @Override
    public Optional<User> getAnyUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getAnyUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getAnyUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsersIncludingDeleted() {
        return userRepository.findAll();
    }

    @Override
    public Page<User> getAllUsersIncludingDeleted(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}