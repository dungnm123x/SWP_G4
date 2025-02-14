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
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>Lịch Trình Tàu - Đường Sắt Việt Nam</title>

        <link href="css/stylesearch.css" rel="stylesheet" type="text/css"/>
        <script src="wp-includes/js/jquery/jquery.js"></script>
    </head>

    <body>
        <div id="tt-wide-layout">
            <div id="tt-header-wrap">
                <header>
                    <div class="center-wrap">
                        <nav>
                            <ul id="menu-main-nav">
                                <li class="menu-item"><a href="home">Trang chủ</a></li>
                            </ul>
                        </nav>
                    </div>
                </header>
            </div>

            <section id="content-container" class="clearfix">
                <div class="center-wrap tt-relative clearfix">
                    <h2>Lịch Trình Tàu</h2>

                    <div class="search-summary">
                        <p><strong>Ga đi:</strong> ${departureStation}</p>
                        <p><strong>Ga đến:</strong> ${arrivalStation}</p>
                        <p><strong>Ngày đi:</strong> ${departureDate}</p>
                    </div>
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
                                                <!-- Nút bấm mở danh sách toa & ghế -->
                                                <button class="btn btn-primary toggle-train" data-trainid="${trip.trainID}">
                                                    ${trainNames[trip.tripID]}
                                                </button>
                                            </td>
                                            <td>${trip.departureTime}</td>
                                            <td>${trip.arrivalTime}</td>
                                            <td>${trip.tripStatus}</td>
                                        </tr>
                                        <!-- Dòng chứa danh sách toa và ghế -->
                                        <tr class="train-details-container" id="train-container-${trip.trainID}" style="display: none;">
                                            <td colspan="4">
                                                <div class="train-details" id="train-${trip.trainID}"></div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                document.querySelectorAll(".toggle-train").forEach(button => {
                    button.addEventListener("click", function () {
                        let trainID = this.getAttribute("data-trainid");
                        let detailsDiv = document.getElementById("train-" + trainID);
                        let containerRow = document.getElementById("train-container-" + trainID);

                        if (detailsDiv.innerHTML === "") {
                            fetch("getcarriageseats?trainID=" + trainID)
                                    .then(response => response.text())
                                    .then(data => {
                                        detailsDiv.innerHTML = data;
                                        containerRow.style.display = "table-row"; // Hiển thị đúng dòng
                                    })
                                    .catch(error => console.error("Lỗi tải dữ liệu: ", error));
                        } else {
                            containerRow.style.display = (containerRow.style.display === "none") ? "table-row" : "none";
                        }
                    });
                });
            });
        </script>
    </body>
</html>

