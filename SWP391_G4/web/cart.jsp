<%-- 
    Document   : cart
    Created on : Feb 28, 2025, 12:47:14 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Giỏ vé</title>
        <!-- Bootstrap CSS -->
        <link 
            rel="stylesheet" 
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
            />
        <!-- File CSS tùy chỉnh nếu có -->
        <link href="css/cart.css" rel="stylesheet" type="text/css"/>
    </head>
    <body class="container my-4">

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
                    <!-- Danh sách vé (dùng list-group hoặc table đều được) -->
                    <div class="list-group mb-3">
                        <c:forEach var="item" items="${cartItems}">
                            <div class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
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
                                        <small>Giá: <strong>${item.price} $</strong></small>
                                    </div>
                                </div>
                                <!-- Nút Xóa vé bên phải -->
                                <div>
                                    <form action="cartitem" method="post" class="m-0 p-0">
                                        <!-- Giữ lại param tìm kiếm (nếu cần) -->
                                        <input type="hidden" name="departureStationID" value="${param.departureStationID}" />
                                        <input type="hidden" name="arrivalStationID"   value="${param.arrivalStationID}" />
                                        <input type="hidden" name="departureDay"       value="${param.departureDay}" />
                                        <input type="hidden" name="tripType"           value="${param.tripType}" />
                                        <input type="hidden" name="returnDate"         value="${param.returnDate}" />

                                        <input type="hidden" name="tripID" value="${item.trip.tripID}" /> 


                                        <!-- Xóa vé -->
                                        <input type="hidden" name="removeSeatID" value="${item.seatID}" />
                                        <button type="submit" class="btn btn-danger btn-sm">
                                            Xóa
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <!-- Nút "Mua vé" (checkout) -->
                    <form action="cartitem" method="post" class="text-end">
                        <input type="hidden" name="action" value="checkout">
                        <input type="hidden" name="tripID" value="${cartItems[0].trip.tripID}" />

                        <button type="submit" class="btn btn-success">
                            Mua vé
                        </button>
                    </form>
                </c:if>
            </div>
        </div>

        <!-- Bootstrap JS (nếu cần) -->
        <script 
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js">
        </script>
        <!-- Nếu muốn dùng icon Bootstrap (bi-...) thì thêm link icon:
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css"/> 
        -->
    </body>
</html>