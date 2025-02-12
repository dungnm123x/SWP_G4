package dal;

import dal.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import dto.TrainDTO;
import java.sql.*;

public class TrainDBContext extends DBContext<TrainDTO> {

    public ArrayList<TrainDTO> getTrains() {
        ArrayList<TrainDTO> trains = new ArrayList<>();
        PreparedStatement stm = null;
        try {
            String sql = "SELECT t.TrainID AS id, MIN(t.TrainName) AS tentau, "
                   + "COUNT(DISTINCT c.CarriageID) AS tongtoa, COUNT(s.SeatID) AS tongghe, "
                   + "MIN(st2.StationName) AS gadi, MIN(st.StationName) AS gaden, "
                   + "MIN(tr.DepartureTime) AS xuatphat, MIN(tr.ArrivalTime) AS dennoi, "
                   + "MIN(r.BasePrice) AS giave "
                   + "FROM Train t "
                   + "JOIN Carriage c ON t.TrainID = c.TrainID "
                   + "JOIN Seat s ON c.CarriageID = s.CarriageID "
                   + "JOIN Trip tr ON tr.TrainID = t.TrainID "
                   + "JOIN Route r ON r.RouteID = tr.RouteID "
                   + "JOIN Station st ON st.StationID = r.ArrivalStationID "
                   + "JOIN Station st2 ON st2.StationID = r.DepartureStationID "
                   + "GROUP BY t.TrainID";
        
            stm = connection.prepareStatement(sql);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                TrainDTO train = new TrainDTO( rs.getInt("id"),
                    rs.getString("tentau"),
                    rs.getInt("tongtoa"),
                    rs.getInt("tongghe"),
                    rs.getString("gadi"),
                    rs.getString("gaden"),
                    rs.getDate("xuatphat"),
                    rs.getDate("dennoi"),
                    rs.getDouble("giave"));
                trains.add(train);

            }
        } catch (SQLException ex) {
            Logger.getLogger(TrainDBContext.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stm != null) {
                    stm.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TrainDBContext.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return trains;
    }

    @Override
    public void insert(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(TrainDTO model) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
