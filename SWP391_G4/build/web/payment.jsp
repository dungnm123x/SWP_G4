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
        <meta charset="UTF-8" />
        <title>Thanh toán</title>

        <!-- Link Bootstrap CSS -->
        <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
            rel="stylesheet"
        />

        <!-- Link file CSS riêng (nếu có) -->
        <link href="css/payment.css" rel="stylesheet" type="text/css" />

        <style>
            .payment-container {
                max-width: 600px;
                margin: 40px auto;
                background: #f8f9fa;
                padding: 20px 30px;
                border-radius: 8px;
            }
            .payment-container h3 {
                margin-bottom: 20px;
            }
            .payment-methods {
                margin-bottom: 20px;
            }
            .btn-space {
                margin-right: 10px;
            }
        </style>

        <jsp:include page="/navbar.jsp"/>
    </head>
    <body>
        <div class="container payment-container">
            <h3 class="text-primary">💳 Thanh toán vé</h3>
            <p>Chọn phương thức thanh toán để hoàn tất đặt vé.</p>

            <!-- Nếu mustLogin = true => hiển popup + chuyển login -->
            <c:if test="${mustLogin == true}">
                <script>
                    alert("Bạn phải đăng nhập trước khi thanh toán!");
                    window.location.href = 'login.jsp';
                </script>
            </c:if>

            <!-- Form POST đến PaymentServlet (mapping = "payment") -->
            <form action="payment" method="post">
                <!-- Ẩn CCCD người đặt (nếu cần) -->
                <input type="hidden" name="bookingCCCD" value="${sessionScope.bookingCCCD}" />

                <div class="payment-methods mb-3">
                    <label class="d-block mb-2">
                        <input
                            type="radio"
                            name="paymentMethod"
                            value="creditCard"
                            required
                        />
                        Thẻ tín dụng/ghi nợ
                    </label>

                    <label class="d-block mb-2">
                        <input
                            type="radio"
                            name="paymentMethod"
                            value="eWallet"
                        />
                        Ví điện tử (Momo, ZaloPay)
                    </label>

                    <label class="d-block mb-2">
                        <input
                            type="radio"
                            name="paymentMethod"
                            value="bankTransfer"
                        />
                        Chuyển khoản ngân hàng
                    </label>

                    <!-- Nếu bạn muốn tích hợp VNPay -->
                    <label class="d-block mb-2">
                        <input
                            type="radio"
                            name="paymentMethod"
                            value="vnpay"
                        />
                        Thanh toán qua VNPay
                    </label>
                </div>

                <button type="submit" class="btn btn-primary btn-space">
                    Xác nhận thanh toán
                </button>
                <a href="passengerinfo" class="btn btn-secondary">Quay lại</a>
            </form>
        </div>

        <!-- Bootstrap JS -->
        <script
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"
        ></script>
    </body>
</html>



