<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter" %> <%-- Not strictly needed if using formatted getters --%>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lí chuyến</title>
        <link rel="stylesheet" href="css/employee.css">
        <style>
            /* Add some basic styling for pagination */
            .pagination {
                margin-top: 20px;
                display:flex;
                justify-content: center;
            }

            .pagination a, .pagination span {
                padding: 8px 12px;
                margin: 0 3px;
                border: 1px solid #ddd;
                text-decoration: none;
                color: black;
                background-color: white;
            }

            .pagination a:hover {
                background-color: #ddd;
            }

            .pagination span { /* Style for the current page */
                font-weight: bold;
                background-color: #4CAF50; /* Or your preferred color */
                color: white;
                border-color: #4CAF50;
            }
            .error-message{
                color: red;
            }
            .admin-back-button {
                display: inline-block;
                padding: 10px 20px;
                background-color: #2C3E50;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                border: none;
                font-size: 16px;
                transition: background-color 0.3s ease
            }

            .admin-back-button:hover {
                background-color: #00509E;
            }
            .admin-back-button i {
                margin-right: 8px;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <%--  Sidebar (assuming you have this) --%>
            <c:if test="${sessionScope.user.roleID == 2}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="avatar">
                    </div>
                    <ul>
                        <li><a href="train">Quản lý tàu</a></li>
                        <li><a href="trip">Quản lý chuyến</a></li>
                        <li><a href="route">Quản lý tuyến tàu</a></li>
                        <li><a href="station">Quản lý ga</a></li>
                        <li><a href="order">Quản lý hóa đơn</a></li>
                        <li><a href="category-blog">Quản lý tiêu đề Blog</a></li>
                        <li><a href="posts-list">Quản lý Blog</a></li>
                        <li><a href="category-rule">Quản lý tiêu đề quy định</a></li>
                        <li><a href="manager-rule-list">Quản lý quy định</a></li>
                        <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                    </ul>
                    <form action="logout" method="GET">
                        <button type="submit" class="logout-button">Logout</button>
                    </form>
                </div>
            </c:if>
            <c:if test="${sessionScope.user.roleID == 1}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="trainpicture">
                    </div>
                    <ul class="menu">
                        <li><a href="admin?view=dashboard">Dashboard</a></li>
                        <li><a href="admin?view=employees">Quản lý nhân viên</a></li>
                        <li><a href="admin?view=customers">Quản lý khách hàng</a></li>
                            <c:if test="${sessionScope.user.userId == 1}">
                            <li><a href="admin?view=userauthorization">Phân quyền</a></li>
                            </c:if>
                        <li><a href="trip">Quản lý chuyến tàu</a></li>
                        <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>

                    </ul>
                    <form action="logout" method="GET">
                        <button type="submit" class="logout-button">Logout</button>
                    </form>
                </div>
                <a href="admin?view=dashboard" class="admin-back-button">
                    <i class="fas fa-arrow-left"></i> Quay lại trang Admin
                </a>
            </c:if>
            <h1>Danh sách chuyến</h1>

            <c:if test="${not empty message}">
                <p style="color: green;">${message}</p>
            </c:if>
            <c:if test="${not empty error}">
                <p style="color: red;">${error}</p>
            </c:if>

            <%-- Filter Form --%>
            <div class="filter-section">
                <form action="trip" method="GET">
                    <label for="departStation">Ga đi:</label>
                    <input type="text" id="departStation" name="departStation" value="${param.departStation}">

                    <label for="arriveStation">Ga đến:</label>
                    <input type="text" id="arriveStation" name="arriveStation" value="${param.arriveStation}">

                    <label for="departureDate">Ngày đi:</label>
                    <input type="date" id="departureDate" name="departureDate" value="${param.departureDate}">

                    <button type="submit">Lọc</button>

                    <input type="hidden" name="action" value="list">
                </form>
                <a href="trip"><button>Xóa lọc</button></a>
                <a href="trip?action=add"><button>Thêm chuyến</button></a>
            </div>




            <%-- Trip List Table --%>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên tàu</th>
                        <th>Tuyến</th>
                        <th>Khởi hành</th>
                        <th>Đến</th>
                        <th>Tình trạng</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${trips}" var="trip" varStatus="loop">
                        <tr>
                            <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                            <td>${trip.trainName}</td>
                            <td>${trip.routeName}</td>
                            <td>${trip.formattedDepartureTime}</td>
                            <td>${trip.formattedArrivalTime}</td>
                            <td>${trip.tripStatus}</td>
                            <td class="actions">
                                <a href="trip?action=edit&id=${trip.tripID}"><button>Sửa</button></a>
                                <a href="trip?action=delete&id=${trip.tripID}" onclick="return confirm('Are you sure you want to delete this trip?');"><button>Xóa</button></a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <%-- Pagination Links --%>
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="trip?page=${currentPage - 1}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">Previous</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <span>${i}</span> <%-- Current page - no link --%>
                        </c:when>
                        <c:otherwise>
                            <a href="trip?page=${i}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="trip?page=${currentPage + 1}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">Next</a>
                </c:if>
            </div>
        </div>
    </body>
</html>