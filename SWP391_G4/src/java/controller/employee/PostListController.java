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
import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 *
 * @author admin
 */
@WebServlet(name = "PostListController", urlPatterns = {"/posts-list"})
public class PostListController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BlogDAO bd;

    @Override
    public void init() throws ServletException {
        bd = new BlogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy tham số từ request
        String sortByParam = request.getParameter("sortBy");
        String statusParam = request.getParameter("status");

        // Chuyển đổi giá trị sortBy từ chuỗi sang số (4 giá trị cho các kiểu sắp xếp)
        int sortBy = 3; // Mặc định là sắp xếp theo ID tăng dần
        if (sortByParam != null && !sortByParam.isEmpty()) {
            switch (sortByParam) {
                case "name_asc":
                    sortBy = 1; // Tên (Tăng dần)
                    break;
                case "name_desc":
                    sortBy = 2; // Tên (Giảm dần)
                    break;
                case "id_asc":
                    sortBy = 3; // ID (Tăng dần)
                    break;
                case "id_desc":
                    sortBy = 4; // ID (Giảm dần)
                    break;
            }
        }

        // Xử lý trạng thái (hỗ trợ "Tất cả" bằng cách đặt status = null)
        Boolean status = null;
        if (statusParam != null && !statusParam.isEmpty() && !"all".equalsIgnoreCase(statusParam)) {
            status = Boolean.parseBoolean(statusParam);
        }

        // Lấy danh mục và tác giả để hiển thị
        List<CategoryBlog> categories = bd.getAllCategories();
        request.setAttribute("categories", categories);
        List<User> User = bd.getAllUser();
        request.setAttribute("User", User);

        // Các tham số tìm kiếm và phân trang
        String blogName = request.getParameter("key");
        String authorIdParam = request.getParameter("authorId");
        String categoryIdParam = request.getParameter("categoryId");
        String pageIndexParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");

        // Xử lý các giá trị số, tránh lỗi NumberFormatException
        int authorId = parseIntOrDefault(authorIdParam, 0);
        int categoryId = parseIntOrDefault(categoryIdParam, 0);
        int pageIndex = parseIntOrDefault(pageIndexParam, 1);
        int pageSize = parseIntOrDefault(pageSizeParam, 5);

        // Gọi service để lấy dữ liệu blog (có phân trang và tìm kiếm)
        List<Blog> blogs = bd.searchAndPagingBlog(blogName, authorId, categoryId, status, sortBy, pageIndex, pageSize);

        // Lấy tổng số lượng blog để tính toán phân trang
        int totalBlogs = bd.getSizeBlog(blogName, authorId, categoryId, status);

        // Tính tổng số trang
        int totalPages = (int) Math.ceil((double) totalBlogs / pageSize);

        // Đưa dữ liệu vào request để hiển thị ở JSP
        request.setAttribute("blogs", blogs);
        request.setAttribute("totalBlogs", totalBlogs);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", pageIndex);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("status", status);

        // Chuyển tiếp đến JSP
        request.getRequestDispatcher("/marketers/MKTPostList.jsp").forward(request, response);
    }

    /**
     * Chuyển đổi String thành Integer an toàn, nếu lỗi hoặc null thì trả về giá
     * trị mặc định.
     */
    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
