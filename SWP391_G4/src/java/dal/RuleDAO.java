package dal;

import model.Rule;
import model.CategoryRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class RuleDAO extends DBContext {

    public List<CategoryRule> getAllCategories() {
        String query = "SELECT categoryRuleID, categoryRuleName, content, img, update_date, status FROM CategoryRule";
        List<CategoryRule> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new CategoryRule(
                        rs.getInt("categoryRuleID"),
                        rs.getString("categoryRuleName"),
                        rs.getString("content"),
                        rs.getString("img"),
                        rs.getDate("update_date"),
                        rs.getBoolean("status")
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RuleDAO.class.getName()).log(Level.SEVERE, "Error fetching categories", ex);
        }
        return list;
    }

    public boolean addCategory(String categoryRuleName, String content, String img, boolean status) throws SQLException {
        String query = "INSERT INTO CategoryRule (categoryRuleName, content, img, Update_Date, status) VALUES (?, ?, ?, GETDATE(), ?)";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, categoryRuleName);
            ps.setString(2, content);
            ps.setString(3, img);
            ps.setBoolean(4, status);

            ps.executeUpdate();
        }
        return true;
    }

    public boolean updateCategory(int id, String name, String content, String img, boolean status) {
        String query = "UPDATE CategoryRule SET categoryRuleName = ?, content = ?, img = ?, status = ? WHERE categoryRuleID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, content);
            ps.setString(3, img);
            ps.setBoolean(4, status);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCategoryRuleStatus(int categoryRuleID, boolean status) {
        String sql = "UPDATE CategoryRule SET status = ? WHERE CategoryRuleID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, categoryRuleID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCategory(int id) {
        String query = "DELETE FROM CategoryRule WHERE categoryRuleID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<CategoryRule> searchAndPagingCategoryRule(String categoryRuleName, Boolean status, int sortBy, int index, int pageSize) {
        List<CategoryRule> rules = new ArrayList<>();
        String sql = "SELECT CategoryRuleID, CategoryRuleName, Content, Img, Update_Date, Status FROM CategoryRule WHERE 1=1 ";

        // Tìm kiếm theo tên CategoryRule nếu có
        if (categoryRuleName != null && !categoryRuleName.trim().isEmpty()) {
            sql += "AND CategoryRuleName LIKE ? ";
        }

        // Lọc theo trạng thái nếu có
        if (status != null) {
            sql += "AND Status = ? ";
        }

        // Xử lý sắp xếp
        switch (sortBy) {
            case 1:
                sql += "ORDER BY CategoryRuleName ASC ";
                break;
            case 2:
                sql += "ORDER BY CategoryRuleName DESC ";
                break;
            case 3:
                sql += "ORDER BY CategoryRuleID ASC ";
                break;
            case 4:
                sql += "ORDER BY CategoryRuleID DESC ";
                break;
            default:
                sql += "ORDER BY CategoryRuleID ASC ";
        }

        // Thêm phân trang
        sql += "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Nếu có tìm kiếm theo tên CategoryRule
            if (categoryRuleName != null && !categoryRuleName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + categoryRuleName.trim() + "%");
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
                    CategoryRule rule = new CategoryRule();
                    rule.setCategoryRuleID(rs.getInt("CategoryRuleID"));
                    rule.setCategoryRuleName(rs.getString("CategoryRuleName"));
                    rule.setContent(rs.getString("Content"));
                    rule.setImg(rs.getString("Img"));
                    rule.setUpdate_date(rs.getDate("Update_Date"));
                    rule.setStatus(rs.getBoolean("Status"));
                    rules.add(rule);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rules;
    }

    public int getSizeCategoryRule(String categoryRuleName, Boolean status) {
        int count = 0;

        String sql = "SELECT COUNT(*) AS TotalCount FROM CategoryRule WHERE 1=1 ";

        // Thêm điều kiện tìm kiếm theo tên CategoryRule nếu có
        if (categoryRuleName != null && !categoryRuleName.trim().isEmpty()) {
            sql += "AND CategoryRuleName LIKE ? ";
        }

        // Thêm điều kiện lọc theo trạng thái nếu có
        if (status != null) {
            sql += "AND Status = ? ";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            // Nếu có tìm kiếm theo tên CategoryRule
            if (categoryRuleName != null && !categoryRuleName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + categoryRuleName.trim() + "%");
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

    public List<Rule> getAllRules() {
        String query = "SELECT ruleID, title, userID, content, img, update_date, status, categoryRuleID FROM [Rule]";
        List<Rule> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Rule(
                        rs.getInt("ruleID"),
                        rs.getString("title"),
                        rs.getInt("userID"),
                        rs.getDate("update_date"),
                        rs.getString("content"),
                        rs.getString("img"),
                        rs.getBoolean("status"),
                        rs.getInt("categoryRuleID")
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RuleDAO.class.getName()).log(Level.SEVERE, "Error fetching rules", ex);
        }
        return list;
    }

    public boolean addRule(String title, int userID, String content, String img, boolean status, int categoryRuleID) throws SQLException {
        String query = "INSERT INTO [Rule] (title, userID, content, img, status, categoryRuleID, update_date) VALUES (?, ?, ?, ?, ?, ?, GETDATE())";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, title);
            ps.setInt(2, userID);
            ps.setString(3, content);
            ps.setString(4, img);
            ps.setBoolean(5, status);
            ps.setInt(6, categoryRuleID);
            ps.executeUpdate();
        }
        return true;
    }

    public boolean updateRule(int ruleID, String title, int userID, String content, boolean status, int categoryRuleID) {
        String sql = "UPDATE [Rule] SET title=?, userID=?, content=?, status=?, categoryRuleID=? WHERE ruleID=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setInt(2, userID);
            ps.setString(3, content);
            ps.setBoolean(4, status);
            ps.setInt(5, categoryRuleID);
            ps.setInt(6, ruleID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteRule(int id) {
        String query = "DELETE FROM [Rule] WHERE ruleID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Rule> searchAndPagingRule(String ruleName, int userID, int categoryId, Boolean status, int sortBy, int index, int pageSize) {
        List<Rule> rules = new ArrayList<>();
        String sql = "SELECT ruleID, title, userID, update_date, content, categoryRuleID, status FROM [Rule] WHERE 1=1 ";

        if (ruleName != null && !ruleName.trim().isEmpty()) {
            sql += "AND title LIKE ? ";
        }
        if (userID > 0) {
            sql += "AND userID = ? ";
        }
        if (categoryId > 0) {
            sql += "AND categoryRuleID = ? ";
        }
        if (status != null) {
            sql += "AND status = ? ";
        }

        switch (sortBy) {
            case 1:
                sql += "ORDER BY title ASC ";
                break;
            case 2:
                sql += "ORDER BY title DESC ";
                break;
            case 3:
                sql += "ORDER BY ruleID ASC ";
                break;
            case 4:
                sql += "ORDER BY ruleID DESC ";
                break;
            default:
                sql += "ORDER BY ruleID ASC ";
        }

        sql += "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            if (ruleName != null && !ruleName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + ruleName.trim() + "%");
            }
            if (userID > 0) {
                ps.setInt(paramIndex++, userID);
            }
            if (categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (status != null) {
                ps.setBoolean(paramIndex++, status);
            }

            int offset = (index - 1) * pageSize;
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Rule rule = new Rule();
                    rule.setRuleID(rs.getInt("ruleID"));
                    rule.setTitle(rs.getString("title"));
                    rule.setUserID(rs.getInt("userID"));
                    rule.setUpdate_date(rs.getDate("update_date"));
                    rule.setContent(rs.getString("content"));
                    rule.setCategoryRuleID(rs.getInt("categoryRuleID"));
                    rule.setStatus(rs.getBoolean("status"));
                    rules.add(rule);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rules;
    }

    public int getSizeRule(String ruleName, int UserID, int categoryRuleID, Boolean status) {
        int count = 0;

        String sql = "SELECT COUNT(*) AS TotalCount FROM [Rule] WHERE 1=1 ";
        if (ruleName != null && !ruleName.trim().isEmpty()) {
            sql += "AND title LIKE ? ";
        }

        if (UserID > 0) {
            sql += "AND UserID = ? ";
        }

        // Thêm điều kiện tìm kiếm theo categoryId nếu có
        if (categoryRuleID > 0) {
            sql += "AND categoryRuleID = ? ";
        }

        // Thêm điều kiện lọc theo trạng thái nếu có (tránh lỗi luôn áp dụng điều kiện)
        if (status != null) {
            sql += "AND status = ? ";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int paramIndex = 1;

            if (ruleName != null && !ruleName.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + ruleName.trim() + "%");
            }

            if (UserID > 0) {
                ps.setInt(paramIndex++, UserID);
            }

            // Nếu có tìm kiếm theo categoryId
            if (categoryRuleID > 0) {
                ps.setInt(paramIndex++, categoryRuleID);
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

    public boolean updateRuleStatus(int ruleID, boolean status) {
        // Cập nhật trạng thái blog trong cơ sở dữ liệu
        String sql = "UPDATE [Rule] SET status = ? WHERE ruleID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setInt(2, ruleID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Rule> getRulesByCategory(int categoryRuleId) {
        List<Rule> list = new ArrayList<>();
        String sql = "SELECT * FROM [Rule] WHERE CategoryRuleID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, categoryRuleId);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Rule rule = new Rule();
                rule.setRuleID(rs.getInt(1));  // ruleID
                rule.setTitle(rs.getString(2)); // title
                rule.setContent(rs.getString(3)); // content
                rule.setImg(rs.getString(4)); // img
                rule.setUpdate_date(rs.getDate(5)); // update_date
                rule.setStatus(rs.getBoolean(6)); // status
                rule.setUserID(rs.getInt(7)); // userID
                rule.setCategoryRuleID(rs.getInt(8)); // categoryRuleID

                list.add(rule);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return list;
    }

    // Lấy một quy định theo RuleID
    public Rule getRuleByID(int ruleID) {
        Rule rule = null;
        String sql = "SELECT * FROM [Rule] WHERE RuleID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, ruleID);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                rule = new Rule();
                rule.setRuleID(rs.getInt(1));
                rule.setTitle(rs.getString(2));
                rule.setContent(rs.getString(3));
                rule.setImg(rs.getString(4));
                rule.setUpdate_date(rs.getDate(5));
                rule.setStatus(rs.getBoolean(6));
                rule.setUserID(rs.getInt(7));
                rule.setCategoryRuleID(rs.getInt(8));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return rule;
    }

    public CategoryRule getCategoryRuleByID(int categoryRuleID) {
        CategoryRule categoryRule = null;
        String sql = "SELECT * FROM CategoryRule WHERE CategoryRuleID = ?";

        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, categoryRuleID);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                categoryRule = new CategoryRule();
                categoryRule.setCategoryRuleID(rs.getInt("CategoryRuleID"));
                categoryRule.setCategoryRuleName(rs.getString("CategoryRuleName"));
                categoryRule.setContent(rs.getString("Content"));
                categoryRule.setImg(rs.getString("Img"));
                categoryRule.setUpdate_date(rs.getDate("Update_date"));
                categoryRule.setStatus(rs.getBoolean("Status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // In ra thông báo lỗi chi tiết
        }

        return categoryRule;
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
        String query = "SELECT COUNT(*) FROM [Rule] WHERE title = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, title);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Nếu số lượng > 0 thì title đã tồn tại
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kiểm tra title: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
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
