<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý tàu</title>
</head>
<body>
    <h2>Quản lý tàu</h2>

    <!-- Hiển thị thông báo -->
    <c:if test="${not empty message}">
        <p style="color: green;">${message}</p>
    </c:if>
    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <!-- Form thêm hoặc chỉnh sửa tàu -->
    <form action="train-management" method="post">
        <input type="hidden" name="action" value="${empty train ? 'add' : 'update'}">
        <c:if test="${not empty train}">
            <input type="hidden" name="trainID" value="${train.trainID}">
        </c:if>

        <label for="trainName">Tên tàu:</label>
        <input type="text" id="trainName" name="trainName" value="${train.trainName}" required>

        <button type="submit">${empty train ? 'Thêm tàu' : 'Cập nhật tàu'}</button>
    </form>

    <!-- Bảng danh sách tàu -->
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Tên tàu</th>
                <th>Số toa</th>
                <th>Số ghế</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="t" items="${trains}">
                <tr>
                    <td>${t.trainID}</td>
                    <td>${t.trainName}</td>
                    <td>${t.totalCarriages}</td>
                    <td>${t.totalSeats}</td>
                    <td class="actions">
                        <a href="train?action=edit&id=${t.trainID}">
                            <button>Chỉnh sửa</button>
                        </a>
                        <a href="train?action=delete&id=${t.trainID}" onclick="return confirm('Bạn có chắc chắn muốn xóa?')">
                            <button style="background-color: red; color: white;">Xóa</button>
                        </a>
                        <a href="train?action=manageCarriages&id=${t.trainID}">
                            <button>Quản lý toa</button>
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
