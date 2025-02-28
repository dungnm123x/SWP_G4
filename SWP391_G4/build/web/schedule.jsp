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

        <script src="wp-includes/js/jquery/jquery.js"></script>

        <jsp:include page="/navbar.jsp"/>

    </head>
    <body>

        <section id="content-container">
            <div class="main-container">
                <div class="schedule-container">
                    <h2>Lịch Trình Tàu</h2>

                    <div class="search-summary">
                        <p><strong>Ga đi:</strong> ${departureStation}</p>
                        <p><strong>Ga đến:</strong> ${arrivalStation}</p>
                        <p><strong>Ngày đi:</strong> ${departureDate}</p>
                    </div>

                    <!-- Bảng hiển thị danh sách chuyến đi -->
                    <h3>Chuyến Đi</h3>
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
                                <c:when test="${empty scheduleList}">
                                    <tr><td colspan="4" class="text-center text-danger">Không có chuyến tàu nào phù hợp!</td></tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="trip" items="${scheduleList}">
                                        <tr>
                                            <td>
                                                <button class="btn btn-primary toggle-train" data-trainid="${trip.train.trainID}">
                                                    ${trip.train.trainName}
                                                </button>
                                            </td>
                                            <td>${trip.departureTime}</td>
                                            <td>${trip.arrivalTime}</td>
                                            <td>${trip.tripStatus}</td>
                                        </tr>
                                        <tr class="train-details-container" id="train-container-${trip.train.trainID}" style="display: none;">
                                            <td colspan="4">
                                                <div class="train-details" id="train-${trip.train.trainID}"></div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>

                    <!-- Nếu là vé khứ hồi, hiển thị danh sách chuyến về -->
                    <c:if test="${not empty returnScheduleList}">
                        <h3>Chuyến Về</h3>
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
                                <c:forEach var="trip" items="${returnScheduleList}">
                                    <tr>
                                        <td>
                                            <button class="btn btn-primary toggle-train" data-trainid="${trip.train.trainID}">
                                                ${trip.train.trainName}
                                            </button>
                                        </td>
                                        <td>${trip.departureTime}</td>
                                        <td>${trip.arrivalTime}</td>
                                        <td>${trip.tripStatus}</td>
                                    </tr>
                                    <tr class="train-details-container" id="train-container-${trip.train.trainID}" style="display: none;">
                                        <td colspan="4">
                                            <div class="train-details" id="train-${trip.train.trainID}"></div>
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
            document.addEventListener("DOMContentLoaded", function () {
                document.querySelectorAll(".toggle-train").forEach(button => {
                    button.addEventListener("click", function () {
                        let trainID = this.getAttribute("data-trainid");
                        let detailsDiv = document.getElementById("train-" + trainID);
                        let containerRow = document.getElementById("train-container-" + trainID);

                        if (!detailsDiv || !containerRow) {
                            console.error("Không tìm thấy container cho trainID:", trainID);
                            return;
                        }

                        if (detailsDiv.innerHTML.trim() === "") {
                            fetch("getcarriageseats?trainID=" + trainID)
                                    .then(response => response.text())
                                    .then(data => {
                                        detailsDiv.innerHTML = data;
                                        containerRow.style.display = "table-row";
                                    })
                                    .catch(error => console.error("Lỗi tải dữ liệu ghế:", error));
                        } else {
                            containerRow.style.display = (containerRow.style.display === "none") ? "table-row" : "none";
                        }
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


