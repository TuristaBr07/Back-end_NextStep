package com.nextstep.backend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("NextStep — Confirme seu e-mail");
            String verificationLink = baseUrl + "/auth/verify-email?token=" + token;
            helper.setText(buildHtml(verificationLink), true);
            mailSender.send(message);
        } catch (Exception e) {
            // Do not block registration if email delivery fails
        }
    }

    private String buildHtml(String verificationLink) {
        return "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>" +
            "<body style=\"font-family:Arial,sans-serif;background-color:#f8fafc;margin:0;padding:0;\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f8fafc;padding:40px 0;\">" +
            "<tr><td align=\"center\">" +
            "<table width=\"520\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.08);\">" +
            "<tr><td style=\"background:linear-gradient(135deg,#059669,#10B981);padding:32px 40px;text-align:center;\">" +
            "<h1 style=\"color:#ffffff;margin:0;font-size:24px;\">NextStep</h1>" +
            "<p style=\"color:#D1FAE5;margin:8px 0 0;font-size:14px;\">Finanças para MEI</p>" +
            "</td></tr>" +
            "<tr><td style=\"padding:40px;\">" +
            "<h2 style=\"color:#0F172A;font-size:20px;margin:0 0 16px;\">Confirme seu e-mail</h2>" +
            "<p style=\"color:#475569;font-size:15px;line-height:1.6;margin:0 0 24px;\">" +
            "Bem-vindo ao NextStep! Clique no botão abaixo para verificar seu e-mail e ativar sua conta." +
            "</p>" +
            "<table cellpadding=\"0\" cellspacing=\"0\"><tr>" +
            "<td style=\"background-color:#059669;border-radius:8px;padding:14px 32px;\">" +
            "<a href=\"" + verificationLink + "\" style=\"color:#ffffff;text-decoration:none;font-size:15px;font-weight:bold;\">Verificar e-mail</a>" +
            "</td></tr></table>" +
            "<p style=\"color:#94A3B8;font-size:13px;margin:24px 0 0;\">" +
            "Se você não criou uma conta no NextStep, ignore este e-mail.<br>O link expira em 24 horas." +
            "</p></td></tr>" +
            "</table></td></tr></table>" +
            "</body></html>";
    }
}
