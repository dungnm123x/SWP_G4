package controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import dal.DAOAdmin;
import model.Feedback;
import model.User; // Import the User class

@WebServlet(name = "FeedbackController", value = "/feedback")
public class FeedbackController extends HttpServlet {

    private DAOAdmin feedbackDAO = new DAOAdmin();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if the user is logged in
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            // Redirect to login page or display an error message
            response.sendRedirect("login"); // Assuming "login" is your login servlet
            return; // Stop further processing
        }

        // Display the feedback form
        request.getRequestDispatcher("view/adm/feedbackForm.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if the user is logged in
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            // Redirect to login page or display an error message
            response.sendRedirect("login"); // Assuming "login" is your login servlet
            return; // Stop further processing
        }

        // Process and save feedback
        int userId = user.getUserId(); // Get UserID from session
        String content = request.getParameter("content");
        int rating = Integer.parseInt(request.getParameter("rating"));

        Feedback feedback = new Feedback(userId, content, rating);
        if (feedbackDAO.addFeedback(feedback)) {
            // Feedback added successfully
            request.getSession().setAttribute("message", "Cảm ơn bạn đã gửi phản hồi!");
            response.sendRedirect("home"); // Redirect to home or a thank you page
        } else {
            // Handle error
            request.setAttribute("error", "Có lỗi xảy ra khi gửi phản hồi. Vui lòng thử lại.");
            request.getRequestDispatcher("view/adm/feedbackForm.jsp").forward(request, response);
        }
    }
}