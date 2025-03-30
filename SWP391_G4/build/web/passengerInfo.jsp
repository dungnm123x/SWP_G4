
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    boolean partialMode = (request.getAttribute("partialMode") != null);
%>
<fmt:setLocale value="en_US"/>
<c:choose>

    <c:when test="${partialMode}">

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
                <c:forEach var="item" items="${sessionScope.cartItems}" varStatus="status">
                    <tr data-confirmedDOB="${sessionScope.confirmedDOB[status.index]}">

                        <!-- Cột nhập thông tin hành khách -->
                        <td class="p-2">
                            <!-- Ở chế độ partialMode -->
                            <input type="hidden" name="adultTicketCode" 
                                   id="adultTicketCodeHidden${status.index}" />




                            <input type="hidden" name="passengerName${status.index}" />
                            <input type="text" class="form-control mb-2"
                                   name="fullName${status.index}" 
                                   value="${sessionScope.fullNameList[status.index]}"
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
                                                    'vipModal${status.index}',
                                                    'idNumber${status.index}'
                                                    )">

                                <option value="Người lớn"
                                        <c:if test="${sessionScope.typeList[status.index] == 'Người lớn'}">
                                            selected
                                        </c:if>>
                                    Người lớn
                                </option>
                                <option value="Trẻ em"
                                        <c:if test="${sessionScope.typeList[status.index] == 'Trẻ em'}">
                                            selected
                                        </c:if>>
                                    Trẻ em
                                </option>
                                <option value="Sinh viên"
                                        <c:if test="${sessionScope.typeList[status.index] == 'Sinh viên'}">
                                            selected
                                        </c:if>>
                                    Sinh viên
                                </option>
                                <option value="Người cao tuổi"
                                        <c:if test="${sessionScope.typeList[status.index] == 'Người cao tuổi'}">
                                            selected
                                        </c:if>>
                                    Người cao tuổi
                                </option>
                               
                            </select>

                            <input type="text" class="form-control"
                                   id="idNumber${status.index}"
                                   name="idNumber${status.index}"
                                   value="${sessionScope.idNumberList[status.index]}"
                                   placeholder="Số CMND/Hộ chiếu"
                                   <c:if test="${sessionScope.confirmedDOB[status.index]}">readonly="readonly"</c:if>
                                       required />

                                   <input type="hidden" name="tripID${status.index}" value="${item.trip.tripID}" />
                            <input type="hidden" name="passengerCCCD${status.index}" />
                        </td>

                        <!-- Cột thông tin chỗ -->
                        <td>
                            <c:choose>
                                <c:when test="${item.returnTrip}">
                                    <strong class="text-danger">Chuyến về:</strong><br/>
                                    <small>${item.arrivalStationName} &rarr; ${item.departureStationName}</small>
                                </c:when>
                                <c:otherwise>
                                    <strong class="text-success">Chuyến đi:</strong><br/>
                                    <small>${item.departureStationName} &rarr; ${item.arrivalStationName}</small>
                                </c:otherwise>
                            </c:choose>
                            <br/>
                            <span>Tàu: <strong>${item.trainName}</strong> - ${item.departureDate}</span>
                            <br/>
                            <span>Toa: <strong>${item.carriageNumber}</strong>, Chỗ <strong>${item.seatNumber}</strong></span>
                        </td>

                        <!-- Cột giá vé gốc -->
                        <td>
                            ${item.price} VND
                            <input type="hidden" id="price${status.index}"
                                   name="price${status.index}"
                                   value="${item.price}" />
                        </td>

                        <!-- Cột giảm giá (hiển thị tạm) -->
                        <td id="discount${status.index}">-0%</td>

                        <!-- Khuyến mại -->
                        <td>Không có khuyến mại</td>

                        <!-- Bảo hiểm -->
                        <td>1,000 VND</td>

                        <!-- Thành tiền tạm (client) -->
                        <td id="displayTotal${status.index}">
                            <c:out value="${item.price + 1000}" /> VND
                        </td>

                        <!-- Nút xóa -->
                        <td>
                            <input type="hidden" name="seatID" 
                                   id="seatIDHidden${status.index}" />
                            <button type="button"
                                    class="btn btn-danger btn-remove"
                                    data-seatid="${item.trip.tripID}_${item.trainName}_${item.departureDate}_${item.carriageNumber}_${item.seatNumber}">
                                Xóa vé
                            </button>

                        </td>
                    </tr>

                    <!-- Modal xác nhận tuổi (Trẻ em / Người cao tuổi) -->
                <div id="ageModal${status.index}" class="modal">
                    <div class="modal-content">
                        <h5>Nhập ngày tháng năm sinh</h5>
                        <div class="mb-2">
                            <select id="birthDay${status.index}" 
                                    name="birthDay${status.index}">
                                <c:forEach var="d" begin="1" end="31">
                                    <option value="${d}">
                                        ${d}
                                    </option>
                                </c:forEach>
                            </select>

                            <select id="birthMonth${status.index}" 
                                    name="birthMonth${status.index}">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}">
                                        ${m}
                                    </option>
                                </c:forEach>
                            </select>

                            <select id="birthYear${status.index}" 
                                    name="birthYear${status.index}">
                                <c:forEach var="y" begin="1920" end="2025">
                                    <option value="${y}">
                                        ${y}
                                    </option>
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
                                                    'birthYear${status.index}',
                                                    'idNumber${status.index}'
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

    <script>

        reapplyDiscount(); // Gọi cho tất cả các hàng => updateDiscountNoModal()
        updateTotalAmount();
        checkIfSingleChildTicket();

    </script>
</c:when>


<c:otherwise>
    <!DOCTYPE html>
    <html>
        <head>
            <meta charset="UTF-8">
            <title>Nhập thông tin hành khách</title>
            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

            <!-- Import file JS duy nhất -->
            <script src="js/passengerInfo.js" type="text/javascript"></script>

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
        <body class="container py-4 mt-5">
            <h3 class="text-primary mb-3">📋 Nhập thông tin hành khách</h3>

            <!-- Hiển thị thông báo lỗi nếu có -->
            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-danger">
                    ${sessionScope.errorMessage}
                </div>
                <c:remove var="errorMessage" scope="session"/>
            </c:if>


            <!-- FORM submit sang Servlet -->
            <form method="post" action="passengerinfo">
                <input type="hidden" name="departureStationID" value="${param.departureStationID}" />
                <input type="hidden" name="arrivalStationID" value="${param.arrivalStationID}" />
                <input type="hidden" name="departureDay" value="${param.departureDay}" />
                <input type="hidden" name="tripType" value="${param.tripType}" />
                <input type="hidden" name="returnDate" value="${param.returnDate}" />
                <input type="hidden" name="adultTicketCode" id="adultTicketCodeHidden" />
                <input type="hidden" name="tripID${status.index}" value="${item.trip.tripID}" />

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
                            <c:forEach var="item" items="${sessionScope.cartItems}" varStatus="status">
                                <tr data-confirmedDOB="${sessionScope.confirmedDOB[status.index]}">

                                    <!-- Cột nhập thông tin hành khách -->
                                    <td class="p-2">
                                        <input type="hidden" name="passengerName${status.index}" />
                                        <input type="text" class="form-control mb-2"
                                               name="fullName${status.index}" 
                                               value="${sessionScope.fullNameList[status.index]}"
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
                    'vipModal${status.index}',
                    'idNumber${status.index}'
                    )">

                                            <option value="Người lớn"
                                                    <c:if test="${sessionScope.typeList[status.index] == 'Người lớn'}">
                                                        selected
                                                    </c:if>>
                                                Người lớn
                                            </option>
                                            <option value="Trẻ em"
                                                    <c:if test="${sessionScope.typeList[status.index] == 'Trẻ em'}">
                                                        selected
                                                    </c:if>>
                                                Trẻ em
                                            </option>
                                            <option value="Sinh viên"
                                                    <c:if test="${sessionScope.typeList[status.index] == 'Sinh viên'}">
                                                        selected
                                                    </c:if>>
                                                Sinh viên
                                            </option>
                                            <option value="Người cao tuổi"
                                                    <c:if test="${sessionScope.typeList[status.index] == 'Người cao tuổi'}">
                                                        selected
                                                    </c:if>>
                                                Người cao tuổi
                                            </option>
                                            
                                        </select>

                                        <input type="text" class="form-control"
                                               id="idNumber${status.index}"
                                               name="idNumber${status.index}"
                                               value="${sessionScope.idNumberList[status.index]}"
                                               placeholder="Số CMND/Hộ chiếu"
                                               <c:if test="${sessionScope.confirmedDOB[status.index]}">readonly="readonly"</c:if>
                                                   required />


                                               <input type="hidden" name="tripID${status.index}" value="${item.trip.tripID}" />
                                        <input type="hidden" name="passengerCCCD${status.index}" />
                                    </td>

                                    <!-- Cột thông tin chỗ -->
                                    <td>
                                        <c:choose>
                                            <c:when test="${item.returnTrip}">
                                                <strong class="text-danger">Chuyến về:</strong><br/>
                                                <small>${item.arrivalStationName} &rarr; ${item.departureStationName}</small>
                                            </c:when>
                                            <c:otherwise>
                                                <strong class="text-success">Chuyến đi:</strong><br/>
                                                <small>${item.departureStationName} &rarr; ${item.arrivalStationName}</small>
                                            </c:otherwise>
                                        </c:choose>
                                        <br/>
                                        <span>Tàu: <strong>${item.trainName}</strong> - ${item.departureDate}</span>
                                        <br/>
                                        <span>Toa: <strong>${item.carriageNumber}</strong>, Chỗ <strong>${item.seatNumber}</strong></span>
                                    </td>

                                    <!-- Cột giá vé gốc -->
                                    <td>
                                        <fmt:formatNumber value="${item.price}" pattern="#,##0.##"  /> VND
                                        <input type="hidden" id="price${status.index}"
                                               name="price${status.index}"
                                               value="${item.price}" />
                                    </td>

                                    <!-- Cột giảm giá (hiển thị tạm) -->
                                    <td id="discount${status.index}">-0%</td>

                                    <!-- Khuyến mại -->
                                    <td>Không có khuyến mại</td>

                                    <!-- Bảo hiểm -->
                                    <td>1,000 VND</td>

                                    <!-- Thành tiền tạm (client) -->
                                    <td id="displayTotal${status.index}">
                                        <c:out value="${item.price + 1000}" /> VND
                                    </td>

                                    <!-- Nút xóa -->
                                    <td>
                                        <input type="hidden" id="seatIDHidden" name="seatID" />
                                        <button type="button"
                                                class="btn btn-danger btn-remove"
                                                data-seatid="${item.trip.tripID}_${item.trainName}_${item.departureDate}_${item.carriageNumber}_${item.seatNumber}">
                                            Xóa vé
                                        </button>

                                    </td>
                                </tr>

                                <!-- Modal xác nhận tuổi (Trẻ em / Người cao tuổi) -->
                            <div id="ageModal${status.index}" class="modal">
                                <div class="modal-content">
                                    <h5>Nhập ngày tháng năm sinh</h5>
                                    <div class="mb-2">
                                        <!-- Ô chọn ngày sinh -->
                                        <select id="birthDay${status.index}" 
                                                name="birthDay${status.index}">
                                            <c:forEach var="d" begin="1" end="31">
                                                <option value="${d}">
                                                    ${d}
                                                </option>
                                            </c:forEach>
                                        </select>

                                        <select id="birthMonth${status.index}" 
                                                name="birthMonth${status.index}">
                                            <c:forEach var="m" begin="1" end="12">
                                                <option value="${m}">
                                                    ${m}
                                                </option>
                                            </c:forEach>
                                        </select>

                                        <select id="birthYear${status.index}" 
                                                name="birthYear${status.index}">
                                            <c:forEach var="y" begin="1920" end="2025">
                                                <option value="${y}">
                                                    ${y}
                                                </option>
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
                                                                'birthYear${status.index}',
                                                                'idNumber${status.index}'
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

                <input type="hidden" name="passengerCount" value="${fn:length(cartItems)}" />
                <div class="d-flex justify-content-between align-items-center mt-3">
                    <button type="submit" name="action" value="clearAll" 
                            class="btn btn-danger"
                            formnovalidate>
                        🗑 Xóa tất cả vé
                    </button>
                    <h5 class="text-primary">
                        Tổng tiền: <span id="totalAmount">0.0</span> VND
                    </h5>
                </div>

                <h4 class="text-primary mt-4">Thông tin người đặt vé</h4>
                <div class="row g-3">
                    <div class="col-md-4">
                        <label class="form-label">Họ và tên</label>
                        <input 
                            type="text" 
                            class="form-control" 
                            name="bookingName" 
                            value="${requestScope.bookingName != null ? requestScope.bookingName : sessionScope.bookingName}" 
                            readOnly 
                            />
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">CCCD/Hộ chiếu (người đặt)</label>
                        <input 
                            type="text" 
                            class="form-control" 
                            name="bookingCCCD"
                            pattern="\d{9}|\d{12}"
                            title="CCCD/Hộ chiếu phải gồm 9 hoặc 12 chữ số"
                            value="${sessionScope.bookingCCCD}"  
                            required 
                            />
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Email</label>
                        <input 
                            type="email" 
                            class="form-control" 
                            name="bookingEmail" 
                            value="${requestScope.bookingEmail != null ? requestScope.bookingEmail : sessionScope.bookingEmail}" 
                            readOnly 
                            />
                    </div>
                    <div class="col-md-4">
                        <label class="form-label">Số điện thoại</label>
                        <input 
                            type="text" 
                            class="form-control" 
                            name="bookingPhone" 
                            value="${requestScope.bookingPhone != null ? requestScope.bookingPhone : sessionScope.bookingPhone}" 
                            readOnly 
                            />                
                    </div>
                </div>

                <div class="d-flex justify-content-between mt-4">
                    <!-- Nút "Quay lại" -->
                    <button type="button"
                            onclick="window.location.href = '<%= session.getAttribute("previousURL") %>'"
                            class="btn btn-secondary">
                        Quay lại
                    </button>

                    <button type="submit" class="btn btn-primary">
                        Tiếp tục
                    </button>
                </div>
            </form>

            <!-- Cuối body => gọi hàm bind lần đầu -->
            <script>

                reapplyDiscount(); // Gọi cho tất cả các hàng => updateDiscountNoModal()
                updateTotalAmount();
            </script>
        </body>
    </html>
</c:otherwise>
</c:choose>
