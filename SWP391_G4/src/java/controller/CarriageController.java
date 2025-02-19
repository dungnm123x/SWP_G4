package controller;

import dal.CarriageDAO;
import dal.TrainDAO;
import dal.TrainDBContext;
import dto.TrainDTO;
import model.Carriage;
import model.Train;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CarriageController extends HttpServlet {

    private CarriageDAO carriageDAO;
    private TrainDAO trainDAO;
    private TrainDBContext trainDB;

    @Override
    public void init() throws ServletException {
        carriageDAO = new CarriageDAO();
        trainDAO = new TrainDAO();
        trainDB = new TrainDBContext(); 
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        TrainDTO train = trainDB.getTrainById(trainID);

        if (action == null) {
            // Hiển thị danh sách toa tàu
            List<Carriage> carriages = carriageDAO.getCarByTrainID(trainID);
            request.setAttribute("train", train);
            request.setAttribute("carriages", carriages);
            request.getRequestDispatcher("view/employee/carriage_management.jsp").forward(request, response);
        } else if (action.equals("edit")) {
            // Chỉnh sửa toa tàu
            int carriageID = Integer.parseInt(request.getParameter("carriageID"));
            Carriage carriage = carriageDAO.getCarriageById(carriageID);
            request.setAttribute("carriage", carriage);
            doGet(request, response);  // Load lại trang với thông tin cập nhật
        } else if (action.equals("delete")) {
            // Xóa toa tàu
            int carriageID = Integer.parseInt(request.getParameter("carriageID"));
            boolean deleted = carriageDAO.deleteCarriage(carriageID);
            if (deleted) {
                request.setAttribute("message", "Xóa toa tàu thành công.");
            } else {
                request.setAttribute("error", "Xóa toa tàu thất bại.");
            }
            doGet(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        String carriageType = request.getParameter("carriageType");
        int seatCount = carriageType.equals("VIP") ? 12 : 10;

        if (action.equals("add")) {
            // Thêm toa tàu mới
            Carriage carriage = new Carriage();
            carriage.setTrain(new Train(trainID));  // Gán trainID
            carriage.setCarriageType(carriageType);
            carriage.setCapacity(seatCount);

            boolean added = carriageDAO.addCarriage(carriage);
            if (added) {
                request.setAttribute("message", "Thêm toa tàu thành công.");
            } else {
                request.setAttribute("error", "Thêm toa tàu thất bại.");
            }
        } else if (action.equals("update")) {
            // Cập nhật toa tàu
            int carriageID = Integer.parseInt(request.getParameter("carriageID"));
            Carriage carriage = new Carriage();
            carriage.setCarriageID(carriageID);
            carriage.setTrain(new Train(trainID));
            carriage.setCarriageType(carriageType);
            carriage.setCapacity(seatCount);

            boolean updated = carriageDAO.updateCarriage(carriage);
            if (updated) {
                request.setAttribute("message", "Cập nhật toa tàu thành công.");
            } else {
                request.setAttribute("error", "Cập nhật toa tàu thất bại.");
            }
        }

        doGet(request, response);
    }
}
