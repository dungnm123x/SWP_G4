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
import model.User;

/**
 *
 * @author dung9
 */
@WebServlet(name="DeleteRuleController", urlPatterns={"/delete-rule"})
public class DeleteRuleController extends HttpServlet {
    private RuleDAO rd;

    @Override
    public void init() throws ServletException {
        rd = new RuleDAO();
    }
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
            out.println("<title>Servlet DeleteRuleController</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet DeleteRuleController at " + request.getContextPath () + "</h1>");
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
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
       try {
            int ruleID = Integer.parseInt(request.getParameter("ruleID"));
            
            // Gọi hàm xóa trong DAO
            boolean success = rd.deleteRule(ruleID);

            if (success) {
                request.setAttribute("success", "Bài viết đã được xóa thành công!");
            } else {
                request.setAttribute("error", "Không thể xóa bài viết. Hãy thử lại.");
            }

            // Chuyển hướng về danh sách bài viết
            response.sendRedirect("manager-rule-list");

        } catch (Exception e) {
            response.sendRedirect("manager-rule-list?error=invalid_request");
        }
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
        processRequest(request, response);
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
