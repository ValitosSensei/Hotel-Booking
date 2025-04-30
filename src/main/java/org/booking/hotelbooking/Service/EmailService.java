package org.booking.hotelbooking.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Надсилає лист підтвердження бронювання.
     */
    public void sendConfirmationEmail(String toEmail, String confirmationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f9f9f9;'>"
                    + "<h2 style='color: #333;'>Підтвердження бронювання</h2>"
                    + "<p>Будь ласка, натисніть кнопку нижче, щоб підтвердити бронювання:</p>"
                    + "<a href='" + confirmationLink + "' style='"
                    + "display: inline-block; padding: 10px 20px; margin-top: 20px; "
                    + "background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;'>"
                    + "Підтвердити</a>"
                    + "<p style='margin-top: 30px; font-size: 12px; color: #888;'>"
                    + "Якщо кнопка не працює, скопіюйте і вставте це посилання у браузер:<br/>"
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

    /**
     * Надсилає лист підтвердження передачі бронювання.
     */
    public void sendTransferConfirmationEmail(String toEmail, String confirmationLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2 style='color: #333;'>Підтвердження передачі бронювання</h2>"
                    + "<p>Натисніть кнопку нижче, щоб прийняти бронювання:</p>"
                    + "<a href='" + confirmationLink + "' style='"
                    + "display: inline-block; padding: 10px 20px; margin-top: 20px; "
                    + "background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px;'>"
                    + "Підтвердити</a>"
                    + "<p style='margin-top: 20px; font-size: 12px; color: #888;'>"
                    + "Посилання дійсне протягом 24 годин.</p>"
                    + "</div>";

            helper.setTo(toEmail);
            helper.setSubject("Підтвердження передачі бронювання");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Помилка відправки листа на {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Не вдалося надіслати лист", e);
        }
    }

    /**
     * Надсилає лист про відхилення запиту на роль менеджера.
     */
    public void sendRequestRejectedEmail(String toEmail, String hotelName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2 style='color: #cc0000;'>Ваш запит на роль менеджера відхилено</h2>"
                    + "<p><strong>Готель:</strong> " + hotelName + "</p>"
                    + "<p>Зв'яжіться з адміністратором для отримання додаткової інформації.</p>"
                    + "</div>";

            helper.setTo(toEmail);
            helper.setSubject("Запит на роль менеджера відхилено");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Помилка відправки листа на {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Не вдалося надіслати лист", e);
        }
    }

    /**
     * Надсилає лист про підтвердження запиту на роль менеджера.
     */
    public void sendRequestApprovedEmail(String toEmail, String hotelName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>"
                    + "<h2 style='color: #4CAF50;'>Ваш запит на роль менеджера схвалено!</h2>"
                    + "<p><strong>Готель:</strong> " + hotelName + "</p>"
                    + "<p>Тепер ви можете керувати бронюваннями для цього готелю.</p>"
                    + "</div>";

            helper.setTo(toEmail);
            helper.setSubject("Запит на роль менеджера схвалено");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Помилка відправки листа на {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Не вдалося надіслати лист", e);
        }
    }
}
