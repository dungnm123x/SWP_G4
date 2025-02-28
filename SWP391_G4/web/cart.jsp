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
        <title>JSP Page</title>
        <link href="css/cart.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div class="cart">
            <h3>🛒 Giỏ vé</h3>
            <c:choose>
                <c:when test="${empty cartItems}">
                    <p style="color: orange; font-weight: bold;">Chưa có vé</p>
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
