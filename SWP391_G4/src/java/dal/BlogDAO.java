/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import model.Blog;
import model.CategoryBlog;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class BlogDAO extends DBContext {

    public List<CategoryBlog> getAllCategories() {
        String query = "SELECT categoryBlog_id, categoryBlogName, status FROM CategoryBlog Where status =1";
        List<CategoryBlog> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            // Duyệt qua kết quả trả về
            while (rs.next()) {
                int id = rs.getInt("categoryBlog_id");
                String name = rs.getString("categoryBlogName");
                int status = rs.getInt("status");

                // Thêm đối tượng CategoryBlog vào danh sách
                list.add(new CategoryBlog(id, name, status));
            }
        } catch (SQLException ex) {
            // Log lỗi nếu có ngoại lệ
            Logger.getLogger(BlogDAO.class.getName()).log(Level.SEVERE, "Error fetching categories", ex);
        }
        return list;
    }

    public boolean addCategory(String name, int status) throws SQLException {
        if (isNameExists(name)) {
            return false;
        }

        String query = "INSERT INTO CategoryBlog (categoryBlogName, status) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setInt(2, status);
            ps.executeUpdate();
        }
        return true;
    }

    public boolean updateCategory(int id, String name, int status) throws SQLException {
        if (isNameExists(name) && !isSameCategory(id, name)) {
            return false;
        }

        String query = "UPDATE CategoryBlog SET categoryBlogName = ?, status = ? WHERE categoryBlog_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setInt(2, status);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
        return true;
    }

    private boolean isSameCategory(int id, String name) throws SQLException {
        String query = "SELECT categoryBlog_id FROM CategoryBlog WHERE categoryBlogName = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("categoryBlog_id") == id;
                }
            }
        }
        return false;
    }

    public boolean deleteCategory(int id) {
        String query = "DELETE FROM CategoryBlog WHERE categoryBlog_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isNameExists(String name) throws SQLException {
        String query = "SELECT COUNT(*) FROM CategoryBlog WHERE categoryBlogName = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean updateStatus(int id, int status) {
        String query = "UPDATE CategoryBlog SET status = ? WHERE categoryBlog_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, status);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CategoryBlog> searchAndPagingCategoryBlog(String categoryName, int status, int sortBy, int index, int pageSize) {
        List<CategoryBlog> categories = new ArrayList<>();
        String sql = "SELECT categoryBlog_id, categoryBlogName, status FROM CategoryBlog WHERE 1=1";

        // Tìm kiếm theo tên danh mục nếu có
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            sql += "AND categoryBlogName LIKE ? ";
        }

        // Lọc theo trạng thái nếu có
        if (status == 0 || status == 1) {
            sql += "AND status = ? ";
        }

        // Xử lý sắp xếp
        switch (sortBy) {
            case 1: // Sắp xếp theo tên A-Z
                sql += "ORDER BY categoryBlogName ASC ";
                break;
            case 2: // Sắp xếp theo tên Z-A
                sql += "ORDER BY categoryBlogName DESC ";
                break;
            case 3: // Sắp xếp theo ID tăng dần
                sql += "ORDER BY categoryBlog_id ASC ";
                break;
            case 4: // Sắp xếp theo ID giảm dần
                sql += "ORDER BY categoryBlog_id DESC ";
                break;
            default: // Không sắp xếp
                sql += "ORDER BY categoryBlog_id ASC ";
        }

        // Thêm phân trang
        sql += "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Nếu có tìm kiếm theo tên
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + categoryName.trim() + "%");
            }

            // Nếu có lọc theo trạng thái
            if (status == 0 || status == 1) {
                ps.setInt(paramIndex++, status);
            }

            // Thêm tham số OFFSET và FETCH
            int offset = (index - 1) * pageSize;
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CategoryBlog category = new CategoryBlog();
                    category.setCategoryBlogId(rs.getInt("categoryBlog_id"));
                    category.setCategoryBlogName(rs.getString("categoryBlogName"));
                    category.setStatus(rs.getInt("status"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

    public int getSizeCategoryBlog(String categoryName, int status) {
        int count = 0;

        String sql = "SELECT COUNT(*) AS TotalCount FROM CategoryBlog WHERE 1=1 AND status =1";

        // Thêm điều kiện tìm kiếm theo tên danh mục nếu có
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            sql += "AND categoryBlogName LIKE ? ";
        }

        // Thêm điều kiện lọc theo trạng thái nếu được chọn
        if (status == 0 || status == 1) {
            sql += "AND status = ? ";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Thêm tham số tìm kiếm theo tên
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + categoryName.trim() + "%");
            }

            // Thêm tham số lọc trạng thái
            if (status == 0 || status == 1) {
                ps.setInt(paramIndex++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    // get all blog
    public List<Blog> getAllBlog() {
        List<Blog> list = new ArrayList<>();
        String sql = "SELECT TOP 2 b.blog_id, b.title, b.UserID, b.update_date, b.content, b.thumbnail, b.brief_infor, b.categoryBlog_id "
                + "FROM Blog b "
                + "JOIN CategoryBlog c ON b.categoryBlog_id = c.categoryBlog_id "
                + "WHERE b.status = 1 AND c.status = 1 "
                + "ORDER BY NEWID()";  // Sắp xếp ngẫu nhiên

        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                Blog c = new Blog();
                c.setBlog_id(rs.getInt("blog_id"));
                c.setTitle(rs.getString("title"));
                c.setUserID(rs.getInt("UserID"));
                c.setUpdated_date(rs.getDate("update_date"));
                c.setContent(rs.getString("content"));
                c.setThumbnail(rs.getString("thumbnail"));
                c.setBrief_infor(rs.getString("brief_infor"));
                c.setCategoryBlog_id(rs.getInt("categoryBlog_id"));

                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    public List<Blog> getTopBlogs() {
        List<Blog> blogs = new ArrayList<>();
        String sql = "SELECT TOP 5 b.blog_id, b.title, b.thumbnail, b.update_date "
                + "FROM Blog b "
                + "JOIN CategoryBlog c ON b.categoryBlog_id = c.categoryBlog_id "
                + "WHERE b.status = 1 AND c.status = 1 "
                + "ORDER BY b.update_date DESC";  // Sắp xếp theo ngày đăng mới nhất

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Blog blog = new Blog();
                blog.setBlog_id(rs.getInt("blog_id"));
                blog.setTitle(rs.getString("title"));
                blog.setThumbnail(rs.getString("thumbnail"));
                blog.setUpdated_date(rs.getDate("update_date"));
                blogs.add(blog);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blogs;
    }

    // get blog by categoryblogId
    public List<Blog> getBlogByCategoryBlogId(int categoryBlog_id) {
        List<Blog> list = new ArrayList<>();
        String sql = "SELECT * FROM Blog WHERE categoryBlog_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, categoryBlog_id);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Blog c = new Blog();
                c.setBlog_id(rs.getInt(1));
                c.setTitle(rs.getString(2));
                c.setUserID(rs.getInt(3));
                c.setUpdated_date(rs.getDate(4));
                c.setContent(rs.getString(5));
                c.setThumbnail(rs.getString(6));
                c.setBrief_infor(rs.getString(7));
                c.setCategoryBlog_id(rs.getInt(8));

                list.add(c);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    // getByBlogId
    public Blog getBlogByBlogId(int blog_id) {
        Blog blog = null;
        String sql = "SELECT * FROM Blog WHERE blog_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, blog_id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                blog = new Blog();
                blog.setBlog_id(rs.getInt(1));
                blog.setTitle(rs.getString(2));
                blog.setUserID(rs.getInt(3));
                blog.setUpdated_date(rs.getDate(4));
                blog.setContent(rs.getString(5));
                blog.setThumbnail(rs.getString(6));
                blog.setBrief_infor(rs.getString(7));
                blog.setCategoryBlog_id(rs.getInt(8));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return blog;
    }

    // getNewBlog
    public Blog getBlogNew() {
        Blog blog = null;
        String sql = "SELECT TOP 1 * FROM Blog WHERE status = 1 ORDER BY updated_date DESC";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                blog = new Blog();
                blog.setBlog_id(rs.getInt(1));
                blog.setTitle(rs.getString(2));
                blog.setUserID(rs.getInt(3));
                blog.setUpdated_date(rs.getDate(4));
                blog.setContent(rs.getString(5));
                blog.setThumbnail(rs.getString(6));
                blog.setBrief_infor(rs.getString(7));
                blog.setCategoryBlog_id(rs.getInt(8));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return blog;
    }

    public void addNewBlog(String title, int UserID, String content, String briefInfor, int categoryId, int status, String fullImagePath) {
        String sql = "INSERT INTO Blog (title, UserID, update_date, content, thumbnail, brief_infor, categoryBlog_id, status) VALUES (?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, title);
            st.setInt(2, UserID);

            // Lấy ngày hiện tại theo kiểu java.sql.Date
            java.util.Date today = Calendar.getInstance().getTime();
            java.sql.Date sqlDate = new java.sql.Date(today.getTime());
            st.setDate(3, sqlDate); // Gán giá trị update_date

            st.setString(4, content);
            st.setString(5, fullImagePath);
            st.setString(6, briefInfor);
            st.setInt(7, categoryId);
            st.setInt(8, status);

            st.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//    public void addNewBlog(String title, int user_id, String content, String brief_infor, int category_id, int status, String imageBase64) {
//        try {
//            String sql = "INSERT INTO [dbo].[Blog] "
//                    + "([title], [author_id], [content], [thumbnail], [brief_infor], [categoryBlog_id], [status]) "
//                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
//
//            PreparedStatement st = connection.prepareStatement(sql);
//            st.setString(1, title);
//            st.setInt(2, user_id);
//            st.setString(3, content);
//            st.setString(4, imageBase64); // Lưu Base64 vào cột thumbnail
//            st.setString(5, brief_infor);
//            st.setInt(6, category_id);
//            st.setInt(7, status);
//
//            st.executeUpdate();
//        } catch (SQLException ex) {
//            System.out.println("Lỗi khi thêm blog: " + ex.getMessage());
//        }
//    }

    public void UpdateBlogById(String title, String content, String brief_infor, int category_id, int status, String url_thumbnail, int blog_id) {
        try {
            String sql = "UPDATE [dbo].[Blog]\n"
                    + "   SET [title] = ?\n"
                    + "      ,[update_date] = getdate()\n" // Sửa lại tên cột từ updated_date thành update_date
                    + "      ,[content] = ?\n"
                    + "      ,[thumbnail] = ?\n"
                    + "      ,[brief_infor] = ?\n"
                    + "      ,[categoryBlog_id] = ?\n"
                    + "      ,[status] = ?\n"
                    + " WHERE blog_id = ?";
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, title);
            st.setString(2, content);
            st.setString(3, url_thumbnail);
            st.setString(4, brief_infor);
            st.setInt(5, category_id);
            st.setInt(6, status);
            st.setInt(7, blog_id);

            st.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public boolean updateBlog(int blogId, String title, String briefInfor, String content, int categoryId, boolean status, String thumbnail) {
        String sql = "UPDATE Blog SET title=?, brief_infor=?, content=?, categoryBlog_id=?, status=?, updated_date=GETDATE(), thumbnail=? WHERE blog_id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, title);
            st.setString(2, briefInfor);
            st.setString(3, content);
            st.setInt(4, categoryId);
            st.setBoolean(5, status);
            st.setString(6, thumbnail);
            st.setInt(7, blogId);

            int rowsUpdated = st.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBlogStatus(int blogId, boolean status) {
        // Cập nhật trạng thái blog trong cơ sở dữ liệu
        String sql = "UPDATE blog SET status = ? WHERE blog_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, blogId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getUrlImageByStatus() {
        List<String> thumbnails = new ArrayList<>();  // Danh sách lưu trữ các thumbnail
        String sql = "SELECT thumbnail FROM Blog WHERE status = 1";  // Lấy thumbnail cho tất cả bài viết có status = 1

        // Sử dụng try-with-resources để tự động đóng tài nguyên
        try (PreparedStatement st = connection.prepareStatement(sql); ResultSet rs = st.executeQuery()) {

            // Lặp qua kết quả và thêm các thumbnail vào danh sách
            while (rs.next()) {
                thumbnails.add(rs.getString("thumbnail"));  // Thêm URL thumbnail vào danh sách
            }

        } catch (SQLException e) {
            // Log lỗi chi tiết để giúp việc gỡ lỗi
            System.out.println("Error fetching image URLs for active posts: " + e.getMessage());
            e.printStackTrace();
        }

        // Trả về danh sách thumbnail, có thể rỗng nếu không tìm thấy
        return thumbnails;
    }

    public String getUserById(int UserID) {
        String sql = "SELECT * WHERE UserID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, UserID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {

                return rs.getString(2);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public List<Blog> searchAndPagingBlog(String blogName, int UserID, int categoryId, Boolean status, int sortBy, int index, int pageSize) {
        List<Blog> blogs = new ArrayList<>();
        String sql = "SELECT blog_id, title, UserID, update_date, content, thumbnail, brief_infor, categoryBlog_id, status FROM Blog WHERE 1=1 ";

        // Tìm kiếm theo tên blog nếu có
        if (blogName != null && !blogName.trim().isEmpty()) {
            sql += "AND title LIKE ? ";
        }

        if (UserID > 0) {
            sql += "AND UserID = ? ";
        }

        // Tìm kiếm theo categoryId nếu có
        if (categoryId > 0) {
            sql += "AND categoryBlog_id = ? ";
        }

        // Lọc theo trạng thái nếu có (nếu status không phải null)
        if (status != null) {
            sql += "AND status = ? ";
        }

        // Xử lý sắp xếp
        switch (sortBy) {
            case 1:
                sql += "ORDER BY title ASC ";
                break;
            case 2:
                sql += "ORDER BY title DESC ";
                break;
            case 3:
                sql += "ORDER BY blog_id ASC ";
                break;
            case 4:
                sql += "ORDER BY blog_id DESC ";
                break;
            default:
                sql += "ORDER BY blog_id ASC ";
        }

        // Thêm phân trang
        sql += "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Nếu có tìm kiếm theo tên blog
            if (blogName != null && !blogName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + blogName.trim() + "%");
            }

            if (UserID > 0) {
                ps.setInt(paramIndex++, UserID);
            }

            // Nếu có tìm kiếm theo categoryId
            if (categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }

            // Nếu có trạng thái thì set giá trị
            if (status != null) {
                ps.setBoolean(paramIndex++, status);
            }

            // Thêm tham số OFFSET và FETCH
            int offset = (index - 1) * pageSize;
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Blog blog = new Blog();
                    blog.setBlog_id(rs.getInt("blog_id"));
                    blog.setTitle(rs.getString("title"));
                    blog.setUserID(rs.getInt("UserID"));
                    blog.setUpdated_date(rs.getDate("update_date"));
                    blog.setContent(rs.getString("content"));
                    blog.setThumbnail(rs.getString("thumbnail"));
                    blog.setBrief_infor(rs.getString("brief_infor"));
                    blog.setCategoryBlog_id(rs.getInt("categoryBlog_id"));
                    blog.setStatus(rs.getBoolean("status"));
                    blogs.add(blog);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blogs;
    }

    public int getSizeBlog(String blogName, int UserID, int categoryId, Boolean status) {
        int count = 0;

        String sql = "SELECT COUNT(*) AS TotalCount FROM Blog WHERE 1=1 ";

        // Thêm điều kiện tìm kiếm theo tên blog nếu có
        if (blogName != null && !blogName.trim().isEmpty()) {
            sql += "AND title LIKE ? ";
        }

        if (UserID > 0) {
            sql += "AND UserID = ? ";
        }

        // Thêm điều kiện tìm kiếm theo categoryId nếu có
        if (categoryId > 0) {
            sql += "AND categoryBlog_id = ? ";
        }

        // Thêm điều kiện lọc theo trạng thái nếu có (tránh lỗi luôn áp dụng điều kiện)
        if (status != null) {
            sql += "AND status = ? ";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Nếu có tìm kiếm theo tên blog
            if (blogName != null && !blogName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + blogName.trim() + "%");
            }

            if (UserID > 0) {
                ps.setInt(paramIndex++, UserID);
            }

            // Nếu có tìm kiếm theo categoryId
            if (categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }

            // Nếu có trạng thái thì set giá trị
            if (status != null) {
                ps.setBoolean(paramIndex++, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public boolean deleteBlog(int blogId) {
        String sql = "DELETE FROM Blog WHERE blog_id = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, blogId);
            int rowsDeleted = st.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Blog> getBlogsByCategory(int categoryId, int excludeBlogId) {
        List<Blog> blogs = new ArrayList<>();
        String sql = "SELECT b.blog_id, b.title, b.content, b.thumbnail, b.brief_infor, b.categoryBlog_id "
                + "FROM Blog b "
                + "JOIN CategoryBlog c ON b.categoryBlog_id = c.categoryBlog_id "
                + "WHERE b.categoryBlog_id = ? "
                + "AND b.blog_id != ? "
                + "AND b.status = 1 " // Trạng thái của bài viết phải là 1
                + "AND c.status = 1";  // Trạng thái của danh mục phải là 1

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, categoryId);       // Set categoryId
            ps.setInt(2, excludeBlogId);    // Loại trừ bài viết hiện tại

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Blog blog = new Blog();
                    blog.setBlog_id(rs.getInt("blog_id"));
                    blog.setTitle(rs.getString("title"));
                    blog.setContent(rs.getString("content"));
                    blog.setThumbnail(rs.getString("thumbnail"));
                    blog.setBrief_infor(rs.getString("brief_infor"));
                    blog.setCategoryBlog_id(rs.getInt("categoryBlog_id"));
                    blogs.add(blog);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blogs;
    }

    public List<Blog> searchAndPagingBlog(String blogName, int categoryId, int index, int pageSize) {
        List<Blog> blogs = new ArrayList<>();
        String sql = "SELECT b.blog_id, b.title, b.UserID, b.update_date, b.content, b.thumbnail, b.brief_infor, b.categoryBlog_id, b.status "
                + "FROM Blog b "
                + "JOIN CategoryBlog c ON b.categoryBlog_id = c.categoryBlog_id "
                + "WHERE 1=1 "
                + "AND b.status = 1 " // Trạng thái của bài viết phải là 1
                + "AND c.status = 1 ";  // Trạng thái của danh mục phải là 1

        // Tìm kiếm theo tên blog nếu có
        if (blogName != null && !blogName.trim().isEmpty()) {
            sql += "AND b.title LIKE ? ";
        }

        // Tìm kiếm theo categoryId nếu có
        if (categoryId > 0) {
            sql += "AND b.categoryBlog_id = ? ";
        }

        // Thêm sắp xếp mặc định (theo blog_id) và phân trang
        sql += "ORDER BY b.blog_id ASC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Nếu có tìm kiếm theo tên blog
            if (blogName != null && !blogName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + blogName.trim() + "%");
            }

            // Nếu có tìm kiếm theo categoryId
            if (categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }

            // Tính toán offset và set tham số OFFSET, FETCH
            int offset = (index - 1) * pageSize;
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Blog blog = new Blog();
                    blog.setBlog_id(rs.getInt("blog_id"));
                    blog.setTitle(rs.getString("title"));
                    blog.setUserID(rs.getInt("UserID"));
                    blog.setUpdated_date(rs.getDate("update_date"));
                    blog.setContent(rs.getString("content"));
                    blog.setThumbnail(rs.getString("thumbnail"));
                    blog.setBrief_infor(rs.getString("brief_infor"));
                    blog.setCategoryBlog_id(rs.getInt("categoryBlog_id"));
                    blog.setStatus(rs.getBoolean("status"));
                    blogs.add(blog);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return blogs;
    }

    public int getSizeBlog(String blogName, int categoryId) {
        int count = 0;

        // Cập nhật SQL để chỉ lấy các bài viết và danh mục có trạng thái = 1
        String sql = "SELECT COUNT(*) AS TotalCount FROM Blog b "
                + "JOIN CategoryBlog c ON b.categoryBlog_id = c.categoryBlog_id "
                + "WHERE 1=1 AND b.status = 1 AND c.status = 1 ";

        // Thêm điều kiện tìm kiếm theo tên blog nếu có
        if (blogName != null && !blogName.trim().isEmpty()) {
            sql += "AND b.title LIKE ? ";
        }

        // Thêm điều kiện tìm kiếm theo categoryId nếu có
        if (categoryId > 0) {
            sql += "AND b.categoryBlog_id = ? ";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Nếu có tìm kiếm theo tên blog
            if (blogName != null && !blogName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + blogName.trim() + "%");
            }

            // Nếu có tìm kiếm theo categoryId
            if (categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("TotalCount");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public List<User> getAllUser() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT *  WHERE Role = 2";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setUsername("UserName");
                list.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;

    }

    public boolean isTitleExists(String title) {
        String sql = "SELECT COUNT(*) FROM Blog WHERE title = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, title);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu có ít nhất một bài viết trùng tiêu đề
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false; // Trả về false nếu không có tiêu đề trùng hoặc có lỗi xảy ra
    }

    public boolean isTitleExistsExceptCurrent(String title, int blogId) {
        String sql = "SELECT COUNT(*) FROM Blog WHERE title = ? AND BlogID != ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, title);
            st.setInt(2, blogId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Trả về true nếu có tiêu đề trùng với bài khác
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra console để debug
        }
        return false; // Mặc định trả về false nếu không có tiêu đề trùng hoặc lỗi xảy ra
    }

    @Override
    public void insert(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
