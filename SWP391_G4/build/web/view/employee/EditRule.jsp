<%-- 
    Document   : EditRule
    Created on : Mar 5, 2025, 2:45:50 PM
    Author     : dung9
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Blog</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.ckeditor.com/4.16.2/full-all/ckeditor.js"></script>
        <link rel="stylesheet" href="css/employee.css">
        <style>
            body {
                font-family: 'Arial', sans-serif;
                background-color: #f4f6f9;
                margin: 0;
                padding: 0;
            }
            .container {
                background-color: #ffffff;
                border-radius: 10px;
                box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
                padding: 0; /* Để header liền kề với phần nội dung */
                max-width: 1200px;
                margin: 30px auto;
            }
            .header {
                padding: 20px;
                background-color: #007bff;
                color: white;
                border-radius: 10px 10px 0 0;
                text-align: center;
            }
            .header h4 {
                margin: 0;
            }
            .inner-form {
                padding: 30px;
            }
            .form-group label {
                font-weight: 600;
            }
            .btn-custom {
                background-color: #007bff;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 5px;
            }
            .btn-custom:hover {
                background-color: #0056b3;
            }
            .btn-outline-custom {
                border-color: #007bff;
                color: #007bff;
            }
            .btn-outline-custom:hover {
                background-color: #007bff;
                color: white;
            }
            .form-control {
                border-radius: 5px;
                border: 1px solid #ddd;
            }
            .form-control:focus {
                border-color: #007bff;
                box-shadow: none; /* Tắt shadow mặc định của bootstrap */
            }
            .col-md-12 {
                margin-bottom: 15px;
            }
            .ck-editor__editable {
                min-height: 300px; /* Chiều cao tối thiểu cho CKEditor */
            }

            .slider-container {
                display: flex;
                justify-content: center;
                align-items: center;
                background-color: #f8f9fa;
                padding: 20px;
                margin-bottom: 20px;
                border-radius: 10px;
            }

            .slider-title {
                font-size: 2rem;
                font-weight: bold;
                text-align: center;
            }
            .admin-back-button:hover {
                background-color: #00509E;
            }
            .admin-back-button i {
                margin-right: 8px;
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="main-container">
            <c:if test="${sessionScope.user.roleID == 2}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="avatar">
                    </div>
                    <ul>
                        <li><a href="train">Quản lý tàu</a></li>
                        <li><a href="trip">Quản lý chuyến</a></li>
                        <li><a href="route">Quản lý tuyến tàu</a></li>
                        <li><a href="station">Quản lý ga</a></li>
                        <li><a href="order">Quản lý hóa đơn</a></li>
                        <li><a href="category-blog">Quản lý tiêu đề Blog</a></li>
                        <li><a href="posts-list">Quản lý Blog</a></li>
                        <li><a href="category-rule">Quản lý tiêu đề quy định</a></li>
                        <li><a href="manager-rule-list">Quản lý quy định</a></li>
                        <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                    </ul>
                    <form action="logout" method="GET">
                        <button type="submit" class="logout-button">Logout</button>
                    </form>
                </div>
            </c:if>
            <c:if test="${sessionScope.user.roleID == 1}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="trainpicture">
                    </div>
                    <ul class="menu">
                        <li><a href="admin?view=dashboard">Dashboard</a></li>
                        <li><a href="admin?view=employees">Quản lý nhân viên</a></li>
                        <li><a href="admin?view=customers">Quản lý khách hàng</a></li>
                            <c:if test="${sessionScope.user.userId == 1}">
                            <li><a href="admin?view=userauthorization">Phân quyền</a></li>
                            </c:if>
                        <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>

                    </ul>
                    <form action="logout" method="GET">
                        <button type="submit" class="logout-button">Logout</button>
                    </form>
                </div>
                <a href="admin?view=dashboard" class="admin-back-button">
                    <i class="fas fa-arrow-left"></i> Quay lại trang Admin
                </a>
            </c:if>    
            <div class="slider-container">
                <h2 class="slider-title">Chỉnh sửa quy </h2>              
            </div>
            <div class="container rounded bg-white mt-5 mb-5">           
                <div class="header text-center p-3 bg-primary text-white">
                    <h4>Chỉnh sửa quy định</h4>
                </div>

                <!-- Hiển thị thông báo lỗi hoặc thành công -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" role="alert">${errorMessage}</div>
                </c:if>
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success" role="alert">${successMessage}</div>
                </c:if>

                <form action="edit-rule" method="post">
                    <input type="hidden" name="ruleID" value="${rule.ruleID}">

                    <div class="row mt-4">
                        <div class="col-md-8">
                            <div class="mb-3">
                                <label class="form-label">Title</label>
                                <input type="text" name="title" class="form-control" value="${rule.title}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Content</label>
                                <textarea id="editor" name="content">${rule.content}</textarea>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <input type="hidden" name="userID" value="${rule.userID}">

                            <div class="mb-3">
                                <label class="form-label">Category Rule</label>
                                <select class="form-control" name="categoryRuleID" required>
                                    <c:forEach items="${categories}" var="item">
                                        <option value="${item.categoryRuleID}" ${rule.categoryRuleID == item.categoryRuleID ? "selected" : ""}>${item.categoryRuleName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Update Date</label>
                                <input type="text" class="form-control" value="${rule.update_date}" readonly>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Status</label><br>
                                <input type="radio" name="status" value="1" ${rule.status ? 'checked' : ''}> Show
                                <input type="radio" name="status" value="0" ${!rule.status ? 'checked' : ''}> Hidden
                            </div>
                        </div>
                    </div>

                    <div class="mt-4 text-center">
                        <a href="manager-rule-list" class="btn btn-outline-dark">Cancel</a>
                        <input class="btn btn-outline-dark" type="submit" value="Update">
                    </div>
                </form>
            </div>

            <script>
                CKEDITOR.replace('editor');
            </script>
        </div>
    </body>
</html>
