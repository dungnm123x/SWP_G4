<%-- 
    Document   : Refund
    Created on : Mar 23, 2025, 2:03:30 PM
    Author     : dung9
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date formatting --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lí hóa đơn</title>
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
            <h1>Danh sách cần hoàn tiền</h1>

            <c:if test="${not empty message}">
                <p style="color: green;">${message}</p>
            </c:if>
            <c:if test="${not empty error}">
                <p style = "color: red;">${error}</p>
            </c:if>

            <table border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên khách hàng</th>
                        <th>Email</th>
                        <th>SĐT</th>
                        <th>Địa chỉ</th>
                        <th>Số tiền</th>
                        <th>Trạng thái</th>
                        <th>Ngày yêu cầu</th>
                        <th>Mã vé</th>
                        <th>Ga đi</th>
                        <th>Ga đến</th>
                        <th>Tàu</th>
                        <th>Ngày khởi hành</th>
                        <th>Chỗ ngồi</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${refunds}" var="refund">
                        <tr>
                            <td>${refund.refundID}</td>
                            <td>${refund.customerName}</td>
                            <td>${refund.customerEmail}</td>
                            <td>${refund.phoneNumber}</td>
                            <td>${refund.address}</td>
                            <td><fmt:formatNumber value="${refund.totalRefund}" type="currency" currencySymbol="VND" /></td>
                            <td>${refund.refundStatus}</td>
                            <td><fmt:formatDate value="${refund.refundDate}" pattern="yyyy-MM-dd HH:mm" /></td>
                            <td>${refund.ticketID}</td>
                            <td>${refund.departureStation}</td>
                            <td>${refund.arrivalStation}</td>
                            <td>${refund.trainCode}</td>
                            <td><fmt:formatDate value="${refund.departureTime}" pattern="yyyy-MM-dd HH:mm" /></td>
                            <td>${refund.departureStation} → ${refund.arrivalStation}</td>
                            <td>${refund.trainName}</td>

                        </tr>
                    </c:forEach>
                </tbody>
            </table>
