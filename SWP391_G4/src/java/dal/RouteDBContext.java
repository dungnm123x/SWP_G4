package dal;

import dto.RouteDTO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RouteDBContext extends DBContext<RouteDTO> {

    public List<RouteDTO> getAllRoutes() {
        List<RouteDTO> routes = new ArrayList<>();
        String sql = "SELECT r.RouteID, r.DepartureStationID, r.ArrivalStationID, " +
                     "s1.StationName AS DepartureStationName, s2.StationName AS ArrivalStationName " +
                     "FROM Route r " +
                     "JOIN Station s1 ON r.DepartureStationID = s1.StationID " +
                     "JOIN Station s2 ON r.ArrivalStationID = s2.StationID";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RouteDTO route = new RouteDTO(
                            rs.getInt("RouteID"),
                            rs.getString("DepartureStationName"),
                            rs.getString("ArrivalStationName"),
                            rs.getInt("DepartureStationID"),
                            rs.getInt("ArrivalStationID")
                    );
                    routes.add(route);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a proper logger
        }
        return routes;
    }

     public RouteDTO getRouteById(int id) {
        String sql = "SELECT r.RouteID, r.DepartureStationID, r.ArrivalStationID, " +
                     "s1.StationName AS DepartureStationName, s2.StationName AS ArrivalStationName " +
                     "FROM Route r " +
                     "JOIN Station s1 ON r.DepartureStationID = s1.StationID " +
                     "JOIN Station s2 ON r.ArrivalStationID = s2.StationID where RouteID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, id);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                     RouteDTO route = new RouteDTO(
                            rs.getInt("RouteID"),
                            rs.getString("DepartureStationName"),
                            rs.getString("ArrivalStationName"),
                            rs.getInt("DepartureStationID"),
                            rs.getInt("ArrivalStationID")
                    );
                    return route;
                }
            }
        } catch (SQLException ex) {
           ex.printStackTrace();
        }
        return null;

    }

    // You can add other methods here if needed (e.g., getRouteById, addRoute, updateRoute, deleteRoute)
    // But for the TripController's current needs, getAllRoutes() is sufficient.

    @Override
    public RouteDTO get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<RouteDTO> list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void insert(RouteDTO t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(RouteDTO t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(RouteDTO t) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}