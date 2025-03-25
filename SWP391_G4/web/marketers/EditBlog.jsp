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
                margin-left: 120px; /* Dịch sang phải 250px để tránh bị che */
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
            #deleteThumbnail {
                font-size: 1.5em; /* Increase the size of the icon */
                background-color: rgba(255, 255, 255, 0.5); /* Add a semi-transparent background */
                border-radius: 50%; /* Make the button round */
                padding: 5px 10px;
                cursor: pointer;
            }

            #deleteThumbnail:hover {
                background-color: rgba(255, 255, 255, 0.8); /* Change background color on hover */
            }

            #deleteThumbnail i {
                color: #ff0000; /* Set the color of the trash icon to red */
            }
            /* Optional: Add padding to the surrounding div to create spacing */
            .position-relative {
                padding-top: 10px; /* Space above the image */
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
                        <li><a href="refund">Quản lý đơn hoàn tiền</a></li>
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
                <h2 class="slider-title">Edit Blog</h2>              
            </div>
            <div class="container rounded mt-5 mb-5">       
                <div class="header text-center p-3 bg-primary text-white">
                    <h4>Edit Blog</h4>
                </div>

                <!-- Hiển thị thông báo lỗi hoặc thành công -->
                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger" role="alert">
                        ${sessionScope.error}
                    </div>
                    <% session.removeAttribute("error"); %>
                </c:if>

                <c:if test="${not empty sessionScope.success}">
                    <div class="alert alert-success" role="alert">
                        ${sessionScope.success}
                    </div>
                    <% session.removeAttribute("success"); %>
                </c:if>
                <form action="edit-blog" method="post" enctype="multipart/form-data" onsubmit="return validateForm()">
                    <input type="hidden" name="blog_id" value="${blog.blog_id}">

                    <div class="row mt-4">
                        <div class="col-md-8">
                            <div class="mb-3">
                                <label class="form-label">Title</label>
                                <input type="text" name="title" class="form-control" value="${blog.title}" required>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Brief Information</label>
                                <textarea class="form-control" name="brief_infor" rows="4" required>${blog.brief_infor}</textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Content</label>
                                <textarea id="editor" name="content">${blog.content}</textarea>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="mb-3">
                                <label class="form-label">Author</label>
                                <c:set var="authorName" value="Không xác định"/>
                                <c:forEach items="${User}" var="a">
                                    <c:if test="${blog.author_id == a.userID}">
                                        <c:set var="authorName" value="${a.fullName} "/>
                                    </c:if>
                                </c:forEach>
                                <input type="text" class="form-control" name="authorName" value="${authorName}" readonly="readonly" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Update Date</label>
                                <input type="text" class="form-control" value="${blog.updated_date}" readonly>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Category</label>
                                <select class="form-control" name="categoryBlog_id" required>
                                    <c:forEach items="${categories}" var="item">
                                        <option value="${item.categoryBlogId}" ${blog.categoryBlog_id == item.categoryBlogId ? "selected" : ""}>${item.categoryBlogName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Status</label><br>
                                <input type="radio" name="status" value="1" ${blog.status ? 'checked' : ''}> Show
                                <input type="radio" name="status" value="0" ${!blog.status ? 'checked' : ''}> Hidden
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Thumbnail</label>
                                <input type="file" name="thumbnail" class="form-control" id="thumbnailInput" onchange="updateImagePreview()">
                                <div class="position-relative mt-2">
                                    <!-- Image Preview -->
                                    <img id="thumbnailPreview" src="${blog.thumbnail}" alt="Thumbnail" class="img-thumbnail" style="max-width: 100%; display: ${blog.thumbnail != '' ? 'block' : 'none'};">
                                    <!-- Delete Button -->
                                    <button type="button" class="btn btn-danger btn-sm position-absolute top-0 end-0" style="display: ${blog.thumbnail != '' ? 'inline-block' : 'none'};" onclick="removeImage()">X</button>
                                </div>
                            </div>

                        </div>
                    </div>

                    <div class="mt-4 text-center">
                        <a href="posts-list" class="btn btn-outline-dark">Cancel</a>
                        <input class="btn btn-outline-dark" type="submit" value="Update">
                    </div>
                </form>
            </div>
        </div>
        <script>
            CKEDITOR.replace('editor');

            function validateForm() {
                let title = document.querySelector('input[name="title"]').value;
                let briefInfor = document.querySelector('textarea[name="brief_infor"]').value;
                let category = document.querySelector('select[name="categoryBlog_id"]').value;

                if (!title || !briefInfor || !category) {
                    alert("Please fill out all required fields.");
                    return false;
                }
                return true;
            }

            function updateImagePreview() {
                var input = document.getElementById('thumbnailInput');
                var preview = document.getElementById('thumbnailPreview');
                var deleteButton = document.querySelector('#thumbnailPreview + button');

                if (input.files && input.files[0]) {
                    var reader = new FileReader();
                    reader.onload = function (e) {
                        preview.src = e.target.result;
                        preview.style.display = 'block'; // Show image preview
                        deleteButton.style.display = 'inline-block'; // Show delete button
                    };
                    reader.readAsDataURL(input.files[0]);
                }
            }

            function removeImage() {
                var preview = document.getElementById('thumbnailPreview');
                var deleteButton = document.querySelector('#thumbnailPreview + button');
                var input = document.getElementById('thumbnailInput');

                // Clear the image preview and hide it
                preview.src = "";
                preview.style.display = 'none';

                // Hide the delete button
                deleteButton.style.display = 'none';

                // Clear the file input
                input.value = "";
            }
        </script>
    </body>
</html>
