<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý Toa Tàu</title>
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
                    <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                </ul>
                <form action="logout" method="GET">
                   <button type="submit" class="logout-button">Logout</button>
                </form>
                
            </div>
    <h2>Quản lý Toa Tàu</h2>
    
    <p><strong>Tên tàu:</strong> ${train.trainName}</p>
    <a href="train"><button>Quay lại danh sách tàu</button></a>

    <!-- Hiển thị thông báo -->
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <!-- Form thêm hoặc chỉnh sửa toa tàu -->
    <form action="carriage" method="post">
        <input type="hidden" name="action" value="${empty carriage ? 'add' : 'update'}">
        <input type="hidden" name="trainID" value="${train.trainID}">
        <c:if test="${not empty carriage}">
            <input type="hidden" name="carriageID" value="${carriage.carriageID}">
        </c:if>

        <label for="carriageType">Loại Toa:</label>
        <select id="carriageType" name="carriageType" required>
            <option value="VIP" ${carriage.carriageType == 'VIP' ? 'selected' : ''}>Toa VIP (12 ghế)</option>
            <option value="Standard" ${carriage.carriageType == 'Standard' ? 'selected' : ''}>Toa Thường (10 ghế)</option>
        </select>

        <button type="submit">${empty carriage ? 'Thêm Toa' : 'Cập nhật Toa'}</button>
    </form>

    <!-- Bảng danh sách toa tàu -->
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Loại Toa</th>
                <th>Số ghế</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="c" items="${carriages}">
                <tr>
                    <td>${c.carriageID}</td>
                    <td>${c.carriageType}</td>
                    <td>${c.capacity}</td>
                    <td class="actions">
                        <a href="carriage?action=edit&trainID=${train.trainID}&carriageID=${c.carriageID}">
                            <button>Chỉnh sửa</button>
                        </a>
                        <a href="carriage?action=delete&trainID=${train.trainID}&carriageID=${c.carriageID}" 
                           onclick="return confirm('Bạn có chắc chắn muốn xóa toa này?')">
                            <button style="background-color: red; color: white;">Xóa</button>
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </div>
</body>
</html>
