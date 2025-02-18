package controller;

import dal.CarriageDAO;
import dal.TrainDAO;
import dal.TrainDBContext;
import dto.TrainDTO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Carriage;
import model.Train;

public class TrainController extends HttpServlet {

    private TrainDBContext trainDB = new TrainDBContext();
    private TrainDAO trainDAO = new TrainDAO();
    private CarriageDAO carriageDAO = new CarriageDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            listTrains(request, response);
        } else {
            switch (action) {
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete": {
                    try {
                        deleteTrain(request, response);
                    } catch (SQLException ex) {
                        Logger.getLogger(TrainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;

                case "manageCarriages":
                    manageCarriages(request, response);
                    break;
                default:
                    listTrains(request, response);
                    break;
            }
        }
    }

    private void listTrains(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<TrainDTO> trains = trainDAO.getAllTrains();
        request.setAttribute("trains", trains);
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int trainID = Integer.parseInt(request.getParameter("id"));
        TrainDTO train = trainDB.getTrainById(trainID);
        request.setAttribute("train", train);
        listTrains(request, response);
    }

    private void deleteTrain(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {
        int trainID = Integer.parseInt(request.getParameter("id"));
        trainDAO.deleteTrain(trainID);
        response.sendRedirect("train");
    }

    private void manageCarriages(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int trainID = Integer.parseInt(request.getParameter("id"));
        TrainDTO train = trainDB.getTrainById(trainID);
        List<Carriage> carriages = carriageDAO.getCarByTrainID(trainID);
        request.setAttribute("train", train);
        request.setAttribute("carriages", carriages);
        request.getRequestDispatcher("view/employee/carriage_management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        switch (action) {
            case "add":
                addTrain(request, response);
                break;
            case "update":
                updateTrain(request, response);
                break;
            case "addCarriage":
                addCarriage(request, response);
                break;
            case "updateCarriage":
                updateCarriage(request, response);
                break;
            case "deleteCarriage":
                deleteCarriage(request, response);
                break;
        }
    }

    private void addTrain(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String trainName = request.getParameter("trainName");
        Train train = new Train(trainName);
        trainDAO.addTrain(train);
        response.sendRedirect("train");
    }

    private void updateTrain(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        String trainName = request.getParameter("trainName");
        Train train = new Train(trainID, trainName);
        trainDAO.updateTrain(train);
        response.sendRedirect("train");
    }

    private void addCarriage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Train train = new Train(Integer.parseInt(request.getParameter("trainID")));
        String carriageType = request.getParameter("carriageType");
        int capacity = carriageType.equals("VIP") ? 12 : 10;
        String carriageNumber =request.getParameter("carriageNumber");

        Carriage carriage = new Carriage(carriageNumber, carriageType,train,capacity);
        carriageDAO.addCarriage(carriage);
        response.sendRedirect("train?action=manageCarriages&id=" + train.getTrainID());
    }

    private void updateCarriage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int carriageID = Integer.parseInt(request.getParameter("carriageID"));
        String carriageNumber = request.getParameter("carriageNumber");
        String carriageType = request.getParameter("carriageType");
        int capacity = carriageType.equals("VIP") ? 12 : 10;

        Carriage carriage = new Carriage(carriageID, carriageNumber, carriageType, capacity);
        carriageDAO.updateCarriage(carriage);
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        response.sendRedirect("train?action=manageCarriages&id=" + trainID);
    }

    private void deleteCarriage(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int carriageID = Integer.parseInt(request.getParameter("carriageID"));
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        carriageDAO.deleteCarriage(carriageID);
        response.sendRedirect("train?action=manageCarriages&id=" + trainID);
    }

}
