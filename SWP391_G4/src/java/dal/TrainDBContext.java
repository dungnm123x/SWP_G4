package dal;

import dal.DBContext;
import dto.RailwayDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import dto.TrainDTO;
import java.sql.*;
import java.util.List;

public class TrainDBContext extends DBContext<TrainDTO> {

    public ArrayList<TrainDTO> getTrains() {
        ArrayList<TrainDTO> trains = new ArrayList<>();
        PreparedStatement stm = null;
        try {
            String sql = "SELECT \n"
                    + "    t.TrainID AS id,\n"
                    + "    MIN(t.TrainName) AS tentau,\n"
                    + "    COUNT(DISTINCT c.CarriageID) AS tongtoa,\n"
                    + "    COUNT(DISTINCT s.SeatID) AS tongghe,\n"
                    + "    MIN(st2.StationName) AS gadi,\n"
                    + "    MIN(st.StationName) AS gaden,\n"
                    + "    MIN(tr.DepartureTime) AS xuatphat,\n"
                    + "    MIN(tr.ArrivalTime) AS dennoi,\n"
                    + "    MIN(r.BasePrice) AS giave\n"
                    + "FROM Train t\n"
                    + "JOIN Carriage c ON t.TrainID = c.TrainID\n"
                    + "JOIN Seat s ON c.CarriageID = s.CarriageID\n"
                    + "JOIN Trip tr ON tr.TrainID = t.TrainID\n"
                    + "JOIN Route r ON r.RouteID = tr.RouteID\n"
                    + "JOIN Station st ON st.StationID = r.ArrivalStationID\n"
                    + "JOIN Station st2 ON st2.StationID = r.DepartureStationID\n"
                    + "GROUP BY t.TrainID, tr.TripID;";

            stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                TrainDTO train = new TrainDTO(); // Use the default constructor
                train.setTrainID(rs.getInt("id"));
                train.setTrainName(rs.getString("tentau"));
                train.setTotalCarriages(rs.getInt("tongtoa"));
                train.setTotalSeats(rs.getInt("tongghe"));
                train.setDepartureStation(rs.getString("gadi"));
                train.setArrivalStation(rs.getString("gaden"));

                // Get Timestamp and convert to LocalDateTime, handling nulls
                Timestamp departureTimestamp = rs.getTimestamp("xuatphat");
                if (departureTimestamp != null) {
                    train.setDepartureTime(departureTimestamp.toLocalDateTime());
                }

                Timestamp arrivalTimestamp = rs.getTimestamp("dennoi");
                if (arrivalTimestamp != null) {
                    train.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                }

                train.setPrice(rs.getDouble("giave"));
                trains.add(train);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TrainDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stm != null) {
                    stm.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrainDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return trains;
    }

    public TrainDTO getFullTrainInfoById(int trainID) {
        TrainDTO train = null;
        String sql = "SELECT t.TrainID, t.TrainName, "
                + "COUNT(c.CarriageID) AS TotalCarriages, COALESCE(SUM(c.Capacity), 0) AS TotalSeats "
                + "FROM Train t "
                + "LEFT JOIN Carriage c ON t.TrainID = c.TrainID "
                + "WHERE t.TrainID = ? " // Thêm điều kiện WHERE
                + "GROUP BY t.TrainID, t.TrainName"; // Gom nhóm theo TrainID

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trainID); // Set tham số TrainID
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    train = new TrainDTO();
                    train.setTrainID(rs.getInt("TrainID"));
                    train.setTrainName(rs.getString("TrainName"));
                    train.setTotalCarriages(rs.getInt("TotalCarriages"));
                    train.setTotalSeats(rs.getInt("TotalSeats"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return train;
    }

    public TrainDTO getTrainById(int trainID) {
        TrainDTO train = null;
        String sql = "SELECT \n"
                + "    MIN(t.TrainName) AS tentau,\n"
                + "    COUNT(DISTINCT c.CarriageID) AS tongtoa,\n"
                + "    COUNT(DISTINCT s.SeatID) AS tongghe\n"
                + "FROM Train t\n"
                + "JOIN Carriage c ON t.TrainID = c.TrainID\n"
                + "JOIN Seat s ON c.CarriageID = s.CarriageID\n"
                + "WHERE t.TrainId = ?\n"
                + "GROUP BY t.TrainID;";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    train = new TrainDTO(trainID, rs.getNString("tentau"), rs.getInt("tongtoa"), rs.getInt("tongghe"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return train;
    }

    public List<TrainDTO> getFilteredTrains(String departStation, String arriveStation, String departureDate) {
        List<TrainDTO> trains = new ArrayList<>();
        String sql = "SELECT t.TrainID AS id, MIN(t.TrainName) AS tentau, COUNT(DISTINCT c.CarriageID) AS tongtoa, "
                + "COUNT(DISTINCT s.SeatID) AS tongghe, MIN(st2.StationName) AS gadi, MIN(st.StationName) AS gaden, "
                + "MIN(tr.DepartureTime) AS xuatphat, MIN(tr.ArrivalTime) AS dennoi, MIN(r.BasePrice) AS giave "
                + "FROM Train t "
                + "JOIN Carriage c ON t.TrainID = c.TrainID "
                + "JOIN Seat s ON c.CarriageID = s.CarriageID "
                + "JOIN Trip tr ON tr.TrainID = t.TrainID "
                + "JOIN Route r ON r.RouteID = tr.RouteID "
                + "JOIN Station st ON st.StationID = r.ArrivalStationID "
                + "JOIN Station st2 ON st2.StationID = r.DepartureStationID "
                + "WHERE 1=1 ";

        if (departStation != null && !departStation.isEmpty()) {
            sql += "AND st2.StationName LIKE ? ";
        }
        if (arriveStation != null && !arriveStation.isEmpty()) {
            sql += "AND st.StationName LIKE ? ";
        }
        if (departureDate != null && !departureDate.isEmpty()) {
            sql += "AND CONVERT(DATE, tr.DepartureTime) = ? ";
        }
        sql += "GROUP BY t.TrainID";

        try (Connection conn = connection; PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            if (departStation != null && !departStation.isEmpty()) {
                stmt.setString(index++, "%" + departStation + "%");
            }
            if (arriveStation != null && !arriveStation.isEmpty()) {
                stmt.setString(index++, "%" + arriveStation + "%");
            }
            if (departureDate != null && !departureDate.isEmpty()) {
                stmt.setDate(index++, Date.valueOf(departureDate));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TrainDTO train = new TrainDTO();
                train.setTrainID(rs.getInt("id"));
                train.setTrainName(rs.getString("tentau"));
                train.setTotalCarriages(rs.getInt("tongtoa"));
                train.setTotalSeats(rs.getInt("tongghe"));
                train.setDepartureStation(rs.getString("gadi"));
                train.setArrivalStation(rs.getString("gaden"));
                Timestamp departureTimestamp = rs.getTimestamp("xuatphat");
                if (departureTimestamp != null) {
                    train.setDepartureTime(departureTimestamp.toLocalDateTime());
                }

                Timestamp arrivalTimestamp = rs.getTimestamp("dennoi");
                if (arrivalTimestamp != null) {
                    train.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                }
                train.setPrice(rs.getDouble("giave"));
                trains.add(train);
            }
        } catch (SQLException e) {
        }
        return trains;
    }

    @Override
    public void insert(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<TrainDTO> list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TrainDTO get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
