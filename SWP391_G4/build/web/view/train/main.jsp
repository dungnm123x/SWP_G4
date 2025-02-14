<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                font-family: 'Arial', sans-serif;
            }

            body {
                background-color: #f4f7f9;
                color: #333;
                display: flex;
            }

            /* Sidebar */
            .sidebar {
                width: 250px;
                background: #003366;
                color: white;
                padding: 20px;
                height: 100vh;
                position: fixed;
                left: 0;
                top: 0;
                overflow-y: auto;
            }

            .sidebar h2 {
                text-align: center;
                font-size: 22px;
                margin-bottom: 20px;
            }

            .sidebar ul {
                list-style: none;
            }

            .sidebar ul li {
                padding: 10px;
                border-bottom: 1px solid rgba(255, 255, 255, 0.2);
            }

            .sidebar ul li a {
                color: white;
                text-decoration: none;
                display: block;
            }

            .sidebar ul li:hover {
                background: #00509e;
            }


            /* Container chính */
            .container {
                margin-left: 250px;
                padding: 20px;
                width: calc(100% - 250px);
                margin-top: 60px;
            }

            h1 {
                text-align: center;
                margin-bottom: 20px;
            }

            .filter-section {
                background: white;
                padding: 20px;
                margin-bottom: 20px;
                border-radius: 10px;
                box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
            }

            .form-row {
                display: flex;
                justify-content: space-between;
                flex-wrap: wrap;
            }

            .form-group {
                flex: 1;
                margin: 0 10px;
            }

            .form-group label {
                font-weight: bold;
                color: #333;
                display: block;
                margin-bottom: 5px;
            }

            .form-control {
                width: 100%;
                padding: 10px;
                border: 1px solid #ddd;
                border-radius: 5px;
                font-size: 16px;
            }

            /* Nút lọc */
            .btn-primary {
                background: #00509e;
                color: white;
                border: none;
                padding: 12px 20px;
                border-radius: 5px;
                cursor: pointer;
                font-size: 16px;
                transition: 0.3s;
                margin-top: 10px;
            }

            .btn-primary:hover {
                background: #003366;
            }

            .returnbtn {
                background: #00509e;
                color: white;
                border: none;
                padding: 12px 20px;
                border-radius: 5px;
                cursor: pointer;
                font-size: 16px;
                transition: 0.3s;
                margin-top: 10px;
                margin-left: 330px;
                display: inline-block;
                text-decoration: none;
                text-align: center;
            }

            .returnbtn:hover {
                background: #003366;
            }


            /* Bảng danh sách */
            table {
                width: 100%;
                border-collapse: collapse;
                background: white;
                border-radius: 10px;
                box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
                overflow: hidden;
            }

            th, td {
                padding: 12px;
                text-align: center;
                border-bottom: 1px solid #ddd;
            }

            th {
                background: #003366;
                color: white;
            }

            tr:hover {
                background: #f1f1f1;
            }

            /* Responsive */
            @media screen and (max-width: 768px) {
                .sidebar {
                    width: 100%;
                    height: auto;
                    position: relative;
                }

                .navbar {
                    width: 100%;
                    left: 0;
                }

                .container {
                    margin-left: 0;
                    width: 100%;
                }

                .form-row {
                    flex-direction: column;
                }

                .form-group {
                    margin: 10px 0;
                }
            }

            .logout-button {
                background: #d9534f; /* Màu đỏ nổi bật */
                color: white;
                border: none;
                padding: 12px 20px;
                border-radius: 5px;
                cursor: pointer;
                font-size: 16px;
                width: 100%;
                margin-top: 450px;
                transition: 0.3s;
            }

            .logout-button:hover {
                background: #c9302c;
            }

        </style>
        <title>Danh sách chuyến tàu</title>
    </head>
    <body>

        <div class="container">
            <div class="sidebar">
                <h2>Group3.5</h2>
                <ul>
                    <li><a href="#">Quản lý tàu</a></li>
                    <li><a href="#">Quản lý chuyến</a></li>
                    <li><a href="#">Quản lý tuyến tàu</a></li>
                </ul>
                <button type="submit" class="logout-button">Logout</button>
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