/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.employee;

import dal.RuleDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CategoryRule;
import model.Rule;
import model.User;

/**
 *
 * @author dung9
 */
@WebServlet(name = "EditCategoryRuleController", urlPatterns = {"/edit-categoryRule"})
public class EditCategoryRuleController extends HttpServlet {

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
            out.println("<title>Servlet EditCategoryRuleController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditCategoryRuleController at " + request.getContextPath() + "</h1>");
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
        if (user == null || (user.getRoleID() != 1 && user.getRoleID() != 2)) {
            response.sendRedirect("login");
            return;
        }
        try {
            int categoryRuleID = Integer.parseInt(request.getParameter("categoryRuleID"));
            RuleDAO ruleDAO = new RuleDAO();
            CategoryRule categoryRule = ruleDAO.getCategoryRuleByID(categoryRuleID);

            if (categoryRule != null) {
                request.setAttribute("categoryRule", categoryRule);

                // Lấy thông báo từ session và xóa để tránh hiển thị lại nhiều lần
                request.setAttribute("successMessage", request.getSession().getAttribute("successMessage"));
                request.setAttribute("errorMessage", request.getSession().getAttribute("errorMessage"));
                request.getSession().removeAttribute("successMessage");
                request.getSession().removeAttribute("errorMessage");

                request.getRequestDispatcher("/view/employee/EditCategoryRule.jsp").forward(request, response);
            } else {
                response.sendRedirect("category-rule?error=notfound");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("category-rule?error=invalidID");
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
            // Lấy dữ liệu từ form
            int categoryRuleID = Integer.parseInt(request.getParameter("categoryRuleID"));
            String categoryRuleName = request.getParameter("categoryRuleName");
            String content = request.getParameter("content");
            boolean status = request.getParameter("status").equals("1");

            // Gọi DAO để cập nhật dữ liệu
            RuleDAO ruleDAO = new RuleDAO();
            boolean result = ruleDAO.updateCategory(categoryRuleID, categoryRuleName, content, "", status);

            if (result) {
                request.getSession().setAttribute("successMessage", "Cập nhật thành công!");
            } else {
                request.getSession().setAttribute("errorMessage", "Cập nhật thất bại.");
            }

            // Chuyển hướng sau khi cập nhật
            response.sendRedirect("edit-categoryRule?categoryRuleID=" + categoryRuleID);
        } catch (NumberFormatException e) {
            response.sendRedirect("category-rule?error=invalidData");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("category-rule?error=serverError");
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
