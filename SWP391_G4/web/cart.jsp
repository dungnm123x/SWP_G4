<%-- 
    Document   : cart
    Created on : Feb 28, 2025, 12:47:14 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Giỏ vé</title>
        <link href="css/cart.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div class="cart">
            <h3>🛒 Giỏ vé</h3>

            <!-- Nếu giỏ rỗng -->
            <c:if test="${empty cartItems}">
                <p style="color: orange; font-weight: bold;">Chưa có vé</p>
            </c:if>

            <!-- Danh sách vé -->
            <ul>
                <c:forEach var="item" items="${cartItems}">
                    <li>

                        <span>${item.trainName} - ${item.departureDate}</span>
                        <span>Toa ${item.carriageNumber} - Chỗ ${item.seatNumber}</span>
                        <span>Giá: ${item.price}</span>

                        <!-- Nút xóa -->
                        <!-- Cách 1: Gửi POST với removeSeatID -->
                        <form action="cartitem" method="post" style="display:inline;">
                            <input type="hidden" name="departureStationID" value="${param.departureStationID}" />
                            <input type="hidden" name="arrivalStationID"   value="${param.arrivalStationID}" />
                            <input type="hidden" name="departureDay"       value="${param.departureDay}" />
                            <input type="hidden" name="tripType"           value="${param.tripType}" />
                            <input type="hidden" name="returnDate"         value="${param.returnDate}" />

                            <input type="hidden" name="removeSeatID" value="${item.seatID}" />
                            <button type="submit" class="remove-ticket">Xóa</button>
                        </form>

                        <!-- Cách 2: Gửi DELETE (nếu muốn) -->
                        <!--
                        <form action="cartitem?seatID=${item.seatID}" method="post" onsubmit="return confirm('Xóa vé này?');">
                            <input type="hidden" name="_method" value="DELETE"/>
                            <button type="submit">Xóa (DELETE)</button>
                        </form>
                        -->
                    </li>
                </c:forEach>
            </ul>

            <!-- Nút thanh toán -->
            <c:if test="${not empty cartItems}">
                <form action="cartitem" method="post">
                    <input type="hidden" name="action" value="checkout">
                    <button type="submit" class="checkout">Mua vé</button>
                </form>
            </c:if>
        </div>

        <!--        <script>
                    document.querySelectorAll(".remove-ticket").forEach(button => {
                        button.addEventListener("click", function (event) {
                            event.preventDefault(); // Ngăn chặn tải lại trang
                            let seatID = this.getAttribute("data-seatid");
        
                            fetch("cartitem", {
                                method: "POST",
                                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                                body: "seatID=" + encodeURIComponent(seatID)
                            }).then(response => response.text())
                                    .then(data => {
                                        document.querySelector(".cart-container").innerHTML = data; // Cập nhật giỏ vé
                                    }).catch(error => console.error("Lỗi:", error));
                        });
                    });
                </script>-->
    </body>
</html>

