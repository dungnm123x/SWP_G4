/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import dto.RailwayDTO;
import java.util.ArrayList;
import java.util.List;
import model.Trip;
import model.Train;
import model.Route;
import model.Station;

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

//    public Trip getTripByTrainID(int trainID) {
//        Trip trip = null;
//        String sql = "SELECT t.TripID, t.TrainID, tr.TrainName, t.DepartureTime "
//                + "FROM Trip t "
//                + "JOIN Train tr ON t.TrainID = tr.TrainID "
//                + "WHERE t.TrainID = ?";
//
//        try (
//                PreparedStatement ps = connection.prepareStatement(sql)) {
//
//            ps.setInt(1, trainID);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                trip = new Trip();
//                Train train = new Train();
//
//                train.setTrainID(rs.getInt("TrainID"));
//                train.setTrainName(rs.getString("TrainName"));
//                trip.setTripID(rs.getInt("TripID"));
//                trip.setTrain(train);
//                trip.setDepartureTime(rs.getTimestamp("DepartureTime"));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return trip;
//    }
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
