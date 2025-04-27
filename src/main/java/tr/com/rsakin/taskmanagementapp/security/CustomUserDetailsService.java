package tr.com.rsakin.taskmanagementapp.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username.equals("user")) {
            return User.builder()
                    .username("user")
                    .password(new BCryptPasswordEncoder().encode("password"))
                    .roles("USER")
                    .build();
        }

        if (username.equals("admin")) {
            return User.builder()
                    .username("admin")
                    .password(new BCryptPasswordEncoder().encode("password123"))
                    .roles("ADMIN")
                    .build();
        }

        throw new UsernameNotFoundException("User not found");
    }

}
