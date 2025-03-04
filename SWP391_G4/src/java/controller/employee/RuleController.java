package controller.employee;

import dal.RuleDAO;
import model.Rule;
import model.CategoryRule;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "RuleController", urlPatterns = {"/rule-list"})
public class RuleController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RuleDAO ruleDAO;

    @Override
    public void init() throws ServletException {
        ruleDAO = new RuleDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RuleController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RuleController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ruleName = request.getParameter("ruleName");
        String category_Id_Raw = request.getParameter("categoryId");
        
        int categoryId = 0;
        int index = 1;
        int pageSize = 5;
        try {
            if (category_Id_Raw != null && !category_Id_Raw.trim().isEmpty()) {
                categoryId = Integer.parseInt(category_Id_Raw.trim());
            }

            String indexParam = request.getParameter("index");
            if (indexParam != null && !indexParam.isEmpty()) {
                index = Integer.parseInt(indexParam);
            }

            String pageSizeParam = request.getParameter("pageSize");
            if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeParam);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int total_size = ruleDAO.getSizeRule(ruleName, categoryId);
        int num = (total_size == 0) ? 1 : (int) Math.ceil((double) total_size / pageSize);

        if (index > num) {
            index = num;
        }
        if (index < 1) {
            index = 1;
        }

        List<Rule> rules = ruleDAO.searchAndPagingRule(ruleName, categoryId, index, pageSize);       
        List<CategoryRule> categories = ruleDAO.getAllCategories()
                .stream()
                .filter(category -> category.isStatus()) // Chỉ giữ CategoryRule có status = true
                .collect(Collectors.toList());

        request.setAttribute("categories", categories);
        request.setAttribute("ruleList", rules);
        request.setAttribute("ruleName", ruleName);
        request.setAttribute("categoryId", categoryId);
        request.setAttribute("num", num);
        request.setAttribute("currentPage", index);
        request.setAttribute("total_size", total_size);
        request.setAttribute("start", (index - 1) * pageSize + 1);
        request.setAttribute("end", Math.min(index * pageSize, total_size));

        request.getRequestDispatcher("RuleList.jsp").forward(request, response);
        
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
