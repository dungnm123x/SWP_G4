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
@WebServlet(name = "ToggleStatusCategoryRule", urlPatterns = {"/status-categoryRule"})
public class ToggleStatusCategoryRule extends HttpServlet {

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
            out.println("<title>Servlet ToggleStatusCategoryRule</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ToggleStatusCategoryRule at " + request.getContextPath() + "</h1>");
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
        String categoryRuleIDStr = request.getParameter("categoryRuleID");
        String statusStr = request.getParameter("status");

        if (categoryRuleIDStr == null || statusStr == null) {
            response.getWriter().println("Error: Missing categoryRuleID or status");
            return;
        }

        try {
            int categoryRuleID = Integer.parseInt(categoryRuleIDStr);
            boolean status = Boolean.parseBoolean(statusStr);

            RuleDAO rd = new RuleDAO();
            boolean result = rd.updateCategoryRuleStatus(categoryRuleID, status);

            if (result) {
                request.getSession().setAttribute("message", "Status updated successfully!");
                response.sendRedirect("category-rule");
            } else {
                response.getWriter().println("Error updating status");
            }
        } catch (NumberFormatException e) {
            response.getWriter().println("Error: Invalid categoryRuleID format");
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
