<%-- 
    Document   : OrderList
    Created on : Mar 26, 2025, 10:05:05 PM
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
        <jsp:include page="/navbar.jsp"></jsp:include>
            <style>
                /* Add some basic styling for pagination */
                .container {
                    margin-top: 70px; /* Adjust this value to match the height of your navbar */
                }
                .pagination {
                    margin-top: 20px;
                    display:flex;
                    justify-content: center;
                }
                th {
                    background-color: #077BFF !important;
                    ;
                    color: white !important;
                    ;
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
                    background-color: #077BFF;
                    color: white;
                    text-decoration: none;
                    border-radius: 5px;
                    border: none;
                    font-size: 16px;
                    transition: background-color 0.3s ease
                }
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
            </style>
        </head>

        <body>

            <div class="container">
                <h1>Danh sách hóa đơn</h1>

            <c:if test="${not empty message}">
                <p style="color: green;">${message}</p>
            </c:if>
            <c:if test="${not empty error}">
                <p style = "color: red;">${error}</p>
            </c:if>
            <div class="filter-section">
                <form action="order-list" method="GET"> <%-- Submit to the OrderController --%>
                    <input type="hidden" name="action" value="list">
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
                <a href="order-list"><button>Xóa lọc</button></a>
            </div>
            <table border="1">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên khách hàng</th>
                        <th>SĐT</th>
                        <th>Email</th>
                        <th>Vé</th>
                        <th>Tuyến</th>
                        <th>Tổng tiền</th>
                        <th>Ngày mua</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${bookings}" var="booking">
                        <c:forEach items="${booking.tickets}" var="ticket" varStatus="loop">
                            <tr>
                                <c:if test="${loop.first}">
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.bookingID}</a></td>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.customerName}</td>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.customerPhone}</td>
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.customerEmail}</td>
                                </c:if>

                                <td>${ticket.ticketID}</td>
                                <td>${ticket.routeName}</td>
                                <c:if test="${loop.first}">
                                    <td rowspan="${fn:length(booking.tickets)}"><fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="VND" /></td>                                 
                                    <td rowspan="${fn:length(booking.tickets)}">${booking.formattedBookingDate}</td>
                                    <td rowspan="${fn:length(booking.tickets)}">
                                        <a href="order-list?action=view&id=${booking.bookingID}"class="btn btn-primary">
                                            <i class="fas fa-eye"></i>
                                        </a>
                                    </td>
                                </c:if>

                            </tr>
                        </c:forEach>
                    </c:forEach>
                </tbody>
            </table>
            <div class="pagination">
                <c:if test="${currentPage > 1}">
                    <a href="order-list?page=${currentPage - 1}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">&laquo; Previous</a>
                </c:if>

                <c:forEach begin="1" end="${totalPages}" var="i">
                    <c:choose>
                        <c:when test="${currentPage == i}">
                            <a class="active" href="order-list?page=${i}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">${i}</a>
                        </c:when>
                        <c:otherwise>
                            <a href="order-list?page=${i}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">${i}</a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:if test="${currentPage < totalPages}">
                    <a href="order-list?page=${currentPage + 1}&departStation=${param.departStation}&arriveStation=${param.arriveStation}&departureDate=${param.departureDate}">Next &raquo;</a>
                </c:if>
            </div>
        </div>
    </body>
</html>
