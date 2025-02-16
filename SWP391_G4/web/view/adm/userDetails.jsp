<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết ${type}</title>
        <link rel="stylesheet" href="css/admin/userDetails.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            .edit-form {
                display: none; /* Ẩn form sửa ban đầu */
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="main-content">
                <div class="header">
                    <h1>Chi tiết ${type}</h1>
                </div>
                <div class="content">
                    <c:if test="${not empty userDetails}">
                        <form action="admin" method="post" class="view-form">  <%-- Form hiển thị thông tin --%>
                            <input type="hidden" name="action" value="update"> <%-- Action cho update --%>
                            <input type="hidden" name="type" value="${type}"> <%-- Lưu type để biết đang sửa employee hay customer --%>
                            <input type="hidden" name="id" value="${userDetails.userId}"> <%-- ID của user để update --%>

                            <p><strong>Username:</strong> ${userDetails.username}</p> <%-- Hiển thị username --%>
                            <p><strong>Họ và tên:</strong> ${userDetails.fullName}</p>
                            <p><strong>Email:</strong> ${userDetails.email}</p> <%-- Hiển thị email --%>
                            <p><strong>Số điện thoại:</strong> ${userDetails.phoneNumber}</p>
                            <p><strong>Địa chỉ:</strong> ${userDetails.address}</p>

                            <button type="button" id="editButton" class="btn btn-primary">Chỉnh sửa</button>
                            <a href="admin?view=${type}" class="btn btn-secondary">Trở lại</a>
                        </form>

                        <form action="admin" method="post" class="edit-form"> <%-- Form chỉnh sửa --%>
                            <input type="hidden" name="action" value="save"> <%-- Action cho save --%>
                            <input type="hidden" name="type" value="${type}">
                             <input type="hidden" name="id" value="${userDetails.userId}">

                            <div class="mb-3">
                                <label for="username" class="form-label">Username:</label>
                                <p>${userDetails.username}</p> <%-- Không cho sửa username --%>
                            </div>
                            <div class="mb-3">
                                <label for="fullName" class="form-label">Họ và tên:</label>
                                <input type="text" class="form-control" id="fullName" name="fullName" value="${userDetails.fullName}" required>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email:</label>
                                <p>${userDetails.email}</p> <%-- Không cho sửa email --%>
                            </div>
                            <div class="mb-3">
                                <label for="phone" class="form-label">Số điện thoại:</label>
                                <input type="tel" class="form-control" id="phone" name="phone" value="${userDetails.phoneNumber}" required>
                            </div>
                            <div class="mb-3">
                                <label for="address" class="form-label">Địa chỉ:</label>
                                <input type="text" class="form-control" id="address" name="address" value="${userDetails.address}" required>
                            </div>

                            <button type="submit" class="btn btn-success">Lưu</button>
                            <button type="button" id="cancelButton" class="btn btn-secondary">Hủy</button>
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

            editButton.addEventListener('click', () => {
                viewForm.style.display = 'none';
                editForm.style.display = 'block';
            });

            cancelButton.addEventListener('click', () => {
                editForm.style.display = 'none';
                viewForm.style.display = 'block';
            });
        </script>
    </body>
</html>