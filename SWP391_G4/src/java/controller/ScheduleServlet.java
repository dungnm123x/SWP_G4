/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.StationDAO;
import dal.TrainDAO;
import dal.TripDAO;
import dto.RailwayDTO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Station;
import model.Train;
import model.Trip;

/**
 *
 * @author Admin
 */
//@WebServlet(name = "ScheduleServlet", urlPatterns = {"/schedule"})
public class ScheduleServlet extends HttpServlet {

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
            out.println("<title>Servlet ScheduleServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ScheduleServlet at " + request.getContextPath() + "</h1>");
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
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        // 1. Lấy param
//        String depIDStr = request.getParameter("departureStationID");
//        String arrIDStr = request.getParameter("arrivalStationID");
//        String departureDay = request.getParameter("departureDay");
//        String tripType = request.getParameter("tripType");
//
//        // 2. Kiểm tra null, rỗng
//        if (depIDStr == null || arrIDStr == null || departureDay == null) {
//            // Lần đầu vào /schedule không có param => forward rỗng
//            request.getRequestDispatcher("schedule.jsp").forward(request, response);
//            return;
//        }
//
//        try {
//            int departureStationID = Integer.parseInt(depIDStr);
//            int arrivalStationID = Integer.parseInt(arrIDStr);
//
//            // Tìm các chuyến
//            TripDAO tripDAO = new TripDAO();
//            List<Trip> tripList = tripDAO.getTripsByRoute(departureStationID, arrivalStationID, departureDay);
//
//            // Nếu tripType=2 => khứ hồi => có returnDate => ...
//            // v.v.
//            // setAttribute(...) => forward
//            request.setAttribute("scheduleList", tripList);
//            // ...
//            request.getRequestDispatcher("schedule.jsp").forward(request, response);
//
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//            // Xử lý lỗi
//            response.sendRedirect("error.jsp");
//        }
//    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy param từ URL
        String depID = request.getParameter("departureStationID");  // Ga đi
        String arrID = request.getParameter("arrivalStationID");    // Ga đến
        String dDay = request.getParameter("departureDay");        // Ngày đi
        String tType = request.getParameter("tripType");            // 1 hoặc 2
        String rDate = request.getParameter("returnDate");          // Ngày về (có thể rỗng)

        String minDate = java.time.LocalDate.now().toString();
        request.setAttribute("minDate", minDate);
        // Lấy danh sách ga
        StationDAO stationDAO = new StationDAO();
        List<Station> gaList = stationDAO.getAllStations();
        request.setAttribute("gaList", gaList);

        // Nếu param chưa có hoặc rỗng => hiển thị form trống
        if (depID == null || depID.trim().isEmpty()
                || arrID == null || arrID.trim().isEmpty()
                || dDay == null || dDay.trim().isEmpty()) {

            // Gán null để searchtickets.jsp không hiển thị "selected"
            request.setAttribute("selectedDeparture", null);
            request.setAttribute("selectedArrival", null);
            request.setAttribute("selectedDate", null);
            request.setAttribute("selectedTicketType", null);
            request.setAttribute("returnDate", null);

            // Forward sang schedule.jsp (hiển thị form trống)
            request.getRequestDispatcher("schedule.jsp").forward(request, response);
            return;
        }

        try {
            // Chuyển depID, arrID sang int
            int departureStationID = Integer.parseInt(depID);
            int arrivalStationID = Integer.parseInt(arrID);

            java.time.LocalDate departureDay = java.time.LocalDate.parse(dDay);
            java.time.LocalDate today = java.time.LocalDate.now();

            if (departureDay.isBefore(today)) {
                request.setAttribute("errorMessage", "Ngày đi không được là quá khứ!");
                request.getRequestDispatcher("searchtickets.jsp").forward(request, response);
                return;
            }

            // Nếu là khứ hồi => kiểm tra ngày về
            if ("2".equals(tType) && rDate != null && !rDate.trim().isEmpty()) {
                java.time.LocalDate returnDay = java.time.LocalDate.parse(rDate);
                if (returnDay.isBefore(today) || returnDay.isBefore(departureDay)) {
                    request.setAttribute("errorMessage", "Ngày về phải >= ngày đi và không thuộc quá khứ!");
                    request.getRequestDispatcher("searchtickets.jsp").forward(request, response);
                    return;
                }
            }
                // Gọi DAO lấy danh sách chuyến
                TripDAO tripDAO = new TripDAO();
                List<Trip> tripList = tripDAO.getTripsByRoute(departureStationID, arrivalStationID, dDay);

                // Nếu tripType=2 => lấy thêm chuyến về
                List<Trip> returnTripList = null;
                if ("2".equals(tType) && rDate != null && !rDate.trim().isEmpty()) {
                    returnTripList = tripDAO.getTripsByRoute(arrivalStationID, departureStationID, rDate);
                }

                // Lưu tripList vào session (hoặc request)
                HttpSession session = request.getSession();
                session.setAttribute("scheduleList", tripList);
                session.setAttribute("returnScheduleList", returnTripList);

                // Lấy tên ga đi/đến để hiển thị
                String departureStationName = stationDAO.getStationNameById(departureStationID);
                String arrivalStationName = stationDAO.getStationNameById(arrivalStationID);

                session.setAttribute("departureStation", departureStationName);
                session.setAttribute("arrivalStation", arrivalStationName);
                session.setAttribute("departureDate", dDay);
                session.setAttribute("selectedTicketType", tType);
                session.setAttribute("returnDate", rDate);

                session.setAttribute("selectedDeparture", departureStationID);
                session.setAttribute("selectedArrival", arrivalStationID);
                session.setAttribute("selectedDate", dDay);
                session.setAttribute("selectedTicketType", tType);
                session.setAttribute("returnDate", rDate);

                // Forward sang schedule.jsp
                request.getRequestDispatcher("schedule.jsp").forward(request, response);

            }catch (NumberFormatException e) {
            e.printStackTrace();
            // Nếu parseInt lỗi => có thể redirect sang trang lỗi
            response.sendRedirect("error.jsp");
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
        protected void doPost
        (HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

            // Đọc param từ form searchtickets.jsp
            String depID = request.getParameter("departureStationID");
            String arrID = request.getParameter("arrivalStationID");
            String dDay = request.getParameter("departureDay");
            String tType = request.getParameter("tripType");
            String rDate = request.getParameter("returnDate");

            // Redirect sang doGet, kèm param
            String redirectURL = "schedule"
                    + "?departureStationID=" + URLEncoder.encode(depID == null ? "" : depID, "UTF-8")
                    + "&arrivalStationID=" + URLEncoder.encode(arrID == null ? "" : arrID, "UTF-8")
                    + "&departureDay=" + URLEncoder.encode(dDay == null ? "" : dDay, "UTF-8")
                    + "&tripType=" + URLEncoder.encode(tType == null ? "" : tType, "UTF-8")
                    + "&returnDate=" + URLEncoder.encode(rDate == null ? "" : rDate, "UTF-8");

            response.sendRedirect(redirectURL);
        }

        /**
         * Returns a short description of the servlet.
         *
         * @return a String containing servlet description
         */
        @Override
        public String getServletInfo
        
            () {
        return "Short description";
        }// </editor-fold>

    }
