package dal;

import dto.TripDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TripDBContext extends DBContext<TripDTO> { // Use TripDTO

    public List<TripDTO> getAllTrips(int page, int pageSize) {
        List<TripDTO> trips = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT tr.TripID, tr.TrainID, t.TrainName, tr.RouteID, " +
                "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, " +
                "tr.DepartureTime, tr.ArrivalTime,tr.TripStatus, " +
                "ROW_NUMBER() OVER (ORDER BY tr.TripID) as row_num " + // Add row number
                "FROM Trip tr " +
                "JOIN Train t ON tr.TrainID = t.TrainID " +
                "JOIN Route r ON tr.RouteID = r.RouteID " +
                "JOIN Station st1 ON r.DepartureStationID = st1.StationID " +
                "JOIN Station st2 ON r.ArrivalStationID = st2.StationID) as x " +
                "WHERE row_num BETWEEN ? AND ?;"; // Add pagination
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize + 1);
            ps.setInt(2, page * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TripDTO trip = new TripDTO();
                    trip.setTripID(rs.getInt("TripID"));
                    trip.setTrainID(rs.getInt("TrainID"));
                    trip.setTrainName(rs.getString("TrainName")); // Set train name
                    trip.setRouteID(rs.getInt("RouteID"));
                    trip.setRouteName(rs.getString("RouteName")); // Set route name

                    Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                    if (departureTimestamp != null) {
                        trip.setDepartureTime(departureTimestamp.toLocalDateTime());
                    }

                    Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                    if (arrivalTimestamp != null) {
                        trip.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                    }
                    trip.setTripStatus(rs.getString("TripStatus"));
                    trips.add(trip);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return trips;
    }
      //Get total trip
     public int getTotalTripsCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM Trip";

        try (PreparedStatement stm = connection.prepareStatement(sql);
             ResultSet rs = stm.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return count;
    }


    public TripDTO getTripById(int tripID) {
        TripDTO trip = null;
        String sql = "SELECT tr.TripID, tr.TrainID, t.TrainName, tr.RouteID, " +
                     "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, " +
                     "tr.DepartureTime, tr.ArrivalTime, tr.TripStatus " +
                     "FROM Trip tr " +
                     "JOIN Train t ON tr.TrainID = t.TrainID " +
                     "JOIN Route r ON tr.RouteID = r.RouteID " +
                     "JOIN Station st1 ON r.DepartureStationID = st1.StationID " +
                     "JOIN Station st2 ON r.ArrivalStationID = st2.StationID " +
                     "WHERE tr.TripID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tripID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    trip = new TripDTO();
                    trip.setTripID(rs.getInt("TripID"));
                    trip.setTrainID(rs.getInt("TrainID"));
                    trip.setTrainName(rs.getString("TrainName")); // Set train name
                    trip.setRouteID(rs.getInt("RouteID"));
                    trip.setRouteName(rs.getString("RouteName")); // Set route name
                    Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                    if (departureTimestamp != null) {
                        trip.setDepartureTime(departureTimestamp.toLocalDateTime());
                    }

                    Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                    if (arrivalTimestamp != null) {
                        trip.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                    }
                    trip.setTripStatus(rs.getString("TripStatus"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return trip;
    }

    public boolean addTrip(TripDTO trip) {
        String sql = "INSERT INTO Trip (TrainID, RouteID, DepartureTime, ArrivalTime,TripStatus) VALUES (?, ?, ?, ?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trip.getTrainID());
            ps.setInt(2, trip.getRouteID());
            ps.setTimestamp(3, trip.getDepartureTime() != null ? Timestamp.valueOf(trip.getDepartureTime()) : null);
            ps.setTimestamp(4, trip.getArrivalTime() != null ? Timestamp.valueOf(trip.getArrivalTime()) : null);
            ps.setString(5, trip.getTripStatus());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrip(TripDTO trip) {
       String sql = "UPDATE Trip SET TrainID = ?, RouteID = ?, DepartureTime = ?, ArrivalTime = ?, TripStatus = ? WHERE TripID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trip.getTrainID());
            ps.setInt(2, trip.getRouteID());
            ps.setTimestamp(3, trip.getDepartureTime() != null ? Timestamp.valueOf(trip.getDepartureTime()) : null);
            ps.setTimestamp(4, trip.getArrivalTime() != null ? Timestamp.valueOf(trip.getArrivalTime()) : null);
            ps.setString(5, trip.getTripStatus());
            ps.setInt(6, trip.getTripID());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTrip(int tripID) {
       String sql = "DELETE FROM Trip WHERE TripID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tripID);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

     // Methods for filtered trips, counts, etc. (similar to TrainDBContext, but for Trips)
      public List<TripDTO> getFilteredTrips(String departStation, String arriveStation, LocalDateTime departureDate, int page, int pageSize) {
        List<TripDTO> trips = new ArrayList<>();
          String sql = "SELECT * FROM (SELECT tr.TripID, tr.TrainID, t.TrainName, tr.RouteID, " +
                "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, " +
                "tr.DepartureTime, tr.ArrivalTime, tr.TripStatus, " +
                "ROW_NUMBER() OVER (ORDER BY tr.TripID) as row_num " + // Add row number
                "FROM Trip tr " +
                "JOIN Train t ON tr.TrainID = t.TrainID " +
                "JOIN Route r ON tr.RouteID = r.RouteID " +
                "JOIN Station st1 ON r.DepartureStationID = st1.StationID " +
                "JOIN Station st2 ON r.ArrivalStationID = st2.StationID " +
                "WHERE 1=1 ";

        if (departStation != null && !departStation.isEmpty()) {
            sql += "AND st1.StationName LIKE ? ";
        }
        if (arriveStation != null && !arriveStation.isEmpty()) {
            sql += "AND st2.StationName LIKE ? ";
        }
        if (departureDate != null) {
             sql += "AND CONVERT(DATE, tr.DepartureTime) = ? "; // Compare only the date part
        }

        sql += ") as x WHERE row_num BETWEEN ? AND ?"; // Add pagination
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int parameterIndex = 1;
            if (departStation != null && !departStation.isEmpty()) {
                ps.setString(parameterIndex++, "%" + departStation + "%");
            }
            if (arriveStation != null && !arriveStation.isEmpty()) {
                ps.setString(parameterIndex++, "%" + arriveStation + "%");
            }
            if (departureDate != null) {
                 ps.setDate(parameterIndex++, java.sql.Date.valueOf(departureDate.toLocalDate())); // Convert to java.sql.Date
            }

            ps.setInt(parameterIndex++, (page - 1) * pageSize + 1);  // Start row
            ps.setInt(parameterIndex++, page * pageSize);              // End row

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TripDTO trip = new TripDTO();
                    trip.setTripID(rs.getInt("TripID"));
                    trip.setTrainID(rs.getInt("TrainID"));
                    trip.setTrainName(rs.getString("TrainName")); // Set train name
                    trip.setRouteID(rs.getInt("RouteID"));
                    trip.setRouteName(rs.getString("RouteName")); // Set route name

                    Timestamp departureTimestamp = rs.getTimestamp("DepartureTime");
                    if (departureTimestamp != null) {
                        trip.setDepartureTime(departureTimestamp.toLocalDateTime());
                    }

                    Timestamp arrivalTimestamp = rs.getTimestamp("ArrivalTime");
                    if (arrivalTimestamp != null) {
                        trip.setArrivalTime(arrivalTimestamp.toLocalDateTime());
                    }
                      trip.setTripStatus(rs.getString("TripStatus"));
                    trips.add(trip);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return trips;
    }

    public int getFilteredTripsCount(String departStation, String arriveStation, LocalDateTime departureDate) {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total " +
                "FROM Trip tr " +
                "JOIN Route r ON tr.RouteID = r.RouteID " +
                "JOIN Station st1 ON r.DepartureStationID = st1.StationID " +
                "JOIN Station st2 ON r.ArrivalStationID = st2.StationID " +
                "WHERE 1=1 ";

        if (departStation != null && !departStation.isEmpty()) {
            sql += "AND st1.StationName LIKE ? ";
        }
        if (arriveStation != null && !arriveStation.isEmpty()) {
            sql += "AND st2.StationName LIKE ? ";
        }
        if (departureDate != null) {
            sql += "AND CONVERT(DATE, tr.DepartureTime) = ? "; // Compare only the date part
        }


        try (PreparedStatement ps = connection.prepareStatement(sql)) {
             int parameterIndex = 1;
            if (departStation != null && !departStation.isEmpty()) {
                ps.setString(parameterIndex++, "%" + departStation + "%");
            }
            if (arriveStation != null && !arriveStation.isEmpty()) {
                ps.setString(parameterIndex++, "%" + arriveStation + "%");
            }
           if (departureDate != null) {
                ps.setDate(parameterIndex++, java.sql.Date.valueOf(departureDate.toLocalDate())); // Convert to java.sql.Date
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return count;
    }

    @Override
    public TripDTO get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<TripDTO> list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void insert(TripDTO t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(TripDTO t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(TripDTO t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}