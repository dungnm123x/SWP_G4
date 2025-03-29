<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>
            <c:choose>
                <c:when test="${type == 'employees'}">Nhân viên</c:when>
                <c:when test="${type == 'customers'}">Khách hàng</c:when>
                <c:otherwise>Chi tiết</c:otherwise>
            </c:choose>
        </title>
        <link rel="stylesheet" href="css/admin/userDetails.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            .edit-form {
                display: none;
            }
            .error {
                color: red;
                font-size: 0.8em;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="main-content">
                <div class="header">
                    <h1>Chi tiết
                        <c:choose>
                            <c:when test="${type == 'employees'}">nhân viên</c:when>
                            <c:when test="${type == 'customers'}">khách hàng</c:when>
                            <c:otherwise>Không gì cả</c:otherwise>
                        </c:choose>
                    </h1>
                </div>
                <div class="content">
                    <c:if test="${not empty userDetails}">
                        <form action="admin" method="post" class="view-form">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="type" value="${type}">
                            <input type="hidden" name="id" value="${userDetails.userId}">

                            <p><strong>Username:</strong> ${userDetails.username}</p>
                            <p><strong>Họ và tên:</strong> ${userDetails.fullName}</p>
                            <p><strong>Email:</strong> ${userDetails.email}</p>
                            <p><strong>Số điện thoại:</strong> ${userDetails.phoneNumber}</p>
                            <p><strong>Địa chỉ:</strong> ${userDetails.address}</p>

                            <button type="button" id="editButton" class="btn btn-primary">Chỉnh sửa</button>
                            <a href="admin?view=${type}" class="btn btn-secondary">Trở lại</a>
                        </form>

                        <form action="admin" method="post" class="edit-form" id="editForm">
                            <input type="hidden" name="action" value="save">
                            <input type="hidden" name="type" value="${type}">
                            <input type="hidden" name="id" value="${userDetails.userId}">

                            <div class="mb-3">
                                <label for="fullName" class="form-label">Họ và tên:</label>
                                <input type="text" class="form-control" id="fullName" name="fullName" value="${userDetails.fullName}" required>
                                <div id="fullNameError" class="error"></div>
                            </div>
                            <div class="mb-3">
                                <label for="address" class="form-label">Địa chỉ:</label>
                                <input type="text" class="form-control" id="address" name="address" value="${userDetails.address}" required>
                                <div id="addressError" class="error"></div>
                            </div>

                            <button type="submit" class="btn btn-success">Lưu</button>
                            <button type="button" id="cancelButton" class="btn btn-secondary">Hủy</button>
                            <c:if test="${not empty sessionScope.messageSave}">
                                <div class="alert alert-info">
                                    ${sessionScope.messageSave}
                                </div>
                                <c:remove var="messageSave" scope="session" /> <%-- Use JSTL to remove the attribute --%>
                            </c:if>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>
        <script>
            const editButton = document.getElementById('editButton');
            const cancelButton = document.getElementById('cancelButton');
            const viewForm = document.querySelector('.view-form');
            const editForm = document.querySelector('.edit-form');
            const fullNameInput = document.getElementById('fullName');
            const addressInput = document.getElementById('address');
            const fullNameError = document.getElementById('fullNameError');
            const addressError = document.getElementById('addressError');

            editButton.addEventListener('click', () => {
                viewForm.style.display = 'none';
                editForm.style.display = 'block';
            });

            cancelButton.addEventListener('click', () => {
                editForm.style.display = 'none';
                viewForm.style.display = 'block';
            });
            editForm.addEventListener('submit', (event) => {
                let isValid = true;
                if (fullNameInput.value.trim() === '') {
                    fullNameError.textContent = 'Họ và tên không được để trống.';
                    isValid = false;
                } else {
                    fullNameError.textContent = '';
                }

                if (addressInput.value.trim() === '') {
                    addressError.textContent = 'Địa chỉ không được để trống.';
                    isValid = false;
                } else {
                    addressError.textContent = '';
                }
                if (!isValid) {
                    event.preventDefault();
                }
            });

        </script>
    </body>
</html>