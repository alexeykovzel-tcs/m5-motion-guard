package com.group35.motionguard;

import com.group35.motionguard.features.account.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordEncoderTest {
    private PasswordEncoder encoder;

    @BeforeEach
    public void setUp() {
        encoder = new PasswordEncoder();
    }

    @Test
    public void givenPassword_thenReturnHash() {
        String h1 = encoder.encode("123");
        String h2 = encoder.encode("hosgfueofnfie");
        String h3 = encoder.encode("C@sy9nje4$jos4P?");

        assertEquals("17bd7d3742cde41351f45c838f9debdebd83491f0ce5f4c6d1da691885e7d898", h1);
        assertEquals("67f23fc1ae5640adb892eb92f4ac820c12199451ff9cedb04aadade773185843", h2);
        assertEquals("fa048a52950ca04f9b7d1aecaa849a408b14621ed8578abbc791c5a55645b6d6", h3);
    }
}
