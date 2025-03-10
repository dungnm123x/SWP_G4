package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dto.RailwayDTO;
import dto.TrainDTO;
import java.util.ArrayList;
import java.util.List;
import model.Train;

public class TrainDAO extends DBContext<RailwayDTO> {

    public Train getTrainById(int trainID) {
        Train train = null;
        String sql = "SELECT TrainID, TrainName FROM Train WHERE TrainID = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);

            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    train = new Train(
                            rs.getInt("TrainID"), 
                            rs.getString("TrainName") 
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return train;
    }

    public String getTrainNameById(int trainID) {
        String trainName = null;
        String sql = "SELECT TrainName FROM Train WHERE TrainID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    trainName = rs.getString("TrainName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainName;
    }

    public List<TrainDTO> getAllTrains() {
        List<TrainDTO> trains = new ArrayList<>();
        String sql = "SELECT t.TrainID, t.TrainName,"
                + "COUNT(c.CarriageID) AS TotalCarriages, COALESCE(SUM(c.Capacity), 0) AS TotalSeats "
                + "FROM Train t "
                + "LEFT JOIN Carriage c ON t.TrainID = c.TrainID "
                + "GROUP BY t.TrainID, t.TrainName";
        try (
                PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TrainDTO train = new TrainDTO();
                train.setTrainID(rs.getInt("TrainID"));
                train.setTrainName(rs.getString("TrainName"));
                train.setTotalCarriages(rs.getInt("TotalCarriages"));
                train.setTotalSeats(rs.getInt("TotalSeats"));
                trains.add(train);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trains;
    }

    // Thêm mới tàu
    public Train addTrain(Train train) {
    String sql = "INSERT INTO Train (TrainName) VALUES (?)";
    try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, train.getTrainName());
        int affectedRows = ps.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating train failed, no rows affected.");
        }

        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                train.setTrainID(generatedKeys.getInt(1)); // Lấy TrainID và set cho đối tượng Train
                return train; // Trả về đối tượng Train đã có ID
            } else {
                throw new SQLException("Creating train failed, no ID obtained.");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return null; // Xử lý lỗi, có thể throw exception hoặc trả về null
    }
}

    // Sửa thông tin tàu
    public boolean updateTrain(Train train) {
        String sql = "UPDATE Train SET TrainName = ? WHERE TrainID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, train.getTrainName());
            ps.setInt(2, train.getTrainID()); // Sửa index từ 3 thành 2
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa tàu (phải xóa tất cả toa trước)
    public boolean deleteTrain(int trainID) throws SQLException {
        String deleteCarriages = "DELETE FROM Carriage WHERE TrainID = ?";
        String deleteTrain = "DELETE FROM Train WHERE TrainID = ?";
        connection.setAutoCommit(false);
        try (PreparedStatement ps1 = connection.prepareStatement(deleteCarriages); PreparedStatement ps2 = connection.prepareStatement(deleteTrain)) {
            ps1.setInt(1, trainID);
            ps1.executeUpdate();
            ps2.setInt(1, trainID);
            ps2.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public void insert(RailwayDTO model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(RailwayDTO model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(RailwayDTO model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ArrayList<RailwayDTO> list() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public RailwayDTO get(int id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
