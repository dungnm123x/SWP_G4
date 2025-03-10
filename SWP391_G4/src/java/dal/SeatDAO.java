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

//    public List<Seat> getSeatsByCarriageID(int carriageID) {
//        List<Seat> list = new ArrayList<>();
//        String sql = "SELECT SeatID, SeatNumber, Status, SeatType, CarriageID FROM Seat WHERE CarriageID = ?";
//
//        try (PreparedStatement stm = connection.prepareStatement(sql)) {
//            stm.setInt(1, carriageID);
//
//            try (ResultSet rs = stm.executeQuery()) {
//                while (rs.next()) {
//
//                    Carriage carriage = new Carriage(rs.getInt("CarriageID"), null, null, null, 0);
//
//                    Seat seat = new Seat();
//                    seat.setSeatID(rs.getInt("SeatID"));
//                    seat.setSeatNumber(String.valueOf(rs.getInt("SeatNumber"))); // Chuyển số thành chuỗi
//                    seat.setStatus(rs.getString("Status") != null ? rs.getString("Status") : "Unknown");
//                    seat.setSeatType(rs.getString("SeatType"));
//                    seat.setCarriage(carriage);
//
//                    list.add(seat);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return list;
//    }

    public List<Seat> getSeatsForTrip(int carriageID, int tripID) {
        List<Seat> result = new ArrayList<>();
        String sql = """
        SELECT 
          s.SeatID,
          s.SeatNumber,
          CASE WHEN EXISTS (
             SELECT 1 FROM Ticket t
             WHERE t.SeatID = s.SeatID
               AND t.TripID = ?
               AND t.TicketStatus IN ('Unused','Used','Reserved')
          ) 
          THEN 'Booked' 
          ELSE 'Available'
          END AS SeatStatus
        FROM Seat s
        WHERE s.CarriageID = ?
        ORDER BY s.SeatNumber
    """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, tripID);
            st.setInt(2, carriageID);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Seat seat = new Seat();
                seat.setSeatID(rs.getInt("SeatID"));
                seat.setSeatNumber(rs.getString("SeatNumber"));
                seat.setStatus(rs.getString("SeatStatus"));
                result.add(seat);
            }
        } catch (SQLException e) {

        }
        return result;
    }

    public void updateSeatStatus(String seatID, String status) {
        String sql = "UPDATE Seats SET status = ? WHERE seatID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, seatID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
