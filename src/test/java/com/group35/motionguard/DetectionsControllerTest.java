package com.group35.motionguard;

import com.group35.motionguard.features.PiHolder;
import com.group35.motionguard.features.PiHolderRepository;
import com.group35.motionguard.features.PiRepository;
import com.group35.motionguard.features.RaspberryPi;
import com.group35.motionguard.features.account.Authority;
import com.group35.motionguard.features.account.PasswordEncoder;
import com.group35.motionguard.features.account.User;
import com.group35.motionguard.features.detection.Detection;
import com.group35.motionguard.features.detection.DetectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles({"test"})
public class DetectionsControllerTest {
    private final static String PI_USER = "test-pi";
    private final static String PI_HOLDER = "test-holder";
    private final static String PASSWORD = "123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DetectionRepository detectionRepository;

    @Autowired
    private PiHolderRepository piHolderRepository;

    @Autowired
    private PiRepository piRepository;

    @BeforeEach
    public void setUp() {
        RaspberryPi pi = new RaspberryPi(mockUser(PI_USER, Authority.PI), "777", "front door");
        PiHolder holder = new PiHolder(mockUser(PI_HOLDER, Authority.PI_HOLDER), List.of(pi));
        pi.setHolder(holder);
        piHolderRepository.save(holder);
    }

    @Test
    @WithMockUser(username = PI_USER, password = PASSWORD, authorities = "PI")
    public void givenPi_whenDetection_thenSave() throws Exception {
        assertEquals(0, detectionRepository.count());
        mockMvc
                .perform(post("/pi/motion"))
                .andExpect(status().isOk());

        List<Detection> detections = detectionRepository.findAll();
        assertEquals(1, detections.size());

        Detection detection = detections.get(0);
        assertEquals(PI_USER, detection.getPi().getId());
        assertThat(detection.getDate().getTime()).isCloseTo(new Date().getTime(), within(2000L));
    }

    @Test
    @WithMockUser(username = PI_HOLDER, password = PASSWORD, authorities = "PI_HOLDER")
    public void givenPiHolder_thenReturnDetections() throws Exception {
        RaspberryPi pi = piRepository.getReferenceById(PI_USER);
        detectionRepository.saveAll(List.of(new Detection(pi), new Detection(pi)));
        mockMvc
                .perform(get("/pi/motion/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].deviceId").value(PI_USER))
                .andExpect(jsonPath("$[1].deviceId").value(PI_USER))
                .andReturn().getResponse().getContentAsString();
    }

    private User mockUser(String username, Authority authority) {
        return new User(username, new PasswordEncoder().encode(PASSWORD), List.of(authority));
    }
}
