package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dto.RailwayDTO;
import model.Carriage;
import model.Train;

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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, carriage.getCarriageNumber()); // Gán số toa
            ps.setString(2, carriage.getCarriageType());
            ps.setInt(3, carriage.getCapacity());
            ps.setInt(4, carriage.getTrain().getTrainID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarriage(Carriage carriage) {
        String sql = "UPDATE Carriage SET CarriageNumber = ?, CarriageType = ?, Capacity = ? WHERE CarriageID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, carriage.getCarriageNumber()); // Gán số toa
            ps.setString(2, carriage.getCarriageType());
            ps.setInt(3, carriage.getCapacity());
            ps.setInt(4, carriage.getCarriageID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCarriage(int carriageID) {
        String sql = "DELETE FROM Carriage WHERE CarriageID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, carriageID);
            return ps.executeUpdate() > 0;
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