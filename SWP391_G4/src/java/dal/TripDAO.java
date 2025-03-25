/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dto.RailwayDTO;
import dto.TripDTO;
import java.util.ArrayList;
import java.util.List;
import model.Trip;
import model.Train;
import model.Route;
import model.Seat;
import model.Station;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class TripDAO extends DBContext<RailwayDTO> {

    public List<Trip> getTripsByRoute(int departureStationID, int arrivalStationID, String departureDate) {
        List<Trip> tripList = new ArrayList<>();

        String sql = "SELECT Trip.*, Train.TrainID, Train.TrainName, "
                + "Route.RouteID, Route.DepartureStationID, Route.ArrivalStationID, Route.Distance, Route.BasePrice, "
                + "DepStation.StationName AS DepartureName, DepStation.Address AS DepartureAddress, "
                + "ArrStation.StationName AS ArrivalName, ArrStation.Address AS ArrivalAddress "
                + "FROM Trip "
                + "JOIN Train ON Trip.TrainID = Train.TrainID "
                + "JOIN Route ON Trip.RouteID = Route.RouteID "
                + "JOIN Station AS DepStation ON Route.DepartureStationID = DepStation.StationID "
                + "JOIN Station AS ArrStation ON Route.ArrivalStationID = ArrStation.StationID "
                + "WHERE Route.DepartureStationID = ? "
                + "AND Route.ArrivalStationID = ? "
                + "AND CONVERT(DATE, Trip.DepartureTime) = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, departureStationID);
            stm.setInt(2, arrivalStationID);
            stm.setString(3, departureDate);

            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    // Tạo đối tượng Train
                    Train train = new Train(rs.getInt("TrainID"), rs.getString("TrainName"));

                    // Tạo đối tượng Station (Ga đi & Ga đến)
                    Station departureStation = new Station(
                            rs.getInt("DepartureStationID"),
                            rs.getString("DepartureName"),
                            rs.getString("DepartureAddress")
                    );

                    Station arrivalStation = new Station(
                            rs.getInt("ArrivalStationID"),
                            rs.getString("ArrivalName"),
                            rs.getString("ArrivalAddress")
                    );

                    // Tạo đối tượng Route
                    Route route = new Route(
                            rs.getInt("RouteID"),
                            departureStation,
                            arrivalStation,
                            rs.getDouble("Distance"),
                            rs.getDouble("BasePrice")
                    );

                    // Tạo đối tượng Trip
                    Trip trip = new Trip();
                    trip.setTripID(rs.getInt("TripID"));
                    trip.setTrain(train);
                    trip.setRoute(route);
                    trip.setDepartureTime(rs.getTimestamp("DepartureTime"));
                    trip.setArrivalTime(rs.getTimestamp("ArrivalTime"));
                    trip.setTripStatus(rs.getString("TripStatus"));
                    trip.setTripType(rs.getString("TripType"));
                    trip.setRoundTripReference(rs.getInt("RoundTripReference") != 0 ? rs.getInt("RoundTripReference") : null);

                    tripList.add(trip);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tripList;
    }

    public List<TripDTO> getTripsByTrainId(int trainId) {
        List<TripDTO> trips = new ArrayList<>();
        String sql = "SELECT tr.TripID, tr.TrainID, t.TrainName, tr.RouteID, "
                + "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, "
                + "tr.DepartureTime, tr.ArrivalTime, tr.TripStatus "
                + "FROM Trip tr "
                + "JOIN Train t ON tr.TrainID = t.TrainID "
                + "JOIN Route r ON tr.RouteID = r.RouteID "
                + "JOIN Station st1 ON r.DepartureStationID = st1.StationID "
                + "JOIN Station st2 ON r.ArrivalStationID = st2.StationID "
                + "WHERE tr.TrainID = ?"; // Filter by TrainID

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trainId); // Set the TrainID parameter
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TripDTO trip = new TripDTO();
                    trip.setTripID(rs.getInt("TripID"));
                    trip.setTrainID(rs.getInt("TrainID"));
                    trip.setTrainName(rs.getString("TrainName"));
                    trip.setRouteID(rs.getInt("RouteID"));
                    trip.setRouteName(rs.getString("RouteName"));

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
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return trips;
    }

    public Trip getTripByID(int tripID) {
        Trip trip = null;
        String sql = "SELECT t.TripID, t.TrainID, tr.TrainName, "
                + "       t.DepartureTime, t.ArrivalTime, t.TripStatus, t.TripType, "
                + "       t.RoundTripReference, r.RouteID, r.Distance, r.BasePrice "
                + "FROM Trip t "
                + "JOIN Train tr ON t.TrainID = tr.TrainID "
                + "JOIN Route r ON t.RouteID = r.RouteID "
                + "WHERE t.TripID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tripID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Tạo Trip
                    trip = new Trip();
                    trip.setTripID(rs.getInt("TripID"));

                    // Train
                    Train train = new Train(rs.getInt("TrainID"), rs.getString("TrainName"));
                    trip.setTrain(train);

                    // Route
                    Route route = new Route();
                    route.setRouteID(rs.getInt("RouteID"));
                    route.setDistance(rs.getDouble("Distance"));
                    route.setBasePrice(rs.getDouble("BasePrice"));
                    trip.setRoute(route);

                    trip.setDepartureTime(rs.getTimestamp("DepartureTime"));
                    trip.setArrivalTime(rs.getTimestamp("ArrivalTime"));
                    trip.setTripStatus(rs.getString("TripStatus"));
                    trip.setTripType(rs.getString("TripType"));
                    int ref = rs.getInt("RoundTripReference");
                    trip.setRoundTripReference(ref != 0 ? ref : null);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trip;
    }

    public TripDTO getTripById(int tripID) {
        TripDTO trip = null;
        String sql = "SELECT tr.TripID, tr.TrainID, t.TrainName, tr.RouteID, "
                + "CONCAT(st1.StationName, ' - ', st2.StationName) AS RouteName, "
                + "tr.DepartureTime, tr.ArrivalTime, tr.TripStatus "
                + "FROM Trip tr "
                + "JOIN Train t ON tr.TrainID = t.TrainID "
                + "JOIN Route r ON tr.RouteID = r.RouteID "
                + "JOIN Station st1 ON r.DepartureStationID = st1.StationID "
                + "JOIN Station st2 ON r.ArrivalStationID = st2.StationID "
                + "WHERE tr.TripID = ?";

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

    public boolean isTripOverlapping(int trainID, LocalDateTime newDeparture, LocalDateTime newArrival, Integer excludeTripId) {
        String sql = "SELECT COUNT(*) FROM Trip "
                + "WHERE TrainID = ? "
                + "AND ((DepartureTime < ? AND ArrivalTime > ?) "
                + // Case 1: New trip starts before existing trip ends, and ends after existing trip starts
                "  OR (DepartureTime < ? AND ArrivalTime > ?))";   // Case 2: New trip starts during existing trip

        // Add exclusion for updates
        if (excludeTripId != null) {
            sql += " AND TripID <> ?"; // Exclude the current trip being updated
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trainID);
            ps.setTimestamp(2, Timestamp.valueOf(newArrival)); // End of *new* trip
            ps.setTimestamp(3, Timestamp.valueOf(newDeparture)); // Start of *new* trip
            ps.setTimestamp(4, Timestamp.valueOf(newDeparture)); // Start of *new* trip
            ps.setTimestamp(5, Timestamp.valueOf(newArrival)); // End of new trip.
            if (excludeTripId != null) {
                ps.setInt(6, excludeTripId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Return true if count > 0 (overlap exists)
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log and handle the exception
            return true; // Assume overlap on error, to be safe
        }
        return false; // No overlap found
    }

// Overload for add, excludeTripId will be null.
    public boolean isTripOverlapping(int trainID, LocalDateTime newDeparture, LocalDateTime newArrival) {
        return isTripOverlapping(trainID, newDeparture, newArrival, null);
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
