<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Quản Lý Vé Tàu</title>
        <link rel="stylesheet" href="./css/admin/admin.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    </head>
    <body>
        <div class="container">
            <div class="sidebar">
                <div class="logo">
                    <img src="./img/logo.jpg" alt="trainpicture">
                </div>
                <ul class="menu">
                    <li><a href="#">Dashboard</a></li>
                    <li><a href="admin?view=employees">Quản lý nhân viên</a></li>
                    <li><a href="admin?view=customers">Quản lý khách hàng</a></li>
                    <li><a href="trip">Quản lý chuyến tàu</a></li>
                    <li><a href="#">Thống kê</a></li>
                    <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                </ul>
                <form action="logout" method="GET">
                    <button type="submit" class="logout-button">Logout</button>
                </form>
            </div>


            <div class="main-content">
                <div class="header">
                    <h1>Trang Quản Trị Hệ Thống Vé Tàu</h1>
                </div>
                <div class="content">
                    <c:choose>
                        <c:when test="${not empty list}">
                            <c:if test="${not empty list}">
                                <div class="search-container">
                                    <form method="get" action="admin">
                                        <input type="hidden" name="view" value="${type}">
                                        <input type="text" name="search" class="search-input" placeholder="Tìm kiếm...">
                                        <button type="submit" class="search-btn"><i class="bi bi-search"></i></button>
                                        <a href="admin?view=${type}" class="reset-btn"><i class="bi bi-arrow-counterclockwise"></i></a>
                                    </form>
                                </div>


                                <table border="1">
                                    <thead>
                                        <tr>
                                            <c:choose>
                                                <c:when test="${type == 'employees' || type == 'customers'}">
                                                    <th></th>
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
                                        <c:forEach var="item" items="${list}" varStatus="status">
                                            <tr>
                                                <c:choose>
                                                    <c:when test="${type == 'employees' || type == 'customers'}">
                                                        <td>${status.index + 1}</td>
                                                        <td>${item.username}</td>
                                                        <td>${item.fullName}</td>
                                                        <td>${item.email}</td>
                                                        <td>${item.phoneNumber}</td>
                                                    </c:when>
                                                </c:choose>
                                                <td>
                                                    <button class="btn btn-outline-primary btn-sm"
                                                            onclick="location.href = 'admin?view=details&type=${type}&id=${item.userId}'">
                                                        <i class="bi bi-eye"></i> Chi Tiết
                                                    </button>

                                                    <c:choose>
                                                        <c:when test="${item.status}">
                                                            <button class="btn btn-outline-danger btn-sm"
                                                                    onclick="location.href = 'admin?view=disable&type=${type}&id=${item.userId}'">
                                                                <i class="bi bi-x-circle"></i> Vô Hiệu
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button class="btn btn-outline-success btn-sm"
                                                                    onclick="location.href = 'admin?view=restore&type=${type}&id=${item.userId}'">
                                                                <i class="bi bi-check-circle"></i> Khôi Phục
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>

                                            </c:forEach>
                                    </tbody>
                                </table>

                                <c:if test="${type eq 'employees'}">
                                    <div class="add-button-container">
                                        <a href="admin?view=addEmployee" class="btn-add">
                                            <i class="bi bi-plus-circle"></i> Thêm Nhân Viên
                                        </a>
                                    </div>
                                </c:if>
                            </c:if>

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
