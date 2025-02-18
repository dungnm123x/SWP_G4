<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý Toa Tàu</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        table, th, td {
            border: 1px solid black;
        }
        th, td {
            padding: 10px;
            text-align: center;
        }
        .actions button {
            margin: 5px;
            padding: 5px 10px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <h2>Quản lý Toa Tàu</h2>
    
    <p><strong>Tên tàu:</strong> ${train.trainName}</p>
    <a href="train-management"><button>Quay lại danh sách tàu</button></a>

    <!-- Hiển thị thông báo -->
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <!-- Form thêm hoặc chỉnh sửa toa tàu -->
    <form action="carriage-management" method="post">
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
                    <td>${c.seatCount}</td>
                    <td class="actions">
                        <a href="carriage-management?action=edit&trainID=${train.trainID}&carriageID=${c.carriageID}">
                            <button>Chỉnh sửa</button>
                        </a>
                        <a href="carriage-management?action=delete&trainID=${train.trainID}&carriageID=${c.carriageID}" 
                           onclick="return confirm('Bạn có chắc chắn muốn xóa toa này?')">
                            <button style="background-color: red; color: white;">Xóa</button>
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
