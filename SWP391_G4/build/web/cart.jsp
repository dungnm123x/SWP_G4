<%-- 
    Document   : cart
    Created on : Feb 27, 2025, 10:22:55 PM
    Author     : Admin
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Giỏ Vé</title>
        <link href="css/stylesearch.css" rel="stylesheet" type="text/css"/>

    </head>
    <body>

        <div class="cart">
            <h3>🛒 Giỏ vé</h3>
            <c:choose>
                <c:when test="${empty cartItems}">
                    <p>Chưa có vé</p>
                </c:when>
                <c:otherwise>
                    <ul>
                        <c:forEach var="item" items="${cartItems}">
                            <li>
                                <span>${item.trainName} - ${item.departureDate}</span>
                                <span>Toa ${item.carriageNumber} Chỗ ${item.seatNumber}</span>
                                <button class="remove-ticket" data-ticketid="${item.ticketID}">🗑</button>
                            </li>
                        </c:forEach>
                    </ul>
                    <button class="checkout">Mua vé</button>
                </c:otherwise>
            </c:choose>
        </div>


    </body>
</html>
