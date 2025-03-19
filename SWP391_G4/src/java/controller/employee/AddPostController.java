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
import java.util.List;
import model.User;

/**
 *
 * @author admin
 */
@WebServlet(name = "AddPostController", urlPatterns = {"/add-post"})
@MultipartConfig(maxFileSize = 16177215)
public class AddPostController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BlogDAO bd;

    @Override
    public void init() throws ServletException {
        bd = new BlogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<CategoryBlog> categories = bd.getAllCategories();
        request.setAttribute("categories", categories);
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        request.getRequestDispatcher("/marketers/AddBlog.jsp").forward(request, response);
        
    }
@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    String title = request.getParameter("title");
    String briefInfo = request.getParameter("brief_infor");
    String content = request.getParameter("content");
    int categoryId = Integer.parseInt(request.getParameter("categoryId"));
    int status = Integer.parseInt(request.getParameter("status"));
    int authorId = 3; 
    Part filePart = request.getPart("thumbnail");

    if (filePart == null || filePart.getSize() == 0) {
        request.setAttribute("error", "Vui lòng chọn ảnh thumbnail!");
        doGet(request, response);
        return;
    }

    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

    if (!fileExtension.equals("jpg") && !fileExtension.equals("png")) {
        request.setAttribute("error", "Ảnh thumbnail phải có định dạng JPG hoặc PNG!");
        doGet(request, response);
        return;
    }

    String thumbnailBase64 = convertImageToBase64(filePart);

    bd.addNewBlog(title, authorId, content, briefInfo, categoryId, status, thumbnailBase64);
    response.sendRedirect("posts-list");
}
private String convertImageToBase64(Part part) throws IOException {
    byte[] fileBytes = inputStreamToByteArray(part.getInputStream());
    String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
    String mimeType = part.getContentType();
    return "data:" + mimeType + ";base64," + base64Encoded;
}

private byte[] inputStreamToByteArray(InputStream is) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    byte[] data = new byte[8192]; // 8 KB buffer size
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
