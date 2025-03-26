package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author dung9
 */
public class RegisterServlet extends HttpServlet {

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
            out.println("<title>Servlet RegisterServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegisterServlet at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("register.jsp").forward(request, response);
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
        // Lấy dữ liệu từ form
        String fullname = request.getParameter("fullname");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String repassword = request.getParameter("repassword");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        UserDAO dao = new UserDAO();
        boolean hasError = false;

        // Kiểm tra mật khẩu
        if (!password.matches("^(?=.*[^A-Za-z0-9])(?=\\S+$).{6,}$")) {
            request.setAttribute("passwordError", "Mật khẩu phải có ít nhất 1 ký tự đặc biệt, tối thiểu 6 ký tự và không được chứa dấu cách!");
            hasError = true;
        } else if (!password.equals(repassword)) {
            request.setAttribute("repasswordError", "Mật khẩu nhập lại không khớp!");
            hasError = true;
        }
        // Kiểm tra email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            request.setAttribute("emailError", "Email không hợp lệ!");
            hasError = true;
        } else if (dao.checkemail(email)) {
            request.setAttribute("emailError", "Email đã tồn tại!");
            hasError = true;
        }

        // Kiểm tra số điện thoại
        if (!phone.matches("\\d{10,11}")) {
            request.setAttribute("phoneError", "Số điện thoại không hợp lệ!");
            hasError = true;
        } else if (dao.checkphone(phone)) {
            request.setAttribute("phoneError", "Số điện thoại đã tồn tại!");
            hasError = true;
        }

        // Kiểm tra Username đã tồn tại chưa
        if (dao.checkAccountExist(username) != null) {
            request.setAttribute("usernameError", "Tên tài khoản đã tồn tại!");
            hasError = true;
        } else if (username.contains(" ")) { // Kiểm tra xem có dấu cách không
            request.setAttribute("usernameError", "Tên tài khoản không được chứa dấu cách!");
            hasError = true;
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) { // Chỉ cho phép chữ, số, và dấu gạch dưới
            request.setAttribute("usernameError", "Tên tài khoản chỉ được chứa chữ cái, số và dấu gạch dưới!");
            hasError = true;
        }
        if (hasError) {
            // Giữ lại dữ liệu đã nhập nếu có lỗi
            request.setAttribute("fullname", fullname);
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("address", address);

            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }

        // Nếu không có lỗi, tạo tài khoản mới
        User newUser = new User();
        newUser.setFullName(fullname);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phone);
        newUser.setAddress(address);
        dao.AddAccount(newUser);

        // Lưu session và chuyển hướng về trang đăng nhập
        HttpSession session = request.getSession();
        session.setAttribute("user", dao.getUserByUsername(username));
        response.sendRedirect(request.getContextPath() + "/login");

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

}
