package com.group35.motionguard.features.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthProvider implements AuthenticationProvider {
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        String encodedPassword = new PasswordEncoder().encode(password);

        // Verify that credentials are valid
        User user = userRepository.findById(username).orElse(null);
        if (user == null || !user.getPassword().equals(encodedPassword)) {
            log.error("Wrong credentials: {} {}", username, password);
            throw new BadCredentialsException("Wrong username or password");
        }

        // Check if 2-Step Verification is enabled
        if (user.isEnabled2FA()) {
            var details = (AuthDetailsSource.AuthDetails) authentication.getDetails();
            String code = details.getVerificationCode();
            String actualCode = user.getCode().getSymbols();
            if (!code.equals(actualCode)) {
                log.error("Wrong verification code: {} != {}", code, actualCode);
                throw new BadCredentialsException("Wrong verification code");
            }
        }

        // Apply user authorities for this session
        log.info("Authorizing user: {} {}", username, user.getAuthorities().toString());
        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}