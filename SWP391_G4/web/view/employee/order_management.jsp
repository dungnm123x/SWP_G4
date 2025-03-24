<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date formatting --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Quản lí hóa đơn</title>
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
            <h1>Danh sách hóa đơn</h1>

            <c:if test="${not empty message}">
                <p style="color: green;">${message}</p>
            </c:if>
            <c:if test="${not empty error}">
                <p style = "color: red;">${error}</p>
            </c:if>

            <%-- Quick Stats (Optional - Requires additional controller logic) --%>
            <%--
            <div class="quick-stats">
                <div>Total Orders: ${totalOrders}</div>
                <div>Paid Orders: ${paidOrders}</div>
                <div>Pending Orders: ${pendingOrders}</div>
            </div>
            --%>

            <%-- Filter Form --%>
            <div class="filter-section">
                <form action="order" method="GET"> <%-- Submit to the OrderController --%>
                    <input type="hidden" name="action" value="list">
                    <label for="customerName">Tên:</label>
                    <input type="text" id="customerName" name="customerName" value="${param.customerName}">

                    <label for="phone">SĐT</label>
                    <input type="text" id="phone" name="phone" value="${param.phone}">

                    <label for="email">Email:</label>
                    <input type="text" id="email" name="email" value="${param.email}">

                    <%--<label for="status">Tình trạng</label>
                    <select id="status" name="status">
                        <option value="" ${param.status == '' ? 'selected' : ''}>Tất cả</option>
                        <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Chờ</option>

                        <option value="Paid" ${param.status == 'Paid' ? 'selected' : ''}>Thanh toán</option>
                        <option value="Cancelled" ${param.status == 'Cancelled' ? 'selected' : ''}>Hủy</option>
                        <option value="Refunded" ${param.status == 'Refunded' ? 'selected' : ''}>Hoàn vé</option>

                    </select>--%>
                    </br>
                    <label for="startDate">Mua từ ngày:</label>
                    <input type="date" id="startDate" name="startDate" value="${param.startDate}">
                    <label for="endDate">Đến ngày </label>
                    <input type="date" id="endDate" name="endDate" value="${param.endDate}">

                    <label for="routeId">Tuyến</label>
                    <select id="routeId" name="routeId">
                        <option value="" ${param.routeId == '' ? 'selected' : ''}>Tất cả</option>
                        <c:forEach items="${routes}" var="route">
                            <option value="${route.routeID}" ${param.routeId == route.routeID ? 'selected' : ''}>
                                ${route.routeID} - ${route.departureStationName} to ${route.arrivalStationName}
                            </option>
                        </c:forEach>
                    </select>

                    <button type="submit">Lọc</button>

                </form>
                <a href="order"><button>Xóa lọc</button></a>
                <%--<div style="margin-bottom: 20px; background-color: #f8f9fa; padding: 15px; border-radius: 8px; border: 1px solid #ddd; box-shadow: 0 2px 5px rgba(0,0,0,0.1);">
                    <strong style="font-size: 18px; color: #333; display: block; margin-bottom: 10px;">Thống kê</strong>
                    <span style="font-weight: bold;">Tổng: ${totalOrders}</span> |
                    <span style="font-weight: bold;">Đã Thanh Toán: ${paidOrders}</span> |
                    <span style="font-weight: bold;">Chờ giải quyết: ${pendingOrders}</span> |
                    <span style="font-weight: bold;">Hủy: ${cancelledOrders}</span>
                </div>--%>
            </div>

            <%-- Order List Table --%>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên khách hàng</th>
                        <th>SĐT</th>
                        <th>Email</th>
                        <th>Tàu</th>
                        <th>Tuyến</th>
                            <%--<th>Khởi hành</th>--%>
                            <%--<th>Đến nơi</th>--%>
                            <%-- <th>Mã vé</th>--%>
                            <%--<th>Số ghế</th>--%>
                        <th>Tổng tiền</th>
                        <%--<th>Tình trạng</th>--%>
                        <th>Ngày mua</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${bookings}" var="booking">
                        <c:forEach items="${booking.tickets}" var="ticket" varStatus="loop">
                            <tr>
                                <c:if test="${loop.first}">
                                    <%-- Only display booking-level details on the first row --%>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.bookingID}</a></td>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.customerName}</td>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.customerPhone}</td>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.customerEmail}</td>
                                </c:if>

                                <td>${ticket.trainName}</td>
                                <td>${ticket.routeName}</td>
                                <%--<td>${ticket.formattedDepartureTime}</td>--%>
                                <%--<td>${ticket.formattedArrivalTime}</td>--%>
                                <%--<td>${ticket.ticketID}</td>--%>
                                <%--<td>${ticket.seatNumber}</td>--%>

                                <c:if test="${loop.first}">
                                    <%--Only display booking-level details on the first row--%>
                                    <td rowspan="${fn:length(booking.tickets)}"><fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="VND" /></td>
                                    <%--<td rowspan="${fn:length(booking.tickets)}">${booking.paymentStatus}</td>--%>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.formattedBookingDate}</td>
                                    <td rowspan="${fn:length(booking.tickets)}">
                                        <a href="order?action=view&id=${booking.bookingID}">Xem</a>
                                        <c:if test="${booking.bookingStatus != 'Cancelled'}">
                                            <a href="order?action=cancel&id=${booking.bookingID}" onclick="return confirm('Are you sure you want to cancel this order?');">Hủy</a>
                                        </c:if>
                                    </td>
                                </c:if>

                            </tr>
                        </c:forEach>
                    </c:forEach>
                </tbody>
            </table>

            <%-- Pagination Links --%>
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="order?page=${currentPage - 1}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">&laquo; Previous</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <a class="active" href="order?page=${i}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">${i}</a>
                        </c:when>
                        <c:otherwise>
                            <a href="order?page=${i}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="order?page=${currentPage + 1}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">Next &raquo;</a>
                </c:if>
            </div>
        </div>
    </body>
</html>