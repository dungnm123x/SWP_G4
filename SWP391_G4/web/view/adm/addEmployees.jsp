<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thêm Nhân Viên</title>
        <link rel="stylesheet" href="./css/admin/addEmployees.css">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="add-employee-container" action="admin" method="post">
            <h1>Thêm Nhân Viên</h1>
            <form method="post" action="admin">
                <input type="hidden" name="action" value="addEmployee">

                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" required placeholder="Nhập username">
                </div>
                <div class="form-group">
                    <label>Password</label>
                    <input type="password" name="password" required placeholder="Nhập mật khẩu">
                </div>
                <div class="form-group">
                    <label>Họ và Tên</label>
                    <input type="text" name="fullName" required placeholder="Nhập họ và tên">
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" required placeholder="Nhập email">
                </div>
                <div class="form-group">
                    <label>Số điện thoại</label>
                    <input type="text" name="phone" required placeholder="Nhập số điện thoại">
                </div>

                <div class="button-group">
                    <button type="submit" class="btn-confirm">Thêm Nhân Viên</button>
                    <a href="admin?view=employees" class="btn-cancel">Hủy</a>
                </div>
                <% String message = (String) request.getAttribute("message"); %>
                <% if (message != null) { %>
                <div style="color: red; font-weight: bold; margin-bottom: 10px;">
                    <%= message %>
                </div>
                <% } %>

            </form>
        </div>
    </body>
</html>
