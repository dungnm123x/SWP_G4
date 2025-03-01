/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.employee;

import dal.BlogDAO;
import model.User;
import model.Blog;
import model.CategoryBlog;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author admin
 */
@WebServlet(name = "EditBlogController", urlPatterns = {"/edit-blog"})
public class EditBlogController extends HttpServlet {

    private BlogDAO blogDAO;

    @Override
    public void init() throws ServletException {
        blogDAO = new BlogDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet EditBlogController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet EditBlogController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int blogId = Integer.parseInt(request.getParameter("blog_id"));
        Blog blog = blogDAO.getBlogByBlogId(blogId);
        List<CategoryBlog> categories = blogDAO.getAllCategories();
        request.setAttribute("categories", categories);
        List<User> User = blogDAO.getAllUser();
        request.setAttribute("User", User);
        if (blog != null) {
            request.setAttribute("blog", blog);
            request.getRequestDispatcher("/marketers/EditBlog.jsp").forward(request, response);
        } else {
            response.sendRedirect("posts-list?error=not_found");
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
        processRequest(request, response);
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
