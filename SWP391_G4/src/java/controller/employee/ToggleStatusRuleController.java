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

/**
 *
 * @author dung9
 */
@WebServlet(name = "ToggleStatusRuleController", urlPatterns = {"/toggle-status-rule"})
public class ToggleStatusRuleController extends HttpServlet {

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
            out.println("<title>Servlet ToggleStatusRuleController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ToggleStatusRuleController at " + request.getContextPath() + "</h1>");
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
        processRequest(request, response);
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
            // Kiểm tra ruleID trước khi parse
            String ruleIDParam = request.getParameter("ruleID");
            if (ruleIDParam == null || ruleIDParam.trim().isEmpty()) {
                response.sendRedirect("manager-rule-list?error=missingRuleID");
                return;
            }
            int ruleID = Integer.parseInt(ruleIDParam);

            // Kiểm tra status trước khi parse
            String statusParam = request.getParameter("status");
            boolean status = (statusParam != null && statusParam.equals("true"));

            RuleDAO ruleDAO = new RuleDAO();
            boolean result = ruleDAO.updateRuleStatus(ruleID, status);

            if (result) {
                response.sendRedirect("manager-rule-list?success=updated");
            } else {
                response.sendRedirect("manager-rule-list?error=updateFailed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("manager-rule-list?error=invalidRuleID");
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
