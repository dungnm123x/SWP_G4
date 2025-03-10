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
    private TrainDAO trainDAO; // Có thể không cần dùng trực tiếp trong controller này, tùy thuộc logic
    private TrainDBContext trainDB;

    @Override
    public void init() throws ServletException {
        carriageDAO = new CarriageDAO();
        trainDAO = new TrainDAO(); // Khởi tạo nếu cần
        trainDB = new TrainDBContext();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String action = request.getParameter("action");
            int trainID = Integer.parseInt(request.getParameter("trainID"));

            // Dùng getFullTrainInfoById để lấy đầy đủ thông tin, bao gồm cả số toa và số ghế
            TrainDTO train = trainDB.getFullTrainInfoById(trainID);

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
                request.setAttribute("train", train); // Đảm bảo train được set
                List<Carriage> carriages = carriageDAO.getCarByTrainID(trainID); // Lấy lại danh sách (tùy chọn)
                request.setAttribute("carriages", carriages);
                request.getRequestDispatcher("view/employee/carriage_management.jsp").forward(request, response);
            } else if (action.equals("delete")) {
                // Xóa toa tàu
                int carriageID = Integer.parseInt(request.getParameter("carriageID"));

                // Gọi stored procedure để xóa (cả ghế và toa)
                boolean deleted = carriageDAO.deleteCarriage(carriageID);

                if (deleted) {
                    request.setAttribute("message", "Xóa toa tàu thành công.");
                } else {
                    request.setAttribute("error", "Xóa toa tàu thất bại.");
                }
                response.sendRedirect("carriage?trainID=" + trainID); // Chuyển hướng về trang quản lý toa
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Lỗi: " + e.getMessage());
            request.getRequestDispatcher("view/employee/carriage_management.jsp").forward(request, response);
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    try {
        String action = request.getParameter("action");
        int trainID = Integer.parseInt(request.getParameter("trainID"));
        String carriageType = request.getParameter("carriageType");
        String carriageNumber = request.getParameter("carriageNumber");

        // Sửa lại phần xác định seatCount cho đúng với giá trị mới
        int seatCount = carriageType.equals("Toa VIP") ? 12 : 10;


        if (action.equals("add")) {

            Carriage carriage = new Carriage();
            carriage.setTrain(new Train(trainID));
            carriage.setCarriageType(carriageType); // Dùng trực tiếp giá trị từ request
            carriage.setCapacity(seatCount);  // Số ghế đã được tính toán ở trên
            carriage.setCarriageNumber(carriageNumber);


            boolean added = carriageDAO.addCarriage(carriage);
            if (added) {
                request.setAttribute("message", "Thêm toa tàu thành công.");
            } else {
                request.setAttribute("error", "Thêm toa tàu thất bại.");
            }


        } else if (action.equals("update")) {
            // Cập nhật toa
            int carriageID = Integer.parseInt(request.getParameter("carriageID"));
            Carriage carriage = new Carriage();
            carriage.setCarriageID(carriageID);
            carriage.setTrain(new Train(trainID));
            carriage.setCarriageType(carriageType);  // Dùng trực tiếp giá trị từ request
            carriage.setCapacity(seatCount);      // Số ghế đã được tính toán ở trên
            carriage.setCarriageNumber(carriageNumber);


            boolean updated = carriageDAO.updateCarriage(carriage);
            if (updated) {
                request.setAttribute("message", "Cập nhật toa tàu thành công.");
            } else {
                request.setAttribute("error", "Cập nhật toa tàu thất bại.");
            }

        }
        response.sendRedirect("carriage?trainID=" + trainID); // Chuyển hướng về trang quản lý toa
    } catch (NumberFormatException e) {
        request.setAttribute("error", "Lỗi: " + e.getMessage());
        request.getRequestDispatcher("view/employee/carriage_management.jsp").forward(request, response);
      }
    }
}