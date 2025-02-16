<%@page import="java.util.*"%>
<%@page import="molder.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Train Login</title>
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
    <input type="hidden" id="msg" value="<%= request.getAttribute("msg") != null ? request.getAttribute("msg") : "" %>">

    <div class="main">
        <section class="sign-in">
            <div class="container">
                <div class="signin-content">
                    <div class="signin-image">
                        <figure><img src="img/Login.jpg" alt="sign up image"></figure>
                        <a href="register.jsp" class="signup-image-link">Create an account</a>
                        <a href="forgotPassword.jsp" class="signup-image-link">Forgot password</a>
                    </div>
                    <div class="signin-form">
                        <h2 class="form-title">Sign in</h2>

                        <!-- Hiển thị lỗi chung nếu có -->
                        <% if (request.getAttribute("loginError") != null) { %>
                            <p class="error-message"><%= request.getAttribute("loginError") %></p>
                        <% } %>

                        <form action="login" method="post" class="register-form" id="login-form">
                            <div class="form-group">
                                <label for="username"><i class="zmdi zmdi-email"></i></label>
                                <input type="text" name="username" id="username" placeholder="UserName"
                                       required oninput="clearError(this)"/>
                            </div>

                            <div class="form-group password-container">
                                <label for="password"><i class="zmdi zmdi-lock"></i></label>
                                <input type="password" name="password" id="password" placeholder="Password"
                                       required oninput="clearError(this)"/>
                                <span id="togglePassword">👁️</span>
                            </div>

                            <div class="form-group">
                                <input type="checkbox" id="remember_me" name="remember_me" class="agree-term"
                                       <%= "on".equals(request.getParameter("remember_me")) ? "checked" : "" %> />
                                <label for="remember_me" class="label-agree-term"><span><span></span></span>Remember me</label>
                            </div>

                            <div class="form-group form-button">
                                <input type="submit" name="signin" id="signin" class="form-submit" value="Login"> 
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </section>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function() {
            const passwordInput = document.getElementById("password");
            const togglePassword = document.getElementById("togglePassword");

            togglePassword.addEventListener("click", function() {
                if (passwordInput.type === "password") {
                    passwordInput.type = "text";
                    togglePassword.innerHTML = "🙈";
                } else {
                    passwordInput.type = "password";
                    togglePassword.innerHTML = "👁️";
                }
            });

            var msg = document.getElementById("msg").value;
            if (msg === "Email or password incorrect!") {
                alert("Wrong Email or Password");
            }
        });

        function clearError(input) {
            input.classList.remove("input-error");
        }
    </script>
</body>
</html>
