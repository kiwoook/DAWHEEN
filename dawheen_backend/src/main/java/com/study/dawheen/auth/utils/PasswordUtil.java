package com.study.dawheen.auth.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordUtil {

    private static final int PASSWORD_LENGTH = 15;
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()-_=+";
    private static final String ALL_CHARACTERS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARACTERS;

    public static String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        List<Character> password;

        password = new ArrayList<>();

        password.add(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.add(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.add(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.add(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        Collections.shuffle(password, random);

        StringBuilder passwordStr = new StringBuilder();
        for (Character c : password) {
            passwordStr.append(c);
        }

        return passwordEncoder.encode(passwordStr.toString());
    }

}
