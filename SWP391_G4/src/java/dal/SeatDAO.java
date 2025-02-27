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
import model.Carriage;
import model.Seat;

public class SeatDAO extends DBContext<RailwayDTO> {

    public List<Seat> getSeatsByCarriageID(int carriageID) {
        List<Seat> list = new ArrayList<>();
        String sql = "SELECT SeatID, SeatNumber, Status, SeatType, CarriageID FROM Seat WHERE CarriageID = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, carriageID);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    
                    Carriage carriage = new Carriage(rs.getInt("CarriageID"), null, null, null, 0);

                    
                    Seat seat = new Seat();
                    seat.setSeatID(rs.getInt("SeatID"));
                    seat.setSeatNumber(String.valueOf(rs.getInt("SeatNumber"))); // Chuyển số thành chuỗi
                    seat.setStatus(rs.getString("Status") != null ? rs.getString("Status") : "Unknown");
                    seat.setSeatType(rs.getString("SeatType"));
                    seat.setCarriage(carriage); 

                    list.add(seat);
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
