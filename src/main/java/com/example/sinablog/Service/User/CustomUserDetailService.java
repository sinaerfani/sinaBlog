package com.example.sinablog.Service.User;

import com.example.sinablog.Repository.UserRepository;
import com.example.sinablog.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service

public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsernameAndDisableDateIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("User.not.found.with.username"));

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User.account.is.disabled");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(user.getPassword())
                .roles(user.getRole().getName().name().replace("ROLE_", ""))
                .disabled(!user.isEnabled())
                .build();
    }
}
