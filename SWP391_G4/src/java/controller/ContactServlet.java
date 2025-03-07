/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author dung9
 */
@WebServlet(name="ContactServlet", urlPatterns={"/contact"})
public class ContactServlet extends HttpServlet {
   
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
            out.println("<title>Servlet ContactServlet</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ContactServlet at " + request.getContextPath () + "</h1>");
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
        request.getRequestDispatcher("contact.jsp").forward(request, response);
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
    String name = request.getParameter("name");
    String email = request.getParameter("email");
    String subject = request.getParameter("subject");
    String msg = request.getParameter("message");
    
    final String username = "dungnmhe173094@fpt.edu.vn";
    final String password = "iysa hxar fbwc ggsv";
    
    Properties props = new Properties();
    props.put("mail.smtp.auth", true);
    props.put("mail.smtp.starttls.enable", true);
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new javax.mail.Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(username));
        message.setSubject("Contact Details");

        // Nội dung email
        StringBuilder content = new StringBuilder();
        content.append("<div style=\"font-family: Arial, sans-serif; line-height: 1.5;\">")
                .append("<p>Người dùng Online Ticket Train Booking đã gửi yêu cầu:</p>")
                .append("<table style=\"width: 100%; border-collapse: collapse; margin: 20px 0;\">")
                .append("<thead><tr>")
                .append("<th style=\"background-color: #0000ff; color: white; padding: 8px; border: 1px solid #ddd;\">Name</th>")
                .append("<th style=\"background-color: #0000ff; color: white; padding: 8px; border: 1px solid #ddd;\">Value</th>")
                .append("</tr></thead><tbody>")
                .append("<tr><td style=\"border: 1px solid #ddd; padding: 8px;\">Name</td><td style=\"border: 1px solid #ddd;\">").append(name).append("</td></tr>")
                .append("<tr><td style=\"border: 1px solid #ddd; padding: 8px;\">Email</td><td style=\"border: 1px solid #ddd;\">").append(email).append("</td></tr>")
                .append("<tr><td style=\"border: 1px solid #ddd; padding: 8px;\">Subject</td><td style=\"border: 1px solid #ddd;\">").append(subject).append("</td></tr>")
                .append("<tr><td style=\"border: 1px solid #ddd; padding: 8px;\">Message</td><td style=\"border: 1px solid #ddd; padding: 8px;\">").append(msg).append("</td></tr>")
                .append("</tbody></table>")
                .append("</div>");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(content.toString(), "text/html;charset=UTF-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textPart);

        message.setContent(multipart);
        Transport.send(message);

        // Gửi thông báo về request
        request.setAttribute("message", "Email đã được gửi thành công!");
        request.setAttribute("status", "success");
        
    } catch (Exception e) {
        request.setAttribute("message", "Lỗi khi gửi email. Vui lòng thử lại!");
        request.setAttribute("status", "error");
        e.printStackTrace();
    }

    // Quay lại trang hiện tại
    request.getRequestDispatcher("contact.jsp").forward(request, response);
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
