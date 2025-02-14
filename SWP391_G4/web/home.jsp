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
</head>
<body>
    <form action="/searchTicket" method="GET">
    <div class="ticket-search">
        <div class="search-field">
            <label for="departureStation">Ga đi</label>
            <input type="text" id="departureStation" name="departureStation" required />
        </div>
        <div class="search-field">
            <label for="arrivalStation">Ga đến</label>
            <input type="text" id="arrivalStation" name="arrivalStation" required />
        </div>

        <!-- Lựa chọn loại vé -->
        <div class="search-field">
            <label for="ticketType">Loại vé</label>
            <select id="ticketType" name="ticketType" required>
                <option value="one-way">Một chiều</option>
                <option value="round-trip">Khứ hồi</option>
            </select>
        </div>

        <!-- Ngày đi (chỉ hiển thị khi chọn một chiều hoặc khứ hồi) -->
        <div class="search-field" id="departureDateField">
            <label for="departureDate">Ngày đi</label>
            <input type="date" id="departureDate" name="departureDate" required />
        </div>

        <!-- Ngày về (chỉ hiển thị khi chọn khứ hồi) -->
        <div class="search-field" id="returnDateField" style="display: none;">
            <label for="returnDate">Ngày về</label>
            <input type="date" id="returnDate" name="returnDate" />
        </div>

        <div class="submit-button">
            <button type="submit">Tìm kiếm</button>
        </div>
    </div>
</form>

<script>
    // JavaScript để ẩn/hiện ngày về dựa trên loại vé
    document.getElementById('ticketType').addEventListener('change', function() {
        var ticketType = this.value;
        var returnDateField = document.getElementById('returnDateField');
        var departureDateField = document.getElementById('departureDateField');
        
        if (ticketType === 'round-trip') {
            returnDateField.style.display = 'block';
            departureDateField.style.display = 'block';
        } else {
            returnDateField.style.display = 'none';
            departureDateField.style.display = 'block';
        }
    });
</script>

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
