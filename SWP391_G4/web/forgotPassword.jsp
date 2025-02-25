<%-- 
    Document   : forgotPassword
    Created on : Feb 18, 2025, 11:57:08 AM
    Author     : dung9
--%>

<%@page import="java.util.*"%>
<%@page import="molder.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Train Forgot Password</title>
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
                            <a href="login.jsp" class="signup-image-link">Back to Login</a>
                        </div>
                        <div class="signin-form">

                            <h2>Forgot password</h2>
                            <form action="sendOtpResetPass" method="POST" class="register-form" id="login-form">
                                <div class="form-group">
                                    <label for="email"><i class="zmdi zmdi-email"></i></label>
                                    <input type="email" name="email"id="email" placeholder="Your Email:">
                                </div>
                                <div class="form-group form-button">
                                    <input type="submit" name="signin" id="signin" class="form-submit" value="Send OTP"> 
                                </div>
                            </form>
                            <p class="text-danger">${Notexisted}</p> <!-- Hi?n th? thông báo n?u email không t?n t?i -->
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </body>
</html>
