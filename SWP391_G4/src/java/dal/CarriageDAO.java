package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dto.RailwayDTO;
import model.Carriage;
import model.Train;
import java.sql.CallableStatement;

public class CarriageDAO extends DBContext<RailwayDTO> {

    public List<Carriage> getCarriagesByTrainID(int trainID) {
        List<Carriage> list = new ArrayList<>();
        String sql = "SELECT * FROM Carriage WHERE TrainID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    Train train = new Train(rs.getInt("TrainID"));

                    list.add(new Carriage(
                            rs.getInt("CarriageID"),
                            rs.getString("CarriageNumber"), // Lấy số toa từ ResultSet
                            rs.getString("CarriageType"),
                            train,
                            rs.getInt("Capacity")

                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Carriage> getCarByTrainID(int trainID) {
        return getCarriagesByTrainID(trainID); // Sử dụng lại phương thức getCarriagesByTrainID
    }

    public boolean addCarriage(Carriage carriage) {
        String sql = "INSERT INTO Carriage (CarriageNumber, CarriageType, Capacity, TrainID) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, carriage.getCarriageNumber());
            ps.setString(2, carriage.getCarriageType());
            ps.setInt(3, carriage.getCapacity());
            ps.setInt(4, carriage.getTrain().getTrainID());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return false; // Thêm toa thất bại
            }

            // Lấy CarriageID vừa được tạo (auto-increment)
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int carriageID = generatedKeys.getInt(1);
                    carriage.setCarriageID(carriageID); // Cập nhật ID cho đối tượng Carriage

                    // Gọi stored procedure để thêm ghế
                    addSeats(carriageID, carriage.getCarriageType(), carriage.getTrain().getTrainID());

                    return true;
                } else {
                    return false; // Không lấy được ID
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Phương thức gọi Stored Procedure
    private void addSeats(int carriageID, String carriageType, int trainID) throws SQLException {
        String sql = "{CALL AddSeatsForNewCarriage(?, ?, ?)}";
        try (CallableStatement cs = connection.prepareCall(sql)) {
            cs.setInt(1, carriageID);
            cs.setString(2, carriageType);
            cs.setInt(3, trainID);
            cs.execute(); // Thực thi stored procedure.
        }
    }

    public boolean updateCarriage(Carriage carriage) {
    String sql = "{CALL UpdateCarriageAndSeats(?, ?, ?, ?)}";
    try (CallableStatement cs = connection.prepareCall(sql)) {
        cs.setInt(1, carriage.getCarriageID());
        cs.setString(2, carriage.getCarriageNumber());
        cs.setString(3, carriage.getCarriageType());
        cs.setInt(4, carriage.getCapacity());
        cs.execute();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean deleteCarriage(int carriageID) {
    String sql = "{CALL DeleteCarriageAndSeats(?)}";
    try (CallableStatement cs = connection.prepareCall(sql)) {
        cs.setInt(1, carriageID);
        cs.execute(); // Không cần kiểm tra kết quả, stored proc sẽ ném exception nếu có lỗi
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public Carriage getCarriageById(int carriageID) {
        String query = "SELECT * FROM Carriage WHERE CarriageID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, carriageID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Carriage carriage = new Carriage();
                carriage.setCarriageID(rs.getInt("CarriageID"));
                carriage.setTrain(new Train(rs.getInt("TrainID")));
                carriage.setCarriageNumber(rs.getString("CarriageNumber")); // Lấy số toa từ ResultSet
                carriage.setCarriageType(rs.getString("CarriageType"));
                carriage.setCapacity(rs.getInt("Capacity"));
                return carriage;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(RailwayDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(RailwayDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(RailwayDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<RailwayDTO> list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public RailwayDTO get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}