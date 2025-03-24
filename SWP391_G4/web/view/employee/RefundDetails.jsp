<%-- 
    Document   : RefundDetails
    Created on : Mar 24, 2025, 5:14:02 PM
    Author     : dung9
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Chi tiết hóa đơn</title>
        <style>
            /* Reset mặc định để đảm bảo giao diện đồng nhất */
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }

            /* Định dạng chung cho trang */
            body {
                background: linear-gradient(135deg, #e6f0fa 0%, #f5f7fa 100%);
                padding: 30px;
                color: #2C3E50;
                display: flex;
                justify-content: center;
                align-items: flex-start;
                min-height: 100vh;
            }

            /* Container chính để căn giữa nội dung */
            .refund-details-container {
                max-width: 1000px;
                width: 100%;
                background: #fff;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
                margin: 0 auto;
            }

            /* Tiêu đề chính */
            h1 {
                text-align: center;
                color: #1a73e8;
                margin-bottom: 30px;
                font-size: 32px;
                font-weight: 600;
                text-transform: uppercase;
                letter-spacing: 1px;
            }

            /* Thông báo lỗi */
            .error-message {
                background-color: #fce4e4;
                color: #d32f2f;
                padding: 12px;
                border-radius: 8px;
                margin-bottom: 20px;
                text-align: center;
                font-size: 14px;
                font-weight: 500;
            }

            /* Tiêu đề phần */
            .section-title {
                font-size: 20px;
                color: #2C3E50;
                margin-bottom: 15px;
                padding-bottom: 8px;
                border-bottom: 2px solid #1a73e8;
                display: inline-block;
                font-weight: 600;
            }

            /* Block chung cho Booking và Customer Information */
            .info-block {
                display: flex;
                flex-wrap: wrap;
                gap: 20px;
                margin-bottom: 30px;
                background-color: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
            }

            /* Mỗi phần Booking và Customer chiếm 50% */
            .info-block .info-section {
                flex: 1;
                min-width: 300px;
            }

            /* Định dạng các đoạn thông tin */
            .info-section p {
                font-size: 16px;
                margin-bottom: 12px;
                display: flex;
                align-items: center;
            }

            .info-section p strong {
                color: #2c3e50;
                font-weight: 600;
                display: inline-block;
                width: 180px;
                min-width: 180px;
            }

            /* Định dạng bảng thông tin vé */
            table {
                width: 100%;
                border-collapse: collapse;
                margin-bottom: 30px;
                background-color: #fff;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
                border-radius: 8px;
                overflow: hidden;
            }

            table th, table td {
                padding: 12px 10px; /* Giảm padding để tiết kiệm không gian */
                text-align: left;
                border-bottom: 1px solid #e0e0e0;
                white-space: nowrap; /* Ngăn text bị xuống dòng */
            }

            table th {
                background-color: #2C3E50;
                color: #fff;
                font-weight: 600;
                font-size: 12px; /* Giảm kích thước chữ trong tiêu đề để tiết kiệm không gian */
                text-transform: uppercase;
            }

            table td {
                font-size: 13px; /* Giảm kích thước chữ trong nội dung */
                color: #2C3E50;
            }

            /* Đảm bảo bảng cuộn ngang khi cần */
            .table-wrapper {
                overflow-x: auto;
                margin-bottom: 20px;
            }

            table tr:last-child td {
                border-bottom: none;
            }

            table tr:nth-child(even) {
                background-color: #f8f9fa;
            }

            table tr:hover {
                background-color: #e3f0ff;
                transition: background-color 0.3s ease;
            }

            /* Định dạng thông báo khi không có vé */
            .no-tickets {
                color: #d32f2f;
                font-style: italic;
                text-align: center;
                margin-bottom: 20px;
                font-size: 16px;
                background-color: #fff3f3;
                padding: 12px;
                border-radius: 8px;
            }

            /* Định dạng nút quay lại */
            .back-link {
                display: inline-block;
                padding: 12px 24px;
                background-color: #6c757d;
                color: #fff;
                text-decoration: none;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 500;
                transition: background-color 0.3s ease, transform 0.2s ease;
                text-align: center;
            }

            .back-link:hover {
                background-color: #5a6268;
                transform: translateY(-2px);
            }

            /* Responsive cho màn hình nhỏ */
            @media (max-width: 768px) {
                body {
                    padding: 15px;
                }

                .order-details-container {
                    padding: 20px;
                }

                h1 {
                    font-size: 26px;
                }

                .section-title {
                    font-size: 18px;
                }

                .info-block {
                    flex-direction: column;
                }

                .info-section p {
                    font-size: 14px;
                    flex-direction: column;
                    align-items: flex-start;
                }

                .info-section p strong {
                    width: auto;
                    margin-bottom: 5px;
                }

                .table-wrapper {
                    overflow-x: auto;
                }

                table th, table td {
                    padding: 8px;
                    font-size: 11px;
                }

                .back-link {
                    padding: 10px 20px;
                    font-size: 14px;
                }
            }

            @media (max-width: 480px) {
                h1 {
                    font-size: 22px;
                }

                .section-title {
                    font-size: 16px;
                }

                .info-section p {
                    font-size: 13px;
                }

                table th, table td {
                    font-size: 10px;
                }
            }
        </style>
    </head>
    <body>
        <div class="refund-details-container">
            <h1>Chi tiết hóa đơn</h1>

            <c:if test="${not empty error}">
                <p class="error-message">${error}</p>
            </c:if>

            <!-- Block chung cho Refund Information và Customer Information -->
            <div class="info-block">
                <!-- Refund Information -->
                <div class="info-section">
                    <div class="section-title">Thông tin đặt vé:</div>
                    <p><strong>Id hóa đơn:</strong> ${refund.refundID}</p>
                    <p><strong>Ngày đặt:</strong> ${refund.refundDate}</p>
                    <p><strong>Tình trạng:</strong> ${refund.refundStatus}</p>
                    <p><strong>Số vé:</strong> ${fn:length(refund.tickets)}</p>
                    <p><strong>Tổng giá:</strong> <fmt:formatNumber value="${refund.totalRefund}" type="currency" currencySymbol="VND" /></p>
                </div>

                <!-- Customer Information -->
                <div class="info-section">
                    <div class="section-title">Thông tin người đặt:</div>
                    <p><strong>Tên:</strong> ${refund.customerName}</p>
                    <p><strong>SĐT:</strong> ${refund.phoneNumber}</p>
                    <p><strong>Email:</strong> ${refund.customerEmail}</p>
                    <p><strong>Ngân hàng</strong> ${refund.bankName}</p>
                    <p><strong>Số tài khoản</strong> ${refund.bankAccountID}</p>
                </div>
            </div>
            <h3>Danh sách vé đã hủy</h3>
            <c:choose>
                <c:when test="${not empty refund.tickets}">
                    <table border="1">
                        <thead>
                            <tr>
                                <th>ID Vé</th>
                                <th>Tuyến</th>
                                <th>Tàu</th>
                                <th>Khởi hành</th>
                                <th>Toa</th>
                                <th>Ghế</th>
                                <th>Hành khách</th>
                                <th>CCCD</th>
                                <th>Giá vé</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="ticket" items="${refund.tickets}">
                                <tr>
                                    <td>${ticket.ticketID}</td>
                                    <td>${ticket.routeName}</td>
                                    <td>${ticket.trainName}</td>
                                    <td>${ticket.formattedDepartureTime}</td>
                                    <td>${ticket.carriageNumber}</td>
                                    <td>${ticket.seatNumber}</td>
                                    <td>${ticket.passengerName}</td>
                                    <td>${ticket.cccd}</td>
                                    <td><fmt:formatNumber value="${ticket.ticketPrice}" type="currency" currencySymbol="VND" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p class="no-tickets">Không có vé đã hủy.</p>
                </c:otherwise>
            </c:choose>
            <div style="text-align: center;">
                <a href="refund" class="back-link">Trở về</a>
            </div>
            <c:if test="${refund.refundStatus eq 'Wait'}">
                <form action="confirmRefund" method="post">
                    <input type="hidden" name="refundID" value="${refund.refundID}" />
                    <input type="hidden" name="customerEmail" value="${refund.customerEmail}" />
                    <button type="submit" class="confirm-btn">Xác nhận hoàn tiền</button>
                </form>
            </c:if>
        </div>
    </body>
</html>
