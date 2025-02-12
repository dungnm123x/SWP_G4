/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import dal.CarriageDAO;
import dal.SeatDAO;
import dto.RailwayDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Carriage;
import model.Seat;

/**
 *
 * @author Admin
 */
//@WebServlet(name = "GetCarriageSeatsServlet", urlPatterns = {"/getcarriageseats"})
public class GetCarriageSeatsServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet GetCarriageSeatsServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet GetCarriageSeatsServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        int trainID = Integer.parseInt(request.getParameter("trainID"));
        CarriageDAO carriageDAO = new CarriageDAO();
        SeatDAO seatDAO = new SeatDAO();

        try (PrintWriter out = response.getWriter()) {
            List<RailwayDTO> carriages = carriageDAO.getCarriagesByTrainID(trainID);
            if (carriages.isEmpty()) {
                out.println("<p class='text-danger'>Không có toa nào cho tàu này.</p>");
                return;
            }

            for (RailwayDTO carriage : carriages) {
                out.println("<div class='carriage-container'>");
                out.println("<h5 class='carriage-header'>Toa " + carriage.getCarriageNumber() + " (" + carriage.getCarriageType() + ")</h5>");
                out.println("<div class='seat-grid'>");

                List<RailwayDTO> seats = seatDAO.getSeatsByCarriageID(carriage.getCarriageID());
                for (RailwayDTO seat : seats) {
                    String seatClass = "seat-available";

                    switch (seat.getSeatStatus()) {
                        case "Booked":
                            seatClass = "seat-booked";
                            break;
                        case "Reserved":
                            seatClass = "seat-reserved";
                            break;
                        case "Out of Service":
                            seatClass = "seat-outofservice";
                            break;
                    }

                    out.println("<div class='seat " + seatClass + "'>"
                            + "Ghế " + seat.getSeatNumber() + " - " + seat.getSeatStatus()
                            + " (" + seat.getSeatType() + ")</div>");
                }

                out.println("</div>");
                out.println("</div>");
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
