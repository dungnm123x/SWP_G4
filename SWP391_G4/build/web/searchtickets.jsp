<%-- 
    Document   : searchtickets
    Created on : Feb 12, 2025, 11:46:52 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page import="java.time.LocalDate" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Tìm Kiếm Vé - Đường Sắt Việt Nam</title>

        <link href="css/stylesearch.css" rel="stylesheet" type="text/css"/>
        <script src="wp-includes/js/jquery/jquery.js"></script>
    </head>

    <body>

        <%-- Kiểm tra nếu có tham số layout, mặc định là "horizontal" nếu không có --%>
        <%
            String layout = request.getParameter("layout");
            if (layout == null) {
                layout = "horizontal"; // Mặc định là ngang
            }
        %>

        <div class="search-container <%= layout.equals("vertical") ? "search-vertical" : "search-horizontal" %>">
            <form method="post" action="schedule" class="search-form">
                <!-- Ga đi -->
                <select name="departureStationID" id="departureStationID" required>
                    <option value="">Chọn ga đi</option>
                    <c:forEach var="ga" items="${gaList}">
                        <!-- so sánh ga.stationID với selectedDeparture -->
                        <option value="${ga.stationID}"
                                ${ga.stationID == selectedDeparture ? 'selected' : ''}>
                            ${ga.stationName}
                        </option>
                    </c:forEach>
                </select>

                <!-- Ga đến -->
                <select name="arrivalStationID" id="arrivalStationID" required>
                    <option value="">Chọn ga đến</option>
                    <c:forEach var="ga" items="${gaList}">
                        <option value="${ga.stationID}"
                                ${ga.stationID == selectedArrival ? 'selected' : ''}>
                            ${ga.stationName}
                        </option>
                    </c:forEach>
                </select>

                <!-- Ngày đi -->
                <!-- Ngày đi -->
                <input type="date" name="departureDay" id="departureDay"
                       value="${selectedDate}"
                       min="${minDate}" 
                       required />



                <!-- Loại vé -->
                <select name="tripType" id="tripType" required onchange="toggleReturnDate()">
                    <option value="1" ${selectedTicketType == '1' ? 'selected' : ''}>Một chiều</option>
                    <option value="2" ${selectedTicketType == '2' ? 'selected' : ''}>Khứ hồi</option>
                </select>

                <!-- Ngày về (ẩn nếu chọn "Một chiều") -->
                <div id="returnDateContainer" style="display: ${selectedTicketType == '2' ? 'block' : 'none'};">
                    <input type="date" name="returnDate" id="returnDate"
                           value="${returnDate}"   min="${minDate}" 
                            />
                </div>

                <button type="submit">Tìm Kiếm</button>
            </form>
        </div>

        <script>
            function toggleReturnDate() {
                var loaiVe = document.getElementById("tripType").value;
                var returnDateContainer = document.getElementById("returnDateContainer");
                if (loaiVe === "1") {
                    returnDateContainer.style.display = "none";
                    document.getElementById("returnDate").value = "";
                } else {
                    returnDateContainer.style.display = "block";
                }
            }
            window.onload = function () {
                toggleReturnDate();
            }

            // Kiểm tra nếu ga đi và ga đến giống nhau thì báo lỗi
            document.querySelector("form").addEventListener("submit", function (event) {
                const diemdi = document.getElementById("departureStationID").value;
                const diemden = document.getElementById("arrivalStationID").value;
                if (diemdi === diemden) {
                    alert("Ga đi và ga đến không thể giống nhau!");
                    event.preventDefault();
                }
            });
        </script>
    </body>
</html>
