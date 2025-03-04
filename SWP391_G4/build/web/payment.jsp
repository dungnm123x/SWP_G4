<%-- 
    Document   : payment
    Created on : Mar 3, 2025, 10:35:08 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Thanh toán</title>
    <link href="css/payment.css" rel="stylesheet" type="text/css"/>
</head>
<body>
    <div class="payment-info">
        <h3>💳 Thanh toán vé</h3>
        <p>Chọn phương thức thanh toán để hoàn tất đặt vé.</p>
        
        <form action="BookingServlet" method="post">
            <c:forEach var="item" items="${cartItems}" varStatus="status">
                <input type="hidden" name="tripID" value="${item.tripID}" />
                <input type="hidden" name="seatID" value="${item.seatID}" />
                <input type="hidden" name="cccd" value="${param["idNumber" + status.index]}" />
                <input type="hidden" name="price" value="${item.price}" />
            </c:forEach>
            
            <h4>Chọn phương thức thanh toán</h4>
            <label>
                <input type="radio" name="paymentMethod" value="creditCard" required> Thẻ tín dụng/ghi nợ
            </label>
            <label>
                <input type="radio" name="paymentMethod" value="eWallet"> Ví điện tử (Momo, ZaloPay)
            </label>
            <label>
                <input type="radio" name="paymentMethod" value="bankTransfer"> Chuyển khoản ngân hàng
            </label>
            
            <h4>Thông tin người đặt vé</h4>
            <p><strong>Họ và tên:</strong> ${param.bookingName}</p>
            <p><strong>Email:</strong> ${param.bookingEmail}</p>
            <p><strong>Số điện thoại:</strong> ${param.bookingPhone}</p>
            
            <button type="submit">Xác nhận thanh toán</button>
            <a href="confirm.jsp" class="edit-link">Quay lại</a>
        </form>
    </div>
</body>
</html>

