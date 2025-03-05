/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.CarriageDAO;
import dal.RouteDAO;
import dal.SeatDAO;
import dal.TripDAO;
import dto.RailwayDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.List;
import model.Carriage;
import model.Seat;
import model.Trip;
import model.Train;

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

        try (PrintWriter out = response.getWriter()) {
            // Lấy param
            int trainID = Integer.parseInt(request.getParameter("trainID"));

            // Các param tìm kiếm để truyền ẩn vào form
            String departureStationID = request.getParameter("departureStationID");
            String arrivalStationID = request.getParameter("arrivalStationID");
            String departureDay = request.getParameter("departureDay");
            String tripType = request.getParameter("tripType");
            String returnDate = request.getParameter("returnDate");

            String isReturnParam = request.getParameter("isReturnTrip");
            CarriageDAO carriageDAO = new CarriageDAO();
            SeatDAO seatDAO = new SeatDAO();
            RouteDAO routeDAO = new RouteDAO();

            List<Carriage> carriages = carriageDAO.getCarriagesByTrainID(trainID);
            if (carriages.isEmpty()) {
                out.println("<p class='text-danger'>Không có toa nào cho tàu này.</p>");
                return;
            }

            // Lấy giá gốc (basePrice) từ Route
            double basePrice = routeDAO.getBasePriceByTrainID(trainID);
            DecimalFormat df = new DecimalFormat("#,##0.00");

            // Lấy Trip (để in trainName, v.v.)
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.getTripByTrainID(trainID);
            int theTripID = trip.getTripID();
            for (Carriage carriage : carriages) {
                out.println("<div class='carriage-container'>");
                out.println("<h5 class='carriage-header'>Toa " + carriage.getCarriageNumber()
                        + " (" + carriage.getCarriageType() + ")</h5>");
                out.println("<div class='seat-grid'>");

                List<Seat> seats = seatDAO.getSeatsByCarriageID(carriage.getCarriageID());
                for (Seat seat : seats) {
                    String seatStatus = (seat.getStatus() != null) ? seat.getStatus() : "Unknown";

                    if ("Booked".equalsIgnoreCase(seatStatus)) {
                        out.println("<button class='seat seat-booked' disabled>"
                                + seat.getSeatNumber() + "</button>");
                        continue;
                    }
                    // Xác định class CSS
                    String seatClass;
                    switch (seatStatus) {
                        case "Booked":
                            seatClass = "seat-booked";
                            break;
                        case "Reserved":
                            seatClass = "seat-reserved";
                            break;
                        case "Out of Service":
                            seatClass = "seat-outofservice";
                            break;
                        default:
                            seatClass = "seat-available";
                            break;
                    }

                    // Tính giá ghế
                    double seatPrice = basePrice;
                    if (carriage.getCarriageType().equalsIgnoreCase("Toa VIP")) {
                        seatPrice *= 1.3; // tăng 30% nếu VIP
                    }

                    out.println("<form action='cartitem' method='post' style='display:inline;'>");

                    // Thông tin vé
                    out.println("<input type='hidden' name='ticketID' value='" + seat.getSeatID() + "'/>");
                    out.println("<input type='hidden' name='trainName' value='" + trip.getTrain().getTrainName() + "'/>");
                    out.println("<input type='hidden' name='departureDate' value='" + trip.getDepartureTime() + "'/>");
                    out.println("<input type='hidden' name='carriageNumber' value='" + carriage.getCarriageNumber() + "'/>");
                    out.println("<input type='hidden' name='seatNumber' value='" + seat.getSeatNumber() + "'/>");
                    out.println("<input type='hidden' name='seatID' value='" + seat.getSeatID() + "'/>");
                    out.println("<input type='hidden' name='price' value='" + seatPrice + "'/>");
                    out.println("<input type='hidden' name='tripID' value='" + theTripID + "'/>");
                    if ("true".equalsIgnoreCase(isReturnParam)) {
                        out.println("<input type='hidden' name='isReturnTrip' value='true'/>");
                    } else {
                        out.println("<input type='hidden' name='isReturnTrip' value='false'/>");
                    }

                    // Thông tin tìm kiếm (giữ lại)
                    out.println("<input type='hidden' name='departureStationID' value='" + departureStationID + "'/>");
                    out.println("<input type='hidden' name='arrivalStationID' value='" + arrivalStationID + "'/>");
                    out.println("<input type='hidden' name='departureDay' value='" + departureDay + "'/>");
                    out.println("<input type='hidden' name='tripType' value='" + tripType + "'/>");
                    out.println("<input type='hidden' name='returnDate' value='" + (returnDate == null ? "" : returnDate) + "'/>");

                    // Nút submit
                    out.println("<button type='submit' class='seat " + seatClass + "'"
                            + " data-tooltip='Ghế " + seat.getSeatNumber() + " - " + seatStatus
                            + " - Giá: " + df.format(seatPrice) + " VND'>"
                            + seat.getSeatNumber()
                            + "</button>");

                    out.println("</form>");

                }
                out.println("</div>");
                out.println("</div>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            // In lỗi nếu muốn
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
        processRequest(request, response);
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
