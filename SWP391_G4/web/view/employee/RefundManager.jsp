<%-- 
    Document   : Refund
    Created on : Mar 23, 2025, 2:03:30 PM
    Author     : dung9
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date formatting --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<html>
    <head>
        <title>Quản lí hóa đơn</title>
        <link rel="stylesheet" href="css/employee.css">
        <style>
            /* Add some basic styling for pagination */
            table {
                width: 100%;
                border-collapse: collapse;
                border: 2px solid black; /* Viền ngoài */
            }

            th, td {
                border: 1px solid black; /* Viền ô */
                padding: 8px;
                text-align: center;
            }
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
            .btn-sm i {
                font-size: 16px; /* Điều chỉnh kích thước icon */
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
                        <li><a href="employeeCalendar">Lịch làm việc</a></li>
                        <li><a href="train">Quản lý tàu</a></li>
                        <li><a href="trip">Quản lý chuyến</a></li>
                        <li><a href="route">Quản lý tuyến tàu</a></li>
                        <li><a href="station">Quản lý ga</a></li>
                        <li><a href="order">Quản lý hóa đơn</a></li>
                        <li><a href="refund">Quản lý đơn hoàn tiền</a></li>
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
                        <li><a href="admin?view=calendar">Lịch</a></li>
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

            <h1>Danh sách đơn hoàn tiền</h1>
            <%-- Filter Form --%>
            <div class="filter-section">
                <form action="refund" method="get"> 
                    <label>Tên khách hàng:</label>
                    <input type="text" name="customerName" value="${param.customerName}" />

                    <label>Số điện thoại:</label>
                    <input type="text" name="phoneNumber" value="${param.phoneNumber}" />

                    <label>Email:</label>
                    <input type="text" name="customerEmail" value="${param.customerEmail}" />
                    <br><label>Số tài khoản:</label>
                    <input type="text" name="bankAccountID" value="${param.bankAccountID}" />

                    <label>Ngày hoàn:</label>
                    <input type="date" name="refundDate" value="${param.refundDate}" />

                    <label>Trạng thái:</label>
                    <select name="refundStatus">
                        <option value="">Tất cả</option>
                        <option value="Pending" ${param.refundStatus == 'Pending' ? 'selected' : ''}>Đang chờ</option>
                        <option value="Complete" ${param.refundStatus == 'Complete' ? 'selected' : ''}>Hoàn tất</option>
                    </select>



                    <button type="submit">Lọc</button>
                </form>

                <a href="refund"><button>Xóa lọc</button></a>
            </div>
            <!-- Hiển thị thông báo lỗi nếu có -->
            <c:if test="${not empty error}">
                <p style="color: red;">${error}</p>
            </c:if>

            <c:choose>
                <c:when test="${empty refunds}">
                    <p>Không có yêu cầu hoàn tiền nào.</p>
                </c:when>
                <c:otherwise>
                    <table border="1">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên khách hàng</th>
                                <th>Email</th>
                                <th>SĐT</th>
                                <th>Ngân hàng</th>
                                <th>Số tài khoản</th>
                                <th>Vé</th>
                                <th>Số tiền hoàn</th>
                                <th>Trạng thái</th>
                                <th>Chi tiết</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="prevRefundID" value="-1" />
                            <c:forEach items="${refunds}" var="refund" varStatus="loop">
                                <c:if test="${refund.refundID ne prevRefundID}">
                                    <c:set var="rowCount" value="0" />
                                    <c:forEach items="${refunds}" var="innerRefund">
                                        <c:if test="${innerRefund.refundID eq refund.refundID}">
                                            <c:set var="rowCount" value="${rowCount + 1}" />
                                        </c:if>
                                    </c:forEach>

                                    <tr>
                                        <td rowspan="${rowCount}">${refund.refundID}</td>
                                        <td rowspan="${rowCount}">${refund.customerName}</td>
                                        <td rowspan="${rowCount}">${refund.customerEmail}</td>
                                        <td rowspan="${rowCount}">${refund.phoneNumber}</td>
                                        <td rowspan="${rowCount}">${refund.bankName}</td>
                                        <td rowspan="${rowCount}">${refund.bankAccountID}</td>
                                        <td>${refund.ticketID}</td>
                                        <td rowspan="${rowCount}"><fmt:formatNumber value="${refund.totalRefund}" type="currency" currencySymbol="VND" /></td>
                                        <td rowspan="${rowCount}">${refund.refundStatus}</td>
                                        <td rowspan="${rowCount}">
                                            <a href="refundDetails?refundID=${refund.refundID}" class="btn btn-primary btn-sm">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:if>
                                <c:if test="${refund.refundID eq prevRefundID}">
                                    <tr>
                                        <td>${refund.ticketID}</td>
                                    </tr>
                                </c:if>
                                <c:set var="prevRefundID" value="${refund.refundID}" />
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>

    </body>
</html>
