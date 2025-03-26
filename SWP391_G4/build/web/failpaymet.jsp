<%-- 
    Document   : failpaymet
    Created on : Mar 26, 2025, 1:39:52 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Thanh toán không thành công</title>
    <!-- Nhúng Bootstrap CSS -->
    <link href="assets/bootstrap.min.css" rel="stylesheet"/>
    <style>
        body {
            background-color: #F5F7F9;
        }
        .notification-container {
            max-width: 600px;
            margin: 50px auto;
            background-color: #fff;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 10px 20px rgba(0,0,0,0.1);
            text-align: center;
        }
        .notification-container h1 {
            color: #dc3545; /* Màu đỏ */
            margin-bottom: 20px;
        }
        .notification-container p {
            font-size: 16px;
            margin-bottom: 30px;
        }
        .back-link {
            text-decoration: none;
            font-size: 18px;
        }
    </style>
    <jsp:include page="/navbar.jsp"></jsp:include>
</head>
<body>
   
    <div class="notification-container">
        <h1>Thanh toán không thành công!</h1>
        <p>Giao dịch của bạn đã không thành công. Vui lòng kiểm tra lại thông tin thanh toán hoặc thử lại sau.</p>
        <a href="payment" class="btn btn-danger">Thử lại thanh toán</a>
    </div>
    <!-- Nhúng Font Awesome để hiển thị icon (nếu cần) -->
    <link rel="stylesheet" 
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</body>
</html>

