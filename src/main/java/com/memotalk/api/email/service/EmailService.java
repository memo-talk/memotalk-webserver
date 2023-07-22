package com.memotalk.api.email.service;

import com.memotalk.api.email.dto.AuthenticationCodeRequestDTO;
import com.memotalk.api.email.repository.EmailCertificationRepository;
import com.memotalk.exception.InternalServerException;
import com.memotalk.exception.NoAuthException;
import com.memotalk.exception.enumeration.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final EmailCertificationRepository emailCertificationRepository;
    private static final int INDEX_EIGHT = 8;
    private static final String CHAR_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String FORM = "testforbe99@gmail.com";
    private static final String SUBJECT = "메모톡 인증번호";
    private static final String ENCODING = "UTF-8";
    private static final String VARIABLE = "authCode";
    private static final String TEMPLATE = "mail";

    @Async
    public void sendEmail(String toEmail) {
        String randomNumber = generateAuthCode();
        sendEmailWithTemplate(toEmail, randomNumber);
        emailCertificationRepository.createEmailCertification(toEmail, randomNumber);
    }

    // 랜덤 인증코드 생성
    public String generateAuthCode() {
        return RandomStringUtils.random(INDEX_EIGHT, CHAR_POOL);
    }

    private void sendEmailWithTemplate(String toEmail, String authCode) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            // 템플릿에서 사용될 변수를 저장
            Context context = new Context();
            context.setVariable(VARIABLE, authCode);
            String content = templateEngine.process(TEMPLATE, context);

            // 이메일 설정
            MimeMessageHelper helper = new MimeMessageHelper(message, true, ENCODING);
            helper.setFrom(FORM);
            helper.setTo(toEmail);
            helper.setSubject(SUBJECT);
            helper.setText(content, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new InternalServerException(ErrorCode.EMAIL_SEND_FAILURE);
        }
    }

    public void verifyAuthCode(AuthenticationCodeRequestDTO requestDTO) {
        if (!emailCertificationRepository.hasKey(requestDTO.getEmail()) ||
                !emailCertificationRepository.getEmailCertification(requestDTO.getEmail())
                        .equals(requestDTO.getAuthCode())) {
            throw new NoAuthException(ErrorCode.AUTHENTICATION_NUMBER_MISMATCH);
        }
    }
}
