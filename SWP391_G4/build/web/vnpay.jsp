<%-- 
    Document   : vnpay
    Created on : Mar 11, 2025, 6:26:44 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8"/>
        <title>Thanh toán qua VNPay</title>
        <!-- Bootstrap CSS nếu cần -->
        <link href="assets/bootstrap.min.css" rel="stylesheet"/>
        <link href="assets/jumbotron-narrow.css" rel="stylesheet">      
        <script src="assets/jquery-1.11.3.min.js"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
    </head>
    <body style="background-color: #F5F7F9">
        <div class="container">
            <a href="confirm.jsp">
                <span style="color: black;font-size: 20px">
                    <i class="fa fa-chevron-circle-left"></i>&nbsp;Quay lại
                </span>
            </a>
        </div>

        <div class="container" style="transform: translateY(-10px); 
             box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2); 
             background-color: white; margin-top: 50px;">
            <div class="header clearfix">
                <h3 class="text-muted">
                    <img src="img/vnlogo.jpg" style="width: 200px;"/>VNPAY
                </h3>
            </div>
            <h3>Tạo yêu cầu thanh toán</h3><br>

            <div class="table-responsive">
                <form action="vnpay" id="frmCreateOrder" method="post">
                    <!-- Lấy totalAmount từ sessionScope (đã được set ở confirm.jsp) -->
                    <div class="form-group">
                        <c:if test="${not empty sessionScope.totalAmount}">
                            <label for="amount" style="font-size: 20px">
                                Số tiền: 
                                <!-- Hiển thị có định dạng -->
                                <fmt:formatNumber value="${sessionScope.totalAmount}" pattern="#,##0"/> $
                            </label>
                            <!-- Gửi amount dạng hidden để servlet VNPayServlet xử lý -->
                            <input 
                                type="hidden" 
                                class="form-control"
                                name="amount"
                                id="amount"
                                value="${sessionScope.totalAmount}"
                            />
                        </c:if>

                        <c:if test="${empty sessionScope.totalAmount}">
                            <label style="color:red;">Chưa có tổng tiền</label>
                            <!-- Tạm để 50000 -->
                            <input type="hidden" name="amount" value="50000" />
                        </c:if>
                    </div>

                    <h4>Chọn phương thức thanh toán</h4>
                    <div class="form-group">

<!--                        <h5>Cách 2: Tách phương thức tại site của đơn vị kết nối</h5>-->
                        <input type="radio" id="bankCode2" name="bankCode" value="VNPAYQR">
                        <label for="bankCode2">Thanh toán bằng ứng dụng hỗ trợ VNPAYQR</label><br>

                        <input type="radio" id="bankCode3" name="bankCode" value="VNBANK">
                        <label for="bankCode3">Thanh toán qua thẻ ATM/Tài khoản nội địa</label><br>

                        <input type="radio" id="bankCode4" name="bankCode" value="INTCARD">
                        <label for="bankCode4">Thanh toán qua thẻ quốc tế</label><br>
                    </div>
                    <div class="form-group">
                        <h5>Chọn ngôn ngữ giao diện thanh toán:</h5>
                        <input type="radio" checked="true" id="languageVN" name="language" value="vn">
                        <label for="languageVN">Tiếng việt</label><br>
                        <input type="radio" id="languageEN" name="language" value="en">
                        <label for="languageEN">Tiếng anh</label><br>
                    </div>
                    <button type="submit" class="btn btn-default">Thanh toán</button>
                </form>
            </div>
            <p>&nbsp;</p>
        </div>

        <!-- Thư viện vnpay -->
        <link href="https://pay.vnpay.vn/lib/vnpay/vnpay.css" rel="stylesheet" />
        <script src="https://pay.vnpay.vn/lib/vnpay/vnpay.min.js"></script>

        <!-- Sử dụng AJAX POST => trả về JSON => Mở popup VNPay -->
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


