package controller;

import dal.TicketDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Ticket;

import java.io.IOException;
import java.util.List;
import model.RailwayDTO;
import model.User;

@WebServlet(name = "TicketListServlet", urlPatterns = {"/ticket-list"})
public class TicketListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        // Nếu chưa có session hoặc chưa đăng nhập, lưu lại trang hiện tại rồi chuyển hướng đến login
        if (session == null || session.getAttribute("user") == null) {
            session = request.getSession(true);
            session.setAttribute("redirectAfterLogin", "ticket-list");
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userID = user.getUserId();

        TicketDAO ticketDAO = new TicketDAO();
        List<RailwayDTO> tickets = ticketDAO.getDetailedTicketsByUserID(userID);
        request.setAttribute("tickets", tickets);

        request.getRequestDispatcher("ticketList.jsp").forward(request, response);
    }
}