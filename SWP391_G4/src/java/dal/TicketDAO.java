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

public class TicketDAO extends DBContext<RailwayDTO> {

    public List<RailwayDTO> searchTicketByCCCD(String cccd) {
        List<RailwayDTO> tickets = new ArrayList<>();
        String sql = "SELECT * FROM Ticket WHERE CCCD = ?";
        try (PreparedStatement stm = connection.prepareStatement(sql)) {
            stm.setString(1, cccd);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new RailwayDTO(
                            0, 0, null, 0, 0,
                            0, 0, null, null,
                            0, null, null,
                            0, 0, 0, 0, 0.0,
                            rs.getInt("TicketID"),
                            rs.getString("CCCD"),
                            rs.getString("TicketStatus"),
                            rs.getDouble("TicketPrice"),
                            0, null, null, null
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
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

