package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DashBoardDAO extends DBContext<Object> {

    private int getCount(String sql) throws SQLException {
        try (PreparedStatement stm = getConnection().prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalUsers() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [User]");
    }

    public int getTotalEmployees() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [User] WHERE RoleID = 2");
    }

    public int getTotalCustomers() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [User] WHERE RoleID = 3");
    }

    public int getTotalTrains() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Train");
    }

    public int getTotalBookings() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Booking");
    }

    public int getTotalTrips() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Trip");
    }

    public int getTotalBlogs() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Blog");
    }

    public int getTotalRules() throws SQLException {
        return getCount("SELECT COUNT(*) FROM [Rule]");
    }

    @Override
    public void insert(Object model) {
        throw new UnsupportedOperationException("Insert operation is not supported.");
    }

    @Override
    public void update(Object model) {
        throw new UnsupportedOperationException("Update operation is not supported.");
    }

    @Override
    public void delete(Object model) {
        throw new UnsupportedOperationException("Delete operation is not supported.");
    }

    @Override
    public ArrayList<Object> list() {
        throw new UnsupportedOperationException("List operation is not supported.");
    }

    @Override
    public Object get(int id) {
        throw new UnsupportedOperationException("Get operation is not supported.");
    }
}
