package org.booking.hotelbooking.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.Provider;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(String toEmail, String confirmationLink) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;'>"
                    + "<h2 style='color: #333;'>Підтвердження бронювання</h2>"
                    + "<p>Будь ласка, натисніть кнопку нижче, щоб підтвердити бронювання:</p>"
                    + "<a href='" + confirmationLink + "' style='"
                    + "display: inline-block; "
                    + "padding: 10px 20px; "
                    + "margin-top: 20px; "
                    + "background-color: #4CAF50; "
                    + "color: white; "
                    + "text-decoration: none; "
                    + "border-radius: 5px;'>Підтвердити</a>"
                    + "<p style='margin-top: 30px; font-size: 12px; color: #888;'>"
                    + "Якщо кнопка не працює, перейдіть за цим посиланням: <br/>"
                    + "<a href='" + confirmationLink + "'>" + confirmationLink + "</a>"
                    + "</p>"
                    + "</div>";
            helper.setTo(toEmail);
            helper.setSubject("Підтвердження бронювання");
            helper.setText(htmlContent, true);
            mailSender.send(message);

            logger.info("Лист підтвердження надіслано на {}", toEmail);

        } catch (MessagingException e) {
            logger.error("Помилка відправки листа на {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Не вдалося надіслати лист", e);
        }
    }
    public void sendTransferConfirmationEmail(String toEmail, String confirmationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = "<div style='font-family: Arial; padding: 20px;'>"
                    + "<h2>Підтвердження передачі бронювання</h2>"
                    + "<p>Натисніть кнопку, щоб прийняти бронювання:</p>"
                    + "<a href='" + confirmationLink + "' style='background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>Підтвердити</a>"
                    + "<p style='margin-top: 20px;'>Посилання дійсне 24 години.</p>"
                    + "</div>";

            helper.setTo(toEmail);
            helper.setSubject("Підтвердження передачі бронювання");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Помилка відправки листа", e);
        }
    }

    public void sendRequestRejectedEmail(String toEmail, String hotelName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = "<div style='font-family: Arial; padding: 20px;'>"
                    + "<h2>Ваш запит на роль менеджера відхилено</h2>"
                    + "<p>Готель: " + hotelName + "</p>"
                    + "<p>Зв'яжіться з адміністратором для деталей.</p>"
                    + "</div>";

            helper.setTo(toEmail);
            helper.setSubject("Запит на роль менеджера відхилено");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Помилка відправки листа", e);
        }
    }
}
