package com.group35.motionguard;

import com.group35.motionguard.features.EmailSender;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmailSenderTest {
    private final static String RECEIVER = "";

    @Autowired
    private EmailSender sender;

//    @Test
//    public void givenReceiver_whenSendEmail_thenSuccess() {
//        boolean success = sender.notifyDetection("front door", new Date(), RECEIVER);
//        assertTrue(success);
//    }
}
