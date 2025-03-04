package dal;

import model.Rule;
import model.CategoryRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public boolean addCategory(String name, String content, String img, boolean status) throws SQLException {
        String query = "INSERT INTO CategoryRule (categoryRuleName, content, img, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, content);
            ps.setString(3, img);
            ps.setBoolean(4, status);
            ps.executeUpdate();
        }
        return true;
    }

    public boolean updateCategory(int id, String name, String content, String img, boolean status) throws SQLException {
        String query = "UPDATE CategoryRule SET categoryRuleName = ?, content = ?, img = ?, status = ? WHERE categoryRuleID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, content);
            ps.setString(3, img);
            ps.setBoolean(4, status);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
        return true;
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

    public boolean updateRule(int id, String title, int userID, String content, String img, boolean status, int categoryRuleID) throws SQLException {
        String query = "UPDATE [Rule] SET title = ?, userID = ?, content = ?, img = ?, status = ?, categoryRuleID = ?, update_date = GETDATE() WHERE ruleID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, title);
            ps.setInt(2, userID);
            ps.setString(3, content);
            ps.setString(4, img);
            ps.setBoolean(5, status);
            ps.setInt(6, categoryRuleID);
            ps.setInt(7, id);
            ps.executeUpdate();
        }
        return true;
    }
public List<Rule> searchAndPagingRule(String ruleName, int categoryId, int index, int pageSize) {
    List<Rule> rules = new ArrayList<>();
    String sql = "SELECT r.ruleID, r.title, r.userID, r.update_date, r.content, r.img, r.status, r.categoryRuleID "
            + "FROM [Rule] r "
            + "JOIN CategoryRule c ON r.categoryRuleID = c.categoryRuleID "
            + "WHERE 1=1 "
            + "AND r.status = 1 "
            + "AND c.status = 1 ";  // Lọc status

    if (ruleName != null && !ruleName.trim().isEmpty()) {
        sql += "AND r.title LIKE ? ";
    }

    if (categoryId > 0) {
        sql += "AND r.categoryRuleID = ? ";
    }

    sql += "ORDER BY r.ruleID ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";  // Phân trang

    System.out.println("SQL Query: " + sql);
    System.out.println("Params: ruleName = " + ruleName + ", categoryId = " + categoryId + ", index = " + index + ", pageSize = " + pageSize);

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        int paramIndex = 1;

        if (ruleName != null && !ruleName.trim().isEmpty()) {
            ps.setString(paramIndex++, "%" + ruleName.trim() + "%");
        }

        if (categoryId > 0) {
            ps.setInt(paramIndex++, categoryId);
        }

        int offset = (index - 1) * pageSize;
        ps.setInt(paramIndex++, offset);
        ps.setInt(paramIndex++, pageSize);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Rule rule = new Rule();
            rule.setRuleID(rs.getInt("ruleID"));
            rule.setTitle(rs.getString("title"));
            rule.setUserID(rs.getInt("userID"));
            rule.setUpdate_date(rs.getDate("update_date"));
            rule.setContent(rs.getString("content"));
            rule.setImg(rs.getString("img"));
            rule.setStatus(rs.getBoolean("status"));
            rule.setCategoryRuleID(rs.getInt("categoryRuleID"));

            System.out.println("Rule Found: " + rule.getRuleID() + " - " + rule.getTitle());

            rules.add(rule);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    System.out.println("Total Rules Found: " + rules.size());
    return rules;
}


public int getSizeRule(String ruleName, int categoryId) {
    int count = 0;

    String sql = "SELECT COUNT(*) AS TotalCount FROM [Rule] r "
            + "JOIN CategoryRule c ON r.categoryRuleID = c.categoryRuleID "
            + "WHERE 1=1 AND r.status = 1 AND c.status = 1 ";

    if (ruleName != null && !ruleName.trim().isEmpty()) {
        sql += "AND r.title LIKE ? ";
    }

    if (categoryId > 0) {
        sql += "AND r.categoryRuleID = ? ";
    }

    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        int paramIndex = 1;

        if (ruleName != null && !ruleName.trim().isEmpty()) {
            ps.setString(paramIndex++, "%" + ruleName.trim() + "%");
        }

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
    public Rule getRuleById(int ruleId) {
        Rule rule = null;
        String sql = "SELECT * FROM [Rule] WHERE RuleID = ?";
        try {
            PreparedStatement st = connection.prepareStatement(sql);
            st.setInt(1, ruleId);
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
