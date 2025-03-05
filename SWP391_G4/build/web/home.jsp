<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Trang Chủ - Bán Vé Tàu</title>
        <link rel="icon" href="images/logo-title.png" type="image/x-icon"/>
        <link rel="stylesheet" href="css/home.css">
        <jsp:include page="/navbar.jsp"></jsp:include>
        <jsp:include page="/searchtickets.jsp">
            <jsp:param name="layout" value="horizontal"/>
        </jsp:include>

    </head>
    <body>

        <section class="about">
            <div class="container">
                <h2>Về chúng tôi</h2>
                <p>Chúng tôi cung cấp dịch vụ bán vé tàu với hệ thống đặt vé trực tuyến đơn giản và nhanh chóng.</p>
            </div>
        </section>

        <section class="services">
            <div class="container">
                <h2>Dịch vụ của chúng tôi</h2>
                <div class="service-item">
                    <i class="fas fa-train"></i>
                    <h3>Vé tàu đường dài</h3>
                    <p>Chúng tôi cung cấp vé tàu cho các chuyến đi đường dài với dịch vụ chất lượng cao.</p>
                </div>
                <div class="service-item">
                    <i class="fas fa-ticket-alt"></i>
                    <h3>Đặt vé trực tuyến</h3>
                    <p>Hệ thống đặt vé trực tuyến dễ sử dụng cho phép bạn đặt vé mọi lúc mọi nơi.</p>
                </div>
            </div>
        </section>

        <footer>
            <div class="container">
                <p>&copy; 2025 Dịch vụ bán vé tàu. Mọi quyền được bảo lưu.</p>
            </div>
        </footer>
    </body>
</html>
