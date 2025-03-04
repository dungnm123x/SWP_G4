<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quy Định</title>
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" rel="stylesheet">
        <style>
            .rules-container {
                background: #fff;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }
            .category-title {
                background: #007bff;
                color: #fff;
                padding: 10px;
                border-radius: 5px;
                font-weight: bold;
                margin-bottom: 10px;
            }
            .rule-item {
                padding: 10px;
                border-bottom: 1px solid #ddd;
            }
            .rule-item a {
                text-decoration: none;
                color: #007bff;
            }
            .rule-item a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <jsp:include page="/navbar.jsp"></jsp:include>
        <div class="container mt-4 rules-container">
            <h3 class="text-primary font-weight-bold">CÁC QUY ĐỊNH</h3>
            <div class="list-group">
                <c:forEach var="category" items="${categories}" varStatus="status">
                    <!-- Category clickable link -->
                    <div class="category-title">
                        <a href="categoryRule-detail?categoryRuleID=${category.categoryRuleID}" style="color: white;">
                            ${status.index + 1}. ${category.categoryRuleName}
                        </a>
                    </div>
                    <c:set var="ruleCounter" value="1" />
                    <c:forEach var="rule" items="${ruleList}">
                        <c:if test="${rule.categoryRuleID == category.categoryRuleID}">
                            <div class="rule-item">
                                <strong>
                                    <a href="rule-details?ruleID=${rule.ruleID}">
                                        ${status.index + 1}.${ruleCounter} ${rule.title}
                                    </a>
                                </strong><br>
                                <c:set var="ruleCounter" value="${ruleCounter + 1}" />
                            </div>
                        </c:if>
                    </c:forEach>
                </c:forEach>
            </div>
        </div>
    </body>
</html>
