package dal;

import java.sql.*;
import java.util.*;
import model.Route;
import model.Station;

public class RouteDAO extends DBContext {

    public boolean addRoute(int departureStationID, int arrivalStationID, double distance, double basePrice) {
        String sql = "INSERT INTO Route (DepartureStationID, ArrivalStationID, Distance, BasePrice) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departureStationID);
            ps.setInt(2, arrivalStationID);
            ps.setDouble(3, distance);
            ps.setDouble(4, basePrice);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM Station";

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stations.add(new Station(rs.getInt("StationID"), rs.getString("StationName"), rs.getString("Address")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;
    }

    public boolean updateRoute(int routeID, int departureStationID, int arrivalStationID, double distance, double basePrice) {
        String sql = "UPDATE Route SET DepartureStationID = ?, ArrivalStationID = ?, Distance = ?, BasePrice = ? WHERE RouteID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departureStationID);
            ps.setInt(2, arrivalStationID);
            ps.setDouble(3, distance);
            ps.setDouble(4, basePrice);
            ps.setInt(5, routeID);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Xóa tuyến tàu
    public boolean deleteRoute(int routeID) {
        String sql = "DELETE FROM Route WHERE RouteID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routeID);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Lấy thông tin một tuyến tàu theo ID
    public Route getRouteById(int routeID) {
        String sql = "SELECT r.RouteID, s.StationID AS DepartureID, s.StationName AS DepartureName, "
                + "s2.StationID AS ArrivalID, s2.StationName AS ArrivalName, r.Distance, r.BasePrice "
                + "FROM Route r "
                + "JOIN Station s ON r.DepartureStationID = s.StationID "
                + "JOIN Station s2 ON r.ArrivalStationID = s2.StationID "
                + "WHERE r.RouteID = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, routeID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Station departure = new Station(rs.getInt("DepartureID"), rs.getString("DepartureName"), "");
                Station arrival = new Station(rs.getInt("ArrivalID"), rs.getString("ArrivalName"), "");
                return new Route(rs.getInt("RouteID"), departure, arrival, rs.getDouble("Distance"), rs.getDouble("BasePrice"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isRouteExists(int departureStationID, int arrivalStationID) {
        String sql = "SELECT COUNT(*) FROM Route WHERE DepartureStationID = ? AND ArrivalStationID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, departureStationID);
            ps.setInt(2, arrivalStationID);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true; // Tuyến đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Tuyến chưa tồn tại
    }

    @Override
    public void insert(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(Object model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<Route> list() {
        ArrayList<Route> routes = new ArrayList<>();
        String sql = "SELECT r.RouteID,s.StationID as DepartureId,s.StationName as DepartureName,s.Address as DepartureAddress\n"
                + ",s2.StationID as ArrivalId,s2.StationName as ArrivalName,s2.Address as ArrivalAddress ,r.Distance, r.BasePrice\n"
                + "from Route r join Station s on r.DepartureStationID= s.StationID \n"
                + "join Station s2 on r.ArrivalStationID=s2.StationID";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Station DepStation = new Station(rs.getInt("DepartureId"),
                        rs.getNString("DepartureName"),
                        rs.getNString("DepartureAddress"));

                Station ArrStation = new Station(rs.getInt("ArrivalId"),
                        rs.getNString("ArrivalName"),
                        rs.getNString("ArrivalAddress"));

                Route route = new Route(
                        rs.getInt("RouteID"),
                        DepStation,
                        ArrStation,
                        rs.getDouble("Distance"),
                        rs.getDouble("BasePrice")
                );
                routes.add(route);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    public Object get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
