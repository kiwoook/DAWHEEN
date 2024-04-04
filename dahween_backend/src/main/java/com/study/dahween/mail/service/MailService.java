package com.study.dahween.mail.service;

import com.study.dahween.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final JwtService jwtService;
    @Value("${spring.mail.username}")
    private String emailAddress;
    @Value("${frontend.server.url}")
    private String frontendUrl;

    @Async
    public void sendResetPassword(String email) throws Exception{
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        String tmpToken = jwtService.createTmpToken(email);

        String text = "비밀번호 초기화 주소: " +
                frontendUrl +
                "/reset-password" +
                "?token=" +
                URLEncoder.encode(tmpToken, StandardCharsets.UTF_8);

        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("다흰 비밀번호 초기화 알림");
        simpleMailMessage.setFrom(emailAddress);
        simpleMailMessage.setText(text);

        javaMailSender.send(simpleMailMessage);
    }
}
