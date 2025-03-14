<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Hủy Vé</title>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <style>
            body {
                padding-top: 70px;
                background-color: #f8f9fa;
            }
            .table-container {
                box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
                border-radius: 10px;
                overflow: hidden;
                background: white;
                padding: 15px;
                margin-top: 20px;
            }
            .no-results {
                color: red;
                font-weight: bold;
            }
        </style>
        <jsp:include page="/navbar.jsp"/>
    </head>
    <body>
        <div class="container mt-4">
            <h2 class="text-center">Hủy Vé</h2>
            <form id="cancelForm" action="cancel-ticket" method="post">
                <div class="table-container">
                    <table class="table table-bordered table-hover text-center">
                        <thead class="bg-dark text-white">
                            <tr>
                                <th>Chọn</th>
                                <th>Mã vé</th>
                                <th>CCCD</th>
                                <th>Hành trình</th>
                                <th>Tàu</th>
                                <th>Thời gian khởi hành</th>
                                <th>Toa</th>
                                <th>Chỗ ngồi</th>
                                <th>Giá vé</th>
                                <th>Trạng thái vé</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="ticket" items="${tickets}">
                                <tr>
                                    <td><input type="checkbox" name="selectedTickets" value="${ticket.ticketID}"></td>
                                    <td>${ticket.ticketID}</td>
                                    <td>${ticket.cccd}</td>
                                    <td>${ticket.route}</td>
                                    <td>${ticket.trainCode}</td>
                                    <td>${ticket.departureTime}</td>
                                    <td>${ticket.carriageNumber}</td>
                                    <td>${ticket.seatNumber}</td>
                                    <td>${ticket.ticketPrice} VND</td>
                                    <td>${ticket.ticketStatus}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    <c:if test="${empty tickets}">
                        <p class="no-results text-center">Không có vé nào.</p>
                    </c:if>
                </div>

                <div class="mt-4 p-3 border rounded bg-white">
                    <h5 class="text-primary">Thông tin người đặt vé</h5>
                    <div class="row mb-2">
                        <div class="col-md-6">
                            <label>Họ và tên</label>
                            <input type="text" class="form-control" value="${user.fullName}" disabled>
                        </div>
                        <div class="col-md-6">
                            <label>Email</label>
                            <input type="email" class="form-control" value="${user.email}" disabled>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label>Số điện thoại</label>
                            <input type="text" class="form-control" value="${user.phoneNumber}" disabled>
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary">Yêu cầu trả vé</button>
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger text-center">${error}</div>
                    </c:if>
                </div>
            </form>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>