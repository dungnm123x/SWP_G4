/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dto.RailwayDTO;

public class CarriageDAO extends DBContext<RailwayDTO> {

    public List<RailwayDTO> getCarriagesByTrainID(int trainID) {
        List<RailwayDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Carriage WHERE TrainID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(new RailwayDTO(
                            rs.getInt("CarriageID"),
                            rs.getInt("CarriageNumber"),
                            rs.getString("CarriageType"),
                            rs.getInt("TrainID"),
                            rs.getInt("Capacity"),
                            0, 0, null, null, 0, null, null, 0, 0, 0, 0, 0.0, 0, null, null, 0.0, 0, null, null, null
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
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
