<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date formatting --%>

<!DOCTYPE html>
<html>
    <head>
        <title>Order Management</title>
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

            <h1>Order Management</h1>

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
                    <label for="customerName">Customer Name:</label>
                    <input type="text" id="customerName" name="customerName" value="${param.customerName}">

                    <label for="phone">Phone:</label>
                    <input type="text" id="phone" name="phone" value="${param.phone}">

                    <label for="email">Email:</label>
                    <input type="text" id="email" name="email" value="${param.email}">

                    <label for="status">Status:</label>
                    <select id="status" name="status">
                        <option value="" ${param.status == '' ? 'selected' : ''}>All</option>
                        <option value="Pending" ${param.status == 'Pending' ? 'selected' : ''}>Pending</option>
                        <option value="Confirmed" ${param.status == 'Confirmed' ? 'selected' : ''}>Confirmed</option>
                        <option value="Paid" ${param.status == 'Paid' ? 'selected' : ''}>Paid</option>
                        <option value="Cancelled" ${param.status == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                        <option value="Refunded" ${param.status == 'Refunded' ? 'selected' : ''}>Refunded</option>
                        <option value="Completed" ${param.status == 'Completed' ? 'selected' : ''}>Completed</option>
                        <%-- Add other status options --%>
                    </select>
                    <label for="startDate">Start Booking Date:</label>
                    <input type="date" id="startDate" name="startDate" value="${param.startDate}">
                    <label for="endDate">End Booking Date </label>
                    <input type="date" id="endDate" name="endDate" value="${param.endDate}">

                    <label for="routeId">Route:</label>
                    <select id="routeId" name="routeId">
                        <option value="" ${param.routeId == '' ? 'selected' : ''}>All</option>
                        <c:forEach items="${routes}" var="route">
                            <option value="${route.routeID}" ${param.routeId == route.routeID ? 'selected' : ''}>
                                ${route.routeID} - ${route.departureStationName} to ${route.arrivalStationName}
                            </option>
                        </c:forEach>
                    </select>
                    <button type="submit">Filter</button>
                    <a href="order">Clear Filter</a>
                </form>
            </div>

            <%-- Order List Table --%>
            <table border="1">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Customer Name</th>
                        <th>Phone</th>
                        <th>Email</th>
                        <th>Train</th>
                        <th>Route</th>
                        <th>Departure</th>
                        <th>Arrival</th>
                        <th>Tickets</th>
                        <th>Total Price</th>
                        <th>Status</th>
                        <th>Booking Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${bookings}" var="booking" varStatus="loop">
                        <tr>
                            <td><a href="order?action=view&id=${booking.bookingID}">${booking.bookingID}</a></td>
                            <td>${booking.customerName}</td>
                            <td>${booking.customerPhone}</td>
                            <td>${booking.customerEmail}</td>
                            <td>${booking.trainName}</td>
                            <td>${booking.routeName}</td>
                            <td>${booking.formattedDepartureTime}</td>
                            <td>${booking.formattedArrivalTime}</td>
                            <td>${booking.tickets.size()}</td> <%--  Number of tickets --%>
                            <td>
                                <fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="$" />
                            </td>
                            <td>${booking.paymentStatus}</td> <%-- Display payment status --%>
                            <td>${booking.formattedBookingDate}</td>

                            <td>
                                <a href="order?action=view&id=${booking.bookingID}">View</a>
                                <c:if test="${booking.paymentStatus != 'Cancelled'}">
                                    <a href="order?action=cancel&id=${booking.bookingID}" onclick="return confirm('Are you sure you want to cancel this order?');">Cancel</a>
                                </c:if>
                                <%--  <a href="order?action=edit&id=${booking.bookingID}">Edit</a> --%>  <%-- Optional Edit --%>
                            </td>
                        </tr>
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