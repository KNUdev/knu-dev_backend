package ua.knu.knudev.knudevsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.knu.knudev.knudevsecurity.repository.AccountAuthRepository;

@Service
@RequiredArgsConstructor
public class EmailUserDetailsService implements UserDetailsService {
    private final AccountAuthRepository accountAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountAuthRepository.findAccountAuthByEmail(username);
    }
}
