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

public class TrainDAO extends DBContext<RailwayDTO> {

    public RailwayDTO getTrainById(int trainID) {
        RailwayDTO train = null;
        String sql = "SELECT * FROM Train WHERE TrainID = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setInt(1, trainID);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    train = new RailwayDTO(
                            0, 0, null, trainID, 0,
                            0, 0, null, null,
                            0, null, null,
                            0, 0, 0, 0, 0.0,
                            0, null, null, 0.0,
                            0, null, null, null
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return train;
    }

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
            TrainDAO trainDAO = new TrainDAO();
            RailwayDTO train = trainDAO.getTrainById(1);
            if (train != null) {
                System.out.println("TrainID: " + train.getTrainID());
            } else {
                System.out.println("Không tìm thấy tàu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
