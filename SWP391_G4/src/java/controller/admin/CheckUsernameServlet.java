import dal.DAOAdmin;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "CheckUsernameServlet", value = "/checkUsername")
public class CheckUsernameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        DAOAdmin dao = new DAOAdmin();
        boolean exists = false;
        try {
            exists = dao.isUsernameTaken(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{\"exists\": " + exists + "}");
        out.flush();
    }
}