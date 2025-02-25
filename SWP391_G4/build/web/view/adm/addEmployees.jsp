<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Thêm Nhân Viên</title>
    <link rel="stylesheet" href="./css/admin/addEmployees.css">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600&display=swap" rel="stylesheet">
    <style>
        .error {
            color: red;
        }
    </style>
</head>
<body>
    <div class="add-employee-container">
        <h1>Thêm Nhân Viên</h1>
        <form method="post" action="admin" onsubmit="return validateForm()">
            <input type="hidden" name="action" value="addEmployee">

            <div class="form-group">
                <label>Username</label>
                <input type="text" id="username" name="username" required placeholder="Nhập username" onblur="checkUsername()">
                <span id="usernameError" class="error"></span>
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
                <input type="email" id="email" name="email" required placeholder="Nhập email" onblur="checkEmail()">
                <span id="emailError" class="error"></span>
            </div>
            <div class="form-group">
                <label>Số điện thoại</label>
                <input type="text" id="phone" name="phone" required placeholder="Nhập số điện thoại" onblur="checkPhone()">
                <span id="phoneError" class="error"></span>
            </div>
            <div class="form-group">
                <label>Địa chỉ</label>
                <input type="text" name="address" required placeholder="Nhập địa chỉ">
            </div>

            <div class="button-group">
                <button type="submit" class="btn-confirm">Thêm Nhân Viên</button>
                <a href="admin?view=employees" class="btn-cancel">Hủy</a>
            </div>

            <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
            <% if (errorMessage != null) { %>
                <div style="color: red; font-weight: bold; margin-bottom: 10px;">
                    <%= errorMessage %>
                </div>
            <% } %>

        </form>
    </div>

    <script>
        function checkUsername() {
            const username = document.getElementById("username").value;
            const errorSpan = document.getElementById("usernameError");

            // Gửi yêu cầu AJAX đến server để kiểm tra username
            fetch(`checkUsername?username=${username}`)
                .then(response => response.json())
                .then(data => {
                    if (data.exists) {
                        errorSpan.textContent = "Username đã tồn tại!";
                    } else {
                        errorSpan.textContent = "";
                    }
                });
        }

        function checkEmail() {
            const email = document.getElementById("email").value;
            const errorSpan = document.getElementById("emailError");

            // Gửi yêu cầu AJAX đến server để kiểm tra email
            fetch(`checkEmail?email=${email}`)
                .then(response => response.json())
                .then(data => {
                    if (data.exists) {
                        errorSpan.textContent = "Email đã tồn tại!";
                    } else {
                        errorSpan.textContent = "";
                    }
                });
        }

        function checkPhone() {
            const phone = document.getElementById("phone").value;
            const errorSpan = document.getElementById("phoneError");

            // Gửi yêu cầu AJAX đến server để kiểm tra số điện thoại
            fetch(`checkPhone?phone=${phone}`)
                .then(response => response.json())
                .then(data => {
                    if (data.exists) {
                        errorSpan.textContent = "Số điện thoại đã tồn tại!";
                    } else {
                        errorSpan.textContent = "";
                    }
                });
        }

        function validateForm() {
            // Kiểm tra lỗi trước khi submit form
            return !(document.getElementById("usernameError").textContent ||
                     document.getElementById("emailError").textContent ||
                     document.getElementById("phoneError").textContent);
        }
    </script>
</body>
</html>