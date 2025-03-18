<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản Lý Ga Tàu</title>
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
        </style>
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
            <h1>Danh sách ga tàu</h1>
            <c:if test="${not empty message}">
                <p style="color: green;">${message}</p>
            </c:if>
            <c:if test="${not empty error}">
                <p style="color: red;">${error}</p>
            </c:if>
            <form id="stationForm" action="station" method="POST">
                <input type="hidden" name="action" id="action" value="${empty station ? 'add' : 'update'}">
                <input type="hidden" name="stationID" id="stationID" value="${station.stationID}">

                <label for="stationName">Tên Ga:</label>
                <input type="text" id="stationName" name="stationName" value="${station.stationName}" required>

                <label for="address">Địa Chỉ:</label>
                <input type="text" id="address" name="address" value="${station.address}"required>

                <button type="submit">${empty station ? 'Thêm Ga' : 'Cập nhật Ga'}</button>
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
                    <c:forEach var="station" items="${stations}" varStatus="loop">
                        <tr>
                            <td>${(currentPage - 1) * pageSize + loop.index + 1}</td>
                            <td>${station.stationName}</td>
                            <td>${station.address}</td>
                            <td>
                                <a href="station?action=edit&stationID=${station.stationID}"><button>Chỉnh sửa</button></a>
                                <button class="delete-btn" onclick="deleteStation(${station.stationID})">Xóa</button>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="station?page=${currentPage - 1}">&laquo; Previous</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <span>${i}</span> <%-- Current page: no link --%>
                        </c:when>
                        <c:otherwise>
                            <a href="station?page=${i}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="station?page=${currentPage + 1}">Next &raquo;</a>
                </c:if>
            </div>
        </div>

        <script>
            function deleteStation(id) {
                if (confirm('Bạn có chắc chắn muốn xóa ga tàu này không?')) {
                    window.location.href = 'station?action=delete&stationID=' + id;
                }
            }
        </script>
    </body>
</html>