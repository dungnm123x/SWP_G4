package controller;

import dal.CarriageDAO;
import dal.TrainDAO;
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

    //Show list train
    private void listTrains(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<TrainDTO> trains = trainDAO.getAllTrains();
        request.setAttribute("trains", trains);
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
    }

      private void showEditForm(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    int trainID = Integer.parseInt(request.getParameter("id"));
    Train train = trainDAO.getTrainById(trainID);  // Get existing train
     // **IMPORTANT FIX:** Get the list of *all* trains for the table display
    List<TrainDTO> trains = trainDAO.getAllTrains();
    request.setAttribute("train", train); // Pass the Train object to the JSP
    request.setAttribute("trains", trains);     // The list of *all* trains for the table
    request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
}

   //Delete Train
    private void deleteTrain(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException, ServletException {
        int trainID = Integer.parseInt(request.getParameter("id"));
        // Check if the train is used in any trips *before* attempting deletion
        if (trainDAO.isTrainUsedInTrips(trainID)) {
            // Train is used, so do NOT delete.  Set an error message and forward back to the list.
            request.setAttribute("error", "Cannot delete train. It is currently used in one or more trips.");
            List<TrainDTO> trains = trainDAO.getAllTrains(); // Re-fetch the train list
            request.setAttribute("trains", trains);         // Make sure the train list is available
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response); // Go back to train management page
            return; // IMPORTANT:  Stop further processing!
        }

        boolean deleted = trainDAO.deleteTrain(trainID); // Use TrainDAO

        if (deleted) {
            // Redirect with a success message.
            response.sendRedirect("train?message=Train and associated carriages deleted successfully.");
        } else {
            // Forward with an error message.
            request.setAttribute("error", "Failed to delete train. It might have associated carriages or other dependencies.");
            List<TrainDTO> trains = trainDAO.getAllTrains();
            request.setAttribute("trains", trains);
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response); // Go back to the list, displaying the error.

        }
    }

    private void manageCarriages(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int trainID = Integer.parseInt(request.getParameter("id"));
        TrainDTO train = trainDAO.getFullTrainInfoById(trainID); // Get full info (needed for display)

        List<Carriage> carriages = carriageDAO.getCarByTrainID(trainID);
        request.setAttribute("train", train); // Use TrainDTO here
        request.setAttribute("carriages", carriages);
        request.getRequestDispatcher("view/employee/carriage_management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";  // Or some other default action
        }

        switch (action) {
            case "add":
                addTrain(request, response);
                break;
            case "update":
                updateTrain(request, response);
                break;
            case "addCarriage": // You'll likely need separate JSPs for carriage management
                addCarriage(request, response);
                break;
            case "updateCarriage":
                updateCarriage(request, response);
                break;
            case "deleteCarriage":
                deleteCarriage(request, response);
                break;
            default:
                listTrains(request, response); // Default action
        }
    }

    private void addTrain(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String trainName = request.getParameter("trainName");
        int totalCarriages = 0;
        int vipCarriages = 0;

        // Input validation (MUST be done)
        try {
            totalCarriages = Integer.parseInt(request.getParameter("totalCarriages"));
            vipCarriages = Integer.parseInt(request.getParameter("vipCarriages"));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format for carriages.");
             List<TrainDTO> trains = trainDAO.getAllTrains();
             request.setAttribute("trains", trains);
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
            return; // Stop processing
        }

        if (totalCarriages <= 0 || vipCarriages < 0 || vipCarriages > totalCarriages) {
            request.setAttribute("error", "Invalid carriage numbers.");
            List<TrainDTO> trains = trainDAO.getAllTrains();
            request.setAttribute("trains", trains);
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
            return; // Stop processing
        }

        // Check for duplicate train names *before* creating the Train object
        if (trainDAO.isTrainNameExist(trainName)) {
            request.setAttribute("error", "Train name already exists.");
            List<TrainDTO> trains = trainDAO.getAllTrains(); // Get the train list again
            request.setAttribute("trains", trains); // And put it back in the request
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
            return;  // Important: Stop processing here!
        }

        Train train = new Train(trainName);
        train = trainDAO.addTrain(train, totalCarriages, vipCarriages); // Use corrected addTrain

        if (train != null) {
            // Success: Redirect to the list page with a success message.
            response.sendRedirect("train?message=Train added successfully!");
        } else {
            // Failure: Set an error and forward back to the form.
            request.setAttribute("error", "Failed to add train.");
             List<TrainDTO> trains = trainDAO.getAllTrains();
             request.setAttribute("trains", trains);
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
        }
    }

    private void updateTrain(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {  // Add ServletException
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        String trainName = request.getParameter("trainName");

        // Check for duplicate names, but only if the name is actually changing
        Train existingTrain = trainDAO.getTrainById(trainID);
        if (existingTrain != null && !existingTrain.getTrainName().equals(trainName) && trainDAO.isTrainNameExist(trainName)) {
            request.setAttribute("error", "Train name already exists. Please choose a different name.");
            List<TrainDTO> trains = trainDAO.getAllTrains(); // Get the train list
            request.setAttribute("trains", trains);          // Put the list back in the request
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response); // Forward
            return; // IMPORTANT: Stop processing here
        }

        Train train = new Train(trainID, trainName);
        if(trainDAO.updateTrain(train)){
             response.sendRedirect("train?message=Train updated successfully");
        } else{
            request.setAttribute("error", "Update train failed!");
             List<TrainDTO> trains = trainDAO.getAllTrains();
             request.setAttribute("trains", trains);
            request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response); // Forward, don't redirect on error
        }


    }
      private void addCarriage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Train train = new Train(Integer.parseInt(request.getParameter("trainID")));
        String carriageType = request.getParameter("carriageType");
        int capacity = carriageType.equals("VIP") ? 12 : 10;
        String carriageNumber = request.getParameter("carriageNumber");

        Carriage carriage = new Carriage(carriageNumber, carriageType, train, capacity);
        carriageDAO.addCarriage(carriage);
        response.sendRedirect("train?action=manageCarriages&id=" + train.getTrainID());
    }

    private void updateCarriage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int carriageID = Integer.parseInt(request.getParameter("carriageID"));
        String carriageNumber = request.getParameter("carriageNumber");
        String carriageType = request.getParameter("carriageType");
        int capacity = carriageType.equals("VIP") ? 12 : 10;

        Carriage carriage = new Carriage(carriageID, carriageNumber, carriageType, capacity);
        carriageDAO.updateCarriage(carriage);
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        response.sendRedirect("train?action=manageCarriages&id=" + trainID);
    }

    private void deleteCarriage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int carriageID = Integer.parseInt(request.getParameter("carriageID"));
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        carriageDAO.deleteCarriage(carriageID);
        response.sendRedirect("train?action=manageCarriages&id=" + trainID);
    }

}