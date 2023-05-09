package com.group35.motionguard.features;

import com.group35.motionguard.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmailSender {
    private static final String EMAIL = "mod5.motionguard@gmail.com";
    private static final String PASSWORD = "uzumxuvdeomljvvq";
    private static final String NAME = "MotionGuard";

    public boolean notifyDetection(String location, Date date, String to) {
        return verifyReceiver(to) && sendEmail("Detection Notification", readFile("email/notify-detection.html")
                .replace("%DATE%", DateUtils.toString(date))
                .replace("%LOCATION%", location), to);
    }

    public boolean sendVerificationCode(String code, String to) {
        return verifyReceiver(to) && sendEmail("Confirmation code", readFile("email/confirm-email.html")
                .replace("%CODE%", code), to);
    }

    private boolean verifyReceiver(String email) {
        if (email == null || email.equals("")) {
            log.error("No email addresses provided");
            return false;
        }
        return true;
    }

    private String readFile(String path) {
        String content = null;
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try (InputStream in = classLoader.getResourceAsStream(path)) {
            if (in == null) throw new IOException();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                content = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            } catch (IOException e) {
                log.error("Could not read the file: {}", e.getMessage());
            }
        } catch (IOException e) {
            log.error("No such file: {}", path);
        }
        return content;
    }

    private boolean sendEmail(String subject, String body, String... to) {
        new Thread(() -> {
            try {
                // Convert recipient addresses
                InternetAddress[] recipients = new InternetAddress[to.length];
                for (int i = 0; i < to.length; i++) {
                    recipients[i] = new InternetAddress(to[i]);
                }

                // Set e-mail meta data
                Message message = new MimeMessage(startSession());
                message.setFrom(new InternetAddress(EMAIL, NAME));
                message.setRecipients(Message.RecipientType.TO, recipients);
                message.setSubject(subject);

                // Set e-mail content
                Multipart multipart = new MimeMultipart();
                MimeBodyPart bodyPart = new MimeBodyPart();
                bodyPart.setContent(body, "text/html; charset=utf-8");
                multipart.addBodyPart(bodyPart);
                message.setContent(multipart);

                // Send e-mail
                Transport.send(message);
            } catch (MessagingException | IOException e) {
                log.error("Could not configure e-mail: {}", e.getMessage());
            }
        }).start();
        return true;
    }

    private Session startSession() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });
    }
}