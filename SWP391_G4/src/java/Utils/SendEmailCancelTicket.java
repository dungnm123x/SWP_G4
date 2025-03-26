/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

/**
 *
 * @author dung9
 */
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
public class SendEmailCancelTicket {
      private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_SENDER = "dungnmhe173094@fpt.edu.vn"; 
    private static final String EMAIL_PASSWORD = "iysa hxar fbwc ggsv";

    /**
     * Gửi email với nội dung động
     *
     * @param recipientEmail Email của người nhận
     * @param subject Tiêu đề email
     * @param htmlContent Nội dung email (có thể là HTML)
     * @return true nếu gửi thành công, false nếu thất bại
     */
    public static boolean sendEmail(String recipientEmail, String subject, String htmlContent) throws UnsupportedEncodingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_SENDER, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_SENDER, "OnlineTicketTrainBooking"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(MimeUtility.encodeText(subject, "UTF-8", "B"));
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("✅ Email đã gửi thành công đến: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi khi gửi email: " + e.getMessage());
            return false;
        }
    }
}
