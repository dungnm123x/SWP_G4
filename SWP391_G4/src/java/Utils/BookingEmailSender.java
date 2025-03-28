/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import model.CartItem;

public class BookingEmailSender {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_SENDER = "dungnmhe173094@fpt.edu.vn";
    private static final String EMAIL_PASSWORD = "iysa hxar fbwc ggsv";

    public static boolean sendBookingSuccessEmail(
            String recipientEmail,
            String bookingName,
            List<CartItem> cartItems,
            List<String> fullNameList,
            List<String> idNumberList,
            List<String> typeList,
            String bookingPhone
    ) throws UnsupportedEncodingException {

        String subject = "Xác nhận đặt vé thành công";

        StringBuilder content = new StringBuilder();

        content.append("<div style='max-width:700px;margin:auto;border:1px solid #ddd;padding:20px;border-radius:10px;font-family:Arial,sans-serif;'>")
                .append("<h2 style='color:#28a745;text-align:center;'>🎉 Đặt vé thành công!</h2>")
                .append("<p>Cảm ơn bạn <strong>").append(bookingName).append("</strong> đã sử dụng dịch vụ đặt vé tàu của chúng tôi.</p>")
                .append("<h4 style='margin-top:20px;'>Thông tin vé:</h4>");

        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            String tripType = item.isReturnTrip() ? "Chuyến về" : "Chuyến đi";

            content.append("<div style='border:1px solid #ccc; padding:10px; margin-bottom:10px; border-radius:6px;'>")
                    .append("<p><strong>Vé ").append(i + 1).append(":</strong></p>")
                    .append("<p><strong>Loại chuyến:</strong> ").append(tripType).append("</p>")
                    .append("<p><strong>Hành trình:</strong> ")
                    .append(item.isReturnTrip()
                            ? item.getArrivalStationName() + " → " + item.getDepartureStationName()
                            : item.getDepartureStationName() + " → " + item.getArrivalStationName())
                    .append("</p>")
                    .append("<p><strong>Tàu:</strong> ").append(item.getTrainName()).append(" - ").append(item.getDepartureDate()).append("</p>")
                    .append("<p><strong>Toa:</strong> ").append(item.getCarriageNumber()).append(", Chỗ ").append(item.getSeatNumber()).append("</p>")
                    .append("<p><strong>Giá:</strong> ").append(item.getPrice()).append(" VND</p>")
                    .append("<p><strong>Hành khách:</strong> ").append(fullNameList.get(i)).append("</p>")
                    .append("<p><strong>CMND/CCCD:</strong> ").append(idNumberList.get(i)).append("</p>")
                    .append("<p><strong>Đối tượng:</strong> ").append(typeList.get(i)).append("</p>")
                    .append("</div>");
        }

        content.append("<h4>Thông tin người đặt vé:</h4>")
                .append("<p><strong>Họ tên:</strong> ").append(bookingName).append("</p>")
                .append("<p><strong>Email:</strong> ").append(recipientEmail).append("</p>")
                .append("<p><strong>Điện thoại:</strong> ").append(bookingPhone).append("</p>")
                .append("<p>Vé điện tử đã được gửi đến email của bạn. Vui lòng kiểm tra email để nhận vé.</p>")
                .append("<p style='text-align:center;margin-top:20px;'>")
                .append("<strong>Trân trọng,</strong><br>OnlineTicketTrainBooking</p>")
                .append("</div>");

        return sendEmail(recipientEmail, subject, content.toString());
    }

    private static boolean sendEmail(String recipientEmail, String subject, String htmlContent) throws UnsupportedEncodingException {
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
            System.out.println("✅ Email đã gửi đến: " + recipientEmail);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Lỗi gửi email: " + e.getMessage());
            return false;
        }
    }
}
