
package controller;

import dal.TrainDBContext;
import dto.TrainDTO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class TripFilterController extends HttpServlet {
    private TrainDBContext trainDB = new TrainDBContext();

    @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String departStation = request.getParameter("departStation");
    String arriveStation = request.getParameter("arriveStation");
    String departureDate = request.getParameter("departureDate");

    TrainDBContext db = new TrainDBContext();
    List<TrainDTO> trains;

    if (departStation == null && arriveStation == null && departureDate == null) {
        trains = db.getTrains();
    } else {
        trains = db.getFilteredTrains(departStation, arriveStation, departureDate);
    }

    request.setAttribute("trains", trains);
    request.getRequestDispatcher("view/employee/main.jsp").forward(request, response);
}
}