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
import model.User;

public class TrainController extends HttpServlet {

    private TrainDAO trainDAO = new TrainDAO();
    private CarriageDAO carriageDAO = new CarriageDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getRoleID() != 1 && user.getRoleID() != 2) {
            response.sendRedirect("login");
            return;
        }
        if (action == null) {
            action = "list"; // Set a default action
        }
        

        switch (action) {
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                try {
                    deleteTrain(request, response);
                } catch (SQLException ex) {
                    // Handle the SQLException appropriately, e.g., log it and show an error page.
                    Logger.getLogger(TrainController.class.getName()).log(Level.SEVERE, null, ex);
                    request.setAttribute("error", "A database error occurred: " + ex.getMessage());
                    listTrains(request, response); // Show list with error

                }
                break;
            case "manageCarriages":
                manageCarriages(request, response);
                break;
            default:  // Including "list"
                listTrains(request, response);
                break;
        }
    }

    private void listTrains(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Pagination Logic
        int page = 1; // Default page
        int pageSize = 10; // Default page size (you can make this configurable)

        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                // Handle invalid page number.  For this example, we'll just default to 1.
                // In a real app, you might redirect to an error page or show a message.
                page = 1;
            }
        }

        List<TrainDTO> trains = trainDAO.getTrains(page, pageSize); // Get paginated list
        int totalTrains = trainDAO.getTotalTrainsCount();         // Get total count
        int totalPages = (int) Math.ceil((double) totalTrains / pageSize);

        request.setAttribute("trains", trains);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize); // Pass pageSize too, useful in JSP.

        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
    }


    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int trainID = Integer.parseInt(request.getParameter("id"));
        Train train = trainDAO.getTrainById(trainID);  // Get existing train
         // Get the list of trains to display in table
        List<TrainDTO> trains = trainDAO.getAllTrains();
        request.setAttribute("trains", trains);
        request.setAttribute("train", train); // Pass the Train object to the JSP
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
    }

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
    TrainDTO train = trainDAO.getFullTrainInfoById(trainID); // Use getFullTrainInfoById

    List<Carriage> carriages = carriageDAO.getCarByTrainID(trainID);
    request.setAttribute("train", train);
    request.setAttribute("carriages", carriages);
    request.getRequestDispatcher("view/employee/carriage_management.jsp").forward(request, response);
}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if(action == null) { //good practice
            action = "list";
        }

        switch (action) {
            case "add":
                addTrain(request, response);
                break;
            case "update":
                updateTrain(request, response);
                break;
            // Other cases (addCarriage, updateCarriage, deleteCarriage) remain the same
             case "addCarriage":
                addCarriage(request, response);
                break;
            case "updateCarriage":
                updateCarriage(request, response);
                break;
            case "deleteCarriage":
                deleteCarriage(request, response);
                break;
            default: // Good practice to have a default
                listTrains(request,response); // Or some other default action
                break;

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
        request.setAttribute("error", "Tên đã tồn tại.");
         List<TrainDTO> trains = trainDAO.getAllTrains(); // Get the train list again
        request.setAttribute("trains", trains); // And put it back in the request
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
        return;  // Important: Stop processing here!
    }
    if (trainName == null || trainName.trim().isEmpty() ) {
        request.setAttribute("error", "Tên tàu không thể để trống.");
        List<TrainDTO> trains = trainDAO.getAllTrains(); // Lấy lại danh sách tàu
        request.setAttribute("trains", trains); // Gửi lại danh sách tàu
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
        return;  // DỪNG xử lý nếu tên không hợp lệ
    }

    Train train = new Train(trainName);
    train = trainDAO.addTrain(train, totalCarriages, vipCarriages); // Use corrected addTrain

    if (train != null) {
        // Success: Redirect to the list page with a success message.
        response.sendRedirect("train?message=Train added successfully!");
    } else {
        // Failure: Set an error and forward back to the form.
        request.setAttribute("error", "Thêm tàu thất bại.");
          List<TrainDTO> trains = trainDAO.getAllTrains();
         request.setAttribute("trains", trains);
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
    }
}
private void updateTrain(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    int trainID = Integer.parseInt(request.getParameter("trainID"));
    String trainName = request.getParameter("trainName");

    // Check for duplicate, but ONLY if the name has changed!
    Train existingTrain = trainDAO.getTrainById(trainID);
    if (existingTrain != null && !existingTrain.getTrainName().equals(trainName) && trainDAO.isTrainNameExist(trainName)) {
        request.setAttribute("error", "Train name already exists. Please choose a different name.");
         List<TrainDTO> trains = trainDAO.getAllTrains(); // Get the train list
        request.setAttribute("trains", trains);          // Put the list back in the request
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response); // Forward!
        return;  // IMPORTANT: Stop processing here!
    }
    if (trainName == null || trainName.trim().isEmpty() ) {
        request.setAttribute("error", "Tên tàu không thể để trống.");
        List<TrainDTO> trains = trainDAO.getAllTrains(); // Lấy lại danh sách tàu
        request.setAttribute("trains", trains); // Gửi lại danh sách tàu
        request.getRequestDispatcher("view/employee/train_management.jsp").forward(request, response);
        return;  // DỪNG xử lý nếu tên không hợp lệ
    }

        Train train = new Train(trainID, trainName); // Create Train object for update
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