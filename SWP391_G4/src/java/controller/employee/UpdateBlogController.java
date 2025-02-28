/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.employee;

import dal.BlogDAO;
import model.Blog;
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
import java.util.Base64;

/**
 *
 * @author admin
 */
@WebServlet(name = "UpdateBlogController", urlPatterns = {"/update-blog"})
@MultipartConfig(maxFileSize = 16177215)
public class UpdateBlogController extends HttpServlet {

    private static final String UPLOAD_DIR = "upload_blogs";
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
            out.println("<title>Servlet UpdateBlogController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateBlogController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
