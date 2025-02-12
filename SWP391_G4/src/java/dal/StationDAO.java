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

public class StationDAO extends DBContext<RailwayDTO> {

    public List<RailwayDTO> getAllStations() {
        List<RailwayDTO> stationList = new ArrayList<>();
        String sql = "SELECT * FROM Station";
        try (PreparedStatement stm = connection.prepareStatement(sql); ResultSet rs = stm.executeQuery()) {
            while (rs.next()) {
                stationList.add(new RailwayDTO(
                        0, 0, null, 0, 0,
                        0, 0, null, null,
                        rs.getInt("StationID"),
                        rs.getString("StationName"),
                        rs.getString("Address"),
                        0, 0, 0, 0, 0.0, 0, null, null, 0.0, 0, null, null, null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stationList;
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
