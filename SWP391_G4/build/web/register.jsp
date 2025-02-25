<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Register</title>
        <!-- Custom styles -->
        <link rel="stylesheet" href="css/login.css">
        <link rel="icon" href="images/cash-register.gif" type="images/x-icon"/>

        <!-- Font Awesome & Google Fonts -->
        <script src="https://kit.fontawesome.com/a81368914c.js"></script>
        <link href="https://fonts.googleapis.com/css2?family=Open+Sans&display=swap" rel="stylesheet">
    </head>
        <input type="hidden" id="status" value="<%= request.getAttribute("status")%>">

        <div class="main">
            <section class="signup">
                <div class="container">
                    <div class="signup-content">
                        <div class="signup-form">
                            <h2 class="form-title">Sign up</h2>
                            <form action="register" method="POST" class="register-form" id="register-form" onsubmit="return onSubmitForm(event)">
                                <div class="form-group">
                                    <label for="username"><i class="fas fa-user"></i></label>
                                    <input type="text" name="username" id="username" placeholder="UserName"
                                           value="${requestScope.username}" class="${requestScope.usernameError != null ? 'input-error' : ''}"
                                           required oninput="clearError(this)"/>
                                    <p class="error-message">${requestScope.usernameError}</p>
                                </div>
                                <div class="form-group">
                                    <label for="email"><i class="fas fa-envelope"></i></label>
                                    <input type="email" name="email" id="email" placeholder="Your Email"
                                           value="${requestScope.email}" class="${requestScope.emailError != null ? 'input-error' : ''}"
                                           required oninput="clearError(this)"/>
                                    <p class="error-message">${requestScope.emailError}</p>
                                </div>
                                <div class="form-group">
                                    <label for="password"><i class="fas fa-lock"></i></label>
                                    <input type="password" name="password" id="password" placeholder="Password"
                                           class="${requestScope.passwordError != null ? 'input-error' : ''}" required/>
                                    <p class="error-message">${requestScope.passwordError}</p>
                                </div>

                                <div class="form-group">
                                    <label for="re_pass"><i class="fas fa-lock"></i></label>
                                    <input type="password" name="repassword" id="re_pass" placeholder="Repeat your password"
                                           class="${requestScope.repasswordError != null ? 'input-error' : ''}" required/>
                                    <p class="error-message">${requestScope.repasswordError}</p>
                                </div>
                                <div class="form-group">
                                    <label for="fullname"><i class="fas fa-user"></i></label>
                                    <input type="text" name="fullname" id="fullname" placeholder="Full Name" value="${requestScope.fullname}" required/>
                                </div>
                                <div class="form-group">
                                    <label for="phone"><i class="fas fa-phone"></i></label>
                                    <input type="text" name="phone" id="phone" placeholder="Your Contact Number"
                                           value="${requestScope.phone}" class="${requestScope.phoneError != null ? 'input-error' : ''}"
                                           required oninput="clearError(this)"/>
                                    <p class="error-message">${requestScope.phoneError}</p>
                                </div>

                                <div class="form-group">
                                    <label for="address"><i class="fas fa-map-marker-alt"></i></label>
                                    <input type="text" name="address" id="address" placeholder="Your City" value="${requestScope.address}" required/>
                                </div>

                                <div class="form-group">
                                    <input type="checkbox" name="agree-term" id="agree-term" class="agree-term" required/>
                                    <label for="agree-term" class="label-agree-term">
                                        <span><span></span></span>I agree to all statements in <a href="#" class="term-service">Terms of service</a>
                                    </label>
                                </div>

                                <div class="form-group form-button">
                                    <input type="submit" name="signup" id="signup" class="form-submit" value="Register"/>
                                </div>

                                <p style="color: red">${requestScope.error}</p>

                            </form>
                        </div>

                        <div class="signup-image">
                            <figure><img src="img/register.png" alt="sign up image"></figure>
                            <a href="login.jsp" class="signup-image-link">I am already a member</a>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                var msg = document.getElementById("status").value;
                if (msg == "success") {
                    swal({
                        title: "Congrats",
                        text: "Account Created Successfully",
                        icon: "success",
                        timer: 3000,
                        button: false
                    });

                    setTimeout(function () {
                        window.location.href = "home";
                    }, 3000);
                }
            });
        </script>
        <script>
            function clearError(input) {
                // Xóa class input-error khi người dùng nhập lại dữ liệu
                input.classList.remove("input-error");

                // Ẩn thông báo lỗi dưới ô nhập
                let errorMsg = input.nextElementSibling;
                if (errorMsg && errorMsg.classList.contains("error-message")) {
                    errorMsg.innerText = "";
                }
            }
        </script>

        <!-- Validation Scripts -->
        <script type="text/javascript" src="js/validation.js"></script>
    </body>
</html>