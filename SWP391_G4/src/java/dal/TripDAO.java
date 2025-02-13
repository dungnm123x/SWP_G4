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

public class TripDAO extends DBContext<RailwayDTO> {

    public List<RailwayDTO> getTripsByRoute(int departureStationID, int arrivalStationID, String departureDate) {
        List<RailwayDTO> tripList = new ArrayList<>();
        String sql = "SELECT * FROM Trip WHERE RouteID IN "
                + "(SELECT RouteID FROM Route WHERE DepartureStationID = ? AND ArrivalStationID = ?) "
                + "AND CONVERT(DATE, DepartureTime) = ?";
        
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, departureStationID);
            stm.setInt(2, arrivalStationID);
            stm.setString(3, departureDate);
            
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    tripList.add(new RailwayDTO(
                            0, 0, null, rs.getInt("TrainID"), 0,
                            0, 0, null, null,
                            0, null, null,
                            rs.getInt("RouteID"), departureStationID, arrivalStationID, 0, 0.0,
                            0, null, null, 0.0,
                            rs.getInt("TripID"),
                            rs.getString("DepartureTime"),
                            rs.getString("ArrivalTime"),
                            rs.getString("TripStatus")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tripList;
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

    public static void main(String[] args) {
        try {
            TripDAO tripDAO = new TripDAO();
            int departureStationID = 1; 
            int arrivalStationID = 2;   
            String departureDate = "2025-02-15"; 

            List<RailwayDTO> tripList = tripDAO.getTripsByRoute(departureStationID, arrivalStationID, departureDate);

            if (tripList.isEmpty()) {
                System.out.println("Không có chuyến tàu nào phù hợp.");
            } else {
                System.out.println("Danh sách chuyến tàu:");
                for (RailwayDTO trip : tripList) {
                    System.out.println("TripID: " + trip.getTripID()
                            + " | TrainID: " + trip.getTrainID()
                            + " | RouteID: " + trip.getRouteID()
                            + " | DepartureTime: " + trip.getDepartureTime()
                            + " | ArrivalTime: " + trip.getArrivalTime()
                            + " | Status: " + trip.getTripStatus());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

