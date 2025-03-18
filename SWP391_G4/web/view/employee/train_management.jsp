<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý tàu</title>
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
            .admin-back-button {
                display: inline-block;
                padding: 10px 20px;
                background-color: #2C3E50;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                border: none;
                font-size: 16px;
                transition: background-color 0.3s ease
            }

            .admin-back-button:hover {
                background-color: #00509E;
            }
            .admin-back-button i {
                margin-right: 8px;
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
            <c:if test="${sessionScope.user.roleID == 1}">
                <a href="admin?view=dashboard" class="admin-back-button">
                    <i class="fas fa-arrow-left"></i> Quay lại trang Admin
                </a>
            </c:if>
            <h1>Danh sách tàu</h1>

            <c:if test="${not empty error}">
                <p class="error">${error}</p>
            </c:if>
            <c:if test="${not empty message}">
                <p class="success">${message}</p>
            </c:if>

            <form action="train" method="post">
                <input type="hidden" name="action" value="${empty train ? 'add' : 'update'}">

                <c:if test="${not empty train}">
                    <input type="hidden" name="trainID" value="${train.trainID}">
                </c:if>

                <label for="trainName">Tên tàu:</label>
                <input type="text" id="trainName" name="trainName" value="${train.trainName}" required>

                <c:if test="${empty train}"> <%-- Only show these fields when adding a new train --%>
                    <label for="totalCarriages">Tổng số toa:</label>
                    <input type="number" id="totalCarriages" name="totalCarriages" required>

                    <label for="vipCarriages">Số toa VIP:</label>
                    <input type="number" id="vipCarriages" name="vipCarriages" required>
                </c:if>

                <button type="submit">${empty train ? 'Thêm tàu' : 'Cập nhật tàu'}</button>
            </form>

            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên tàu</th>
                        <th>Tổng số toa</th>
                        <th>Tổng số ghế</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="train" items="${trains}">
                        <tr>
                            <td>${train.trainID}</td>
                            <td>${train.trainName}</td>
                            <td>${train.totalCarriages}</td>
                            <td>${train.totalSeats}</td>
                            <td class="actions">
                                <a href="train?action=edit&id=${train.trainID}"><button>Chỉnh sửa</button></a>
                                <a href="train?action=delete&id=${train.trainID}" onclick="return confirm('Bạn có chắc chắn muốn xóa tàu này (và tất cả các toa liên quan)?')"><button style="background-color: red; color: white;">Xóa</button></a>
                                <a href="train?action=manageCarriages&id=${train.trainID}"><button>Quản lý toa</button></a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>

            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="train?page=${currentPage - 1}">&laquo; Previous</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <a class="active" href="train?page=${i}">${i}</a>
                        </c:when>
                        <c:otherwise>
                            <a href="train?page=${i}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="train?page=${currentPage + 1}">Next &raquo;</a>
                </c:if>
            </div>
        </div>
    </body>
</html>