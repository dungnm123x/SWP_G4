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
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.CategoryRule;
import model.Rule;
import model.User;

/**
 *
 * @author dung9
 */
@WebServlet(name = "EditRuleController", urlPatterns = {"/edit-rule"})
public class EditRuleController extends HttpServlet {

    private RuleDAO rb;

    @Override
    public void init() throws ServletException {
        rb = new RuleDAO();
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
            out.println("<title>Servlet EditRuleController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditRuleController at " + request.getContextPath() + "</h1>");
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
        try {
            String ruleIDStr = request.getParameter("ruleID");

            if (ruleIDStr == null || !ruleIDStr.matches("\\d+")) {
                response.sendRedirect("manager-rule-list?error=invalid_ruleID");
                return;
            }
            int ruleID = Integer.parseInt(ruleIDStr);

            Rule rule = rb.getRuleByID(ruleID);
            if (rule == null) {
                response.sendRedirect("manager-rule-list?error=not_found");
                return;
            }

            List<CategoryRule> categories = rb.getAllCategories();
            request.setAttribute("categories", categories);

            List<User> users = rb.getAllUser();
            request.setAttribute("users", users);
            request.setAttribute("rule", rule);

            request.getRequestDispatcher("/view/employee/EditRule.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("manager-rule-list?error=exception");
        }

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
        try {
            // Kiểm tra và lấy ruleID
            String ruleIDStr = request.getParameter("ruleID");
            if (ruleIDStr == null || !ruleIDStr.matches("\\d+")) {
                response.sendRedirect("manager-rule-list?error=invalid_ruleID");
                return;
            }
            int ruleID = Integer.parseInt(ruleIDStr);

            // Kiểm tra và lấy userID
            String userIDStr = request.getParameter("userID");
            if (userIDStr == null || !userIDStr.matches("\\d+")) {
                response.sendRedirect("edit-rule?ruleID=" + ruleID + "&error=invalid_userID");
                return;
            }
            int userID = Integer.parseInt(userIDStr);

            // Kiểm tra và lấy categoryRuleID
            String categoryRuleIDStr = request.getParameter("categoryRuleID");
            if (categoryRuleIDStr == null || !categoryRuleIDStr.matches("\\d+")) {
                response.sendRedirect("edit-rule?ruleID=" + ruleID + "&error=invalid_category");
                return;
            }
            int categoryRuleID = Integer.parseInt(categoryRuleIDStr);

            String title = request.getParameter("title");
            String content = request.getParameter("content");

            // Kiểm tra title và content không được nhập toàn dấu cách
            if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                request.setAttribute("errorMessage", "❌ Tiêu đề và nội dung không được để trống hoặc chỉ chứa khoảng trắng!");

                // Load lại dữ liệu để hiển thị
                Rule rule = rb.getRuleByID(ruleID);
                List<CategoryRule> categories = rb.getAllCategories();
                request.setAttribute("rule", rule);
                request.setAttribute("categories", categories);

                request.getRequestDispatcher("/view/employee/EditRule.jsp").forward(request, response);
                return;
            }
            if (rb.isTitleExists(title) && !title.equals(rb.getRuleByID(ruleID).getTitle())) {
                request.setAttribute("errorMessage", "Tiêu đề đã tồn tại, vui lòng chọn tiêu đề khác!");

                // Load lại dữ liệu để hiển thị
                Rule rule = rb.getRuleByID(ruleID);
                List<CategoryRule> categories = rb.getAllCategories();
                request.setAttribute("rule", rule);
                request.setAttribute("categories", categories);

                request.getRequestDispatcher("/view/employee/EditRule.jsp").forward(request, response);
                return;
            }
            boolean status = "1".equals(request.getParameter("status"));

            // Debug thông tin đầu vào
            System.out.println("Updating rule with: " + ruleID + ", " + title + ", " + userID + ", " + content + ", " + status + ", " + categoryRuleID);

            // Gọi DAO cập nhật dữ liệu
            boolean success = rb.updateRule(ruleID, title, userID, content, status, categoryRuleID);

            if (success) {
                request.setAttribute("successMessage", "Cập nhật thành công!");
            } else {
                request.setAttribute("errorMessage", "Cập nhật thất bại, vui lòng thử lại!");
            }

            // Load lại dữ liệu để hiển thị
            Rule rule = rb.getRuleByID(ruleID);
            List<CategoryRule> categories = rb.getAllCategories();
            request.setAttribute("rule", rule);
            request.setAttribute("categories", categories);

            // Quay lại trang EditRule.jsp
            request.getRequestDispatcher("/view/employee/EditRule.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3>Error occurred:</h3>");
            response.getWriter().println("<pre>");
            e.printStackTrace(new PrintWriter(response.getWriter()));
            response.getWriter().println("</pre>");
        }
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
