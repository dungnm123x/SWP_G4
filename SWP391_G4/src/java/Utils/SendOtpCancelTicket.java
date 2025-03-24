/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package Utils;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.Random;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author dung9
 */
@WebServlet(name="SendOtpCancelTicket", urlPatterns={"/SendOtpCancelTicket"})
public class SendOtpCancelTicket extends HttpServlet {
    private static final int OTP_EXPIRY_TIME = 120 * 1000; // 120 giây
    private static final String EMAIL_SENDER = "dungnmhe173094@fpt.edu.vn"; // Thay bằng email gửi
    private static final String EMAIL_PASSWORD = "iysa hxar fbwc ggsv"; // Thay bằng mật khẩu ứng dụng
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SendOtpCancelTicket</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SendOtpCancelTicket at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        HttpSession session = request.getSession();
        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            response.getWriter().write("Lỗi: Email không hợp lệ.");
            return;
        }

        // Tạo OTP ngẫu nhiên 6 chữ số
        int otp = generateOtp();
        long timestamp = System.currentTimeMillis();

        // Lưu OTP vào session
        session.setAttribute("otp", otp);
        session.setAttribute("otpTimestamp", timestamp);

        // Gửi OTP qua email với giao diện HTML
        boolean isSent = sendOtpEmail(email, otp);

        if (isSent) {
            response.getWriter().write("OTP đã được gửi thành công.");
        } else {
            response.getWriter().write("Lỗi khi gửi OTP. Vui lòng thử lại.");
        }
    }

    // Tạo mã OTP ngẫu nhiên (6 chữ số)
    private int generateOtp() {
        SecureRandom random = new SecureRandom();
        return 100000 + random.nextInt(900000);
    }

    // Hàm gửi email chứa OTP với giao diện HTML
    private boolean sendOtpEmail(String recipientEmail, int otp) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_SENDER, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_SENDER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("OnlineBookingTicketTrain: OTP Code for Ticket Cancellation");

            // Nội dung email có định dạng HTML
            String htmlContent = "<!DOCTYPE html>"
                    + "<html>"
                    + "<body style=\"font-family: Arial, sans-serif; background-color: #ffffff;\">"
                    + "<div style=\"max-width: 600px; margin: auto; padding: 20px; border: 1px solid #cccccc; background-color: #ffffff;\">"
                    + "<h2 style=\"color: #007bff;\">OnlineBookingTicketTrain</h2>"
                    + "<p style=\"font-size: 16px; color: #333333;\">Xin chào,</p>"
                    + "<p style=\"font-size: 16px; color: #333333;\">Mã OTP xác nhận hủy vé của bạn là:</p>"
                    + "<h1 style=\"font-size: 24px; color: #007bff; text-align: center;\">" + otp + "</h1>"
                    + "<p style=\"font-size: 16px; color: #333333;\">Mã có hiệu lực trong 120 giây.</p>"
                    + "<p style=\"font-size: 16px; color: #333333;\">Nếu bạn không yêu cầu hủy vé, vui lòng bỏ qua email này.</p>"
                    + "<br>"
                    + "<p style=\"font-size: 16px; color: #333333;\">Trân trọng,<br>OnlineBookingTicketTrain</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Gửi email
            Transport.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
