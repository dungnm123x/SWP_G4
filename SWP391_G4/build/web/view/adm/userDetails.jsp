<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>User Details</title>
    <link rel="stylesheet" href="./css/admin/detail.css"> </head>
<body>
    <div class="add-employee-container">  <%-- Sử dụng class CSS đã có --%>
        <h1>Chi Tiết Người Dùng</h1>

        <div class="form-group">
            <label>ID:</label>
            <input type="text" value="${userDetails.userId}" readonly> <%-- readonly để không thể chỉnh sửa --%>
        </div>

        <div class="form-group">
            <label>Username:</label>
            <input type="text" value="${userDetails.username}" readonly>
        </div>

        <div class="form-group">
            <label>Full Name:</label>
            <input type="text" value="${userDetails.fullName}" readonly>
        </div>

        <div class="form-group">
            <label>Email:</label>
            <input type="text" value="${userDetails.email}" readonly>
        </div>

        <div class="form-group">
            <label>Phone Number:</label>
            <input type="text" value="${userDetails.phoneNumber}" readonly>
        </div>
        <div class="form-group">
            <label>Address:</label>
            <input type="text" value="${userDetails.address}" readonly>
        </div>

        <div class="form-group">
            <label>Role:</label>
            <input type="text" value="${type == 'employees' ? 'Employee' : 'Customer'}" readonly>
        </div>

        <div class="button-group">
            <a href="admin?view=${type}" class="btn-cancel">Trở lại</a> <%-- Chỉ có nút "Trở lại" --%>
        </div>
    </div>
</body>
</html>