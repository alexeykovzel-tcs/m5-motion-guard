package com.group35.motionguard.features.account;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {
    public static final String HASH_ALGORITHM = "SHA-256";
    public static final String HASH_SALT = "RLmboEN!G6Y6eNC9";

    public String encode(String password) {
        password = addSalt(password);
        password = hash(password);
        return password;
    }

    private String hash(String value) {
        if (value == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("[ERROR] No such algorith: " + HASH_ALGORITHM);
            return null;
        }
    }

    private String encodeToString(byte[] hash) {
        StringBuilder result = new StringBuilder();
        for (byte b : hash) {
            result.append(String.format("%02x", b & 0xff));
        }
        return result.toString();
    }

    private String addSalt(String value) {
        return HASH_SALT + value;
    }
}
