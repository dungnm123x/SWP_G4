/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.employee;

import dal.RuleDAO;
import model.CategoryBlog;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.CategoryRule;
import model.User;

/**
 *
 * @author admin
 */
@WebServlet(name = "AddCategoryController", urlPatterns = {"/add-categoryRule"})
@MultipartConfig(maxFileSize = 16177215)
public class AddCategoryRuleController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RuleDAO rd;

    @Override
    public void init() throws ServletException {
        rd = new RuleDAO();
    }

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
        request.getRequestDispatcher("/view/employee/AddCategoryRule.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy dữ liệu từ form
        String name = request.getParameter("categoryRuleName");
        String content = request.getParameter("content");
        String img = request.getParameter("img");
        boolean status = "1".equals(request.getParameter("status")); // Chuyển đổi từ radio button
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        if (name == null || name.trim().isEmpty()
                || content == null || content.trim().isEmpty()) {
            request.setAttribute("message", "Tên danh mục, nội dung không được để trống hoặc chỉ chứa dấu cách.");
            request.getRequestDispatcher("/view/employee/AddCategoryRule.jsp").forward(request, response);
            return;
        }
        System.out.println("🔹 Dữ liệu nhận được: " + name + ", " + content + ", " + img + ", " + status);

        RuleDAO ruleDAO = new RuleDAO();
        String message;

        try {
            boolean success = ruleDAO.addCategory(name, content, img, status);
            if (success) {
                System.out.println("✅ Thêm thành công!");
                message = "Thêm danh mục thành công!";
            } else {
                System.out.println("❌ Thêm thất bại!");
                message = "Thêm danh mục thất bại!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            message = "Không Được Đặt Trùng Tên";
        }

        // Gửi lại message về trang JSP
        request.setAttribute("message", message);
        request.getRequestDispatcher("/view/employee/AddCategoryRule.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
