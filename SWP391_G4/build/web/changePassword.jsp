<%-- 
    Document   : changePassword
    Created on : Feb 17, 2025, 11:16:15 PM
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
                            <a href="updateuser" class="signup-image-link">Back to Profile</a>
                        </div>
                        <div class="signin-form">

                            <h2>New Password</h2>

                            <!-- Form đổi mật khẩu -->
                            <form action="changePassword" method="post">

                                <input type="password" name="currentPassword" placeholder="Password Now" ><br>
                                <input type="password" name="newPassword" placeholder="NewPass:"> <br>
                                <input type="password" name="confirmPassword"placeholder="CofirmPass:"><br>


                                <div class="form-group form-button">
                                    <input type="submit" name="signin" id="signin" class="form-submit" value="Submit"> 
                                </div>
                            </form>

                            <c:if test="${not empty passwordError}">
                                <div style="color: red;">${passwordError}</div>
                            </c:if>
                            <c:if test="${not empty repasswordError}">
                                <div style="color: red;">${repasswordError}</div>
                            </c:if>

                            <c:if test="${not empty successMessage}">
                                <div style="color: green;">${successMessage}</div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </section>
        </div>

    </body>
</html>

