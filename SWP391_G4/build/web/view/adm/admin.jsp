<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Quản Lý Vé Tàu</title>
    <link rel="stylesheet" href="./css/admin.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>
    <div class="container">
        <div class="sidebar">
            <div class="logo">
                <img src="./img/Screenshot 2025-02-12 142854.png" alt="RAILWAYVN">
            </div>
            <ul class="menu">
                <li><a href="dashboard.jsp">Dashboard</a></li>
                <li><a href="admin?view=employees">Quản lý nhân viên</a></li>
                <li><a href="admin?view=customers">Quản lý khách hàng</a></li>
                <li><a href="admin?view=trains">Quản lý chuyến tàu</a></li>
                <li><a href="thongke.jsp">Thống kê</a></li>
            </ul>
        </div>

        <div class="main-content">
            <div class="header">
                <h1>Trang Quản Trị Hệ Thống Vé Tàu</h1>
            </div>
            <div class="content">
                <c:choose>
                    <c:when test="${not empty list}">
                        <table border="1">
                            <thead>
                                <tr>
                                    <c:choose>
                                        <c:when test="${type == 'employees' || type == 'customers'}">
                                            <th>ID</th>
                                            <th>Username</th>
                                            <th>Full Name</th>
                                            <th>Email</th>
                                            <th>Phone</th>
                                            <th>Action</th>
                                        </c:when>
                                        <c:when test="${type == 'trains'}">
                                            <th>Train ID</th>
                                            <th>Train Name</th>
                                            <th>Action</th>
                                        </c:when>
                                    </c:choose>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${list}">
                                    <tr>
                                        <c:choose>
                                            <c:when test="${type == 'employees' || type == 'customers'}">
                                                <td>${item.userId}</td>
                                                <td>${item.username}</td>
                                                <td>${item.fullName}</td>
                                                <td>${item.email}</td>
                                                <td>${item.phoneNumber}</td>
                                            </c:when>
                                            <c:when test="${type == 'trains'}">
                                                <td>${item.trainID}</td>
                                                <td>${item.trainName}</td>
                                            </c:when>
                                        </c:choose>
                                        <td>
                                            <form method="post" action="admin">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="type" value="${type}">
                                                <input type="hidden" name="id" value="${type == 'trains' ? item.trainID : item.userId}">
                                                <button type="submit" class="btn-delete"><i class="bi bi-trash"></i></button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>                
                    <c:otherwise>
                        <p>Chọn chức năng từ menu bên trái để bắt đầu quản lý hệ thống.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</body>
</html>
