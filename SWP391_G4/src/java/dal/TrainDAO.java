package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import dto.TrainDTO;
import model.Carriage;
import model.Train;
import java.sql.CallableStatement;

public class TrainDAO extends DBContext<TrainDTO> {

    // Get Train by ID (for editing)
    public Train getTrainById(int trainID) {
        Train train = null;
        String sql = "SELECT TrainID, TrainName FROM Train WHERE TrainID = ?";

        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    train = new Train(rs.getInt("TrainID"), rs.getString("TrainName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return train;
    }

    // Check if a train name already exists
    public boolean isTrainNameExist(String trainName) {
      String sql = "SELECT COUNT(*) FROM Train WHERE TrainName = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, trainName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if count > 0 (name exists)
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
        }
        return false; // Assume no existence on error
    }
    // Get Train name by ID.
    public String getTrainNameById(int trainID) {
        String trainName = null;
        String sql = "SELECT TrainName FROM Train WHERE TrainID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    trainName = rs.getString("TrainName");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trainName;
    }

    // Get ALL trains (for dropdowns) - NO pagination needed here
    public List<TrainDTO> getAllTrains() {
        List<TrainDTO> trains = new ArrayList<>();
        String sql = "SELECT t.TrainID, t.TrainName,"
                + "COUNT(c.CarriageID) AS TotalCarriages, COALESCE(SUM(c.Capacity), 0) AS TotalSeats "
                + "FROM Train t "
                + "LEFT JOIN Carriage c ON t.TrainID = c.TrainID "
                + "GROUP BY t.TrainID, t.TrainName";  // Corrected GROUP BY
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                TrainDTO train = new TrainDTO();
                train.setTrainID(rs.getInt("TrainID"));
                train.setTrainName(rs.getString("TrainName"));
                train.setTotalCarriages(rs.getInt("TotalCarriages"));
                train.setTotalSeats(rs.getInt("TotalSeats"));
                trains.add(train);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trains;
    }
     // Get paginated list of trains
    public List<TrainDTO> getTrains(int page, int pageSize) {
        List<TrainDTO> trains = new ArrayList<>();
        String sql = "SELECT * FROM (" +
                "SELECT t.TrainID, t.TrainName, " +
                "COUNT(c.CarriageID) AS TotalCarriages, COALESCE(SUM(c.Capacity), 0) AS TotalSeats, " +
                "ROW_NUMBER() OVER (ORDER BY t.TrainID) as row_num " + // Row number is essential
                "FROM Train t " +
                "LEFT JOIN Carriage c ON t.TrainID = c.TrainID " +
                "GROUP BY t.TrainID, t.TrainName" +
                ") as x WHERE row_num BETWEEN ? AND ?"; // Add pagination
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
             ps.setInt(1, (page -1 ) * pageSize + 1);
             ps.setInt(2, page * pageSize );
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                     TrainDTO train = new TrainDTO();
                    train.setTrainID(rs.getInt("TrainID"));
                    train.setTrainName(rs.getString("TrainName"));
                    train.setTotalCarriages(rs.getInt("TotalCarriages"));
                    train.setTotalSeats(rs.getInt("TotalSeats"));
                    trains.add(train);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Or use a logger
             return null;
        }
        return trains;
    }
     // Get total number of trains
    public int getTotalTrainsCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) AS total FROM Train";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    //Get train by id with total carriages and seats
     public TrainDTO getFullTrainInfoById(int trainID) {
        TrainDTO train = null;
        String sql = "SELECT t.TrainID, t.TrainName, "
                + "COUNT(c.CarriageID) AS TotalCarriages, COALESCE(SUM(c.Capacity), 0) AS TotalSeats "
                + "FROM Train t "
                + "LEFT JOIN Carriage c ON t.TrainID = c.TrainID "
                + "WHERE t.TrainID = ? " // Filter by TrainID
                + "GROUP BY t.TrainID, t.TrainName"; // Correct GROUP BY

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
             ps.setInt(1, trainID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    train = new TrainDTO();
                    train.setTrainID(rs.getInt("TrainID"));
                    train.setTrainName(rs.getString("TrainName"));
                    train.setTotalCarriages(rs.getInt("TotalCarriages"));
                    train.setTotalSeats(rs.getInt("TotalSeats"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Or use a logger.
        }
        return train;
    }

    // Add new Train (using Stored Procedure)
    public Train addTrain(Train train, int totalCarriages, int vipCarriages) {
        try (CallableStatement cstmt = connection.prepareCall("{? = call AddTrainWithCarriages(?, ?, ?)}")) {

            cstmt.registerOutParameter(1, java.sql.Types.INTEGER); // Register output parameter (TrainID)
            cstmt.setString(2, train.getTrainName());
            cstmt.setInt(3, totalCarriages);
            cstmt.setInt(4, vipCarriages);

            cstmt.execute(); // Execute the stored procedure

            // Get the returned TrainID
            int trainId = cstmt.getInt(1);
            train.setTrainID(trainId);
            return train;

        } catch (SQLException e) {
            e.printStackTrace(); // Log properly (use a logging framework in a real application)
            return null;
        }
    }


    // Update train
    public boolean updateTrain(Train train) {
        String sql = "UPDATE Train SET TrainName = ? WHERE TrainID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, train.getTrainName());
            ps.setInt(2, train.getTrainID());
            return ps.executeUpdate() > 0;  //Returns true if rows are updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
      // Check if a train is used in any trips
    public boolean isTrainUsedInTrips(int trainID) {
        String sql = "SELECT COUNT(*) FROM Trip WHERE TrainID = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, trainID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Returns true if count > 0 (train is used)
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or log the exception as needed
             return false;
        }
       
        return false; // Assume not used if there's an error
    }

    // Delete Train - Using Stored Procedure
      public boolean deleteTrain(int trainID) throws SQLException {
        try (CallableStatement cstmt = connection.prepareCall("{call DeleteTrainIfUnused(?)}")) {
            cstmt.setInt(1, trainID);
            cstmt.execute();
            return true; // Assume success.  The stored proc handles the check.
        } catch (SQLException e) {
            e.printStackTrace(); // Log properly
            return false;
        }
    }

    @Override
    public void insert(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet.");
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