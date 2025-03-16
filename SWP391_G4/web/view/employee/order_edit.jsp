<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Order</title>
      <link rel="stylesheet" href="css/employee.css">
    <style>
        /* Add your CSS styles here */
        label {
            display: block;
            margin-bottom: 5px;
        }
        input[type="text"], input[type="datetime-local"], select, input[type="number"], input[type="email"]{
            width: 100%;
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ddd;
            box-sizing: border-box;
        }
        button[type="submit"] {
            padding: 10px 15px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        .form-container {
            width: 50%; /* Adjust as needed */
            margin: 0 auto; /* Center the form */
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
    <div class ="form-container">
    <h1>Edit Order</h1>

    <c:if test="${not empty error}">
        <p style="color: red;">${error}</p>
    </c:if>

    <form action="order" method="post">
        <input type="hidden" name="action" value="update">
        <input type="hidden" name="bookingID" value="${booking.bookingID}"> <%-- IMPORTANT: Include the booking ID --%>

       <label for="trainID">Train:</label>
        <select id="trainID" name="trainID" required onchange="loadTrips(this.value)">
            <c:forEach items="${trains}" var="train">
                <option value="${train.trainID}" ${train.trainID == trip.trainID ? 'selected' : ''}>${train.trainName}</option>
            </c:forEach>
        </select><br>

         <label for="tripID">Trip:</label>
        <select id="tripID" name="tripID" required>
          <option value = "" disabled selected>Select a Trip</option>
           <c:forEach var="trip" items="${trips}">
                <option value="${trip.tripID}" ${trip.tripID == booking.tripID ? 'selected' :''}>${trip.routeName} - ${trip.formattedDepartureTime}</option>
            </c:forEach>
        </select><br>

        <%-- Customer Information (Read-only or editable, depending on your requirements) --%>
        <label for="customerName">Customer Name:</label>
        <input type="text" id="customerName" name="customerName" value="${booking.customerName}" required><br>

        <label for="customerPhone">Phone:</label>
        <input type="text" id="customerPhone" name="customerPhone" value="${booking.customerPhone}" required><br>

        <label for="customerEmail">Email:</label>
        <input type="email" id="customerEmail" name="customerEmail" value="${booking.customerEmail}" required><br>
        
        <label for="customerCCCD">CCCD:</label>
        <input type="text" id="customerCCCD" name="customerCCCD" value="${ticket.cccd}" required><br> <%--- How to get cccd value? -->
        
        <label for="customerAddress">Address</label>
        <input type="text" id = "customerAddress" name="customerAddress" value="${user.address}"><br> <%--- How to get customer address -->
        

        <label for="numTickets">Number of Tickets:</label>
        <input type="number" id="numTickets" name="numTickets" min="1" value="${booking.tickets.size()}" required><br>

       <div id="seatSelection">
            <%-- Seat selection inputs will be added here --%>
        </div>

        <label for="paymentStatus">Payment Status:</label>
            <select id="paymentStatus" name="paymentStatus" required>
                <option value="Pending" ${booking.paymentStatus == 'Pending' ? 'selected' : ''}>Pending</option>
                <option value="Paid" ${booking.paymentStatus == 'Paid' ? 'selected' : ''}>Paid</option>
                <option value="Cancelled" ${booking.paymentStatus == 'Cancelled' ? 'selected' : ''}>Cancelled</option>
                <option value="Refunded" ${booking.paymentStatus == 'Refunded' ? 'selected' : ''}>Refunded</option>
        </select><br>
        
        <label for="bookingStatus">Booking Status:</label>
            <select id="bookingStatus" name="bookingStatus" required>
                <option value="Active" ${booking.bookingStatus == 'Active' ? 'selected' : ''}>Active</option>
                <option value="Expired" ${booking.bookingStatus == 'Expired' ? 'selected' : ''}>Expired</option>
        </select><br>
        <button type="submit">Update Order</button>
    </form>
    </div>
</div>
<script>

    function loadTrips(trainId) {
        const tripSelect = document.getElementById("tripID");
        tripSelect.innerHTML = '<option value="" disabled selected>Select a Trip</option>'; // Clear existing options

        if (!trainId) {
            return; // No train selected, so do nothing.
        }

        // Use AJAX to fetch trips for the selected train.
        fetch(`trip?action=getTrips&trainId=${trainId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(trips => {
                trips.forEach(trip => {
                    const option = document.createElement("option");
                    option.value = trip.tripID;
                    // Format the date/time for display.
                    const departureDateTime = new Date(trip.departureTime);
                    const formattedDeparture = `${departureDateTime.getFullYear()}-${String(departureDateTime.getMonth() + 1).padStart(2, '0')}-${String(departureDateTime.getDate()).padStart(2, '0')} ${String(departureDateTime.getHours()).padStart(2, '0')}:${String(departureDateTime.getMinutes()).padStart(2, '0')}`;

                    option.text = `${trip.routeName} - ${formattedDeparture}`; // Display route and departure
                    tripSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error("Error fetching trips:", error);
                alert("Error loading trips.  See console for details.");
            });
    }
     // Function to update the seat selection UI based on the selected trip
    function updateSeatSelection() {
       //TODO:
    }

    // Add an event listener to the trip dropdown
    document.getElementById("tripID").addEventListener("change", function() {
        updateSeatSelection();
    });

     // Call loadTrips initially to populate the trips dropdown based on pre-selected train
    // This ensures that when the page loads with an existing train selected, the corresponding trips are loaded.
    document.addEventListener('DOMContentLoaded', (event) => {
        const trainSelect = document.getElementById("trainID");
        if (trainSelect.value) { // if a train is selected
            loadTrips(trainSelect.value);
        }
    });

</script>

</body>
</html>