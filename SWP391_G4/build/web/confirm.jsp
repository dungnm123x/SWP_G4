<%-- 
    Document   : confirm
    Created on : Mar 3, 2025, 10:32:59 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Xác nhận thông tin vé</title>
        <link href="css/confirm.css" rel="stylesheet" type="text/css"/>
    </head>
    <body>
        <div class="confirm-info">
            <h3>✅ Xác nhận thông tin vé</h3>
            <p>Vui lòng kiểm tra lại thông tin trước khi thanh toán.</p>

            <form action="payment.jsp" method="post">
                <c:forEach var="item" items="${cartItems}" varStatus="status">
                    <fieldset>
                        <legend>Vé ${status.index + 1}</legend>
                        <p><strong>Tàu:</strong> ${item.trainName} - ${item.departureDate}</p>
                        <p><strong>Toa:</strong> ${item.carriageNumber} - Chỗ ${item.seatNumber}</p>
                        <p><strong>Giá:</strong> ${item.price} VND</p>

                        <p><strong>Hành khách:</strong> ${param["fullName" + status.index]}</p>
                        <p><strong>Số CMND/Hộ chiếu:</strong> ${param["idNumber" + status.index]}</p>
                        <p><strong>Đối tượng:</strong> ${param["passengerType" + status.index]}</p>
                    </fieldset>
                </c:forEach>

                <h4>Thông tin người đặt vé</h4>
                <p><strong>Họ và tên:</strong> ${param.bookingName}</p>
                <p><strong>Email:</strong> ${param.bookingEmail}</p>
                <p><strong>Số điện thoại:</strong> ${param.bookingPhone}</p>

                <button type="submit">Thanh toán</button>
                <a href="passengerInfo.jsp" class="edit-link">Chỉnh sửa</a>
            </form>
        </div>
    </body>
</html>

