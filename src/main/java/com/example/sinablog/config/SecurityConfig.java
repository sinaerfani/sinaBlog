package com.example.sinablog.config;

import com.example.sinablog.Service.User.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;

    public SecurityConfig(CustomUserDetailService customUserDetailService) {
        this.customUserDetailService = customUserDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .userDetailsService(customUserDetailService)

                // پیکربندی CORS برای ارتباط با frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // مدیریت خطاهای دسترسی
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/403") // صفحه خطای دسترسی ممنوع
                )

                // پیکربندی فرم لاگین
                .formLogin(form -> form
                        .loginPage("/login") // صفحه لاگین سفارشی
                        .loginProcessingUrl("/api/auth/login") // آدرس پردازش لاگین
                        .defaultSuccessUrl("/", true) // صفحه پس از لاگین موفق
                        .failureUrl("/login?error=true") // صفحه پس از لاگین ناموفق
                        .permitAll() // دسترسی آزاد به صفحه لاگین
                )

                // پیکربندی logout
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout") // آدرس logout
                        .logoutSuccessUrl("/login?logout") // صفحه پس از logout موفق
                        .invalidateHttpSession(true) // بی‌اعتبار کردن session
                        .deleteCookies("JSESSIONID") // حذف cookie session
                        .permitAll() // دسترسی آزاد به logout
                )

                // پیکربندی مجوزهای دسترسی
                .authorizeHttpRequests(auth -> auth
                        // endpointهای عمومی - نیاز به احراز هویت ندارند
                        .requestMatchers(
                                "/login",           // صفحه لاگین
                                "/",                // صفحه اصلی
//                                "/register",        // صفحه ثبت‌نام
                                "/error",           // صفحه خطا
                                "/403",             // صفحه دسترسی ممنوع
                                "/api/auth/login",  // endpoint لاگین
                                "/api/auth/register", // endpoint ثبت‌نام
                                "/api/auth/logout"  // endpoint logout
                        ).permitAll()

                        // endpointهای خواندن API - برای همه قابل دسترسی
                        .requestMatchers(HttpMethod.GET,
                                "/api/categories/**", // دسته‌بندی‌ها
                                "/api/posts/**",      // پست‌ها
                                "/api/comments/**"    // نظرات
                        ).permitAll()

                        // endpointهای مدیریتی - فقط برای ادمین‌ها
                        .requestMatchers(
                                "/api/admin/**",                 // مدیریت ادمین
                                "/api/categories/deleted",       // دسته‌بندی‌های حذف شده
                                "/api/categories/**/permanent",  // حذف دائمی
                                "/api/categories/**/restore"     // بازیابی
                        ).hasRole("ADMIN")

                        // ایجاد دسته‌بندی - برای ادمین و ادیتور
                        .requestMatchers(HttpMethod.POST, "/api/categories/**")
                        .hasAnyRole("ADMIN", "EDITOR")

                        // ویرایش دسته‌بندی - برای ادمین و ادیتور
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**")
                        .hasAnyRole("ADMIN", "EDITOR")

                        // حذف دسته‌بندی - برای ادمین و ادیتور
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**")
                        .hasAnyRole("ADMIN", "EDITOR")

                        // ایجاد پست و نظر - برای کاربران احراز هویت شده
                        .requestMatchers(HttpMethod.POST, "/api/posts/**", "/api/comments/**")
                        .authenticated()

                        // ویرایش پست و نظر - برای کاربران احراز هویت شده
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**", "/api/comments/**")
                        .authenticated()

                        // حذف پست و نظر - برای کاربران احراز هویت شده
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**", "/api/comments/**")
                        .authenticated()

                        // دریافت اطلاعات کاربر جاری - برای کاربران احراز هویت شده
                        .requestMatchers("/api/auth/current-user")
                        .authenticated()

                        // سایر درخواست‌ها نیاز به احراز هویت دارند
                        .anyRequest().authenticated()
                )

                // پیکربندی محافظت در برابر CSRF
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers( // غیرفعال برای endpointهای احراز هویت
                                "/api/auth/login",
                                "/api/auth/register",
                                "/api/auth/logout"
                        )
                )

                // مدیریت session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // ایجاد session در صورت نیاز
                        .sessionFixation().migrateSession() // محافظت در برابر session fixation
                        .maximumSessions(1) // حداکثر یک session فعال برای هر کاربر
                        .maxSessionsPreventsLogin(false) // اجازه login جدید با logout اتوماتیک session قدیمی
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // دامنه‌های مجاز برای frontend
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:8083",    // توسعه React
                "http://localhost:8080"   // توسعه Vue
        ));
        // متدهای HTTP مجاز
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        // هدرهای مجاز
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "X-CSRF-TOKEN",
                "Accept", "Origin", "X-Requested-With", "X-XSRF-TOKEN"
        ));
        // هدرهای قابل دسترسی از frontend
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "X-CSRF-TOKEN", "X-XSRF-TOKEN"
        ));
        // اجازه ارسال credentials (cookies, authentication)
        configuration.setAllowCredentials(true);
        // مدت زمان cache کردن تنظیمات CORS (1 ساعت)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // استفاده از BCrypt برای رمزنگاری پسوردها
        return new BCryptPasswordEncoder();
    }
}