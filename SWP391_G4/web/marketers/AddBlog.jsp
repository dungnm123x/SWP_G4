<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
        <meta name="description" content="" />
        <title>Add New Blog</title>

        <!-- Bootstrap 5.3.0 CSS -->
        <link 
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" 
            rel="stylesheet" 
            />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

        <!-- Font Awesome (nếu cần) -->
        <script 
            src="https://use.fontawesome.com/releases/v6.1.0/js/all.js" 
            crossorigin="anonymous">
        </script>
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
            .admin-back-button:hover {
                background-color: #00509E;
            }
            .admin-back-button i {
                margin-right: 8px;
            }
        </style>

        <!-- CKEditor 4 (full-all) CDN -->
        <script src="https://cdn.ckeditor.com/4.16.2/full-all/ckeditor.js"></script>
        <!-- CKFinder 3 CDN -->
        <script src="https://cdn.ckeditor.com/ckfinder/3.5.2/ckfinder.js"></script>
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
                <h2 class="slider-title">Add New Blog</h2>              
            </div>
            <div class="container rounded mt-5 mb-5">
                <div class="header">
                    <h4>Tạo Blog Mới</h4>
                </div>

                <!-- Hiển thị thông báo lỗi nếu có -->
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
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if> 
                <form action="add-post" method="post" enctype="multipart/form-data" class="inner-form">
                    <div class="row">
                        <div class="col-md-8">
                            <div class="mb-3">
                                <label for="title" class="form-label">Title</label>
                                <input type="text" name="title" class="form-control" placeholder="Enter title" required/>
                            </div>
                            <div class="mb-3">
                                <label for="brief_infor" class="form-label">Brief Information</label>
                                <textarea class="form-control" name="brief_infor" rows="4" placeholder="Descrip short about blog..."></textarea>
                            </div>
                            <div class="mb-3">
                                <label for="content" class="form-label">Content</label>
                                <textarea cols="20" rows="10" id="editor" name="content"></textarea>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="mb-3 position-relative">
                                <label for="category" class="form-label" style="cursor: pointer; color: #007bff; font-weight: bold;">Category Blog</label>
                                <select class="form-select" name="categoryId" id="categorySelect">
                                    <c:forEach var="category" items="${categories}">
                                        <option value="${category.categoryBlogId}">${category.categoryBlogName}</option>
                                    </c:forEach>
                                </select>
                            </div>


                            <script>
                function toggleCategorySelect() {
                    var selectBox = document.getElementById("categorySelect");
                    if (selectBox.classList.contains("d-none")) {
                        selectBox.classList.remove("d-none");
                    }
                }
                            </script>
                            <div class="mb-3">
                                <label for="status" class="form-label">Status</label><br/>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="status" value="1" checked/>
                                    <label class="form-check-label">Show</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="status" value="0"/>
                                    <label class="form-check-label">Hiden</label>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="thumbnail" class="form-label">Title Thumbnail</label>
                                <input type="file" name="thumbnail" class="form-control" id="thumbnailInput" accept="image/*" onchange="previewImage(event)"/>
                                <div class="mt-3" style="position: relative;">
                                    <img id="thumbnailPreview" src="" alt="Ảnh xem trước" style="max-width: 100%; display: none;"/>
                                    <button id="deleteThumbnail" type="button" style="display: none; position: absolute; top: 10px; right: 10px; background: rgba(255, 255, 255, 0.5); border: none; padding: 5px 10px; border-radius: 5px; cursor: pointer;" onclick="deleteImage()">
                                        <i class="bi bi-trash" style="color: #ff0000;"></i>
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="mt-4 text-center">
                        <a href="posts-list"><button class="btn btn-outline-custom" type="button">Again</button></a>
                        <input class="btn btn-outline-custom" type="submit" value="Add"/>
                    </div>
                </form>
            </div>


            <script>
                function previewImage(event) {
                    var input = event.target;
                    var preview = document.getElementById('thumbnailPreview');
                    var deleteButton = document.getElementById('deleteThumbnail');

                    if (input.files && input.files[0]) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            preview.src = e.target.result;
                            preview.style.display = 'block';
                            deleteButton.style.display = 'inline-block'; // Show delete button when an image is selected
                        };
                        reader.readAsDataURL(input.files[0]);
                    } else {
                        preview.src = "";
                        preview.style.display = 'none';
                        deleteButton.style.display = 'none'; // Hide delete button if no image is selected
                    }
                }

                function deleteImage() {
                    var preview = document.getElementById('thumbnailPreview');
                    var deleteButton = document.getElementById('deleteThumbnail');
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

        </div>

        <!-- Bootstrap 5.3.0 JS -->
        <script 
            src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" 
            crossorigin="anonymous">
        </script>

        <!-- Cấu hình CKEditor + CKFinder với cấu hình nâng cao -->
        <script>
            CKEDITOR.replace('editor', {
                // Kích hoạt các plugin mở rộng: hỗ trợ màu sắc, font, căn lề, upload ảnh,...
                extraPlugins: 'colorbutton,colordialog,font,justify,uploadimage,image2',
                // Cấu hình toolbar nâng cao với nhiều icon và tùy chọn hơn
                toolbar: [
                    {name: 'document', items: ['Source', '-', 'Save', 'NewPage', 'Preview', 'Print', '-', 'Templates']},
                    {name: 'clipboard', items: ['Cut', 'Copy', 'Paste', 'PasteText', 'PasteFromWord', '-', 'Undo', 'Redo']},
                    {name: 'editing', items: ['Find', 'Replace', '-', 'SelectAll', '-', 'Scayt']},
                    '/',
                    {name: 'styles', items: ['Styles', 'Format', 'Font', 'FontSize']},
                    {name: 'colors', items: ['TextColor', 'BGColor']},
                    {name: 'basicstyles', items: ['Bold', 'Italic', 'Underline', 'Strike', '-', 'RemoveFormat', 'CopyFormatting']},
                    {name: 'paragraph', items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-',
                            'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']},
                    {name: 'insert', items: ['Image', 'Table', 'HorizontalRule', 'Smiley', 'SpecialChar', 'PageBreak', 'Iframe']},
                    {name: 'links', items: ['Link', 'Unlink', 'Anchor']},
                    {name: 'tools', items: ['Maximize', 'ShowBlocks', 'About']}
                ],
                // Cấu hình tích hợp CKFinder
                filebrowserBrowseUrl: '/ckfinder/ckfinder.html',
                filebrowserUploadUrl: '/ckfinder/core/connector/php/connector.php?command=QuickUpload&type=Files',
                filebrowserImageUploadUrl: '/ckfinder/core/connector/php/connector.php?command=QuickUpload&type=Images',
                uploadUrl: '/ckfinder/core/connector/php/connector.php?command=QuickUpload&type=Images',
                // Cấu hình thêm tùy chọn cho bảng màu nâng cao
                colorButton_colors: '000,800000,8B4513,2F4F4F,008080,000080,4B0082,696969,'
                        + 'B22222,A52A2A,DAA520,006400,40E0D0,0000CD,800080,FF0000,'
                        + 'FF8C00,FFD700,008000,00CED1,4682B4,EE82EE,A9A9A9,FFA07A,'
                        + 'FFA500,FFFF00,00FF00,00FFFF,1E90FF,FF00FF,DCDCDC,FFDAB9,'
                        + 'FFFACD,90EE90,AFEEEE,B0E0E6,ADD8E6,FFB6C1,FFA07A,FF69B4,'
                        + 'FF1493,DB7093',
                colorButton_enableAutomatic: true,
                colorButton_enableMore: true
            });
        </script>
    </body>
</html>








