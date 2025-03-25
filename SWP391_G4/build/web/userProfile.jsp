<%-- 
    Document   : userProfile
    Created on : Feb 14, 2025, 10:45:27 AM
    Author     : dung9
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>User Profile</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <script src="https://cdn.tailwindcss.com"></script>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">

        <style>
            input[type="date"] {
                padding: 0.5rem; /* Thêm khoảng cách bên trong */
                border: 1px solid #cbd5e0; /* Màu viền */
                border-radius: 0.25rem; /* Bo tròn góc */
                font-size: 1rem; /* Kích thước chữ */
                color: #4a5568; /* Màu chữ */
                background-color: #fff; /* Màu nền */
                transition: border-color 0.2s, box-shadow 0.2s; /* Hiệu ứng chuyển đổi */
            }

            input[type="date"]:focus {
                border-color: #63b3ed; /* Màu viền khi focus */
                box-shadow: 0 0 0 3px rgba(99, 179, 237, 0.5); /* Hiệu ứng bóng khi focus */
                outline: none; /* Bỏ viền mặc định khi focus */
            }
            input[type=number]::-webkit-outer-spin-button,
            input[type=number]::-webkit-inner-spin-button {
                -webkit-appearance: none;
                margin: 0;
            }
            input[type=number] {
                -moz-appearance: textfield;
            }
        </style>
    </head>

    <body class="bg-gray-100">

        <div class="mx-auto mt-10">

            <div class="flex">
                <!-- Sidebar -->
                <c:set value="${requestScope.userProfile}" var="user"/>
                <c:set value="${sessionScope.user}" var="account"/>
                <div class="w-1/4 bg-white p-4 rounded shadow">
                    <div class="flex items-center mb-6">
                        <div class="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center">
                            <i class="fas fa-user text-gray-400 text-4xl"></i>
                        </div>
                        <div class="ml-4">
                            <div class="font-bold">${account.username}</div>
                        </div>
                    </div>
                    <ul>
                        <li class="mb-4">
                            <a href="updateuser" class="flex items-center text-red-500">
                                <i class="fas fa-id-card mr-2"></i>
                                Hồ Sơ
                            </a>
                        </li>
                        <li class="mb-4">
                            <a href="changePassword.jsp" class="flex items-center text-gray-700">
                                <i class="fas fa-lock mr-2"></i>
                                Đổi Mật Khẩu
                            </a>
                        </li>
                        <li class="mb-4">
                            <a id="backButton" href="#" class="flex items-center text-gray-700">
                                <i class="fas fa-backward mr-2"></i> Trở về
                            </a>
                        </li>


                    </ul>
                </div>

                <!-- Main Content -->
                <div class="w-3/4 bg-white p-6 rounded shadow ml-6">
                    <h2 class="text-2xl font-bold mb-4">Hồ Sơ Của Tôi</h2>
                    <p class="text-gray-600 mb-6">Quản lý thông tin hồ sơ để bảo mật tài khoản</p>

                    <form action="updateuser" method="post">
                        <div class="mb-4">
                            <label class="block text-gray-700">Tên đăng nhập</label>
                            <input type="text" readonly value="${account.username}" class="w-full p-2 border border-gray-300 rounded" disabled>
                        </div>
                        <div class="mb-4">
                            <label class="block text-gray-700">Họ và Tên</label>
                            <input type="text" value="${user.fullName}" class="w-full p-2 border border-gray-300 rounded" required name="fullName">
                            <h4 style="color: red">${requestScope.err1}</h4>
                        </div>
                        <div class="mb-4">
                            <label class="block text-gray-700">Địa chỉ</label>
                            <input type="text" value="${user.address}" class="w-full p-2 border border-gray-300 rounded" required name="address">
                            <h4 style="color: red">${requestScope.err1}</h4>
                        </div>
                        <div class="mb-4">
                            <label class="block text-gray-700">Email</label>
                            <div class="flex items-center">
                                <input type="email" value="${account.email}" class="w-full p-2 border border-gray-300 rounded" disabled name="email">                    
                            </div>
                        </div>
                        <div class="mb-4">
                            <label class="block text-gray-700">Số điện thoại</label>
                            <div class="flex items-center">
                                <input type="text" value="${user.phoneNumber}" class="w-full p-2 border border-gray-300 rounded" name="phone" required>
                            </div>
                            <h4 style="color: red">${requestScope.err}</h4>
                        </div>
                        <br>
                        <button class="bg-red-500 text-white px-4 py-2 rounded" type="submit">Lưu</button>
                        <h3 style="color: #00c4a7;">${requestScope.mess}</h3>
                    </form>

                </div>
            </div>
    </body>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const dateInput = document.getElementById('dateInput');
            const today = new Date().toISOString().split('T')[0];
            dateInput.setAttribute('max', today);
        });
    </script>
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            let userRole = "<%= session.getAttribute("role") %>"; // Lấy role từ session
            let backButton = document.getElementById("backButton");

            if (userRole === "1" || userRole === "2") { // 1 = admin, 2 = customer
                backButton.setAttribute("href", "javascript:history.back()");
            } else {
                backButton.setAttribute("href", "home.jsp");
                backButton.innerHTML = '<i class="fas fa-home mr-2"></i> Trang chủ';
            }
        });
    </script>
</html>