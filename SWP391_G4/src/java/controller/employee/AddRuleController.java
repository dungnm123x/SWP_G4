/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.employee;

import dal.RuleDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.sql.SQLException;
import java.util.List;
import model.CategoryRule;
import model.User;

/**
 *
 * @author dung9
 */
@WebServlet(name = "AddRuleController", urlPatterns = {"/add-rule"})
@MultipartConfig(maxFileSize = 16177215)
public class AddRuleController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RuleDAO rd;

    @Override
    public void init() throws ServletException {
        rd = new RuleDAO();
    }

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
            out.println("<title>Servlet AddRuleController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddRuleController at " + request.getContextPath() + "</h1>");
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
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        List<CategoryRule> categories = rd.getAllCategories();
        request.setAttribute("categories", categories);
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        request.getRequestDispatcher("/view/employee/AddRule.jsp").forward(request, response);

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

        String name = request.getParameter("ruleName");
        String content = request.getParameter("content");

        Part filePart = request.getPart("img");
        String img = (filePart != null) ? filePart.getSubmittedFileName() : null;

        String userIDParam = request.getParameter("userID");
        String categoryRuleIDParam = request.getParameter("categoryRuleID");
        String statusParam = request.getParameter("status");

        String message = null;
        String error = null; // Biến lưu lỗi

        try {
            if (name == null || content == null || userIDParam == null || categoryRuleIDParam == null || statusParam == null) {
                throw new IllegalArgumentException("Thiếu dữ liệu đầu vào.");
            }
            if (name.trim().isEmpty() || content.trim().isEmpty()) {
                error = "❌ Title và nội dung không được để trống hoặc chỉ chứa khoảng trắng!";
                request.getSession().setAttribute("error", error);
                response.sendRedirect("add-rule"); // Điều hướng lại trang thêm quy định
                return;
            }
            int userID = Integer.parseInt(userIDParam);
            int categoryRuleID = Integer.parseInt(categoryRuleIDParam);
            boolean status = "1".equals(statusParam);

            RuleDAO ruleDAO = new RuleDAO();

            // Kiểm tra title trùng
            if (ruleDAO.isTitleExists(name)) {
                error = "❌ Title đã tồn tại. Vui lòng chọn một title khác!";
            } else {
                // Nếu title chưa tồn tại, thêm mới
                boolean success = ruleDAO.addRule(name, userID, content, img, status, categoryRuleID);
                if (success) {
                    message = "✅ Thêm quy định thành công!";
                } else {
                    error = "❌ Thêm quy định thất bại!";
                }
            }

        } catch (NumberFormatException e) {
            error = "Dữ liệu không hợp lệ!";
        } catch (SQLException e) {
            error = "Lỗi cơ sở dữ liệu!";
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            error = "" + e.getMessage();
        }

        // Lấy lại danh sách category trước khi forward về JSP
        List<CategoryRule> categories = rd.getAllCategories();
        request.setAttribute("categories", categories);

        request.setAttribute("error", error);
        request.setAttribute("message", message);
        request.getRequestDispatcher("/view/employee/AddRule.jsp").forward(request, response);
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
