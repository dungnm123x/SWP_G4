<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Quản lý Tuyến Tàu</title>
    <link rel="stylesheet" href="css/employee.css">
</head>
<body>
    <div class="container">
    <div class="sidebar">
                <div class="logo">
                    <img src="./img/logo.jpg" alt="avatar">
                </div>
                <ul>
                    <li><a href="train">Quản lý tàu</a></li>
                    <li><a href="trip">Quản lý chuyến</a></li>
                    <li><a href="route">Quản lý tuyến tàu</a></li>
                    <li><a href="station">Quản lý ga</a></li>
                    <li><a href="category-blog">Quản lý tiêu đề Blog</a></li>
                    <li><a href="posts-list">Quản lý Blog</a></li>
                    <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                </ul>
                <form action="logout" method="GET">
                   <button type="submit" class="logout-button">Logout</button>
                </form>
                
            </div>
    <h1>Danh sách Tuyến Tàu</h1>
     <c:if test="${not empty message}">
        <p style="color:green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>

    <h3><c:if test="${not empty editRoute}">Sửa</c:if><c:if test="${empty editRoute}">Thêm</c:if> Tuyến Tàu</h3>
    <form method="post" action="route">
        <input type="hidden" name="routeID" value="${editRoute.routeID}">
        <input type="hidden" name="action" value="${not empty editRoute ? 'update' : 'add'}">

        <label for="departureStation">Ga đi:</label>
        <select name="departureStation" required>
            <c:forEach var="station" items="${stations}">
                <option value="${station.stationID}" ${station.stationID == editRoute.departureStation.stationID ? 'selected' : ''}>
                    ${station.stationName}
                </option>
            </c:forEach>
        </select>

        <label for="arrivalStation">Ga đến:</label>
        <select name="arrivalStation" required>
            <c:forEach var="station" items="${stations}">
                <option value="${station.stationID}" ${station.stationID == editRoute.arrivalStation.stationID ? 'selected' : ''}>
                    ${station.stationName}
                </option>
            </c:forEach>
        </select>

        <label for="distance">Khoảng cách (km):</label>
        <input type="number" name="distance" step="0.1" min="0" value="${editRoute.distance}" required>

        <label for="basePrice">Giá cơ bản:</label>
        <input type="number" name="basePrice" step="0.01" min="0" value="${editRoute.basePrice}" required>

        <button type="submit"><c:if test="${not empty editRoute}">Cập Nhật</c:if><c:if test="${empty editRoute}">Thêm</c:if></button>
    </form>

    <h3>Danh Sách Tuyến Tàu</h3>
    <table border="1">
        <tr>
            <th>Mã Tuyến</th>
            <th>Điểm Xuất Phát</th>
            <th>Điểm Đến</th>
            <th>Khoảng Cách</th>
            <th>Giá Cơ Bản</th>
            <th>Hành Động</th>
        </tr>
        <c:forEach var="route" items="${routes}">
            <tr>
                <td>${route.routeID}</td>
                <td>${route.departureStation.stationName}</td>
                <td>${route.arrivalStation.stationName}</td>
                <td>${route.distance}</td>
                <td>${route.basePrice}</td>
                <td>
                    <a href="route?editId=${route.routeID}"><button>Chỉnh sửa</button></a> | 
                    <form method="post" action="route" style="display:inline;">
                        <input type="hidden" name="routeID" value="${route.routeID}">
                        <input type="hidden" name="action" value="delete">
                        <button type="submit" style="background-color: red; color: white;">Xóa</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
