package controller.employee;

import dal.BlogDAO;
import model.User;
import model.Blog;
import model.CategoryBlog;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
@WebServlet(name = "EditBlogController", urlPatterns = {"/edit-blog"})
public class EditBlogController extends HttpServlet {

    private BlogDAO blogDAO;

    @Override
    public void init() throws ServletException {
        blogDAO = new BlogDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || (user.getRoleID() != 1 && user.getRoleID() != 2)) {
            response.sendRedirect("login");
            return;
        }

        try {
            int blogId = Integer.parseInt(request.getParameter("blog_id"));
            Blog blog = blogDAO.getBlogByBlogId(blogId);
            if (blog == null) {
                response.sendRedirect("posts-list?error=not_found");
                return;
            }

            List<CategoryBlog> categories = blogDAO.getAllCategories();
            request.setAttribute("blog", blog);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/marketers/EditBlog.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("posts-list?error=invalid_id");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int blogId = Integer.parseInt(request.getParameter("blog_id"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String briefInfor = request.getParameter("brief_infor");
            int categoryId = Integer.parseInt(request.getParameter("categoryBlog_id"));
            int status = Integer.parseInt(request.getParameter("status"));

            Part filePart = request.getPart("thumbnail");
            if (title.trim().isEmpty() || briefInfor.trim().isEmpty() || content.trim().isEmpty()) {
                request.getSession().setAttribute("error", "Tiêu đề, Tóm tắt và Nội dung không được để trống hoặc chỉ chứa khoảng trắng!");
                response.sendRedirect("edit-blog?blog_id=" + blogId);
                return;
            }

            Blog existingBlog = blogDAO.getBlogByBlogId(blogId);
            if (existingBlog == null) {
                request.getSession().setAttribute("error", "Blog không tồn tại.");
                response.sendRedirect("edit-blog?blog_id=" + blogId);
                return;
            }

            if (blogDAO.isTitleExistsExceptCurrent(title, blogId)) {
                request.getSession().setAttribute("error", "Tiêu đề blog đã tồn tại. Vui lòng chọn tiêu đề khác!");
                response.sendRedirect("edit-blog?blog_id=" + blogId);
                return;
            }

            String thumbnailBase64 = existingBlog.getThumbnail();
            if (filePart != null && filePart.getSize() > 0) {
                String fileName = extractFileName(filePart);
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (!fileExtension.equals("jpg") && !fileExtension.equals("png")) {
                    request.getSession().setAttribute("error", "Chỉ chấp nhận file JPG và PNG.");
                    response.sendRedirect("edit-blog?blog_id=" + blogId);
                    return;
                }
                thumbnailBase64 = convertImageToBase64(filePart);
            }

            boolean isUpdated = blogDAO.updateBlog(blogId, title, briefInfor, content, categoryId, status == 1, thumbnailBase64);
            if (isUpdated) {
                request.getSession().setAttribute("success", "Cập nhật blog thành công!");
                response.sendRedirect("edit-blog?blog_id=" + blogId);
            } else {
                request.getSession().setAttribute("error", "Cập nhật thất bại, vui lòng thử lại.");
                response.sendRedirect("edit-blog?blog_id=" + blogId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi trong quá trình cập nhật. Vui lòng thử lại.");
            response.sendRedirect("edit-blog");
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
}
