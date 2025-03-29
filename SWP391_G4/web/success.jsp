<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en_US"/>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Đặt vé thành công</title>

        <!-- Link Bootstrap CSS -->
        <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
            rel="stylesheet"
            />

        <!-- Link file CSS riêng (nếu có) -->
        <link href="css/success.css" rel="stylesheet" type="text/css" />

        <style>
            /* CSS tùy chỉnh (có thể bỏ vào file success.css) */
            .success-container {
                max-width: 700px;
                margin: 40px auto;
                background: #f8f9fa;
                padding: 20px 30px;
                border-radius: 8px;
            }

            .success-container h3 {
                margin-bottom: 20px;
            }

            .ticket-info {
                margin-bottom: 15px;
                padding: 15px;
                border: 1px solid #ddd;
                border-radius: 6px;
            }

            .home-link {
                margin-top: 20px;
                display: inline-block;
            }
        </style>
        <jsp:include page="/navbar.jsp"/>
    </head>
    <body>
        <div class="container success-container">
            <h3 class="text-success">🎉 Đặt vé thành công!</h3>
            <p>
                Cảm ơn bạn đã sử dụng dịch vụ đặt vé tàu. Dưới đây là thông tin vé của
                bạn:
            </p>

            <c:forEach var="item" items="${cartItems}" varStatus="status">
                <div class="ticket-info">
                    <h5>Vé ${status.index + 1}</h5>

                    <p>
                        <strong>Loại chuyến:</strong>
                        <c:choose>
                            <c:when test="${item.returnTrip}">
                                <span class="text-danger">Chuyến về</span>
                            </c:when>
                            <c:otherwise>
                                <span class="text-success">Chuyến đi</span>
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <p>
                        <strong>Hành trình:</strong> 
                        <c:choose>
                            <c:when test="${item.returnTrip}">
                                ${item.arrivalStationName} → ${item.departureStationName}
                            </c:when>
                            <c:otherwise>
                                ${item.departureStationName} → ${item.arrivalStationName}
                            </c:otherwise>
                        </c:choose>
                    </p>

                    <p><strong>Tàu:</strong> ${item.trainName} - ${item.departureDate}</p>
                    <p><strong>Toa:</strong> ${item.carriageNumber} - Chỗ ${item.seatNumber}</p>

                    <!-- Chỉ hiển thị giá của vé hiện tại -->
                    <p>
                        <strong>Giá vé:</strong>
                        <fmt:formatNumber value="${item.price}" pattern="#,##0"/> VND
                    </p>

                    <p><strong>Hành khách:</strong> ${fullNameList[status.index]}</p>
                    <p><strong>Số CMND/Hộ chiếu:</strong> ${idNumberList[status.index]}</p>
                    <p><strong>Đối tượng:</strong> ${typeList[status.index]}</p>
                </div>
            </c:forEach>

            <!-- Nếu muốn hiển thị tổng tiền cả booking (tất cả vé) thì đặt ngoài vòng lặp -->
            <c:if test="${not empty cartItems}">
                <p style="margin-top: 20px;">
                    <strong>Tổng tiền toàn bộ vé:</strong>
                    <fmt:formatNumber value="${totalAmount}" pattern="#,##0"/> VND
                </p>
            </c:if>

            <h5 class="mt-4">Thông tin người đặt vé</h5>
            <p><strong>Họ và tên:</strong> ${bookingName}</p>
            <p><strong>Email:</strong> ${bookingEmail}</p>
            <p><strong>Số điện thoại:</strong> ${bookingPhone}</p>

            <p>Vé điện tử đã được gửi đến email của bạn. Vui lòng kiểm tra email để nhận vé.</p>
            <a href="home" class="btn btn-primary home-link">Về trang chủ</a>

        </div>

        <!-- Bootstrap JS (nếu cần JavaScript của Bootstrap) -->
        <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        ></script>
    </body>
</html>