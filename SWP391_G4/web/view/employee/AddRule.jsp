    <%-- 
    Document   : AddRule
    Created on : Mar 5, 2025, 1:14:59 PM
    Author     : dung9
--%>

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
        </style>

        <!-- CKEditor 4 (full-all) CDN -->
        <script src="https://cdn.ckeditor.com/4.16.2/full-all/ckeditor.js"></script>
        <!-- CKFinder 3 CDN -->
        <script src="https://cdn.ckeditor.com/ckfinder/3.5.2/ckfinder.js"></script>
    </head>
    <body>
        <div class="slider-container">
            <h2 class="slider-title">Thêm quy định mới</h2>              
        </div>
        <div class="container rounded mt-5 mb-5">
            <div class="header">
                <h4>Thêm quy định mới</h4>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    ${error}
                </div>
            </c:if>
            <c:if test="${not empty message}">
                <div class="alert alert-info" role="alert">
                    ${message}
                </div>
            </c:if>
            <form action="add-rule" method="POST" enctype="multipart/form-data" class="inner-form">
                <div class="row">
                    <div class="col-md-8">
                        <div class="mb-3">
                            <label for="ruleName" class="form-label">Title</label>
                            <input type="text" name="ruleName" class="form-control" placeholder="Enter title" required/>
                        </div>
                        <div class="mb-3">
                            <label for="content" class="form-label">Content</label>
                            <textarea cols="20" rows="10" id="editor" name="content"></textarea>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="mb-3">
                            <label for="categoryRuleID" class="form-label">Category Rule</label>
                            <select class="form-select" name="categoryRuleID">
                                <c:forEach var="category" items="${categories}">
                                    <option value="${category.categoryRuleID}">${category.categoryRuleName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label for="status" class="form-label">Status</label><br/>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="status" value="1" checked/>
                                <label class="form-check-label">Show</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="radio" name="status" value="0"/>
                                <label class="form-check-label">Hidden</label>
                            </div>
                        </div>
                    </div>
                </div>

                <input type="hidden" name="userID" value="1"/> <%-- Cần lấy userID từ session nếu có --%>

                <div class="mt-4 text-center">
                    <a href="manager-rule-list"><button class="btn btn-outline-custom" type="button">Again</button></a>
                    <input class="btn btn-primary" type="submit" value="Add"/>
                </div>
            </form>
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

