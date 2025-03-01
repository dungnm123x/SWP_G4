/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.employee;

import dal.BlogDAO;
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
import model.User;

/**
 *
 * @author admin
 */
@WebServlet(name = "BlogController", urlPatterns = {"/blog-list"})
public class BlogController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BlogDAO bd;

    @Override
    public void init() throws ServletException {
        bd = new BlogDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BlogController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BlogController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String blogName = request.getParameter("blogName");
        String category_Id_Raw = request.getParameter("categoryId");
        int categoryId = 0;
        int index = 1;
        int pageSize = 5;
        try {
            // Kiểm tra categoryId
            if (category_Id_Raw != null && !category_Id_Raw.trim().isEmpty()) {
                categoryId = Integer.parseInt(category_Id_Raw.trim());
            }

            // Kiểm tra index (trang hiện tại)
            String indexParam = request.getParameter("index");
            if (indexParam != null && !indexParam.isEmpty()) {
                index = Integer.parseInt(indexParam);
            }

            // Kiểm tra pageSize
            String pageSizeParam = request.getParameter("pageSize");
            if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeParam);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // ---- GỌI ĐẾN DAO ----
        int total_size = bd.getSizeBlog(blogName, categoryId);
        int num = (total_size == 0) ? 1 : (int) Math.ceil((double) total_size / pageSize);

        if (index > num) {
            index = num;
        }
        if (index < 1) {
            index = 1;
        }

        // Lấy danh sách blog kết hợp phân trang
        List<Blog> blog = bd.searchAndPagingBlog(blogName, categoryId, index, pageSize);
        List<CategoryBlog> categories = bd.getAllCategories();
        List<User> User = bd.getAllUser();
        List<String> thumbnails = bd.getUrlImageByStatus();
        // Truyền thông tin vào request
        request.setAttribute("categories", categories);
        request.setAttribute("User", User);
        request.setAttribute("blogList", blog);
        request.setAttribute("blogName", blogName);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("num", num);
        request.setAttribute("currentPage", index);
        request.setAttribute("total_size", total_size);
        request.setAttribute("start", (index - 1) * pageSize + 1);
        request.setAttribute("end", Math.min(index * pageSize, total_size));

        request.setAttribute("thumbnails", thumbnails);

        // Chuyển hướng sang trang JSP để hiển thị kết quả
        request.getRequestDispatcher("BlogList.jsp").forward(request, response);
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
