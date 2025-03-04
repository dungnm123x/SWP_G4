<%-- 
    Document   : success
    Created on : Mar 3, 2025, 10:38:30 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Đặt vé thành công</title>
    <link href="css/success.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <div class="success-info">
        <h3>🎉 Đặt vé thành công!</h3>
        <p>Cảm ơn bạn đã sử dụng dịch vụ đặt vé tàu. Dưới đây là thông tin vé của bạn:</p>
        
        <c:forEach var="item" items="${cartItems}" varStatus="status">
            <fieldset>
                <legend>Vé ${status.index + 1}</legend>
                <p><strong>Tàu:</strong> ${item.trainName} - ${item.departureDate}</p>
                <p><strong>Toa:</strong> ${item.carriageNumber} - Chỗ ${item.seatNumber}</p>
                <p><strong>Giá:</strong> ${item.price} VND</p>
                <p><strong>Hành khách:</strong> ${param["fullName" + status.index]}</p>
                <p><strong>Số CMND/Hộ chiếu:</strong> ${param["idNumber" + status.index]}</p>
            </fieldset>
        </c:forEach>
        
        <h4>Thông tin người đặt vé</h4>
        <p><strong>Họ và tên:</strong> ${param.bookingName}</p>
        <p><strong>Email:</strong> ${param.bookingEmail}</p>
        <p><strong>Số điện thoại:</strong> ${param.bookingPhone}</p>
        
        <p>Vé điện tử đã được gửi đến email của bạn. Vui lòng kiểm tra email để nhận vé.</p>
        
        <a href="index.jsp" class="home-link">Về trang chủ</a>
    </div>
</body>
</html>
