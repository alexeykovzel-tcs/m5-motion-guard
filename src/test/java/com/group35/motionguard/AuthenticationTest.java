package com.group35.motionguard;

import com.group35.motionguard.features.account.Authority;
import com.group35.motionguard.features.account.PasswordEncoder;
import com.group35.motionguard.features.account.User;
import com.group35.motionguard.features.account.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User user = getTestUser();
        user.setPassword(new PasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    @WithAnonymousUser
    public void givenNotAuthorized_thenRedirectLogin() throws Exception {
        mockMvc
                .perform(get("/"))
                .andExpect(status().isFound());
    }

    @Test
    @WithAnonymousUser
    void givenSuccessLogin_thenRedirectIndex() throws Exception {
        User user = getTestUser();
        mockMvc
                .perform(formLogin()
                        .loginProcessingUrl("/perform-login")
                        .user(user.getUsername())
                        .password(user.getPassword()))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(user.getUsername()));
    }

    @Test
    @WithMockUser
    void givenLoggedIn_whenLogOut_thenRedirectLogin() throws Exception {
        mockMvc
                .perform(post("/logout"))
                .andExpect(redirectedUrl("/login"));
    }

    private User getTestUser() {
        return new User("user", "123", Authority.PI_HOLDER.single());
    }
}
