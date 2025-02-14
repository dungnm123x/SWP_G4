<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        
        <!-- Custom styles-path -->
        <link rel="stylesheet" href="css/register.css">

        <!-- Font Awesome kit script -->
        <script src="https://kit.fontawesome.com/a81368914c.js"></script>

        <!-- Google Fonts Open Sans-->
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans&display=swap" rel="stylesheet">
    </head>

    <body style="background-image: url('img/login.jpg'); background-size: cover;">

        <div class="container">
            <div class="img">
                <!-- You can add an image here if needed -->
            </div>
            <div class="login-container">
                <form action="register" method="post" onsubmit="return onSubmitForm(event)">
                    <h2>REGISTER</h2>
                    <div id="whitespaceMessage"></div>

                    <!-- Username -->
                    <div class="input-div one">
                        <div class="i">
                            <i class="fas fa-user"></i>
                        </div>
                        <div>
                            <h5>Tên đăng nhập <div id="usernameMessage"></div></h5>
                            <input class="input" type="text" id="username" name="username" required oninput="validateUsername(),checkWhitespace()">
                        </div>
                    </div>

                    <!-- Phone Number -->
                    <div class="input-div one">
                        <div class="i">
                            <i class="fas fa-phone"></i>
                        </div>
                        <div>
                            <h5>Số điện thoại <div id="phoneMessage"></div></h5>
                            <input class="input" type="text" id="phoneNumber" name="phone" required oninput="validatePhoneNumber(),checkWhitespace()">
                        </div>
                    </div>

                    <!-- Password -->
                    <div class="input-div one">
                        <div class="i">
                            <i class="fas fa-key"></i>
                        </div>
                        <div>
                            <h5>Mật khẩu <div id="passwordMessage"></div></h5>
                            <input class="input" type="password" id="password" name="password" required oninput="validatePassword(),checkWhitespace()">
                        </div>
                    </div>

                    <!-- Repeat Password -->
                    <div class="input-div two">
                        <div class="i">
                            <i class="fas fa-key"></i>
                        </div>
                        <div>
                            <h5>Nhập lại mật khẩu <div id="repeatPasswordMessage"></div></h5>
                            <input class="input" type="password" id="re_pass" name="repassword" required oninput="validateRepeatPassword(),checkWhitespace()">
                        </div>
                    </div>

                    <!-- Email -->
                    <div class="input-div one">
                        <div class="i">
                            <i class="fas fa-envelope"></i>
                        </div>
                        <div>
                            <h5>Email <div id="emailMessage"></div></h5>
                            <input class="input" type="email" id="email" name="email" required oninput="validateEmail(),checkWhitespace()">
                        </div>
                    </div>
 
                    <div class="input-div one">
                        <div class="i">
                            <i class="fas fa-map-marker-alt"></i>
                        </div>
                        <div>
                            <h5>Địa chỉ <div id="addressMessage"></div></h5>
                            <input class="input" type="text" id="address" name="address" required oninput="checkWhitespace()">
                        </div>
                    </div>

                    <!-- Error message -->
                    <h3 style="color: red">${requestScope.error}</h3>

                    <!-- Submit button -->
                    <div class="btn-container">
                        <input style="margin-bottom: 10px" type="submit" class="btn" value="Đăng ký">
                        <br>
                        <a class="col-sm-6" href="login">
                            <button class="btn btn-primary btn-block" type="button" id="btn-signup"> Trở về</button>
                        </a>
                    </div>

                    <div class="account">
                        <p>Bạn đã có tài khoản?</p>
                        <a href="login">Đăng nhập.</a>
                    </div>
                </form>
            </div>
        </div>

        <!-- Validation Scripts -->
        <script type="text/javascript" src="js/validation.js"></script>
        <script type="text/javascript" src="js/login.js"></script>
    </body>
</html>
