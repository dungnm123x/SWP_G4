<%-- 
    Document   : confirm
    Created on : Mar 3, 2025, 10:32:59 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en_US"/>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Xác nhận thông tin đặt mua vé tàu</title>
        <!-- Bootstrap CSS -->
        <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
            rel="stylesheet"
            />
        <style>
            /* Nếu muốn tùy chỉnh thêm, bạn có thể viết CSS ở đây hoặc file .css riêng */
            .header-title {
                margin-top: 20px;
                margin-bottom: 30px;
            }
            .info-section h5 {
                font-weight: bold;
                margin-bottom: 15px;
            }
            .info-section ul {
                padding-left: 0;
                list-style: none;
            }
            .table thead th {
                background-color: #f8f9fa; /* màu nền header */
            }
            .table tfoot td {
                font-weight: bold;
            }
            .btn-space {
                margin-right: 10px;
            }
            /* Tùy biến thêm nếu cần */
        </style>
        <jsp:include page="/navbar.jsp"/>
    </head>
    <body>
        <div class="container mt-5">
            <h3 class="text-primary header-title">Xác nhận thông tin đặt mua vé tàu</h3>

            <!-- Phần hiển thị thông tin người mua vé & thông tin hóa đơn -->
            <div class="row mb-4">
                <!-- Bên trái: Thông tin người mua vé -->
                <div class="col-md-6 info-section">
                    <h5>Thông tin người mua vé</h5>
                    <ul>
                        <li><strong>Họ và tên:</strong> ${param.bookingName}</li>
                        <li><strong>Số CMND/Hộ chiếu:</strong> ${param.bookingCCCD}<!-- Bạn có thể chèn thêm nếu cần --></li>
                        <li><strong>Số điện thoại:</strong> ${param.bookingPhone}</li>
                        <li><strong>Email:</strong> ${param.bookingEmail}</li>
                    </ul>
                </div>

                <!-- Bên phải: Thông tin hóa đơn (nếu có) -->
                <!--                <div class="col-md-6 info-section">
                                    <h5>Thông tin hóa đơn</h5>
                                    <ul>
                                        <li><strong>Tên công ty:</strong>  Nếu cần  </li>
                                        <li><strong>MST:</strong>  Nếu cần  </li>
                                        <li><strong>Địa chỉ:</strong>  Nếu cần  </li>
                                        <li><strong>Phương thức thanh toán:</strong> Thanh toán trực tuyến (VNPAY)  Ví dụ  </li>
                                    </ul>
                                </div>-->
            </div>

            <!-- Form gửi sang payment.jsp (hoặc PaymentServlet) -->
            <form action="payment" method="get">
                <h5 class="mb-3">Thông tin vé mua</h5>
                <div class="table-responsive">
                    <table class="table table-bordered text-center align-middle">
                        <thead>
                            <tr>
                                <th>STT</th>
                                <th>Thông tin vé mua</th>
                                <th>Giá (VNĐ)</th>
                                <th>Thành tiền (VNĐ)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${sessionScope.cartItems}" varStatus="status">
                                <tr>
                                    <td>${status.index + 1}</td>
                                    <td class="text-start">
                                        <!-- Hiển thị loại chuyến -->
                                        <strong>Loại chuyến:</strong>
                                        <c:choose>
                                            <c:when test="${item.returnTrip}">
                                                <span class="text-danger">Chuyến về</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-success">Chuyến đi</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <br/>

                                        <!-- Hiển thị thông tin vé -->
                                        <strong>Hành trình:</strong> 
                                        <c:choose>
                                            <c:when test="${item.returnTrip}">
                                                ${item.arrivalStationName} → ${item.departureStationName}
                                            </c:when>
                                            <c:otherwise>
                                                ${item.departureStationName} → ${item.arrivalStationName}
                                            </c:otherwise>
                                        </c:choose>
                                        <br/>

                                        <strong>Tàu:</strong> ${item.trainName} - ${item.departureDate}<br>
                                        <strong>Toa:</strong> ${item.carriageNumber}, Chỗ ${item.seatNumber}<br>

                                        <!-- Thông tin hành khách (nếu bạn đang lưu) -->
                                        <strong>Hành khách:</strong> ${sessionScope.fullNameList[status.index]} <br>
                                        <strong>Số CMND/Hộ chiếu:</strong> ${sessionScope.idNumberList[status.index]} <br>
                                        <strong>Đối tượng:</strong> ${sessionScope.typeList[status.index]}
                                    </td>
                                    <td>
                                        <fmt:formatNumber value="${item.price}" pattern="#,##0.##" />
                                    </td>

                                    <td>
                                        <fmt:formatNumber value="${sessionScope.finalPriceList[status.index]}" pattern="#,##0.##"/>
                                    </td>


                                </tr>
                            </c:forEach>

                        </tbody>
                        <tfoot>
                            <c:if test="${not empty totalAmount}">
                                <tr>
                                    <td colspan="3" class="text-end">Tổng cộng:</td>
                                    <td>
                                        <fmt:formatNumber value="${totalAmount}" pattern="#,##0.##" /> VNĐ
                                    </td>

                                </tr>
                            </c:if>
                        </tfoot>
                    </table>
                </div>

                <p class="mt-3">
                    Vui lòng kiểm tra kỹ và xác nhận các thông tin đã nhập trước khi thực
                    hiện giao dịch mua vé. Sau khi thực hiện giao dịch, bạn sẽ không thể thay
                    đổi thông tin vé.
                </p>

                <!-- Ẩn totalAmount nếu muốn gửi qua payment.jsp -->
                <c:if test="${not empty totalAmount}">
                    <input type="hidden" name="totalAmount" value="${totalAmount}" />
                </c:if>

                <div class="d-flex justify-content-between mt-4">
                    <!-- confirm.jsp -->
                    <c:url var="reInputURL" value="passengerinfo">
                        <c:param name="tripID" value="${param.tripID}" />
                        <c:param name="departureStationID" value="${param.departureStationID}" />
                        <c:param name="arrivalStationID" value="${param.arrivalStationID}" />
                        <c:param name="departureDay" value="${param.departureDay}" />
                        <c:param name="tripType" value="${param.tripType}" />
                        <c:param name="returnDate" value="${param.returnDate}" />
                    </c:url>


                    <a href="${reInputURL}" class="btn btn-secondary">
                        Nhập lại
                    </a>




                    <!-- confirm.jsp -->
                    <a href="payment" class="btn btn-primary">
                        Chọn phương thức thanh toán
                    </a>


                </div>
            </form>
        </div>

        <!-- Bootstrap JS (nếu cần dùng) -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>




