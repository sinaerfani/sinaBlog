package com.example.sinablog.Repository;

import com.example.sinablog.model.User;
import com.example.sinablog.model.enums.RoleName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // یافتن کاربران فعال (حذف نشده)
    Optional<User> findByIdAndDisableDateIsNull(Long id);

    Optional<User> findByUsernameAndDisableDateIsNull(String username);

    Optional<User> findByEmailAndDisableDateIsNull(String email);

    List<User> findAllByDisableDateIsNull();

    Page<User> findAllByDisableDateIsNull(Pageable pageable);

    List<User> findByRole_NameAndDisableDateIsNull(RoleName roleName);

    Page<User> findByRole_NameAndDisableDateIsNull(RoleName roleName, Pageable pageable);

    boolean existsByUsernameAndDisableDateIsNull(String username);

    boolean existsByEmailAndDisableDateIsNull(String email);

    boolean existsByIdAndRole_NameAndDisableDateIsNull(Long id, RoleName roleName);

    long countByDisableDateIsNull();

    long countByRole_NameAndDisableDateIsNull(RoleName roleName);

    // یافتن کاربران حذف شده
    List<User> findByDisableDateIsNotNull();

    Page<User> findByDisableDateIsNotNull(Pageable pageable);

    Optional<User> findByIdAndDisableDateIsNotNull(Long id);

    // یافتن بدون در نظر گرفتن وضعیت حذف (برای ادمین)
    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
