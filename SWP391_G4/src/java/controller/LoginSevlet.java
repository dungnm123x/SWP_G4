package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import Utils.Encryptor;
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author dung9
 */
public class LoginSevlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
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
            out.println("<title>Servlet LoginSevlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginSevlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password");

        System.out.println("DEBUG: Username nhập vào = " + username);
        System.out.println("DEBUG: Password nhập vào = " + password);

        // Mã hóa mật khẩu nhập vào để so sánh với DB
        String encryptedPassword = Encryptor.encryptPassword(password);

        System.out.println("DEBUG: Username nhập vào = " + username);
        System.out.println("DEBUG: Password sau khi mã hóa = " + encryptedPassword);

        UserDAO userDAO = new UserDAO();
        User user = userDAO.checkUserLogin(username, encryptedPassword);

        boolean hasError = false;

        if (user == null) {
            request.setAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.setAttribute("username", username);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        if (!user.isStatus()) { // Check the status!
            request.setAttribute("loginError", "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ quản trị viên.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        if (hasError) {
            request.setAttribute("username", username);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        System.out.println("DEBUG: Đăng nhập thành công với tài khoản " + username);
        HttpSession session = request.getSession();
        session.setAttribute("user", user);

        boolean rememberMe = request.getParameter("remember") != null;
        if (rememberMe) {
            Cookie cuser = new Cookie("cuser", username);
            Cookie cpass = new Cookie("cpass", password);
            Cookie crem = new Cookie("crem", "on");

            cuser.setMaxAge(60 * 60 * 24 * 5);
            cpass.setMaxAge(60 * 60 * 24 * 5);
            crem.setMaxAge(60 * 60 * 24 * 5);

            response.addCookie(cuser);
            response.addCookie(cpass);
            response.addCookie(crem);
        } else {
            clearCookies(response);
        }

//        if (user.getRoleID() == 2) {
//            response.sendRedirect("trip");
//        } else if (user.getRoleID() == 1) {
//            response.sendRedirect("admin");
//        } else {
//            response.sendRedirect("home");
//        }
        String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
        if (redirectUrl != null) {
            session.removeAttribute("redirectAfterLogin");
        } else if (user.getRoleID() == 2) {
            redirectUrl = "trip";
        } else if (user.getRoleID() == 1) {
            redirectUrl = "admin";
        } else {
            redirectUrl = "home";
        }

        response.sendRedirect(redirectUrl);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void clearCookies(HttpServletResponse response) {
        Cookie cuser = new Cookie("cuser", "");
        Cookie cpass = new Cookie("cpass", "");
        Cookie crem = new Cookie("crem", "");

        cuser.setMaxAge(0);
        cpass.setMaxAge(0);
        crem.setMaxAge(0);

        response.addCookie(cuser);
        response.addCookie(cpass);
        response.addCookie(crem);
    }
}
