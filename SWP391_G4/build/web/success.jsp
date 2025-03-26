<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Thanh toán qua VNPay</title>
    <!-- Bootstrap CSS -->
    <link href="assets/bootstrap.min.css" rel="stylesheet"/>
    <link href="assets/jumbotron-narrow.css" rel="stylesheet">      
    <script src="assets/jquery-1.11.3.min.js"></script>
    <link rel="stylesheet" 
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    <style>
        body {
            background-color: #F5F7F9;
        }
        .payment-container {
            max-width: 600px;
            margin: 50px auto;
            background-color: #fff;
            padding: 30px;
            box-shadow: 0 10px 20px rgba(0,0,0,0.1);
            border-radius: 8px;
        }
        .payment-header {
            text-align: center;
            margin-bottom: 20px;
        }
        .payment-header img {
            width: 200px;
        }
        .form-label {
            font-size: 20px;
        }
    </style>
</head>
<body>
    <div class="container mt-3">
        <a href="payment" class="text-decoration-none">
            <span style="color: black; font-size: 20px">
                <i class="fa fa-chevron-circle-left"></i>&nbsp;Quay lại
            </span>
        </a>
    </div>
    <div class="payment-container">
        <div class="payment-header">
            <h3 class="text-muted">
                <img src="img/vnlogo.jpg" alt="VNPay Logo"/>
                VNPAY
            </h3>
            <h3>Tạo yêu cầu thanh toán</h3>
        </div>
        <div class="table-responsive">
            <form action="vnpay" id="frmCreateOrder" method="post">
                <div class="form-group mb-3">
                    <c:if test="${not empty sessionScope.totalAmount}">
                        <label for="amount" class="form-label">
                            Số tiền: 
                            <fmt:formatNumber value="${sessionScope.totalAmount}" pattern="#,##0"/> VNĐ
                        </label>
                        <input type="hidden" class="form-control" name="amount" id="amount" value="${sessionScope.totalAmount}" />
                    </c:if>
                    <c:if test="${empty sessionScope.totalAmount}">
                        <label class="form-label text-danger">Chưa có tổng tiền</label>
                        <input type="hidden" name="amount" value="50000" />
                    </c:if>
                </div>
                <h4>Chọn phương thức thanh toán</h4>
                <div class="form-group mb-3">
                    <div class="form-check">
                        <input class="form-check-input" type="radio" id="bankCode2" name="bankCode" value="VNPAYQR">
                        <label class="form-check-label" for="bankCode2">Thanh toán bằng ứng dụng hỗ trợ VNPAYQR</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" id="bankCode3" name="bankCode" value="VNBANK">
                        <label class="form-check-label" for="bankCode3">Thanh toán qua thẻ ATM/Tài khoản nội địa</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" id="bankCode4" name="bankCode" value="INTCARD">
                        <label class="form-check-label" for="bankCode4">Thanh toán qua thẻ quốc tế</label>
                    </div>
                </div>
                <div class="form-group mb-3">
                    <h5>Chọn ngôn ngữ giao diện thanh toán:</h5>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" checked="true" id="languageVN" name="language" value="vn">
                        <label class="form-check-label" for="languageVN">Tiếng Việt</label>
                    </div>
                    <div class="form-check">
                        <input class="form-check-input" type="radio" id="languageEN" name="language" value="en">
                        <label class="form-check-label" for="languageEN">Tiếng Anh</label>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Thanh toán</button>
            </form>
        </div>
    </div>
    <!-- Thư viện vnpay -->
    <link href="https://pay.vnpay.vn/lib/vnpay/vnpay.css" rel="stylesheet" />
    <script src="https://pay.vnpay.vn/lib/vnpay/vnpay.min.js"></script>
    <script type="text/javascript">
        $("#frmCreateOrder").submit(function (e) {
            e.preventDefault();
            var postData = $(this).serialize();
            var submitUrl = $(this).attr("action");
            $.ajax({
                type: "POST",
                url: submitUrl,
                data: postData,
                dataType: 'JSON',
                success: function (x) {
                    if (x.code === '00') {
                        if (window.vnpay) {
                            vnpay.open({width: 768, height: 600, url: x.data});
                        } else {
                            location.href = x.data;
                        }
                    } else {
                        alert(x.Message);
                    }
                }
            });
        });
    </script>
</body>
</html>
