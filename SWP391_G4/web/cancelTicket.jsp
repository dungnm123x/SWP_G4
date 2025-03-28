<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
                background: white;
                padding: 20px;
                margin-top: 20px;
                overflow-x: auto; /* Cho phép cuộn ngang nếu bảng quá rộng */
            }

            @media (max-width: 768px) {
                .table-container {
                    padding: 10px;
                }

                table {
                    font-size: 14px; /* Giảm kích thước chữ trên màn hình nhỏ */
                }
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
            <form method="get" action="cancel-ticket">
                <div class="row">
                    <div class="col-md-3">
                        <label>Mã vé:</label>
                        <input type="text" class="form-control" name="filterTicketID" value="${param.filterTicketID}">
                    </div>
                    <div class="col-md-3">
                        <label>Tên:</label>
                        <input type="text" class="form-control" name="filterPassengerName" value="${param.filterPassengerName}">
                    </div>
                    <div class="col-md-3">
                        <label>CCCD:</label>
                        <input type="text" class="form-control" name="filterCCCD" value="${param.filterCCCD}">
                    </div>
                    <div class="col-md-3">
                        <label>Hành trình:</label>
                        <input type="text" class="form-control" name="filterRoute" value="${param.filterRoute}">
                    </div>
                </div>
                <div class="mt-3">
                    <button type="submit" class="btn btn-primary">Lọc</button>
                    <a href="cancel-ticket" class="btn btn-secondary">Reset</a>
                </div>
            </form>

            <form id="cancelForm" action="cancel-ticket" method="post">
                <div class="table-container">
                    <table class="table table-bordered table-hover text-center">
                        <thead class="bg-dark text-white">
                            <tr>
                                <th>Chọn</th>
                                <th>Mã vé</th>
                                <th>Tên</th>
                                <th>Đối Tượng</th>
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
                                <c:if test="${(empty param.filterTicketID or fn:contains(ticket.ticketID, param.filterTicketID)) 
                                              and (empty param.filterPassengerName or fn:containsIgnoreCase(ticket.passengerName, param.filterPassengerName)) 
                                              and (empty param.filterCCCD or fn:contains(ticket.cccd, param.filterCCCD)) 
                                              and (empty param.filterRoute or fn:containsIgnoreCase(ticket.route, param.filterRoute))}">
                                      <tr>
                                          <td><input type="checkbox" name="selectedTickets" value="${ticket.ticketID}"></td>
                                          <td>${ticket.ticketID}</td>
                                          <td>${ticket.passengerName}</td>
                                          <td>${ticket.passengerType}</td>
                                          <td>${ticket.cccd}</td>
                                          <td>${ticket.route}</td>
                                          <td>${ticket.trainCode}</td>
                                          <td>${ticket.departureTime}</td>
                                          <td>${ticket.carriageNumber}</td>
                                          <td>${ticket.seatNumber}</td>
                                          <td>${ticket.ticketPrice} VND</td>
                                          <td>${ticket.ticketStatus}</td>
                                      </tr>
                                </c:if>                        
                            </c:forEach>
                        </tbody>
                    </table>
                    <div class="pagination">
                        <c:if test="${totalPages > 1}">
                            <ul class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage - 1}&filterTicketID=${param.filterTicketID}&filterPassengerName=${param.filterPassengerName}&filterCCCD=${param.filterCCCD}&filterRoute=${param.filterRoute}">Trước</a>
                                    </li>
                                </c:if>
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}&filterTicketID=${param.filterTicketID}&filterPassengerName=${param.filterPassengerName}&filterCCCD=${param.filterCCCD}&filterRoute=${param.filterRoute}">${i}</a>
                                    </li>
                                </c:forEach>
                                <c:if test="${currentPage < totalPages}">
                                    <li class="page-item">
                                        <a class="page-link" href="?page=${currentPage + 1}&filterTicketID=${param.filterTicketID}&filterPassengerName=${param.filterPassengerName}&filterCCCD=${param.filterCCCD}&filterRoute=${param.filterRoute}">Sau</a>
                                    </li>
                                </c:if>
                            </ul>
                        </c:if>
                    </div>  
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
                </div>
            </form>

        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html> 