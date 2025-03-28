<%-- 
    Document   : RefundList
    Created on : Mar 27, 2025, 10:46:32 PM
    Author     : dung9
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- For date formatting --%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

<html>
    <head>
        <title>Quản lí hóa đơn</title>
        <jsp:include page="/navbar.jsp"></jsp:include>
            <link rel="stylesheet" href="css/employee.css">
            <style>
                /* Reset mặc định để đảm bảo giao diện đồng nhất */
                * {
                    margin: 0;
                    padding: 0;
                    box-sizing: border-box;
                    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                }
                .container {
                    margin-top: 70px; /* Adjust this value to match the height of your navbar */
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
                    border: 2px solid black; /* Thêm viền đen cho bảng */
                }

                table th, table td {
                    padding: 12px 10px;
                    text-align: left;
                    border: 1px solid black; /* Thêm viền đen cho ô bảng */
                    white-space: nowrap;
                }

                table th {
                    background-color: #077BFF;
                    color: #fff;
                    font-weight: 600;
                    font-size: 12px;
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
            </style>
        </head>
        <body>
            <div class="container">
                <h1>Danh sách đơn hoàn tiền</h1>
            <%-- Filter Form --%>
            <div class="filter-section">
                <form action="refund-list" method="get"> 
                    <label>ID vé:</label>
                    <input type="text" name="ticketID" value="${param.ticketID}" />

                    <label>Tên ngân hàng:</label>
                    <input type="text" name="bankName" value="${param.bankName}" />
                    <br><label>Số tài khoản:</label>
                    <input type="text" name="bankAccountID" value="${param.bankAccountID}" />

                    <label>Ngày yêu cầu:</label>
                    <input type="date" name="refundDate" value="${param.refundDate}" />

                    <label>Trạng thái:</label>
                    <select name="refundStatus">
                        <option value="">Tất cả</option>
                        <option value="Pending" ${param.refundStatus == 'Pending' ? 'selected' : ''}>Đang chờ</option>
                        <option value="Complete" ${param.refundStatus == 'Complete' ? 'selected' : ''}>Hoàn tất</option>
                    </select>
                    <button type="submit">Lọc</button>
                </form>

                <a href="refund-list"><button>Xóa lọc</button></a>
            </div>
            <!-- Hiển thị thông báo lỗi nếu có -->
            <c:if test="${not empty error}">
                <p style="color: red;">${error}</p>
            </c:if>

            <c:choose>
                <c:when test="${empty refunds}">
                    <p>Không có yêu cầu hoàn tiền nào.</p>
                </c:when>
                <c:otherwise>
                    <table border="1">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên người dùng</th>
                                <th>Email</th>
                                <th>SĐT</th>
                                <th>Ngân hàng</th>
                                <th>Số tài khoản</th>
                                <th>Vé</th>
                                <th>Tổng tiền hoàn</th>
                                <th>Ngày yêu cầu</th>
                                <th>Trạng thái</th>
                                <th>Chi tiết</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:set var="prevRefundID" value="-1" />
                            <c:forEach items="${refunds}" var="refund" varStatus="loop">
                                <c:if test="${refund.refundID ne prevRefundID}">
                                    <c:set var="rowCount" value="0" />
                                    <c:forEach items="${refunds}" var="innerRefund">
                                        <c:if test="${innerRefund.refundID eq refund.refundID}">
                                            <c:set var="rowCount" value="${rowCount + 1}" />
                                        </c:if>
                                    </c:forEach>

                                    <tr>
                                        <td rowspan="${rowCount}">${refund.refundID}</td>
                                        <td rowspan="${rowCount}">${refund.customerName}</td>
                                        <td rowspan="${rowCount}">${refund.customerEmail}</td>
                                        <td rowspan="${rowCount}">${refund.phoneNumber}</td>
                                        <td rowspan="${rowCount}">${refund.bankName}</td>
                                        <td rowspan="${rowCount}">${refund.bankAccountID}</td>
                                        <td>${refund.ticketID}</td>
                                        <td rowspan="${rowCount}"><fmt:formatNumber value="${refund.totalRefund}" type="currency" currencySymbol="VND" /></td>
                                        <td rowspan="${rowCount}">${refund.refundDate}</td>
                                        <td rowspan="${rowCount}">${refund.refundStatus}</td>
                                        <td rowspan="${rowCount}">
                                            <a href="refund-details?refundID=${refund.refundID}" class="btn btn-primary btn-sm">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:if>
                                <c:if test="${refund.refundID eq prevRefundID}">
                                    <tr>
                                        <td>${refund.ticketID}</td>
                                    </tr>
                                </c:if>
                                <c:set var="prevRefundID" value="${refund.refundID}" />
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
    </body>
</html>
