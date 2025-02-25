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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("schedule.jsp").forward(request, response);
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
        try {
            int departureStationID = Integer.parseInt(request.getParameter("diemdi"));
            int arrivalStationID = Integer.parseInt(request.getParameter("diemden"));
            String departureDate = request.getParameter("ngaydi");
            String ticketType = request.getParameter("loaive");

            StationDAO stationDAO = new StationDAO();
            String departureStationName = stationDAO.getStationNameById(departureStationID);
            String arrivalStationName = stationDAO.getStationNameById(arrivalStationID);
            List<RailwayDTO> gaList = stationDAO.getAllStations(); // Lấy danh sách Ga
            request.setAttribute("gaList", gaList);

            request.setAttribute("departureStation", departureStationName);
            request.setAttribute("arrivalStation", arrivalStationName);
            request.setAttribute("departureDate", departureDate);

            TripDAO tripDAO = new TripDAO();
            List<RailwayDTO> tripList = tripDAO.getTripsByRoute(departureStationID, arrivalStationID, departureDate);

            TrainDAO trainDAO = new TrainDAO();
            Map<Integer, String> trainNames = new HashMap<>();
            for (RailwayDTO trip : tripList) {
                String trainName = trainDAO.getTrainNameById(trip.getTrainID());
                trainNames.put(trip.getTripID(), trainName);
            }

            // Truyền lại dữ liệu form tìm kiếm về JSP để giữ lại giá trị đã nhập
            request.setAttribute("selectedDeparture", departureStationID);
            request.setAttribute("selectedArrival", arrivalStationID);
            request.setAttribute("selectedDate", departureDate);
            request.setAttribute("selectedTicketType", ticketType);

            request.setAttribute("scheduleList", tripList);
            request.setAttribute("trainNames", trainNames);

            request.getRequestDispatcher("schedule.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
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
