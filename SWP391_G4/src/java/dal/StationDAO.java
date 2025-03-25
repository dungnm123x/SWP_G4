package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dto.RailwayDTO;
import model.Station;

public class StationDAO extends DBContext<RailwayDTO> {

    // --- Existing Methods (Keep these!) ---
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

    @Deprecated // Keep the old method, but mark it as deprecated
    public List<Station> getStations() {
        return getAllStations(); // Or, if getStations() had different logic, keep that logic here
    }

    // --- New Methods for Pagination ---
    public List<Station> getStations(int page, int pageSize) {
        List<Station> stations = new ArrayList<>();
        String sql = "SELECT * FROM (SELECT *, ROW_NUMBER() OVER (ORDER BY StationID) as row_num FROM Station) AS x WHERE row_num BETWEEN ? AND ?;";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize + 1); // Correct start row
            ps.setInt(2, page * pageSize);          // Correct end row
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stations.add(new Station(
                            rs.getInt("StationID"),
                            rs.getString("StationName"),
                            rs.getString("Address")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return stations;
    }

    public int getTotalStationCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM Station";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total"); // Use alias for clarity
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return count;
    }
    //Added to check if station is in use before deleting

    public boolean isStationUsed(int stationID) {
    String sql = "SELECT COUNT(*) FROM Route WHERE DepartureStationID = ? OR ArrivalStationID = ?";
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, stationID);
        ps.setInt(2, stationID);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // Returns true if count > 0 (station is used)
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Log or handle the exception appropriately
        return true; // Consider a station in use in case of database error
    }
    return false; // Station not used, safe to delete
}

    public boolean isStationNameExist(String stationName, int stationId) {
    String sql = "SELECT COUNT(*) FROM Station WHERE StationName = ? AND StationID <> ?"; // Exclude current station
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, stationName);
        ps.setInt(2, stationId); // Exclude the station being edited
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0; // Return true if count > 0 (name exists)
            }
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Or use a logger
    }
    return false; // Assume no existence on error
}

public boolean isStationNameExist(String stationName) {
    //For adding new station, stationId is not exist yet.
    return isStationNameExist(stationName, 0); // Pass 0 for new station
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
