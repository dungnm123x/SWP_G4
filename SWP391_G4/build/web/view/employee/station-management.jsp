<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản Lý Ga Tàu</title>
        <link rel="stylesheet" href="css/employee.css">
    </head>
    <body>
        <div class="container">
            <h1>Danh sách ga tàu</h1>
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

            <!-- Form Thêm/Sửa Ga -->
            <form id="stationForm" action="station" method="POST">
                <input type="hidden" name="action" id="action" value="add">
                <input type="hidden" name="stationID" id="stationID">

                <label for="stationName">Tên Ga:</label>
                <input type="text" id="stationName" name="stationName" required>

                <label for="address">Địa Chỉ:</label>
                <input type="text" id="address" name="address" required>

                <button type="submit">Lưu</button>
                <button type="button" id="resetBtn">Hủy</button>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên Ga</th>
                        <th>Địa Chỉ</th>
                        <th>Hành Động</th>
                    </tr>
                </thead>
                <tbody>

                    <c:forEach var="station" items="${stations}">
                        <tr>
                            <td>${station.stationID}</td>
                            <td>${station.stationName}</td>
                            <td>${station.address}</td>
                            <td>
                                <a href="editstation?action=edit&stationID=${station.stationID}" class="edit-link">Sửa</a>
                                <button class="delete-btn" onclick="deleteStation(${station.stationID})">Xóa</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <script>

            function deleteStation(id) {
                if (confirm("Chắc chưa?")) {
                    window.location.href = "station?action=delete&stationID=" + id;
                }
            }

            document.getElementById("resetBtn").addEventListener("click", function () {
                document.getElementById("stationForm").reset();
                document.getElementById("action").value = "add";
            });

        </script>
    </body>
</html>
