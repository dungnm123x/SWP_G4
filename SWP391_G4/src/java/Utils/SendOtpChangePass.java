package Utils;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */


import dal.UserDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
public class SendOtpChangePass extends HttpServlet {
   
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
            out.println("<title>Servlet SendOtpChangePass</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SendOtpChangePass at " + request.getContextPath () + "</h1>");
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
                UserDAO userDAO = new UserDAO();
        String email = request.getParameter("email");
        RequestDispatcher dispatcher = null;
        int otpvalue = 0;
        HttpSession mySession = request.getSession();

        // Kiểm tra email có tồn tại trong cơ sở dữ liệu không
        if (userDAO.checkemail(email) == false) {
            request.setAttribute("Notexisted", "Email không tồn tại!");
            request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
            return;
        }

        // Nếu email hợp lệ, gửi OTP
        if (email != null && !email.isEmpty()) {
            // Tạo mã OTP ngẫu nhiên
            Random rand = new Random();
            otpvalue = 100000 + rand.nextInt(900000); 

            // Thông tin người gửi email
            String to = email;
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            // Cấu hình session gửi email
            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("dungnmhe173094@fpt.edu.vn", "iysa hxar fbwc ggsv");
                }
            });

            // Tạo và gửi email OTP
            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("dungnmhe173094@fpt.edu.vn"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                message.setSubject("TrainTicketBooking: OTP Code to verify account");
                String htmlContent = "<!DOCTYPE html>"
                        + "<html>"
                        + "<body style=\"font-family: Arial, sans-serif; background-color: #ffffff;\">"
                        + "<div style=\"max-width: 600px; margin: auto; padding: 20px; border: 1px solid #cccccc; background-color: #ffffff;\">"
                        + "<h2 style=\"color: #007bff;\">TrainTicketBooking</h2>"
                        + "<p style=\"font-size: 16px; color: #333333;\">Hello,</p>"
                        + "<p style=\"font-size: 16px; color: #333333;\">Your OTP code to verify your account is:</p>"
                        + "<h1 style=\"font-size: 24px; color: #007bff; text-align: center;\">" + otpvalue + "</h1>"
                        + "<p style=\"font-size: 16px; color: #333333;\">Please use this code to complete your verification process.</p>"
                        + "<p style=\"font-size: 16px; color: #333333;\">If you did not request this code, please ignore this email.</p>"
                        + "<br>"
                        + "<p style=\"font-size: 16px; color: #333333;\">Thank you,<br>TrainTicketBooking </p>"
                        + "</div>"
                        + "</body>"
                        + "</html>";

                message.setContent(htmlContent, "text/html; charset=utf-8");

                // Gửi email
                Transport.send(message);
                System.out.println("Message sent successfully");

            } catch (MessagingException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            // Lưu OTP vào session để sử dụng cho bước sau
            mySession.setAttribute("otp", otpvalue);
            mySession.setAttribute("email", email);

            // Chuyển hướng người dùng đến trang nhập OTP
            dispatcher = request.getRequestDispatcher("EnterOTPResetPass.jsp");
            request.setAttribute("message", "An OTP code has been sent to your email: " + email);
            dispatcher.forward(request, response);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet to send OTP for password reset";
    }// </editor-fold>

}
