package com.example.sinablog.cotroller.user;

import com.example.sinablog.Service.User.UserService;
import com.example.sinablog.customeExeption.RuleException;
import com.example.sinablog.dtos.user.LoginRequestDto;
import com.example.sinablog.dtos.user.LoginResponseDto;
import com.example.sinablog.dtos.user.UserDto;
import com.example.sinablog.dtos.user.UserResponseDto;
import com.example.sinablog.model.User;
import com.example.sinablog.model.enums.RoleName;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserDto userDto) {
        try {
            User user = convertToEntity(userDto);
            User registeredUser = userService.registerUser(user, RoleName.ROLE_USER);
            UserResponseDto responseDto = convertToResponseDto(registeredUser);

            return ResponseEntity.ok(responseDto);

        } catch (RuleException e) {
            throw e; // RuleException به صورت global handle می‌شود
        } catch (Exception e) {
            throw new RuleException("Registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto,
                                                  HttpServletRequest request) {
        try {
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(
                            loginRequestDto.getUsername(),
                            loginRequestDto.getPassword()
                    );

            Authentication authentication = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // ایجاد سشن
            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            // گرفتن اطلاعات کاربر
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(","));

            // پیدا کردن کاربر کامل برای گرفتن fullName
            User user = userService.getUserByUsername(loginRequestDto.getUsername())
                    .orElseThrow(() -> new RuleException("User not found after authentication"));

            LoginResponseDto response = new LoginResponseDto();
            response.setMessage("Login successful");
            response.setUsername(user.getUsername());
            response.setFullName(user.getFullName());
            response.setRole(RoleName.valueOf(role.replace("ROLE_", "")));
            response.setSuccess(true);

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            throw new RuleException("Invalid username or password");
        } catch (DisabledException e) {
            throw new RuleException("Account is disabled");
        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponseDto> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();

            LoginResponseDto response = new LoginResponseDto();
            response.setMessage("Logout successful");
            response.setSuccess(true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            throw new RuleException("Logout failed: " + e.getMessage());
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new RuleException("Not authenticated");
            }

            String username = authentication.getName();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuleException("User not found"));

            UserResponseDto responseDto = convertToResponseDto(user);
            return ResponseEntity.ok(responseDto);

        } catch (RuleException e) {
            throw e;
        } catch (Exception e) {
            throw new RuleException("Failed to get current user: " + e.getMessage());
        }
    }

    // متدهای کمکی برای تبدیل
    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword()); // بعداً در سرویس رمزنگاری می‌شود
        user.setFullName(userDto.getFullName());
        return user;
    }

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