<%-- 
    Document   : passengerInfo
    Created on : Mar 3, 2025, 10:30:51 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Nhập thông tin hành khách</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

        <script>
            // Mức giảm giá TẠM TÍNH ở client
            const discountRates = {
                "Người lớn": 0,
                "Trẻ em": 50,
                "Sinh viên": 20,
                "Người cao tuổi": 30,
                "VIP": 10
            };

            // Khi chọn loại hành khách
            function updateDiscount(selectElement, priceId, discountId, totalId, ageModalId, vipModalId) {
                let selectedOption = selectElement.value;
                let basePrice = parseFloat(document.getElementById(priceId).value) || 0;

                // Nếu chọn Trẻ em / Người cao tuổi => hiện popup nhập ngày sinh
                if (selectedOption === "Trẻ em" || selectedOption === "Người cao tuổi") {
                    document.getElementById(ageModalId).style.display = 'flex';
                    return;
                }
                // Nếu chọn VIP => hiện popup nhập thẻ VIP
                if (selectedOption === "VIP") {
                    document.getElementById(vipModalId).style.display = 'flex';
                    return;
                }

                // Các loại khác (Người lớn, Sinh viên) => tính luôn
                let rate = discountRates[selectedOption] || 0;
                let discountAmount = basePrice * rate / 100;
                let finalPrice = basePrice - discountAmount + 1000;
                document.getElementById(discountId).innerText = '-' + rate + '%';
                document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
                updateTotalAmount();
            }

            // Tính tổng tiền tạm (client)
            function updateTotalAmount() {
                let sum = 0;
                document.querySelectorAll('[id^="displayTotal"]').forEach(function (elem) {
                    let val = parseFloat(elem.innerText.replace(/[^\d\.]/g, ''));
                    if (!isNaN(val)) {
                        sum += val;
                    }
                });
                document.getElementById('totalAmount').innerText = sum.toLocaleString();
            }

            // Đóng modal
            function closeModal(id) {
                document.getElementById(id).style.display = 'none';
            }

            // Tính tuổi ở client
            function getAge(day, month, year) {
                // month - 1 vì trong JS, tháng tính từ 0..11
                let birthDate = new Date(year, month - 1, day);
                let now = new Date();
                let age = now.getFullYear() - birthDate.getFullYear();
                let m = now.getMonth() - birthDate.getMonth();
                if (m < 0 || (m === 0 && now.getDate() < birthDate.getDate())) {
                    age--;
                }
                return age;
            }

            // Xác nhận tuổi (Trẻ em / Người cao tuổi) phía client
            function confirmAge(modalId, selectId, priceId, discountId, totalId, dayId, monthId, yearId) {
                closeModal(modalId);

                let day = document.getElementById(dayId).value;
                let month = document.getElementById(monthId).value;
                let year = document.getElementById(yearId).value;

                let age = getAge(day, month, year);

                let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
                let selectedOption = document.getElementById(selectId).value;

                let rate = 0;
                if (selectedOption === "Trẻ em") {
                    // 6..10 => 50%. <6 => không cần vé, >10 => không hợp lệ
                    if (age < 6) {
                        alert("Trẻ em dưới 6 tuổi không cần vé. Vui lòng xóa vé này nếu không cần.");
                        rate = 0;
                    } else if (age > 10) {
                        alert("Không đúng độ tuổi Trẻ em (6-10)!");
                        rate = 0;
                    } else {
                        rate = 50;
                    }
                } else {
                    // Người cao tuổi => >=60 => 30%
                    if (age < 60) {
                        alert("Chưa đủ 60 tuổi để giảm giá Người cao tuổi!");
                        rate = 0;
                    } else {
                        rate = 30;
                    }
                }

                let discountAmount = basePrice * rate / 100;
                let finalPrice = basePrice - discountAmount + 1000;

                document.getElementById(discountId).innerText = '-' + rate + '%';
                document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
                updateTotalAmount();
            }

            // Xác nhận VIP (client)
            function confirmVIP(modalId, selectId, priceId, discountId, totalId) {
                let vipInputId = 'vipCard' + selectId.replace('passengerType', '');
                let vipCard = document.getElementById(vipInputId).value.trim();
                if (vipCard === "") {
                    alert("Vui lòng nhập thông tin thẻ VIP!");
                    return;
                }
                closeModal(modalId);

                let basePrice = parseFloat(document.getElementById(priceId).value) || 0;
                let rate = 10; // Tạm cứng 10% (client)
                let discountAmount = basePrice * rate / 100;
                let finalPrice = basePrice - discountAmount + 1000;

                document.getElementById(discountId).innerText = '-10%';
                document.getElementById(totalId).innerText = finalPrice.toLocaleString() + ' VND';
                updateTotalAmount();
            }

            // Khi load trang => tính tổng tạm
            window.onload = function () {
                updateTotalAmount();
            };
        </script>

        <style>
            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background-color: rgba(0,0,0,0.5);
                justify-content: center;
                align-items: center;
            }
            .modal-content {
                background: white;
                padding: 20px;
                border-radius: 10px;
                width: 350px;
                text-align: center;
            }

        </style>
        <jsp:include page="/navbar.jsp"/>
    </head>
    <body class="container py-4">
        <h3 class="text-primary mb-3">📋 Nhập thông tin hành khách</h3>

        <!-- Hiển thị thông báo lỗi nếu có -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger">
                ${errorMessage}
            </div>
        </c:if>

        <!-- FORM submit sang Servlet -->
        <form method="post" action="passengerinfo">

            <div class="table-responsive">
                <table class="table table-bordered text-center">
                    <thead class="table-primary">
                        <tr>
                            <th>Họ tên</th>
                            <th>Thông tin chỗ</th>
                            <th>Giá vé</th>
                            <th>Giảm đối tượng</th>
                            <th>Khuyến mại</th>
                            <th>Bảo hiểm</th>
                            <th>Thành tiền (VND)</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}" varStatus="status">
                            <tr>
                                <!-- Cột nhập thông tin hành khách -->
                                <td class="p-2">
                                    <input type="text" class="form-control mb-2"
                                           name="fullName${status.index}" 
                                           placeholder="Họ và tên" required />

                                    <select id="passengerType${status.index}"
                                            class="form-select mb-2"
                                            name="passengerType${status.index}"
                                            required
                                            onchange="updateDiscount(
                                                            this,
                                                            'price${status.index}',
                                                            'discount${status.index}',
                                                            'displayTotal${status.index}',
                                                            'ageModal${status.index}',
                                                            'vipModal${status.index}')">
                                        <option value="Người lớn">Người lớn</option>
                                        <option value="Trẻ em">Trẻ em</option>
                                        <option value="Sinh viên">Sinh viên</option>
                                        <option value="Người cao tuổi">Người cao tuổi</option>
                                        <option value="VIP">Hội viên VIP</option>
                                    </select>

                                    <input type="text" class="form-control" 
                                           name="idNumber${status.index}" 
                                           placeholder="Số CMND/Hộ chiếu" required />
                                </td>

                                <!-- Cột thông tin chỗ -->
                                <td>
                                    ${item.trainName} - ${item.departureDate}<br>
                                    Toa ${item.carriageNumber}, Chỗ ${item.seatNumber}
                                </td>

                                <!-- Cột giá vé gốc -->
                                <td>
                                    ${item.price} VND
                                    <!-- Input hidden để Servlet đọc -->
                                    <input type="hidden" id="price${status.index}"
                                           name="price${status.index}"
                                           value="${item.price}" />
                                </td>

                                <!-- Cột giảm giá (hiển thị tạm) -->
                                <td id="discount${status.index}">-0%</td>

                                <!-- Khuyến mại cố định (nếu có) -->
                                <td>Không có khuyến mại</td>

                                <!-- Bảo hiểm -->
                                <td>1,000</td>

                                <!-- Thành tiền tạm (client) -->
                                <td id="displayTotal${status.index}">
                                    <c:out value="${item.price + 1000}" /> VND
                                </td>

                                <!-- Nút xóa -->
                                <td><input type="hidden" name="seatIndex" value="${status.index}" />
                                    <button type="submit" name="action" value="removeOne" 
                                            class="btn btn-danger" 
                                            formnovalidate>
                                        Xóa vé
                                    </button>


                                </td>

                            </tr>

                            <!-- Modal xác nhận tuổi (Trẻ em / Người cao tuổi) -->
                        <div id="ageModal${status.index}" class="modal">
                            <div class="modal-content">
                                <h5>Nhập ngày tháng năm sinh</h5>
                                <div class="mb-2">
                                    <!-- Thêm id để JS lấy giá trị -->
                                    <select id="birthDay${status.index}" name="birthDay${status.index}">
                                        <c:forEach var="i" begin="1" end="31">
                                            <option value="${i}">${i}</option>
                                        </c:forEach>
                                    </select>
                                    <select id="birthMonth${status.index}" name="birthMonth${status.index}">
                                        <c:forEach var="i" begin="1" end="12">
                                            <option value="${i}">${i}</option>
                                        </c:forEach>
                                    </select>
                                    <select id="birthYear${status.index}" name="birthYear${status.index}">
                                        <c:forEach var="i" begin="1920" end="2025">
                                            <option value="${i}">${i}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div>
                                    <button type="button" 
                                            onclick="closeModal('ageModal${status.index}')"
                                            class="btn btn-secondary">Hủy</button>
                                    <button type="button"
                                            onclick="confirmAge(
                                                            'ageModal${status.index}',
                                                            'passengerType${status.index}',
                                                            'price${status.index}',
                                                            'discount${status.index}',
                                                            'displayTotal${status.index}',
                                                            'birthDay${status.index}',
                                                            'birthMonth${status.index}',
                                                            'birthYear${status.index}'
                                                            )"
                                            class="btn btn-primary">
                                        Xác nhận
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- Modal VIP -->
                        <div id="vipModal${status.index}" class="modal">
                            <div class="modal-content">
                                <h5>Xác nhận thẻ VIP</h5>
                                <div>
                                    <input type="text"
                                           id="vipCard${status.index}"
                                           name="vipCard${status.index}"
                                           placeholder="Số thẻ VIP / CMND/CCCD" />
                                </div>
                                <div class="mt-3">
                                    <button type="button"
                                            onclick="closeModal('vipModal${status.index}')"
                                            class="btn btn-secondary">Hủy</button>
                                    <button type="button"
                                            onclick="confirmVIP(
                                                            'vipModal${status.index}',
                                                            'passengerType${status.index}',
                                                            'price${status.index}',
                                                            'discount${status.index}',
                                                            'displayTotal${status.index}'
                                                            )"
                                            class="btn btn-primary">
                                        Xác nhận
                                    </button>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <!-- Tổng số hành khách -->
            <input type="hidden" name="passengerCount" value="${fn:length(cartItems)}" />

            <!-- Hiển thị tổng tiền tạm (client) -->
            <div class="d-flex justify-content-between align-items-center mt-3">
                <!-- Nút Xóa tất cả vé: gửi action=clearAll -->
                <!-- Nút Xóa tất cả vé: gửi action=clearAll, bỏ qua validation -->
                <button type="submit" name="action" value="clearAll" 
                        class="btn btn-danger"
                        formnovalidate>
                    🗑 Xóa tất cả vé
                </button>


                <h5 class="text-primary">Tổng tiền: <span id="totalAmount">0</span> VND</h5>
            </div>

            <!-- Thông tin người đặt vé -->
            <h4 class="text-primary mt-4">Thông tin người đặt vé</h4>
            <div class="row g-3">
                <div class="col-md-4">
                    <label class="form-label">Họ và tên</label>
                    <input type="text" class="form-control" name="bookingName" required />
                </div>
                <div class="col-md-4">
                    <label class="form-label">Email</label>
                    <input type="email" class="form-control" name="bookingEmail" required />
                </div>
                <div class="col-md-4">
                    <label class="form-label">Số điện thoại</label>
                    <input type="text" class="form-control" name="bookingPhone" required />
                </div>
            </div>
            <div class="d-flex justify-content-between mt-4">
                <!-- Nút "Quay lại" đến 1 trang cụ thể -->
                <button type="button" class="btn btn-secondary"
                        onclick="window.location.href = 'schedule'">
                    Quay lại
                </button>

                <button type="submit" class="btn btn-primary">
                    Tiếp tục
                </button>
            </div>

        </form>
    </body>
</html>




