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
    <div class="collapse navbar-collapse d-flex justify-content-center" id="navbarSupportedContent">
        <ul class="navbar-nav">
            <li class="nav-item"><a class="nav-link text-white" href="home"><i class="fas fa-house"></i> <strong>Home</strong></a></li>
            <li class="nav-item"><a class="nav-link text-white" href="cancel-ticket"><i class="fas fa-ticket-alt"></i> <strong>Hoàn vé</strong></a></li>
            <li class="nav-item"><a class="nav-link text-white" href="blog-list"><i class="fas fa-newspaper"></i> <strong>Blog</strong></a></li>
            <li class="nav-item"><a class="nav-link text-white" href="rule-list"><i class="fas fa-file-contract"></i> <strong>Quy Định</strong></a></li>
            <li class="nav-item"><a class="nav-link text-white" href="contact"><i class="fas fa-phone"></i> <strong>Liên hệ</strong></a></li>
        </ul>
    </div>

    <!---Start TaggoAI--->
    <script async data-taggo-botid="67d2c8af0cf62d81f8f97d44" src="https://widget.taggo.chat/v2.js"></script>
    <!---End TaggoAI--->

    <!-- Kiểm tra xem người dùng đã đăng nhập chưa -->
    <div class="navbar-nav" id="userNav">
        <div class="nav-item dropdown">
            <a class="nav-link dropdown-toggle text-white"  id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <strong>Hello</strong>
            </a>
            <div class="dropdown-menu" aria-labelledby="userDropdown" id="userMenu">
                <a class="dropdown-item" href="login.jsp" id="loginOption">Đăng Nhập</a>
            </div>
        </div>
    </div>
</nav>

<script>
    document.addEventListener("DOMContentLoaded", function () {
        var user = ${sessionScope.user != null ? 'true' : 'false'};
        var userName = "${sessionScope.user != null ? sessionScope.user.fullName : ''}";
        var userDropdown = document.getElementById("userDropdown");
        var userMenu = document.getElementById("userMenu");
        var dropdownContainer = document.querySelector(".dropdown");

        if (user) {
            userDropdown.innerHTML = "<strong>Hello, " + userName + "</strong>";
            userMenu.innerHTML = '<a class="dropdown-item" href="updateuser">Profile</a>'
                    + '<a class="dropdown-item" href="ticket-list">Purchase order</a>' +
                    '<a class="dropdown-item" href="logout">Logout</a>';
        }

        // Giữ dropdown mở khi click vào menu
        dropdownContainer.addEventListener("click", function (event) {
            event.stopPropagation(); // Ngăn dropdown đóng ngay lập tức
            this.classList.toggle("show");
            userMenu.classList.toggle("show");
        });

        // Đóng dropdown khi click ra ngoài
        document.addEventListener("click", function (event) {
            if (!dropdownContainer.contains(event.target)) {
                userMenu.classList.remove("show");
            }
        });
    });
</script>
<br>
