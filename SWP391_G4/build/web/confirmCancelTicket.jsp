<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Xác Nhận Hủy Vé</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
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
        #otpMessage {
            margin-top: 10px;
            color: green;
            font-weight: bold;
        }
        #countdown {
            font-weight: bold;
            color: red;
            margin-left: 10px;
            display: none; /* Ban đầu ẩn */
        }
    </style>
    <jsp:include page="/navbar.jsp"/>
</head>
<body>
    <div class="container mt-4">
        <h2 class="text-center">Xác Nhận Hủy Vé</h2>
        <form id="confirmCancelForm" action="confirm-cancel" method="post">
            <div class="table-container">
                <table class="table table-bordered table-hover text-center">
                    <thead class="bg-dark text-white">
                        <tr>
                            <th>Mã vé</th>
                            <th>Hành trình</th>
                            <th>Tàu</th>
                            <th>Thời gian khởi hành</th>
                            <th>Toa</th>
                            <th>Chỗ ngồi</th>
                            <th>Giá vé</th>
                            <th>Tiền hoàn</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:set var="totalRefund" value="0" />
                        <c:set var="ticketCount" value="0" />
                        <c:forEach var="ticket" items="${sessionScope.pendingCancelTickets}">
                            <c:set var="refundAmount" value="${ticket.ticketPrice * 0.8}" />
                            <c:set var="totalRefund" value="${totalRefund + refundAmount}" />
                            <c:set var="ticketCount" value="${ticketCount + 1}" />
                            <tr>
                                <td>${ticket.ticketID}</td>
                                <td>${ticket.route}</td>
                                <td>${ticket.trainCode}</td>
                                <td>${ticket.departureTime}</td>
                                <td>${ticket.carriageNumber}</td>
                                <td>${ticket.seatNumber}</td>
                                <td>${ticket.ticketPrice} VND</td>
                                <td>${refundAmount} VND</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <p class="text-end fw-bold">Tổng số vé: <span class="text-primary">${ticketCount}</span></p>
                <p class="text-end fw-bold">Tổng tiền hoàn: <span class="text-success">${totalRefund} VND</span></p>
            </div>

            <div class="mt-4">
                <h5>Phương thức xác nhận:</h5>
                <p>Email: <strong>${sessionScope.user.email}</strong></p>
                <label for="otpInput">Mã xác nhận (6 số):</label>
                <input type="text" id="otpInput" name="otp" class="form-control d-inline w-25" maxlength="6">
                <button type="button" id="sendOtpBtn" class="btn btn-primary" onclick="sendOtp()">Gửi mã</button>
                <span id="countdown"></span>
                <p id="otpMessage"></p>
            </div>

            <div class="mt-4 text-center">
                <button type="submit" class="btn btn-danger">Xác nhận hủy vé</button>
                <a href="cancel-ticket" class="btn btn-secondary">Quay lại</a>
            </div>
        </form>
    </div>

    <script>
        let countdown = 120;
        let timer;

        function startCountdown() {
            console.log("Bắt đầu đếm ngược"); // Debug
            const sendButton = document.getElementById("sendOtpBtn");
            const countdownDisplay = document.getElementById("countdown");

            sendButton.setAttribute("disabled", "true");
            countdownDisplay.style.display = "inline"; // Hiện bộ đếm
            countdown = 120;
            countdownDisplay.innerText = ` (${countdown}s)`;

            timer = setInterval(() => {
                countdown--;
                countdownDisplay.innerText = ` (${countdown}s)`;

                if (countdown <= 0) {
                    clearInterval(timer);
                    sendButton.removeAttribute("disabled");
                    sendButton.innerText = "Gửi mã";
                    countdownDisplay.innerText = "";
                    countdownDisplay.style.display = "none"; // Ẩn khi kết thúc
                }
            }, 1000);
        }

        function sendOtp() {
            console.log("Gửi yêu cầu OTP...");
            const otpMessage = document.getElementById("otpMessage");
            otpMessage.innerText = ""; // Xóa thông báo cũ

            const email = "<c:out value='${sessionScope.user.email}'/>";
            if (!email || email.trim() === "") {
                alert("Không tìm thấy email. Vui lòng đăng nhập lại!");
                return;
            }

            fetch("SendOtpCancelTicket", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: "email=" + encodeURIComponent(email)
            }).then(response => response.text())
            .then(data => {
                console.log("Phản hồi từ server:", data);
                if (data.includes("OTP đã được gửi")) {
                    otpMessage.innerText = "Mã OTP đã được gửi thành công!";
                    startCountdown();
                } else {
                    otpMessage.innerText = "Gửi OTP thất bại. Vui lòng thử lại!";
                }
            })
            .catch(error => {
                console.error("Lỗi gửi OTP:", error);
                otpMessage.innerText = "Lỗi máy chủ. Vui lòng thử lại!";
            });
        }
    </script>
</body>
</html>
