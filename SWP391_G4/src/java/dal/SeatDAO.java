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

public class SeatDAO extends DBContext<RailwayDTO> {

    public List<RailwayDTO> getSeatsByCarriageID(int carriageID) {
        List<RailwayDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Seat WHERE CarriageID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, carriageID);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    list.add(new RailwayDTO(
                            0, 0, null, 0, 0,
                            rs.getInt("SeatID"),
                            rs.getInt("SeatNumber"),
                            rs.getString("Status"),
                            rs.getString("SeatType"),
                            0, null, null, 0, 0, 0, 0, 0.0, 0, null, null, 0.0, 0, null, null, null
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

