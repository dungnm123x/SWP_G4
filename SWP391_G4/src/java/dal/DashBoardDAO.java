package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Feedback;
import model.StationWithCoordinates;
import model.User;

public class DashBoardDAO extends DBContext<Object> {

    public int getTotalTicketsSold() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Tickets";
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(total_amount) FROM Orders"; // Thay total_amount bằng tên cột doanh thu
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        }
        return 0.0;
    }

    public int getTotalOrders() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Orders";
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    public int getTotalEvents() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Events";
        try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    public List<RevenueData> getRevenueData(String period, String ticketStatus, String selectedDate) throws SQLException {
        List<RevenueData> revenueDataList = new ArrayList<>();
        String sql = "";
        PreparedStatement stm = null;

        try {
            switch (period.toLowerCase()) {
                case "weekly":
                    // Lấy doanh thu 7 ngày gần nhất từ ngày được chọn
                    sql = "SELECT CAST(t.DepartureTime AS DATE) AS TimePeriod, "
                            + "SUM(ti.TicketPrice) AS TotalRevenue "
                            + "FROM Ticket ti "
                            + "JOIN Trip t ON ti.TripID = t.TripID "
                            + "WHERE ti.TicketStatus = ? "
                            + "AND t.DepartureTime >= DATEADD(DAY, -6, CAST(? AS DATE)) "
                            + "AND t.DepartureTime <= CAST(? AS DATE) "
                            + "GROUP BY CAST(t.DepartureTime AS DATE) "
                            + "ORDER BY CAST(t.DepartureTime AS DATE)";
                    stm = getConnection().prepareStatement(sql);
                    stm.setString(1, ticketStatus);
                    stm.setString(2, selectedDate);
                    stm.setString(3, selectedDate);
                    break;

                case "monthly":
                    // Lấy doanh thu từng ngày trong tháng được chọn
                    sql = "SELECT DAY(t.DepartureTime) AS TimePeriod, "
                            + "SUM(ti.TicketPrice) AS TotalRevenue "
                            + "FROM Ticket ti "
                            + "JOIN Trip t ON ti.TripID = t.TripID "
                            + "WHERE ti.TicketStatus = ? "
                            + "AND YEAR(t.DepartureTime) = YEAR(CAST(? AS DATE)) "
                            + "AND MONTH(t.DepartureTime) = MONTH(CAST(? AS DATE)) "
                            + "GROUP BY DAY(t.DepartureTime) "
                            + "ORDER BY DAY(t.DepartureTime)";
                    stm = getConnection().prepareStatement(sql);
                    stm.setString(1, ticketStatus);
                    stm.setString(2, selectedDate);
                    stm.setString(3, selectedDate);
                    break;

                case "yearly":
                    // Lấy doanh thu 12 tháng trong năm được chọn
                    sql = "SELECT MONTH(t.DepartureTime) AS TimePeriod, "
                            + "SUM(ti.TicketPrice) AS TotalRevenue "
                            + "FROM Ticket ti "
                            + "JOIN Trip t ON ti.TripID = t.TripID "
                            + "WHERE ti.TicketStatus = ? "
                            + "AND YEAR(t.DepartureTime) = ? "
                            + "GROUP BY MONTH(t.DepartureTime) "
                            + "ORDER BY MONTH(t.DepartureTime)";
                    stm = getConnection().prepareStatement(sql);
                    stm.setString(1, ticketStatus);
                    stm.setString(2, selectedDate);
                    break;

                default:
                    throw new IllegalArgumentException("Invalid period: " + period);
            }

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    RevenueData data = new RevenueData();
                    data.setTimePeriod(rs.getInt("TimePeriod"));
                    data.setTotalRevenue(rs.getDouble("TotalRevenue"));
                    revenueDataList.add(data);
                }
            }
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
        return revenueDataList;
    }

    // Helper class to store revenue data
    public static class RevenueData {

        private int timePeriod;
        private double totalRevenue;

        public int getTimePeriod() {
            return timePeriod;
        }

        public void setTimePeriod(int timePeriod) {
            this.timePeriod = timePeriod;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }
    }

    private int getCount(String sql) throws SQLException {
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Feedback> getLatestFeedbacks() throws SQLException {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT TOP 3 f.*, u.Email FROM Feedback f JOIN [User] u ON f.UserID = u.UserID ORDER BY f.FeedbackDate DESC"; // Get top 5 latest feedback with user email
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                Feedback feedback = new Feedback();
                User user = new User();
                user.setUserId(rs.getInt("UserID"));
                user.setEmail(rs.getString("Email")); // Lấy email từ ResultSet

                feedback.setFeedbackId(rs.getInt("FeedbackID"));
                feedback.setUser(user); // Set User object
                feedback.setContent(rs.getString("Content"));
                feedback.setRating(rs.getInt("Rating"));
                feedback.setFeedbackDate(rs.getTimestamp("FeedbackDate"));
                feedback.setStatus(rs.getBoolean("Status"));
                feedbacks.add(feedback);
            }
        }
        return feedbacks;
    }

    public int[] getStarDistribution() throws SQLException {
        int[] distribution = new int[5]; // Mảng để đếm số lượng sao từ 1 đến 5
        String sql = "SELECT Rating, COUNT(*) AS Count FROM Feedback GROUP BY Rating";
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                int rating = rs.getInt("Rating");
                int count = rs.getInt("Count");
                distribution[rating - 1] = count; // điều chỉnh chỉ số mảng cho phù hợp với rating từ 1 đến 5
            }
        }
        return distribution;
    }

    public List<StationWithCoordinates> getStationsWithCoordinates() throws SQLException {
        List<StationWithCoordinates> stations = new ArrayList<>();
        String sql = "SELECT s.StationID, s.StationName, s.Address, sc.Latitude, sc.Longitude "
                + "FROM Station s "
                + "JOIN StationCoordinates sc ON s.StationID = sc.StationID";

        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                StationWithCoordinates station = new StationWithCoordinates();
                station.setStationId(rs.getInt("StationID"));
                station.setStationName(rs.getString("StationName"));
                station.setAddress(rs.getString("Address"));
                station.setLatitude(rs.getDouble("Latitude"));
                station.setLongitude(rs.getDouble("Longitude"));
                stations.add(station);
            }
        }
        return stations;
    }
    public int getTotalTickets() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Ticket";
        try (PreparedStatement stm = getConnection().prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Map<String, Integer> getTripStatistics() throws SQLException {
        Map<String, Integer> tripStats = new HashMap<>();
        String sql = "SELECT TripStatus, COUNT(*) AS Count FROM Trip GROUP BY TripStatus";
        try (PreparedStatement stm = getConnection().prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                tripStats.put(rs.getString("TripStatus"), rs.getInt("Count"));
            }
        }
        return tripStats;
    }
    public int getTotalRoutesCount() throws SQLException {
    String query = "SELECT COUNT(*) FROM Route";
    try (PreparedStatement stmt = connection.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
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

    public int getTotalStations() throws SQLException {
        return getCount("SELECT COUNT(*) FROM Station");
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
