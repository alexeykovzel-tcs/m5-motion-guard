package com.group35.motionguard.features.account;

import com.group35.motionguard.features.PiHolder;
import com.group35.motionguard.features.PiHolderRepository;
import com.group35.motionguard.features.PiRepository;
import com.group35.motionguard.features.RaspberryPi;
import com.group35.motionguard.features.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class AccountController {
    private final static String ACTIVATION_CODE = "activationCode";
    private final static String ENABLED_2FA = "enabled2FA";
    private final static String USERNAME = "username";
    private final static String PASSWORD = "password";
    private final static String PHONE = "phone";
    private final static String EMAIL = "email";

    private final PiHolderRepository piHolderRepository;
    private final UserRepository userRepository;
    private final PiRepository piRepository;
    private final AuthService authService;
    private final EmailSender sender;

    @PostMapping("/login")
    public String login(@RequestBody MultiValueMap<String, String> data) {
        Credentials credentials = getCredentials(data);

        // Verify that such user exists and the password matches
        User user = userRepository.findByUsername(credentials.getUsername());
        if (user == null || !user.getPassword().equals(credentials.encodePassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username or password");
        }
        if (user.isEnabled2FA()) {
            // Send verification code if 2FA is enabled
            user.setCode(sendVerificationCode(user));
            userRepository.save(user);
            return user.getEmail();
        } else {
            // Otherwise, authenticate user by default
            authService.authenticate(credentials);
            return null;
        }
    }

    @Transactional
    @PostMapping("/register")
    public String registerPiHolder(@RequestBody MultiValueMap<String, String> data) {
        Credentials credentials = getAndVerifyCredentials(data);
        User user = credentials.buildUser(Authority.PI_HOLDER);

        // Verify and set e-mail parameter
        String email = getField(data, EMAIL);
        verifyEmail(email);
        user.setEmail(email);

        // Verify and set phone parameter
        String phone = getField(data, PHONE);
        verifyPhone(phone);
        user.setPhone(phone);

        // Send verification code if 2FA is enabled
        boolean enabled2FA = "on".equals(getField(data, ENABLED_2FA));
        if (enabled2FA) {
            VerificationCode code = sendVerificationCode(user);
            user.setEnabled2FA(true);
            user.setCode(code);
        }

        // Save account details before authentication
        piHolderRepository.save(new PiHolder(user));

        if (enabled2FA) {
            log.info("Sign up with 2FA: {} {}", credentials.getUsername(), credentials.getPassword());
            return email;
        } else {
            log.info("Sign up without 2FA: {} {}", credentials.getUsername(), credentials.getPassword());
            authService.authenticate(credentials);
            return null;
        }
    }

    private VerificationCode sendVerificationCode(User user) {
        String email = user.getEmail();
        if (email == null || email.equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "2FA requires e-mail to be present");
        }
        VerificationCode code = new VerificationCode();
        sender.sendVerificationCode(code.getSymbols(), email);
        log.info("Sending verification code: {}", code);
        return code;
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void registerAdmin(@RequestBody MultiValueMap<String, String> data) {
        User user = getAndVerifyCredentials(data).buildUser(Authority.ADMIN);
        userRepository.save(user);
    }

    @PostMapping("/register/pi")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void registerPi(@RequestBody MultiValueMap<String, String> data) {
        User user = getAndVerifyCredentials(data).buildUser(Authority.PI);
        String activationCode = getField(data, ACTIVATION_CODE);
        RaspberryPi pi = new RaspberryPi(user, activationCode);
        piRepository.save(pi);
    }

    @GetMapping("/details")
    @PreAuthorize("isAuthenticated()")
    public Map<String, String> getAccountDetails() {
        String username = authService.getId();
        Map<String, String> details = new HashMap<>();
        userRepository.findById(username).ifPresent(user -> {
            details.put(USERNAME, user.getUsername());
            details.put(EMAIL, user.getEmail());
            details.put(PHONE, user.getPhone());
        });
        return details;
    }

    @Transactional
    @PostMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public void updateAccount(@RequestBody MultiValueMap<String, String> data) {
        User user = userRepository.findByUsername(authService.getId());

        // update user e-mail
        String email = getField(data, EMAIL);
        if (email != null) {
            if (email.equals("") && user.isEnabled2FA()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "2FA requires e-mail to be present");
            }
            verifyEmail(email);
            user.setEmail(email);
        }
        // update user phone
        String phone = getField(data, PHONE);
        if (phone != null) {
            verifyPhone(phone);
            user.setPhone(phone);
        }
        // update user password
        String password = getField(data, PASSWORD);
        if (!isEmpty(password)) {
            verifyPassword(password);
            password = new PasswordEncoder().encode(password);
            user.setPassword(password);
        }
        userRepository.save(user);
    }

    private Credentials getAndVerifyCredentials(MultiValueMap<String, String> data) {
        Credentials credentials = getCredentials(data);
        verifyCredentials(credentials);
        return credentials;
    }

    private Credentials getCredentials(MultiValueMap<String, String> data) {
        String username = getField(data, USERNAME);
        String password = getField(data, PASSWORD);
        return new Credentials(username, password);
    }

    private void verifyCredentials(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        if (isEmpty(username) || isEmpty(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing username or password");
        }
        if (userRepository.existsById(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This username is already taken");
        }
        verifyPassword(password);
    }

    private void verifyPassword(String password) {
        verifyRegex(password, ".{8,32}", "Password should be 8-32 characters long");
        verifyRegex(password, ".*[0-9].*", "Password should contain at least one digit");
        verifyRegex(password, ".*[a-z].*", "Password should contain at least one lowercase letter");
        verifyRegex(password, ".*[A-Z].*", "Password should contain at least one uppercase letter");
        verifyRegex(password, ".*[@#$%^&+=!()].*", "Password contain contain at least one special character");
    }

    private void verifyEmail(String email) {
        if (isEmpty(email)) return;
        verifyRegex(email, "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", "Invalid e-mail");
    }

    private void verifyPhone(String phone) {
        if (isEmpty(phone)) return;
        verifyRegex(phone, "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$", "Invalid phone");
    }

    private void verifyRegex(String value, String regex, String error) {
        if (!value.matches(regex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error);
        }
    }

    private String getField(MultiValueMap<String, String> data, String name) {
        List<String> elements = data.get(name);
        return (elements == null || elements.isEmpty()) ? null : elements.get(0);
    }

    private boolean isEmpty(String value) {
        return value == null || value.equals("");
    }
}
