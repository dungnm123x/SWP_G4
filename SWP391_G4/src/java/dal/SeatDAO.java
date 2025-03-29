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

    public String getSeatStatus(String seatID) {
        String sql = "SELECT Status FROM Seat WHERE SeatID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, seatID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Nếu không tìm thấy
    }

    public List<Seat> getSeatsForTrip(int carriageID, int tripID) {
        List<Seat> result = new ArrayList<>();
        String sql = """
        SELECT
            s.SeatID,
            s.SeatNumber,
            CASE
                WHEN s.Status = 'Out of Service' THEN 'Out of Service'
                WHEN s.Status = 'Booked' THEN 'Booked'
                WHEN EXISTS (
                    SELECT 1 FROM Ticket t
                    WHERE t.SeatID = s.SeatID
                      AND t.TripID = ?
                      AND t.TicketStatus IN ('Used', 'Unused', 'Reserved')
                ) THEN 'Booked'
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
            e.printStackTrace();
        }
        return result;
    }

//    public List<Seat> getSeatsForTrip(int carriageID, int tripID) {
//        List<Seat> result = new ArrayList<>();
//        String sql = "SELECT SeatID, SeatNumber, Status AS SeatStatus FROM Seat WHERE CarriageID = ? ORDER BY SeatNumber";
//        try (PreparedStatement st = connection.prepareStatement(sql)) {
//            st.setInt(1, carriageID);
//            try (ResultSet rs = st.executeQuery()) {
//                while (rs.next()) {
//                    Seat seat = new Seat();
//                    seat.setSeatID(rs.getInt("SeatID"));
//                    seat.setSeatNumber(rs.getString("SeatNumber"));
//                    seat.setStatus(rs.getString("SeatStatus"));
//                    result.add(seat);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
    public void updateSeatStatus(String seatID, String status) {
        String sql = "UPDATE Seat SET status = ? WHERE seatID = ?";
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
