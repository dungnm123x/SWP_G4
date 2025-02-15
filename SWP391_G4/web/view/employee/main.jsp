<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="css/employee.css">
        <title>Danh sách chuyến tàu</title>
    </head>
    <body>

        <div class="container">
            <div class="sidebar">
                <div class="logo">
                    <img src="./img/logo.jpg" alt="RAILWAYVN">
                </div>
                <ul>
                    <li><a href="#">Quản lý tàu</a></li>
                    <li><a href="#">Quản lý chuyến</a></li>
                    <li><a href="#">Quản lý tuyến tàu</a></li>
                    <li><a href="station">Quản lý ga</a></li>
                    <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                </ul>
                <form action="logout" method="GET">
                   <button type="submit" class="logout-button">Logout</button>
                </form>
                
            </div>
            <h1>Danh sách chuyến tàu</h1>
            <div class="filter-section">
                <form action="filter" method="GET">
                    <div class="form-row">
                        <div class="form-group col-md-4">
                            <label for="departStation">Ga Đi</label>
                            <input type="text" class="form-control" id="departStation" name="departStation" placeholder="Nhập Ga Đi">
                        </div>
                        <div class="form-group col-md-4">
                            <label for="arriveStation">Ga Đến</label>
                            <input type="text" class="form-control" id="arriveStation" name="arriveStation" placeholder="Nhập Ga Đến">
                        </div>
                        <div class="form-group col-md-4">
                            <label for="departureDate">Ngày Khởi Hành</label>
                            <input type="date" class="form-control" id="departureDate" name="departureDate">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">Lọc</button>
                    <a href="listtrain" class="returnbtn" onclick="resetForm()">Trở về</a>
                    <script>
                        function resetForm() {
                            document.querySelector("form").reset();
                        }
                    </script>
                </form>
            </div>
            <table border="1">
                <tr>
                    <th></th>
                    <th>Tên tàu</th>
                    <th>Tổng toa</th>
                    <th>Tổng ghế</th>
                    <th>Ga đi</th>
                    <th>Ga đến</th>
                    <th>Xuất phát</th>
                    <th>Đến nơi</th>
                    <th>Giá vé</th>
                </tr>
                <% int stt=1; %>
                <tr>
                    <c:forEach var="t" items="${trains}">
                    <tr>
                        <td><%= stt++ %></td>
                        <td>${t.trainName}</td>
                        <td>${t.totalCarriages}</td>
                        <td>${t.totalSeats}</td>
                        <td>${t.departureStation}</td>
                        <td>${t.arrivalStation}</td>
                        <td>${t.departureTime}</td>
                        <td>${t.arrivalTime}</td>
                        <td>${t.price}</td>
                    </tr>
                </c:forEach>
                </tr>

            </table>
    </body>
</html>