<%-- 
    Document   : RefundDetails
    Created on : Mar 24, 2025, 5:14:02 PM
    Author     : dung9
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<link rel="stylesheet" href="css/employee.css">
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
                display: inline-block !important; /* Đảm bảo nút không bị kéo dài */
                width: auto !important; /* Không cho phép kéo dài */
                padding: 10px 16px;
                background-color: #6c757d;
                color: #fff;
                text-decoration: none;
                border-radius: 6px;
                font-size: 14px;
                font-weight: 500;
                transition: background-color 0.3s ease, transform 0.2s ease;
                white-space: nowrap; /* Ngăn chữ bị xuống dòng */
                text-align: center;
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
            .button-group {
                display: flex;
                justify-content: center;
                gap: 15px;
                flex-wrap: wrap;
                align-items: center; /* Căn giữa nút */
            }
            .confirm-btn {
                background-color: #28a745; /* Màu xanh lá */
                color: white;
                border: none;
                padding: 12px 20px;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 500;
                cursor: pointer;
                transition: background-color 0.3s ease, transform 0.2s ease;
            }
            .confirm-btn:hover {
                background-color: #218838;
                transform: translateY(-2px);
            }
            .admin-back-button:hover {
                background-color: #00509E;
            }
            .admin-back-button i {
                margin-right: 8px;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <c:if test="${sessionScope.user.roleID == 2}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="avatar">
                    </div>
                    <ul>
                        <li><a href="train">Quản lý tàu</a></li>
                        <li><a href="trip">Quản lý chuyến</a></li>
                        <li><a href="route">Quản lý tuyến tàu</a></li>
                        <li><a href="station">Quản lý ga</a></li>
                        <li><a href="order">Quản lý hóa đơn</a></li>
                        <li><a href="refund">Quản lý đơn hoàn tiền</a></li>
                        <li><a href="category-blog">Quản lý tiêu đề Blog</a></li>
                        <li><a href="posts-list">Quản lý Blog</a></li>
                        <li><a href="category-rule">Quản lý tiêu đề quy định</a></li>
                        <li><a href="manager-rule-list">Quản lý quy định</a></li>
                        <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                    </ul>
                    <form action="logout" method="GET">
                        <button type="submit" class="logout-button">Logout</button>
                    </form>
                </div>
            </c:if>
            <c:if test="${sessionScope.user.roleID == 1}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="trainpicture">
                    </div>
                    <ul class="menu">
                        <li><a href="admin?view=dashboard">Dashboard</a></li>
                        <li><a href="admin?view=employees">Quản lý nhân viên</a></li>
                        <li><a href="admin?view=customers">Quản lý khách hàng</a></li>
                            <c:if test="${sessionScope.user.userId == 1}">
                            <li><a href="admin?view=userauthorization">Phân quyền</a></li>
                            </c:if>
                        <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>

                    </ul>
                    <form action="logout" method="GET">
                        <button type="submit" class="logout-button">Logout</button>
                    </form>
                </div>
                <a href="admin?view=dashboard" class="admin-back-button">
                    <i class="fas fa-arrow-left"></i> Quay lại trang Admin
                </a>
            </c:if>
            <h1>Chi tiết hóa đơn</h1>

            <c:if test="${not empty error}">
                <p class="error-message">${error}</p>
            </c:if>

            <!-- Block chung cho Refund Information và Customer Information -->
            <div class="info-block">
                <!-- Refund Information -->
                <div class="info-section">
                    <div class="section-title">Thông tin hóa đơn trả tiền:</div>
                    <p><strong>Id hóa đơn:</strong> ${refund.refundID}</p>
                    <p><strong>Ngày yêu cầu:</strong> ${refund.refundDate}</p>
                    <p><strong>Ngày trả:</strong> ${refund.confirmRefundDate}</p>
                    <p><strong>Tình trạng:</strong> ${refund.refundStatus}</p>
                    <p><strong>Tổng số vé:</strong> ${fn:length(refund.tickets)}</p>
                    <p><strong>Tổng số tiền hoàn:</strong> <fmt:formatNumber value="${refund.totalRefund}" type="currency" currencySymbol="VND" /></p>
                </div>

                <!-- Customer Information -->
                <div class="info-section">
                    <div class="section-title">Thông tin người đặt:</div>
                    <p><strong>Tên người dùng:</strong> ${refund.customerName}</p>
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
                                <th>Tên hành khách</th>
                                <th>Đối Tượng</th>
                                <th>CCCD</th>
                                <th>Giá vé</th>
                                <th>Tiền hoàn</th>
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
                                    <td>${ticket.passengerType}</td>
                                    <td>${ticket.cccd}</td>
                                    <td><fmt:formatNumber value="${ticket.ticketPrice}" type="currency" currencySymbol="VND" /></td>
                                    <td><fmt:formatNumber value="${ticket.ticketPrice*0.8}" type="currency" currencySymbol="VND" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p class="no-tickets">Không có vé đã hủy.</p>
                </c:otherwise>
            </c:choose>
            <div class="button-group">
                <a href="refund" class="back-link">Trở về</a>

                <c:if test="${refund.refundStatus eq 'Wait'}">
                    <form action="confirmRefund" method="post">
                        <input type="hidden" name="refundID" value="${refund.refundID}" />
                        <input type="hidden" name="customerEmail" value="${refund.customerEmail}" />
                        <button type="submit" class="confirm-btn">Xác nhận hoàn tiền</button>
                    </form>
                </c:if>
            </div>

        </div>
    </body>
</html>
