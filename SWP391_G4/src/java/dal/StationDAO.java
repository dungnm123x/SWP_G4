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
import model.Station;

public class StationDAO extends DBContext<RailwayDTO> {

    public List<Station> getAllStations() {
        List<Station> stationList = new ArrayList<>();
        String sql = "SELECT * FROM Station";
        try (PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                stationList.add(new Station(
                      rs.getInt("StationID"),
                        rs.getString("StationName"), 
                        rs.getString("Address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stationList;
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM Station");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                stations.add(new Station(rs.getInt("StationID"), rs.getString("StationName"), rs.getString("Address")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;
    }

    public String getStationNameById(int stationID) {
        String stationName = null;
        String sql = "SELECT StationName FROM Station WHERE StationID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, stationID);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    stationName = rs.getString("StationName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stationName;
    }

    public Station getStationById(int stationID) {
        String sql = "SELECT * FROM Station WHERE StationID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, stationID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Station(
                            rs.getInt("StationID"),
                            rs.getString("StationName"),
                            rs.getString("Address")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addStation(Station station) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Station (StationName, Address) VALUES (?, ?)");
            ps.setString(1, station.getStationName());
            ps.setString(2, station.getAddress());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStation(Station station) {
        String sql = "UPDATE Station SET StationName = ?, Address = ? WHERE StationID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, station.getStationName());
            ps.setString(2, station.getAddress());
            ps.setInt(3, station.getStationID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStation(int stationID) {
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM Station WHERE StationID=?");
            ps.setInt(1, stationID);
            ps.executeUpdate();
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
    