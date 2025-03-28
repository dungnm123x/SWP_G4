<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Sửa chuyến</title>
        <link rel="stylesheet" href="css/employee.css">  <%-- Include your CSS --%>
        <style>
            label {
                display: block; /* Make labels block-level for better layout */
                margin-bottom: 5px;
            }
            input[type="text"], input[type="datetime-local"], select{
                width: 100%;
                padding: 8px;
                margin-bottom: 10px;
                border: 1px solid #ddd;
                box-sizing: border-box; /* Include padding and border in element's total width/height */
            }
            button[type="submit"] {
                padding: 10px 15px;
                background-color: #4CAF50;
                color: white;
                border: none;
                cursor: pointer;
            }
            .form-container {
                width: 50%; /* Adjust as needed */
                margin: 0 auto; /* Center the form */
            }
        </style>
    </head>
    <body>
        <div class="container">
            <c:if test="${sessionScope.user.roleID == 2}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="avatar">
                    </div>
                    <ul>
                        <li><a href="employeeCalendar">Lịch làm việc</a></li>
                        <li><a href="train">Quản lý tàu</a></li>
                        <li><a href="trip">Quản lý chuyến</a></li>
                        <li><a href="route">Quản lý tuyến tàu</a></li>
                        <li><a href="station">Quản lý ga</a></li>
                        <li><a href="order">Quản lý hóa đơn</a></li>
                        <li><a href="refund">Quản lý đơn hoàn tiền</a></li>
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
                        <li><a href="admin?view=calendar">Lịch</a></li>
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
            <div class="form-container">
                <h1>Sửa chuyến</h1>

                <c:if test="${not empty error}">
                    <p style="color: red;">${error}</p>
                </c:if>

                <form action="trip" method="post">
                    <input type="hidden" name="action" value="update"> <%--  Tell the controller it's an update --%>
                    <input type="hidden" name="tripID" value="${trip.tripID}"> <%--  Hidden field for the trip ID --%>

                    <label for="trainID">Train:</label>
                    <select id="trainID" name="trainID" required>
                        <c:forEach items="${trains}" var="train">
                            <option value="${train.trainID}" ${train.trainID == trip.trainID ? 'selected' : ''}>
                                ${train.trainName}
                            </option>
                        </c:forEach>
                    </select><br>

                    <label for="routeID">Route:</label>
                    <select id="routeID" name="routeID" required>
                        <c:forEach items="${routes}" var="route">
                            <option value="${route.routeID}" ${route.routeID == trip.routeID ? 'selected' : ''}>
                                ${route.routeID} - ${route.departureStationName} to ${route.arrivalStationName}
                            </option>
                        </c:forEach>
                    </select><br>

                    <label for="departureTime">Departure Time:</label>
                    <input type="datetime-local" id="departureTime" name="departureTime" value="${trip.formattedDepartureTime}" required><br>

                    <label for="arrivalTime">Arrival Time:</label>
                    <input type="datetime-local" id="arrivalTime" name="arrivalTime" value="${trip.formattedArrivalTime}" required><br>
                    <label for="tripStatus">Trip Status:</label>
                    <select id="tripStatus" name="tripStatus" required>
                        <option value="Scheduled" ${trip.tripStatus == 'Scheduled' ? 'selected' : ''}>Scheduled</option>
                        <option value="Departed" ${trip.tripStatus == 'Departed' ? 'selected' : ''}>Departed</option>
                        <option value="Arrived" ${trip.tripStatus == 'Arrived' ? 'selected' : ''}>Arrived</option>
                        <option value="Cancelled" ${trip.tripStatus == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                        <%-- Add other status options as needed --%>
                    </select><br>

                    <button type="submit">Lưu</button>

                </form>
                <a href="trip"><button>Quay lại</button></a>
            </div>
        </div>
    </body>
</html>