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
            // 1) Đọc tripID (thay vì trainID)
            String tripIDStr = request.getParameter("tripID");
            if (tripIDStr == null || tripIDStr.isEmpty()) {
                out.println("<p class='text-danger'>Thiếu tripID!</p>");
                return;
            }
            int tripID = Integer.parseInt(tripIDStr);

            // 2) Lấy các param tìm kiếm (nếu cần giữ lại)
            String departureStationID = request.getParameter("departureStationID");
            String arrivalStationID = request.getParameter("arrivalStationID");
            String departureDay = request.getParameter("departureDay");
            String tripType = request.getParameter("tripType");
            String returnDate = request.getParameter("returnDate");
            String isReturnParam = request.getParameter("isReturnTrip");

            // 3) Lấy thông tin Trip từ DB
            TripDAO tripDAO = new TripDAO();
            Trip trip = tripDAO.getTripByID(tripID);
            if (trip == null) {
                out.println("<p class='text-danger'>Không tìm thấy chuyến này (tripID=" + tripID + ").</p>");
                return;
            }

            // 4) Từ trip => lấy trainID để load Carriage
            int trainID = trip.getTrain().getTrainID();

            CarriageDAO carriageDAO = new CarriageDAO();
            List<Carriage> carriages = carriageDAO.getCarriagesByTrainID(trainID);
            if (carriages.isEmpty()) {
                out.println("<p class='text-danger'>Không có toa nào cho tàu " + trip.getTrain().getTrainName() + ".</p>");
                return;
            }

            // 5) Tính giá gốc basePrice
            //   - Nếu getTripByID() đã JOIN Route => có trip.getRoute().getBasePrice()
            //   - Hoặc bạn có thể gọi routeDAO.getBasePriceByTrainID(trainID) tuỳ logic
            double basePrice;
            if (trip.getRoute() != null) {
                basePrice = trip.getRoute().getBasePrice();
            } else {
                // fallback
                RouteDAO routeDAO = new RouteDAO();
                basePrice = routeDAO.getBasePriceByTrainID(trainID);
            }
            DecimalFormat df = new DecimalFormat("#,##0");

            // 6) In giao diện: lặp qua các toa => lặp ghế => in form
            SeatDAO seatDAO = new SeatDAO();

            for (Carriage carriage : carriages) {
                out.println("<div class='carriage-container'>");
                out.println("<h5 class='carriage-header'>Toa " + carriage.getCarriageNumber()
                        + " (" + carriage.getCarriageType() + ")</h5>");
                out.println("<div class='seat-grid'>");

                List<Seat> seats = seatDAO.getSeatsForTrip(carriage.getCarriageID(), tripID);

                for (Seat seat : seats) {
                    String seatStatus = (seat.getStatus() != null) ? seat.getStatus() : "Unknown";

                    // Nếu ghế đã Booked => disable
                    if ("Booked".equalsIgnoreCase(seatStatus)) {
                        out.println("<button class='seat seat-booked' disabled "
                                + "data-tooltip='Ghế " + seat.getSeatNumber() + " - Đã Booked'>"
                                + seat.getSeatNumber() + "</button>");
                        continue;
                    }

                    // Chọn class CSS
                    String seatClass;
                    switch (seatStatus) {
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
                        seatPrice *= 1.3; // Tăng 30% nếu VIP
                    }

                    // 7) In form => khi submit => CartServlet
                    out.println("<form action='cartitem' method='post' style='display:inline;'>");

                    // Gửi tripID để CartServlet biết chuyến nào
                    out.println("<input type='hidden' name='tripID' value='" + tripID + "'/>");

                    // Thông tin vé
                    out.println("<input type='hidden' name='ticketID' value='" + seat.getSeatID() + "'/>");
                    out.println("<input type='hidden' name='trainName' value='" + trip.getTrain().getTrainName() + "'/>");
                    out.println("<input type='hidden' name='departureDate' value='" + trip.getDepartureTime() + "'/>");
                    out.println("<input type='hidden' name='carriageNumber' value='" + carriage.getCarriageNumber() + "'/>");
                    out.println("<input type='hidden' name='seatNumber' value='" + seat.getSeatNumber() + "'/>");
                    out.println("<input type='hidden' name='seatID' value='" + seat.getSeatID() + "'/>");
                    out.println("<input type='hidden' name='price' value='" + seatPrice + "'/>");

                    // isReturnTrip
                    if ("true".equalsIgnoreCase(isReturnParam)) {
                        out.println("<input type='hidden' name='isReturnTrip' value='true'/>");
                    } else {
                        out.println("<input type='hidden' name='isReturnTrip' value='false'/>");
                    }

                    // Thông tin tìm kiếm (giữ lại nếu cần quay lại schedule)
                    out.println("<input type='hidden' name='departureStationID' value='" + departureStationID + "'/>");
                    out.println("<input type='hidden' name='arrivalStationID' value='" + arrivalStationID + "'/>");
                    out.println("<input type='hidden' name='departureDay' value='" + departureDay + "'/>");
                    out.println("<input type='hidden' name='tripType' value='" + tripType + "'/>");
                    out.println("<input type='hidden' name='returnDate' value='" + (returnDate == null ? "" : returnDate) + "'/>");

                    // Nút submit
                    out.println("<button type='submit' class='seat " + seatClass + "' "
                            + "data-tooltip='Ghế " + seat.getSeatNumber() + " - " + seatStatus
                            + " - Giá: " + df.format(seatPrice) + " VND'>"
                            + seat.getSeatNumber()
                            + "</button>");

                    out.println("</form>");
                }

                out.println("</div>"); // .seat-grid
                out.println("</div>"); // .carriage-container
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
