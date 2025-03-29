package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import dal.DAOAdmin;
import dal.DashBoardDAO;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Feedback;
import model.User; // Import the User class

@WebServlet(name = "FeedbackController", value = "/feedback")
public class FeedbackController extends HttpServlet {

    private DAOAdmin feedbackDAO = new DAOAdmin();
    private DashBoardDAO dashboardDAo = new DashBoardDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        List<Feedback> feedbackList = null;
        try {
            feedbackList = dashboardDAo.getLatestFeedbacks(); // Or a method to get *all* feedback
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackController.class.getName()).log(Level.SEVERE, null, ex);
        }
        request.setAttribute("feedbackList", feedbackList);

        request.getRequestDispatcher("view/adm/feedbackForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String content = request.getParameter("content");
        int rating = Integer.parseInt(request.getParameter("rating"));

        Feedback feedback = new Feedback(user, content, rating); // Sử dụng User object từ session
        if (feedbackDAO.addFeedback(feedback)) {
            request.getSession().setAttribute("message3", "Cảm ơn bạn đã gửi phản hồi!");
            response.sendRedirect("feedback");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra khi gửi phản hồi. Vui lòng thử lại.");
            request.getRequestDispatcher("view/adm/feedbackForm.jsp").forward(request, response);
        }
    }
}
