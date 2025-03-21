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
import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Lấy và kiểm tra các tham số
            int blogId = Integer.parseInt(request.getParameter("blog_id"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String briefInfor = request.getParameter("brief_infor");
            int categoryId = Integer.parseInt(request.getParameter("categoryBlog_id"));
            int status = Integer.parseInt(request.getParameter("status"));
            Part filePart = request.getPart("thumbnail");
            BlogDAO blogDAO = new BlogDAO();
            Blog existingBlog = blogDAO.getBlogByBlogId(blogId);
            String thumbnailBase64 = existingBlog.getThumbnail();

            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

                if (!fileExtension.equals("jpg") && !fileExtension.equals("png")) {
                    request.setAttribute("errorMessage", "Only JPG and PNG files are allowed.");
                    request.getRequestDispatcher("/marketers/EditBlog.jsp").forward(request, response);
                    return;
                }
                thumbnailBase64 = convertImageToBase64(filePart);
            }
            blogDAO.UpdateBlogById(title, content, briefInfor, categoryId, status, thumbnailBase64, blogId);
            response.sendRedirect("edit-blog?blog_id=" + blogId);
        } catch (Exception e) {
            // Log lỗi
            e.printStackTrace();
            request.setAttribute("errorMessage", "Failed to update blog. Please try again.");
            request.getRequestDispatcher("/marketers/EditBlog.jsp").forward(request, response);
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String content : contentDisp.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf("=") + 2, content.length() - 1);
            }
        }
        return "";
    }

    private String convertImageToBase64(Part part) throws IOException {
        byte[] fileBytes = inputStreamToByteArray(part.getInputStream());
        String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
        String mimeType = part.getContentType();
        return "data:" + mimeType + ";base64," + base64Encoded;
    }

    private byte[] inputStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int bytesRead;
        while ((bytesRead = is.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
