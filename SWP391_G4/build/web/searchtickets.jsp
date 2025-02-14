<%-- 
    Document   : searchtickets
    Created on : Feb 12, 2025, 11:46:52 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
        <div id="tt-wide-layout">
            <div id="tt-header-wrap">
                <header>
                    <div class="center-wrap">
                        <nav>
                            <ul id="menu-main-nav">
                                <li class="menu-item"><a href="index.jsp">Trang chủ</a></li>
                            </ul>
                        </nav>
                    </div>
                </header>
            </div>

            <section id="content-container" class="clearfix">
                <div class="center-wrap tt-relative clearfix">
                    <h2>Tìm kiếm vé</h2>

                    <form method="post" action="schedule">
                        <label for="diemdi">Ga đi:</label>
                        <select name="diemdi" id="diemdi" required>
                            <option value="">Chọn ga đi</option>
                            <c:if test="${not empty gaList}">
                                <c:forEach var="ga" items="${gaList}">
                                    <option value="${ga.stationID}">${ga.stationName}</option>
                                </c:forEach>
                            </c:if>
                        </select>


                        <label for="diemden">Ga đến:</label>
                        <select name="diemden" id="diemden" required>
                            <option value="">Chọn ga đến</option>
                            <c:forEach var="ga" items="${gaList}">
                                <option value="${ga.stationID}">${ga.stationName}</option>
                            </c:forEach>
                        </select>

                        <label for="ngaydi">Ngày đi:</label>
                        <input type="date" name="ngaydi" id="ngaydi" required>

                        <label for="loaive">Loại vé:</label>
                        <select name="loaive" id="loaive" required onchange="toggleReturnDate()">
                            <option value="1">Một chiều</option>
                            <option value="2">Khứ hồi</option>
                        </select>

                        <div id="returnDateContainer">
                            <label for="ngayve">Ngày về:</label>
                            <input type="date" name="ngayve" id="ngayve">
                        </div>

                        <button type="submit">Tìm Kiếm</button>
                    </form>
                </div>
            </section>
        </div>

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