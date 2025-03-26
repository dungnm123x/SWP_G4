<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="en_US"/>
<!-- Bắt đầu snippet giỏ vé -->
<div class="card shadow-sm">
    <div class="card-header bg-primary text-white">
        <h4 class="mb-0">
            <i class="bi bi-cart-fill"></i> Giỏ vé
        </h4>
    </div>

    <div class="card-body">
        <!-- Nếu giỏ rỗng -->
        <c:if test="${empty cartItems}">
            <div class="alert alert-warning" role="alert">
                Chưa có vé trong giỏ.
            </div>
        </c:if>

        <!-- Nếu có vé -->
        <c:if test="${not empty cartItems}">
            <div class="list-group mb-3">
                <c:set var="totalPrice" value="0" scope="page"/>
                <c:forEach var="item" items="${cartItems}">
                    <c:set var="totalPrice" value="${totalPrice + item.price}" scope="page" />

                    <div class="list-group-item d-flex justify-content-between align-items-center">
                        <!-- Thông tin vé bên trái -->
                        <div>
                            <div class="fw-bold">
                                <span>${item.trainName}</span>
                                <span class="text-muted">- ${item.departureDate}</span>
                            </div>

                            <c:choose>
                                <c:when test="${item.returnTrip}">
                                    <!-- Hiển thị ngược -->
                                    <small><strong>${item.arrivalStationName} → ${item.departureStationName}</strong></small>
                                </c:when>
                                <c:otherwise>
                                    <!-- Hiển thị xuôi -->
                                    <small><strong>${item.departureStationName} → ${item.arrivalStationName}</strong></small>
                                </c:otherwise>
                            </c:choose>

                            <div>
                                <small>Toa ${item.carriageNumber}, Ghế ${item.seatNumber}</small><br/>
                                <small>
                                    Giá: 
                                    <strong>
                                        <fmt:formatNumber value="${item.price}" pattern="###,##0.##"/> VND
                                    </strong>
                                </small>
                            </div>
                        </div>

                        <!-- Nút Xóa vé bên phải -->
                        <div>
                            <form action="cartitem" method="post" class="remove-form m-0 p-0">
                                <!-- Ẩn param để CartServlet biết cần xóa vé -->
                                <input type="hidden" name="removeSeatID" value="${item.seatID}" />

                                <!-- Giữ lại param tìm kiếm (nếu cần) -->
                                <input type="hidden" name="departureStationID" value="${param.departureStationID}" />
                                <input type="hidden" name="arrivalStationID"   value="${param.arrivalStationID}" />
                                <input type="hidden" name="departureDay"       value="${param.departureDay}" />
                                <input type="hidden" name="tripType"           value="${param.tripType}" />
                                <input type="hidden" name="returnDate"         value="${param.returnDate}" />

                                <input type="hidden" name="tripID" value="${item.trip.tripID}" />

                                <button type="submit" class="btn btn-danger btn-sm btn-remove">
                                    Xóa
                                </button>
                            </form>
                        </div>
                    </div>
                </c:forEach>
                <div class="mt-3 text-end">
                    Tổng tiền: 
                    <strong>
                        <fmt:formatNumber value="${totalPrice}" pattern="###,##0.##"/>
                    </strong> VND
                </div>

            </div>

            <!-- Nút "Mua vé" (checkout) -->
            <!-- form checkout trong cart.jsp -->
            <form action="cartitem" method="post" class="checkout-form text-end">
                <input type="hidden" name="action" value="checkout">
                <input type="hidden" name="tripID" value="${cartItems[0].trip.tripID}" />

                <!-- Giữ lại param tìm kiếm -->
                <input type="hidden" name="departureStationID" value="${param.departureStationID}" />
                <input type="hidden" name="arrivalStationID"   value="${param.arrivalStationID}" />
                <input type="hidden" name="departureDay"       value="${param.departureDay}" />
                <input type="hidden" name="tripType"           value="${param.tripType}" />
                <input type="hidden" name="returnDate"         value="${param.returnDate}" />

                <button type="submit" class="btn btn-success">Mua vé</button>
            </form>

        </c:if>
    </div>
</div>
<script>

</script>
<!-- Kết thúc snippet giỏ vé -->
