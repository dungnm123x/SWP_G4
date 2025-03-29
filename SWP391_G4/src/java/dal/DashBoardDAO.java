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

    // Phương thức lấy dữ liệu doanh thu theo ngày trong một tháng
    public Map<String, double[]> getRevenueByMonth(int year, int month) throws SQLException {
        Map<String, double[]> revenueData = new HashMap<>();
        // Khởi tạo dữ liệu cho tất cả các ngày trong tháng (giả sử tối đa 31 ngày)
        for (int day = 1; day <= 31; day++) {
            revenueData.put(String.valueOf(day), new double[]{0.0, 0.0, 0.0}); // [A, B, A-B]
        }

        // Truy vấn A: Tổng TotalPrice từ Booking với PaymentStatus = 'Paid'
        String sqlBooking = "SELECT DAY(BookingDate) AS Day, SUM(TotalPrice) AS Total "
                + "FROM Booking "
                + "WHERE PaymentStatus = 'Paid' "
                + "AND YEAR(BookingDate) = ? AND MONTH(BookingDate) = ? "
                + "GROUP BY DAY(BookingDate)";

        try (PreparedStatement stm = getConnection().prepareStatement(sqlBooking)) {
            stm.setInt(1, year);
            stm.setInt(2, month);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("Day");
                    double totalBooking = rs.getDouble("Total");
                    double[] data = revenueData.get(String.valueOf(day));
                    data[0] = totalBooking; // A
                    revenueData.put(String.valueOf(day), data);
                }
            }
        }

        // Truy vấn B: Tổng TotalRefund từ Refund với RefundStatus = 'Complete'
        String sqlRefund = "SELECT DAY(ConfirmRefundDate) AS Day, SUM(TotalRefund) AS Total "
                + "FROM Refund "
                + "WHERE RefundStatus = 'Complete' "
                + "AND YEAR(ConfirmRefundDate) = ? AND MONTH(ConfirmRefundDate) = ? "
                + "GROUP BY DAY(ConfirmRefundDate)";

        try (PreparedStatement stm = getConnection().prepareStatement(sqlRefund)) {
            stm.setInt(1, year);
            stm.setInt(2, month);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int day = rs.getInt("Day");
                    double totalRefund = rs.getDouble("Total");
                    double[] data = revenueData.get(String.valueOf(day));
                    data[1] = totalRefund; // B
                    revenueData.put(String.valueOf(day), data);
                }
            }
        }

        // Tính A - B cho từng ngày
        for (int day = 1; day <= 31; day++) {
            double[] data = revenueData.get(String.valueOf(day));
            data[2] = data[0] - data[1]; // A - B
            revenueData.put(String.valueOf(day), data);
        }

        return revenueData;
    }

    // Phương thức lấy dữ liệu doanh thu theo tháng trong một năm
    public Map<String, double[]> getRevenueByYear(int year) throws SQLException {
        Map<String, double[]> revenueData = new HashMap<>();
        // Khởi tạo dữ liệu cho tất cả các tháng trong năm
        for (int month = 1; month <= 12; month++) {
            revenueData.put(String.valueOf(month), new double[]{0.0, 0.0, 0.0}); // [A, B, A-B]
        }

        // Truy vấn A: Tổng TotalPrice từ Booking với PaymentStatus = 'Paid'
        String sqlBooking = "SELECT MONTH(BookingDate) AS Month, SUM(TotalPrice) AS Total "
                + "FROM Booking "
                + "WHERE PaymentStatus = 'Paid' "
                + "AND YEAR(BookingDate) = ? "
                + "GROUP BY MONTH(BookingDate)";

        try (PreparedStatement stm = getConnection().prepareStatement(sqlBooking)) {
            stm.setInt(1, year);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("Month");
                    double totalBooking = rs.getDouble("Total");
                    double[] data = revenueData.get(String.valueOf(month));
                    data[0] = totalBooking; // A
                    revenueData.put(String.valueOf(month), data);
                }
            }
        }

        // Truy vấn B: Tổng TotalRefund từ Refund với RefundStatus = 'Complete'
        String sqlRefund = "SELECT MONTH(ConfirmRefundDate) AS Month, SUM(TotalRefund) AS Total "
                + "FROM Refund "
                + "WHERE RefundStatus = 'Complete' "
                + "AND YEAR(ConfirmRefundDate) = ? "
                + "GROUP BY MONTH(ConfirmRefundDate)";

        try (PreparedStatement stm = getConnection().prepareStatement(sqlRefund)) {
            stm.setInt(1, year);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int month = rs.getInt("Month");
                    double totalRefund = rs.getDouble("Total");
                    double[] data = revenueData.get(String.valueOf(month));
                    data[1] = totalRefund; // B
                    revenueData.put(String.valueOf(month), data);
                }
            }
        }

        // Tính A - B cho từng tháng
        for (int month = 1; month <= 12; month++) {
            double[] data = revenueData.get(String.valueOf(month));
            data[2] = data[0] - data[1]; // A - B
            revenueData.put(String.valueOf(month), data);
        }

        return revenueData;
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
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public Map<String, Integer> getTripStatistics() throws SQLException {
        Map<String, Integer> tripStats = new HashMap<>();
        String sql = "SELECT TripStatus, COUNT(*) AS Count FROM Trip GROUP BY TripStatus";
        try (PreparedStatement stm = getConnection().prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                tripStats.put(rs.getString("TripStatus"), rs.getInt("Count"));
            }
        }
        return tripStats;
    }

    public int getTotalRoutesCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM Route";
        try (PreparedStatement stmt = connection.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getTotalRefundComplete() throws SQLException {
        String sql = "SELECT SUM(TotalRefund) AS Total FROM Refund WHERE RefundStatus = 'Complete'";
        try (PreparedStatement statement = getConnection().prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("Total");
            }
        }
        return 0.0;
    }

    public double getTotalRefundWait() throws SQLException {
        String sql = "SELECT SUM(TotalRefund) AS Total FROM Refund WHERE RefundStatus = 'Wait'";
        try (PreparedStatement statement = getConnection().prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getDouble("Total");
            }
        }
        return 0.0;
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
