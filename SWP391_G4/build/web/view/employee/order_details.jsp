<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <title>Order Details</title>
    <link rel="stylesheet" href="css/employee.css"> <%-- Your stylesheet --%>
    <style>
        /* Add some basic styling */
        .detail-section {
            margin-bottom: 20px;
            border: 1px solid #ddd;
            padding: 10px;
        }
        .detail-label {
            font-weight: bold;
            display: inline-block; /* Labels on the same line as values */
            width: 150px; /* Or whatever width you like */
        }
        .detail-value {
            display: inline-block;
        }
        .ticket-table {
            width: 100%;
            border-collapse: collapse;
        }
        .ticket-table th, .ticket-table td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        .ticket-table th {
            background-color: #f2f2f2;
        }
        .actions {
            margin-top: 20px;
        }
        .actions a {
            margin-right: 10px;
            text-decoration: none;
            padding: 8px 12px;
            border: 1px solid #ddd;
            background-color: white;
            color: black;
        }
        .actions a:hover {
             background-color: #ddd;
        }
        .back-button { /* Style for the back button */
            display: inline-block;
            padding: 10px 15px;
            background-color: #007bff; /* Blue */
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .back-button:hover {
            background-color: #0056b3; /* Darker blue on hover */
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
    <h2>Order Details - ID: ${booking.bookingID}</h2>

    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <%-- Order Information Section --%>
    <div class="detail-section">
        <h3>Order Information</h3>
        <p><span class="detail-label">Order ID:</span> <span class="detail-value">${booking.bookingID}</span></p>
        <p><span class="detail-label">Booking Date:</span> <span class="detail-value">${booking.formattedBookingDate}</span></p>
        <p><span class="detail-label">Status:</span> <span class="detail-value">${booking.bookingStatus}</span></p>
        <p><span class="detail-label">Total Price:</span> <span class="detail-value"><fmt:formatNumber value="${booking.totalPrice}" type="currency" currencySymbol="$" /></span></p>
        <p><span class="detail-label">Payment Status:</span><span class="detail-value">${booking.paymentStatus}</span></p>
    </div>

    <%-- Customer Information Section --%>
    <div class="detail-section">
        <h3>Customer Information</h3>
        <p><span class="detail-label">Name:</span> <span class="detail-value">${booking.customerName}</span></p>
        <p><span class="detail-label">Phone:</span> <span class="detail-value">${booking.customerPhone}</span></p>
        <p><span class="detail-label">Email:</span> <span class="detail-value">${booking.customerEmail}</span></p>
        <%-- Add address and CCCD if you have them in your DTO --%>
         <p><span class="detail-label">Address:</span> <span class="detail-value">${user.address}</span></p>
         <p><span class="detail-label">CCCD:</span> <span class="detail-value">${ticket.cccd}</span></p>
    </div>

    <%-- Trip information Section--%>
     <div class="detail-section">
        <h3>Trip Information</h3>
        <p><span class="detail-label">Train:</span> <span class="detail-value">${booking.trainName}</span></p>
        <p><span class="detail-label">Route:</span> <span class="detail-value">${booking.routeName}</span></p>
        <p><span class="detail-label">Departure Time:</span> <span class="detail-value">${booking.formattedDepartureTime}</span></p>
         <p><span class="detail-label">Arrival Time:</span> <span class="detail-value">${booking.formattedArrivalTime}</span></p>
    </div>

    <%-- Ticket Information Section --%>
    <div class="detail-section">
        <h3>Tickets</h3>
        <table class="ticket-table">
            <thead>
                <tr>
                    <th>Ticket ID</th>
                    <th>Passenger Name</th>
                    <th>Seat Number</th>
                    <th>Carriage</th>
                     <th>Type</th>
                    <th>Price</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${booking.tickets}" var="ticket">
                    <tr>
                        <td>${ticket.ticketID}</td>
                        <td>${ticket.passengerName}</td>
                        <td>${ticket.seatNumber}</td>
                        <td>${ticket.carriageNumber}</td>
                         <td>${ticket.seatType}</td>
                        <td><fmt:formatNumber value="${ticket.ticketPrice}" type="currency" currencySymbol="$" /></td>
                        <td>${ticket.ticketStatus}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <%-- Order History Section (Optional) --%>
    <%--
    <div class="detail-section">
        <h3>Order History</h3>
        <ul>
            <c:forEach items="${orderHistory}" var="event">
                <li>${event.timestamp} - ${event.status} - ${event.description}</li>
            </c:forEach>
        </ul>
    </div>
    --%>


    <%-- Action Buttons --%>
    <div class="actions">
        <c:if test="${booking.bookingStatus != 'Cancelled'}">
            <a href="order?action=cancel&id=${booking.bookingID}" onclick="return confirm('Are you sure you want to cancel this order?');">Cancel Order</a>
           <%-- <a href="order?action=edit&id=${booking.bookingID}">Edit Order</a>--%>
        </c:if>

        <a href="#" onclick="window.print();return false;">Print Ticket</a>  <%-- Basic print functionality --%>
        <a href="order" class="back-button">Go Back</a>
    </div>
</div>
</body>
</html>