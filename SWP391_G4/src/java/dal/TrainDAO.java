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
import java.sql.CallableStatement; // **QUAN TRỌNG: Thêm import này**

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

     public Train addTrain(Train train, int totalCarriages, int vipCarriages) {
        try (CallableStatement cstmt = connection.prepareCall("{? = call AddTrainWithCarriages(?, ?, ?)}")) { // Correct syntax

            cstmt.registerOutParameter(1, java.sql.Types.INTEGER); // Register the output parameter
            cstmt.setString(2, train.getTrainName());
            cstmt.setInt(3, totalCarriages);
            cstmt.setInt(4, vipCarriages);

            cstmt.execute(); // Execute the stored procedure

            // Retrieve the returned TrainID from the output parameter
            int trainId = cstmt.getInt(1);  // Get the output parameter (TrainID)
            train.setTrainID(trainId);     // Set the TrainID in the Train object
            return train; // Return the Train object

        } catch (SQLException e) {
            e.printStackTrace();  // Log the exception (use a logger in production)
            return null;        // Indicate failure
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

    // Delete Train
    public boolean deleteTrain(int trainID) throws SQLException {
        String deleteCarriages = "DELETE FROM Carriage WHERE TrainID = ?";
        String deleteTrain = "DELETE FROM Train WHERE TrainID = ?";
        connection.setAutoCommit(false);  // Start a transaction
        try (PreparedStatement ps1 = connection.prepareStatement(deleteCarriages);
             PreparedStatement ps2 = connection.prepareStatement(deleteTrain)) {

            ps1.setInt(1, trainID);
            ps1.executeUpdate(); // Delete carriages first (due to foreign key constraint)

            ps2.setInt(1, trainID);
            ps2.executeUpdate(); // Then delete the train

            connection.commit(); // Commit the transaction
            return true;

        } catch (SQLException e) {
            connection.rollback(); // Rollback if error
            e.printStackTrace();
            return false;
        } finally {
             connection.setAutoCommit(true); // Restore auto commit
        }
    }

   //getList
    @Override
    public ArrayList<TrainDTO> list() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    //get
    @Override
    public TrainDTO get(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

}