<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css" integrity="sha384-vpZl2lJD5zzOykKkLrBbEPv9Wp0yqDgqQ5m9vJkzQJqJpzz/3ZvVoKHyN1p+qLiX" crossorigin="anonymous">
<link rel="stylesheet" href="css/navbar.css"> <!-- Link tới file CSS đã tạo -->
<link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" rel="stylesheet">

<nav class="navbar navbar-expand-md navbar-light bg-primary" id="includes-topnav">
    <!-- Left side of the navbar - Logo -->
    <a class="navbar-brand" href="home">
        <img src="img/logo.jpg" alt="Logo" />
    </a>
    
    <!-- Center section of the navbar - Menu items -->
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <ul class="navbar-nav">
            <li class="nav-item"><a class="nav-link text-white" href="home"><i class="fas fa-house"></i> <strong>Home</strong></a></li>
            <li class="nav-item"><a class="nav-link text-white" href="about.jsp"><i class="fas fa-info-circle"></i> <strong>Giới thiệu</strong></a></li>

            <li class="nav-item"><a class="nav-link text-white" href="#"><i class="fas fa-ticket-alt"></i> <strong>Hoàn vé</strong></a></li>


            <li class="nav-item"><a class="nav-link text-white" href="blog-list"><i class="fas fa-newspaper"></i> <strong>Blog</strong></a></li>
            <li class="nav-item"><a class="nav-link text-white" href="rule-list"><i class="fas fa-file-contract"></i> <strong>Quy Định</strong></a></li>
            <li class="nav-item"><a class="nav-link text-white" href="contact.jsp"><i class="fas fa-phone"></i> <strong>Liên hệ</strong></a></li>
        </ul>
    </div>

    <!-- Kiểm tra xem người dùng đã đăng nhập chưa -->
    <c:choose>
        <c:when test="${sessionScope.user != null}">
            <!-- Nếu người dùng đã đăng nhập, hiển thị tên và nút Đăng xuất -->
            <div class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="updateuser"><i class="fas fa-user"></i> Hello, ${sessionScope.user.fullName}</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="logout"><i class="fas fa-sign-out-alt"></i> Đăng Xuất</a>
                </li>
            </div>
        </c:when>
        <c:otherwise>
            <!-- Nếu chưa đăng nhập, hiển thị nút Đăng nhập -->
            <div class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="login.jsp"><i class="fas fa-sign-in-alt"></i> Đăng Nhập</a>
                </li>
            </div>
        </c:otherwise>
    </c:choose>

</nav>
<br>
