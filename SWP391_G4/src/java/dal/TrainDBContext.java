package dal;

import dal.DBContext;
import dto.RailwayDTO; // Make sure you have this, if applicable
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import dto.TrainDTO;
import java.sql.*;
import java.time.LocalDateTime; // Import LocalDateTime
import java.time.format.DateTimeFormatter; // Import DateTimeFormatter
import java.util.List;


public class TrainDBContext extends DBContext<TrainDTO> {

     public ArrayList<TrainDTO> getTrains() {
        ArrayList<TrainDTO> trains = new ArrayList<>();
        PreparedStatement stm = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT "
                    + "t.TrainID AS id, "
                    + "MIN(t.TrainName) AS tentau, "
                    + "COUNT(DISTINCT c.CarriageID) AS tongtoa, "
                    + "COUNT(DISTINCT s.SeatID) AS tongghe, "
                    + "MIN(st2.StationName) AS gadi, "
                    + "MIN(st.StationName) AS gaden, "
                    + "MIN(tr.DepartureTime) AS xuatphat, " // Correct column names
                    + "MIN(tr.ArrivalTime) AS dennoi, "      // Correct column names
                    + "MIN(r.BasePrice) AS giave "
                    + "FROM Train t "
                    + "JOIN Carriage c ON t.TrainID = c.TrainID "
                    + "JOIN Seat s ON c.CarriageID = s.CarriageID "
                    + "JOIN Trip tr ON tr.TrainID = t.TrainID "
                    + "JOIN Route r ON r.RouteID = tr.RouteID "
                    + "JOIN Station st ON st.StationID = r.ArrivalStationID "
                    + "JOIN Station st2 ON st2.StationID = r.DepartureStationID "
                    + "GROUP BY t.TrainID, tr.TripID"; // Include tr.TripID in GROUP BY

            stm = connection.prepareStatement(sql);
            rs = stm.executeQuery();

            while (rs.next()) {
                TrainDTO train = new TrainDTO();
                train.setTrainID(rs.getInt("id"));
                train.setTrainName(rs.getString("tentau"));
                train.setTotalCarriages(rs.getInt("tongtoa"));
                train.setTotalSeats(rs.getInt("tongghe"));
                train.setDepartureStation(rs.getString("gadi"));
                train.setArrivalStation(rs.getString("gaden"));

                // Correctly get Timestamp, then convert to LocalDateTime
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
                if (rs != null) rs.close();
                if (stm != null) stm.close();
                //  if (connection != null) connection.close(); // Do NOT close if using a connection pool
            } catch (SQLException ex) {
                Logger.getLogger(TrainDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return trains;
    }
    public TrainDTO getFullTrainInfoById(int trainID) {
      TrainDTO train = null;
        PreparedStatement ps = null; // Declare PreparedStatement here
        ResultSet rs = null;
        try {
            String sql = "SELECT t.TrainID, t.TrainName, " +
                    "COUNT(c.CarriageID) AS TotalCarriages, " +
                    "COALESCE(SUM(c.Capacity), 0) AS TotalSeats, " +
                    "t.DepartureStation, t.ArrivalStation, t.DepartureTime, t.ArrivalTime, t.Price " + // Include other columns
                    "FROM Train t " +
                    "LEFT JOIN Carriage c ON t.TrainID = c.TrainID " +
                    "WHERE t.TrainID = ? " +
                    "GROUP BY t.TrainID, t.TrainName, t.DepartureStation, t.ArrivalStation, t.DepartureTime, t.ArrivalTime, t.Price"; // Corrected GROUP BY

            ps = connection.prepareStatement(sql);
            ps.setInt(1, trainID);
            rs = ps.executeQuery();

            if (rs.next()) {
                train = new TrainDTO();
                train.setTrainID(rs.getInt("TrainID"));
                train.setTrainName(rs.getString("TrainName"));
                train.setTotalCarriages(rs.getInt("TotalCarriages"));
                train.setTotalSeats(rs.getInt("TotalSeats"));
                train.setDepartureStation(rs.getString("DepartureStation"));
                train.setArrivalStation(rs.getString("ArrivalStation"));

                // Convert Timestamp to LocalDateTime
                Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                if (departureTimestamp != null) {
                    train.setDepartureTime(departureTimestamp.toLocalDateTime());
                }

                Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                if (arrivalTimestamp != null) {
                    train.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                }

                train.setPrice(rs.getDouble("Price"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
              //  if (connection != null) connection.close();// Do NOT close connection in DAO if using a connection pool
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return train;
    }

    public TrainDTO getTrainById(int trainID) {
      TrainDTO train = null;
        PreparedStatement stm = null; // Declare PreparedStatement here
        ResultSet rs = null;
        try {
            String sql = "SELECT \n" +
                    "       MIN(t.TrainName) AS tentau,\n" +
                    "       COUNT(DISTINCT c.CarriageID) AS tongtoa,\n" +
                    "       COUNT(DISTINCT s.SeatID) AS tongghe,\n " +
                    "t.DepartureStation, t.ArrivalStation, t.DepartureTime, t.ArrivalTime, t.Price \n" + //get datetime
                    "FROM Train t\n" +
                    "JOIN Carriage c ON t.TrainID = c.TrainID\n" +
                    "JOIN Seat s ON c.CarriageID = s.CarriageID\n" +
                    "WHERE t.TrainId = ?\n" +
                    "GROUP BY t.TrainID, t.DepartureStation, t.ArrivalStation, t.DepartureTime, t.ArrivalTime, t.Price;"; // Corrected GROUP BY

            stm = connection.prepareStatement(sql);
            stm.setInt(1, trainID);
            rs = stm.executeQuery();

            if (rs.next()) {
                 train = new TrainDTO();

                train.setTrainID(trainID);
                train.setTrainName(rs.getNString("tentau"));
                train.setTotalCarriages(rs.getInt("tongtoa"));
                train.setTotalSeats(rs.getInt("tongghe"));
                train.setDepartureStation(rs.getString("DepartureStation"));
                train.setArrivalStation(rs.getString("ArrivalStation"));

              // Convert Timestamp to LocalDateTime
                Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                if (departureTimestamp != null) {
                    train.setDepartureTime(departureTimestamp.toLocalDateTime());
                }

                Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                if (arrivalTimestamp != null) {
                    train.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                }

                train.setPrice(rs.getDouble("Price"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null) rs.close();
                if (stm != null) stm.close();
                //if (connection != null) connection.close(); // Do NOT close the connection here if using a pool
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return train;
    }
     public List<TrainDTO> getFilteredTrains(String departStation, String arriveStation, String departureDate) {
        List<TrainDTO> trains = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
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
            sql += "AND CONVERT(DATE, tr.DepartureTime) = ? "; // Keep as DATE if comparing only the date part
            }
            sql += "GROUP BY t.TrainID";

            stmt = connection.prepareStatement(sql);

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


            rs = stmt.executeQuery();
            while (rs.next()) {
                TrainDTO train = new TrainDTO();
                train.setTrainID(rs.getInt("id"));
                train.setTrainName(rs.getString("tentau"));
                train.setTotalCarriages(rs.getInt("tongtoa"));
                train.setTotalSeats(rs.getInt("tongghe"));
                train.setDepartureStation(rs.getString("gadi"));
                train.setArrivalStation(rs.getString("gaden"));

                // Convert Timestamp to LocalDateTime, handling nulls
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
             e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                // Do NOT close the connection here if you are using a connection pool
            } catch (SQLException e) {
                e.printStackTrace(); // Log the exception
            }
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