<%-- 
   Document   : schedule
   Created on : Feb 12, 2025, 11:46:05 PM
   Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch Trình Tàu - Đường Sắt Việt Nam</title>
        <link href="css/cart.css" rel="stylesheet" type="text/css"/>
        <jsp:include page="/navbar.jsp"/>

    </head>
    <body>

        <section id="content-container">
            <div class="main-container">
                <div class="schedule-container">
                    <h2>Lịch Trình Tàu</h2>
                    <!-- Bảng hiển thị danh sách chuyến đi -->
                    <h4>Chuyến Đi: ngày ${departureDate} từ ${departureStation} đến ${arrivalStation}  </h4>
                    <table class="table table-bordered">
                        <thead class="bg-primary text-white">
                            <tr>
                                <th>Tàu</th>
                                <th>Thời Gian Khởi Hành</th>
                                <th>Thời Gian Đến</th>
                                <th>Trạng Thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty sessionScope.scheduleList}">
                                    <tr><td colspan="4" class="text-center text-danger">Không có chuyến tàu nào phù hợp!</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="trip" items="${sessionScope.scheduleList}">

                                        <tr>
                                            <td>
                                                <button class="btn btn-primary toggle-train" data-tripid="${trip.tripID}"
                                                        data-isreturn="false">
                                                    ${trip.train.trainName}
                                                </button>
                                            </td>
                                            <td>${trip.departureTime}</td>
                                            <td>${trip.arrivalTime}</td>
                                            <td>${trip.tripStatus}</td>
                                        </tr>
                                        <tr class="train-details-container" id="trip-container-${trip.tripID}" style="display: none;">
                                            <td colspan="4">

                                                <div class="trip-details" id="trip-${trip.tripID}"></div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>

                    <!-- Nếu là vé khứ hồi, hiển thị danh sách chuyến về -->
                    <c:if test="${not empty sessionScope.returnScheduleList}">
                        <h4>Chuyến Về: ngày ${returnDate} từ ${arrivalStation} đến ${departureStation}      </h4>
                        <table class="table table-bordered">
                            <thead>
                                <tr>
                                    <th>Tàu</th>
                                    <th>Thời Gian Khởi Hành</th>
                                    <th>Thời Gian Đến</th>
                                    <th>Trạng Thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="trip" items="${sessionScope.returnScheduleList}">
                                    <tr>
                                        <td>
                                            <button class="btn btn-primary toggle-train" data-tripid="${trip.tripID}"
                                                    data-isreturn="true">
                                                ${trip.train.trainName}
                                            </button>
                                        </td>
                                        <td>${trip.departureTime}</td>
                                        <td>${trip.arrivalTime}</td>
                                        <td>${trip.tripStatus}</td>
                                    </tr>
                                    <tr class="train-details-container" id="trip-container-${trip.tripID}" style="display: none;">
                                        <td colspan="4">
                                            <div class="trip-details" id="trip-${trip.tripID}"></div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:if>

                </div>
                <div class="sidebar-container">
                    <!-- Giỏ vé -->
                    <div class="cart-container">
                        <jsp:include page="/cart.jsp"/>
                    </div>

                    <!-- Form tìm kiếm -->
                    <div class="search-container">
                        <jsp:include page="/searchtickets.jsp">
                            <jsp:param name="layout" value="vertical"/>
                        </jsp:include>

                    </div>
                </div>
            </div>

        </section>

        <script>
            // ---- CHỈ KHAI BÁO MỘT LẦN ----
            const departureStationIDParam = "${param.departureStationID}";
            const arrivalStationIDParam = "${param.arrivalStationID}";
            const departureDayParam = "${param.departureDay}";
            const tripTypeParam = "${param.tripType}";
            const returnDateParam = "${param.returnDate}";

            // Nếu bạn muốn, có thể khai báo THÊM biến attribute:
            const departureStationName = "${departureStation}";
            const arrivalStationName = "${arrivalStation}";
            const departureDateVal = "${departureDate}";
            const selectedTicketType = "${selectedTicketType}";
            const returnDateVal = "${returnDate}";

            document.addEventListener("DOMContentLoaded", function () {
                // 1) Gắn sự kiện .toggle-train
                document.querySelectorAll(".toggle-train").forEach(button => {
                    button.addEventListener("click", function () {
                        let tripID = this.getAttribute("data-tripid");
                        let isReturnParam = this.getAttribute("data-isreturn");
                        let containerRow = document.getElementById("trip-container-" + tripID);
                        let detailsDiv = document.getElementById("trip-" + tripID);
                        if (!detailsDiv)
                            return;

                        // Nếu chưa load ghế -> fetch
                        if (detailsDiv.innerHTML.trim() === "") {
                            let url = "getcarriageseats"
                                    + "?tripID=" + tripID
                                    + "&departureStationID=" + encodeURIComponent(departureStationIDParam)
                                    + "&arrivalStationID=" + encodeURIComponent(arrivalStationIDParam)
                                    + "&departureDay=" + encodeURIComponent(departureDayParam)
                                    + "&tripType=" + encodeURIComponent(tripTypeParam)
                                    + "&returnDate=" + encodeURIComponent(returnDateParam)
                                    + "&isReturnTrip=" + isReturnParam;  // <-- chuyến đi


                            fetch(url)
                                    .then(response => response.text())
                                    .then(data => {
                                        detailsDiv.innerHTML = data;
                                        containerRow.style.display = "table-row";
                                    })
                                    .catch(err => console.error(err));
                        } else {
                            // Đóng/mở
                            containerRow.style.display =
                                    (containerRow.style.display === "none") ? "table-row" : "none";
                        }
                    });
                });

                // 2) Tự động click lại train đã mở trước đó
                let openedTrainID = sessionStorage.getItem("openedTrainID");
                if (openedTrainID) {
                    let btn = document.querySelector(`[data-tripid='${openedTrainID}']`);
                    if (btn) {
                        btn.click();
                    }
                }


                // 3) Lưu lại trainID khi click
                document.querySelectorAll(".toggle-train").forEach(button => {
                    button.addEventListener("click", function () {
                        let tripID = this.getAttribute("data-tripid");
                        sessionStorage.setItem("openedTrainID", tripID);
                    });
                });
            });
        </script>

        <script>
            document.addEventListener("DOMContentLoaded", function () {
                let seats = document.querySelectorAll(".seat");
                seats.forEach(seat => {
                    seat.addEventListener("mouseover", function () {
                        let tooltip = document.createElement("div");
                        tooltip.className = "tooltip";
                        tooltip.innerText = seat.getAttribute("data-tooltip");
                        seat.appendChild(tooltip);
                    });
                    seat.addEventListener("mouseout", function () {
                        let tooltip = seat.querySelector(".tooltip");
                        if (tooltip)
                            seat.removeChild(tooltip);
                    });
                });
            });
            // Hiển thị tooltip khi di chuột vào ghế ngồi
            function showTooltip(element) {
                let tooltip = document.createElement("div");
                tooltip.className = "tooltip";
                tooltip.innerText = element.getAttribute("data-tooltip");
                tooltip.style.position = "absolute";
                tooltip.style.backgroundColor = "rgba(0, 0, 0, 0.7)";
                tooltip.style.color = "white";
                tooltip.style.padding = "5px";
                tooltip.style.fontSize = "12px";
                tooltip.style.borderRadius = "5px";
                tooltip.style.top = (element.offsetTop - 30) + "px";
                tooltip.style.left = (element.offsetLeft + 20) + "px";
                tooltip.style.whiteSpace = "nowrap";
                tooltip.style.zIndex = "10";
                element.appendChild(tooltip);
            }

            // Ẩn tooltip khi di chuột ra khỏi ghế ngồi
            function hideTooltip(element) {
                let tooltip = element.querySelector(".tooltip");
                if (tooltip) {
                    element.removeChild(tooltip);
                }
            }

        </script>
        <script>

            document.body.addEventListener("click", function (event) {
                const target = event.target;
                if (target.classList.contains("seat")) {
                    event.preventDefault();

                    const form = target.closest("form");
                    if (form) {
                        // Tạo FormData
                        const fd = new FormData(form);

                        // Chuyển thành URLSearchParams để servlet parse được
                        const params = new URLSearchParams(fd);

                        fetch("cartitem", {
                            method: "POST",
                            headers: {
                                "Content-Type": "application/x-www-form-urlencoded"
                            },
                            body: params.toString() // "ticketID=xxx&seatID=yyy&price=zzz..."
                        })
                                .then(response => response.text())
                                .then(data => {
                                    document.querySelector(".cart-container").innerHTML = data;
                                })
                                .catch(error => console.error("Lỗi:", error));
                    }
                }
            });


        
            document.addEventListener("DOMContentLoaded", function () {
                document.body.addEventListener("click", function (event) {
                    const target = event.target;
                    // Nếu là nút xóa
                    if (target.classList.contains("btn-remove")) {
                        event.preventDefault(); // Chặn submit form mặc định
                        const form = target.closest("form");
                        if (form) {
                            const fd = new FormData(form);
                            const params = new URLSearchParams(fd);

                            fetch("cartitem", {
                                method: "POST",
                                headers: {
                                    "Content-Type": "application/x-www-form-urlencoded"
                                },
                                body: params.toString() // ticketID=xxx&removeSeatID=yyy&...
                            })
                                    .then(response => response.text())
                                    .then(html => {
                                        document.querySelector(".cart-container").innerHTML = html;
                                    })
                                    .catch(error => console.error("Lỗi:", error));
                        }
                    }
                });
            });

        </script>
        <style>
            .tooltip {
                position: absolute;
                background-color: rgba(0, 0, 0, 0.7);
                color: white;
                padding: 5px;
                font-size: 12px;
                border-radius: 5px;
                top: -30px;
                left: 50%;
                transform: translateX(-50%);
                white-space: nowrap;
                z-index: 10;
            }
        </style>

    </body>
</html>