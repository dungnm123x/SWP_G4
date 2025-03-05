package controller.employee;

import dal.RuleDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.CategoryRule;
import model.Rule;

@WebServlet(name = "ManagerRuleListController", urlPatterns = {"/manager-rule-list"})
public class ManagerRuleListController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RuleDAO rd;

    @Override
    public void init() throws ServletException {
        rd = new RuleDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String sortByParam = request.getParameter("sortBy");
        String statusParam = request.getParameter("status");

        int sortBy = 3;
        if (sortByParam != null && !sortByParam.isEmpty()) {
            switch (sortByParam) {
                case "name_asc":
                    sortBy = 1;
                    break;
                case "name_desc":
                    sortBy = 2;
                    break;
                case "id_asc":
                    sortBy = 3;
                    break;
                case "id_desc":
                    sortBy = 4;
                    break;
            }
        }

        Boolean status = null;
        if (statusParam != null && !statusParam.isEmpty() && !"all".equalsIgnoreCase(statusParam)) {
            status = Boolean.parseBoolean(statusParam);
        }

        List<CategoryRule> categories = rd.getAllCategories();
        request.setAttribute("categories", categories);

        String ruleName = request.getParameter("key");
        String categoryIdParam = request.getParameter("categoryRuleID");
        String pageIndexParam = request.getParameter("page");
        String pageSizeParam = request.getParameter("pageSize");

        int categoryRuleID = parseIntOrDefault(categoryIdParam, 0);
        int pageIndex = parseIntOrDefault(pageIndexParam, 1);
        int pageSize = parseIntOrDefault(pageSizeParam, 5);
        int userID = 0; // Không lọc theo UserID

        List<Rule> rules = rd.searchAndPagingRule(ruleName, userID, categoryRuleID, status, sortBy, pageIndex, pageSize);
        int totalRules = rd.getSizeRule(ruleName,userID, categoryRuleID, status);
        int totalPages = (int) Math.ceil((double) totalRules / pageSize);

        request.setAttribute("rules", rules);
        request.setAttribute("totalRules", totalRules);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("currentPage", pageIndex);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("status", status);

        request.getRequestDispatcher("/view/employee/ManagerRuleList.jsp").forward(request, response);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return (value != null && !value.isEmpty()) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}