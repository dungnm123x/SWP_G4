/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.employee;

import dal.BlogDAO;
import model.CategoryBlog;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.User;

/**
 *
 * @author admin
 */
@WebServlet(name = "CategoryBlogController", urlPatterns = {"/category-blog"})
public class CategoryBlogController extends HttpServlet {

    private static final long serialVersionUID = 1L;
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
            out.println("<title>Servlet CategoryBlogController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CategoryBlogController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        try {
            String searchName = request.getParameter("categoryName");
            int status = request.getParameter("status") != null ? Integer.parseInt(request.getParameter("status")) : -1;
            int sortBy = request.getParameter("sortBy") != null ? Integer.parseInt(request.getParameter("sortBy")) : 0;
            int pageSize = request.getParameter("pageSize") != null ? Integer.parseInt(request.getParameter("pageSize")) : 10;
            int currentPage = request.getParameter("index") != null ? Integer.parseInt(request.getParameter("index")) : 1;

            List<CategoryBlog> categories = blogDAO.searchAndPagingCategoryBlog(searchName, status, sortBy, currentPage, pageSize);

            int totalCategories = blogDAO.getSizeCategoryBlog(searchName, status);
            int num = (int) Math.ceil((double) totalCategories / pageSize);

            request.setAttribute("categories", categories);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("num", num);
            request.setAttribute("categoryName", searchName);
            request.setAttribute("status", status);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalCategories", totalCategories); // Để kiểm tra có dữ liệu hay không

            request.getRequestDispatcher("categoryBlog.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "An error occurred while processing the request.");
            request.getRequestDispatcher("categoryBlog.jsp").forward(request, response);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                String name = request.getParameter("name");
                int status = Integer.parseInt(request.getParameter("status"));
                if (name == null || name.trim().isEmpty()) { // Kiểm tra nếu toàn dấu cách
                    request.getSession().setAttribute("error", "Tên danh mục không được để trống hoặc chỉ chứa khoảng trắng!");
                    response.sendRedirect("category-blog");
                    return;
                }
                if (blogDAO.addCategory(name, status)) {
                    request.getSession().setAttribute("message", "Category added successfully!");
                } else {
                    request.getSession().setAttribute("error", "Category name already exists!");
                }
                response.sendRedirect("category-blog");

            } else if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("name");
                int status = Integer.parseInt(request.getParameter("status"));
                if (name == null || name.trim().isEmpty()) { // Kiểm tra nếu toàn dấu cách
                    request.getSession().setAttribute("error", "Tên danh mục không được để trống hoặc chỉ chứa khoảng trắng!");
                    response.sendRedirect("category-blog");
                    return;
                }
                if (blogDAO.updateCategory(id, name, status)) {
                    request.getSession().setAttribute("message", "Category updated successfully!");
                } else {
                    request.getSession().setAttribute("error", "Category name already exists!");
                }
                response.sendRedirect("category-blog");

            } else if ("delete".equals(action)) {
                String idParam = request.getParameter("id");

                if (idParam == null || idParam.trim().isEmpty()) {
                    request.getSession().setAttribute("error", "Invalid category ID!");
                    response.sendRedirect("category-blog");
                    return;
                }

                try {
                    int id = Integer.parseInt(idParam);
                    boolean isDeleted = blogDAO.deleteCategory(id);

                    if (isDeleted) {
                        request.getSession().setAttribute("message", "Category deleted successfully!");
                    } else {
                        request.getSession().setAttribute("error", "Failed to delete category!");
                    }
                } catch (NumberFormatException e) {
                    request.getSession().setAttribute("error", "Invalid category ID format!");
                }

                response.sendRedirect("category-blog");
            }

        } catch (Exception e) {
            throw new ServletException(e);
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
