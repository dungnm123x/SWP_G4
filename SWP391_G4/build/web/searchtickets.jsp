<%-- 
    Document   : searchtickets
    Created on : Feb 12, 2025, 11:46:52 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
    <section id="content-container">
        <div class="center-wrap">
            <form method="post" action="schedule" class="search-form">

                <!-- Ga đi -->
                <select name="diemdi" id="diemdi" required>
                    <option value="">Chọn ga đi</option>
                    <c:forEach var="ga" items="${gaList}">
                        <option value="${ga.stationID}" ${ga.stationID == selectedDeparture ? 'selected' : ''}>
                            ${ga.stationName}
                        </option>
                    </c:forEach>
                </select>

                <!-- Ga đến -->
                <select name="diemden" id="diemden" required>
                    <option value="">Chọn ga đến</option>
                    <c:forEach var="ga" items="${gaList}">
                        <option value="${ga.stationID}" ${ga.stationID == selectedArrival ? 'selected' : ''}>
                            ${ga.stationName}
                        </option>
                    </c:forEach>
                </select>

                <!-- Ngày đi -->
                <input type="date" name="ngaydi" id="ngaydi" value="${selectedDate}" required>

                <!-- Loại vé -->
                <select name="loaive" id="loaive" required onchange="toggleReturnDate()">
                    <option value="1" ${selectedTicketType == '1' ? 'selected' : ''}>Một chiều</option>
                    <option value="2" ${selectedTicketType == '2' ? 'selected' : ''}>Khứ hồi</option>
                </select>

                <!-- Ngày về (ẩn nếu chọn "Một chiều") -->
                <div id="returnDateContainer" style="display: ${selectedTicketType == '2' ? 'block' : 'none'};">
                    <input type="date" name="ngayve" id="ngayve" value="${returnDate}">
                </div>

                <button type="submit">Tìm Kiếm</button>
            </form>
        </div>
    </section>

    <script>
        function toggleReturnDate() {
            var loaiVe = document.getElementById("loaive").value;
            var returnDateContainer = document.getElementById("returnDateContainer");

            if (loaiVe == "1") {
                returnDateContainer.style.display = "none";
                document.getElementById("ngayve").value = "";
            } else {
                returnDateContainer.style.display = "block";
            }
        }

        window.onload = function () {
            toggleReturnDate();
        }

        // Kiểm tra nếu ga đi và ga đến giống nhau thì báo lỗi
        document.querySelector("form").addEventListener("submit", function (event) {
            const diemdi = document.getElementById("diemdi").value;
            const diemden = document.getElementById("diemden").value;
            if (diemdi === diemden) {
                alert("Ga đi và ga đến không thể giống nhau!");
                event.preventDefault();
            }
        });
    </script>
</body>
</html>
