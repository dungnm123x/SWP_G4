<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>Quản lý Tuyến Tàu</title>
    <link rel="stylesheet" href="css/employee.css">
    <style>
        /* Your existing styles */
        .admin-back-button {
            display: inline-block;
            padding: 10px 20px;
            background-color: #2C3E50;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            border: none;
            font-size: 16px;
            transition: background-color 0.3s ease;
        }

        .admin-back-button:hover {
            background-color: #00509E;
        }

        .admin-back-button i {
            margin-right: 8px;
        }

        /* Pagination CSS */
        .pagination {
            margin-top: 20px;
            display: flex;
            justify-content: center; /* Center the links */
            align-items: center;
        }

        .pagination a, .pagination span {
            padding: 8px 12px;
            margin: 0 5px;
            border: 1px solid #ddd;
            text-decoration: none;
            color: black;
            background-color: white;
        }

        .pagination a:hover {
            background-color: #eee;
        }

        .pagination span { /* Style for the current page */
            font-weight: bold;
            background-color: #007bff; /* Blue background */
            color: white;
            border-color: #007bff;
        }
        
    </style>
</head>
<body>
    <div class="container">
        <c:if test="${sessionScope.user.roleID == 2}">
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
        </c:if>
        <c:if test="${sessionScope.user.roleID == 1}">
            <div class="sidebar">
                <div class="logo">
                    <img src="./img/logo.jpg" alt="trainpicture">
                </div>
                <ul class="menu">
                    <li><a href="admin?view=dashboard">Dashboard</a></li>
                    <li><a href="admin?view=employees">Quản lý nhân viên</a></li>
                    <li><a href="admin?view=customers">Quản lý khách hàng</a></li>
                    <c:if test="${sessionScope.user.userId == 1}">
                        <li><a href="admin?view=userauthorization">Phân quyền</a></li>
                    </c:if>
                    <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>

                </ul>
                <form action="logout" method="GET">
                    <button type="submit" class="logout-button">Logout</button>
                </form>
            </div>
            <a href="admin?view=dashboard" class="admin-back-button">
                <i class="fas fa-arrow-left"></i> Quay lại trang Admin
            </a>
        </c:if>
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
            <input type="number" name="distance" step="1" min="0" value="${editRoute.distance}" required>

            <label for="basePrice">Giá cơ bản:</label>
            <input type="number" name="basePrice" step="1" min="0" value="${editRoute.basePrice}" required>

            <button type="submit"><c:if test="${not empty editRoute}">Cập Nhật</c:if><c:if test="${empty editRoute}">Thêm</c:if></button>
                <button type="button" id="resetBtn">Hủy</button>
            </form>

            <h3>Danh Sách Tuyến Tàu</h3>
            <table border="1">
                <thead>
                    <tr>
                         <th>STT</th>
                        <th>Mã Tuyến</th>
                        <th>Điểm Xuất Phát</th>
                        <th>Điểm Đến</th>
                        <th>Khoảng Cách</th>
                        <th>Giá Cơ Bản</th>
                        <th>Hành Động</th>
                    </tr>
                </thead>
            <tbody>
               <c:forEach var="route" items="${routes}" varStatus="loop">
    <tr>
        <td>${(currentPage - 1) * pageSize + loop.index + 1}</td>
        <td>${route.routeID}</td>
        <td>${route.departureStation.stationName}</td>
        <td>${route.arrivalStation.stationName}</td>
        <td><fmt:formatNumber value="${route.distance}" pattern="#,##0" /> Km</td>
        <td><fmt:formatNumber value="${route.basePrice}" type="currency" currencySymbol="VND" /></td>
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
            </tbody>
        </table>

        <%-- Pagination Links --%>
       <div class="pagination">
    <c:if test="${currentPage > 1}">
        <a href="route?page=${currentPage - 1}">&laquo; Previous</a>
    </c:if>

    <c:forEach begin="1" end="${totalPages}" var="i">
        <c:choose>
            <c:when test="${currentPage == i}">
                <span>${i}</span> <%-- Current page - no link --%>
            </c:when>
            <c:otherwise>
                <a href="route?page=${i}">${i}</a>
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <c:if test="${currentPage < totalPages}">
        <a href="route?page=${currentPage + 1}">Next &raquo;</a>
    </c:if>
</div>
    </div>
     <script>
         document.getElementById("resetBtn").addEventListener("click", function () {
            document.getElementById("stationForm").reset();
            document.getElementById("action").value = "add";
             document.getElementById("routeID").value = "";
        });
        </script>
</body>
</html>