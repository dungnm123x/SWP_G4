<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Train Ticket Shop - Đăng nhập</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Custom styles -->
        <link rel="stylesheet" href="css/login.css">
        <script type="text/javascript" src="js/validation.js"></script>

        <!-- Font Awesome kit -->
        <script src="https://kit.fontawesome.com/a81368914c.js"></script>

        <!-- Google Fonts -->
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans&display=swap" rel="stylesheet">
    </head>

    <body style="background-image: url('img/Login.jpg');">
        <div class="container">
            <div class="login-container">
                <form action="login" method="post">
                    <h2>Đăng Nhập</h2>
                    <p>Chào Mừng Trở Lại!</p>

                    <!-- Hiển thị lỗi nếu có -->
                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) { %>
                        <h3 style="color: red; text-align: center;"><%= error %></h3>
                    <% } %>

                    <!-- Lấy dữ liệu từ Cookie -->
                    <%
                        String savedUsername = "";
                        String savedPassword = "";
                        boolean rememberMeChecked = false;
                        
                        Cookie[] cookies = request.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if (cookie.getName().equals("cuser")) {
                                    savedUsername = cookie.getValue();
                                }
                                if (cookie.getName().equals("cpass")) {
                                    savedPassword = cookie.getValue();
                                }
                                if (cookie.getName().equals("crem") && "on".equals(cookie.getValue())) {
                                    rememberMeChecked = true;
                                }
                            }
                        }
                    %>

                    <!-- Ô nhập Username -->
                    <div class="input-div one">
                        <div class="i">
                            <i class="fas fa-user"></i>
                        </div>
                        <div>
                            <h5>Tên đăng nhập</h5>
                            <input class="input" type="text" name="username" value="<%= savedUsername %>" required oninput="validateForm()">
                        </div>
                    </div>

                    <!-- Ô nhập Password -->
                    <div class="input-div two">
                        <div class="i">
                            <i class="fas fa-key"></i>
                        </div>
                        <div>
                            <h5>Mật khẩu</h5>
                            <input class="input" type="password" name="password" value="<%= savedPassword %>" required oninput="validateForm()">
                        </div>
                    </div>

                    <!-- Ô "Nhớ mật khẩu" -->
                    <div class="remember-me">
                        <input type="checkbox" name="remember" <%= rememberMeChecked ? "checked" : "" %>> 
                        <label>Nhớ mật khẩu</label>
                    </div>

                    <!-- Nút Đăng nhập -->
                    <input type="submit" class="btn" name="submit" value="Đăng Nhập">

                    <!-- Phần đăng ký & quên mật khẩu -->
                    <div class="account-options">
                        <div class="register">
                            <p>Chưa có tài khoản?</p>
                            <a href="register" class="btn-secondary">Đăng ký ngay</a>
                        </div>
                        <div class="forgot-password">
                            <p>Quên mật khẩu?</p>
                            <a href="forgotPassword.jsp" class="btn-secondary">Khôi phục mật khẩu</a>
                        </div>
                    </div>

                </form>
            </div>
        </div>

        <script type="text/javascript" src="js/login.js"></script>
        <script type="text/javascript">
            // Giữ trạng thái placeholder nếu input có giá trị
            window.addEventListener('load', function () {
                document.querySelectorAll('.input').forEach(function (input) {
                    if (input.value.trim() !== '') {
                        input.focus();
                    }
                });
            });
        </script>
    </body>
</html>