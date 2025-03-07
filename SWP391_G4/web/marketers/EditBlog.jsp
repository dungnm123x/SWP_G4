<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Blog</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.ckeditor.com/4.16.2/full-all/ckeditor.js"></script>
        <style>
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
            /* Style for the image preview */
            #thumbnailPreview {
                max-width: 100%; /* Ensure the image doesn't exceed the container's width */
                border-radius: 5px; /* Rounded corners for the image */
            }

            /* Style for the delete button */
            #thumbnailPreview + button {
                font-size: 1.2em; /* Increase button font size */
                background-color:#D19C97 ; /* Semi-transparent white background */
                border-radius: 100%; /* Circular button */
                padding: 5px 8px;
                cursor: pointer;
                z-index: 10; /* Ensure the button appears above the image */
            }

            /* Hover effect for the delete button */
            #thumbnailPreview + button:hover {
                background-color: #D19C97; /* Full white background on hover */
            }

            /* Optional: Add padding to the surrounding div to create spacing */
            .position-relative {
                padding-top: 10px; /* Space above the image */
            }

        </style>
    </head>
    <body>
        <div class="slider-container">
            <h2 class="slider-title">Edit Blog</h2>              
        </div>
        <div class="container rounded bg-white mt-5 mb-5">           
            <div class="header text-center p-3 bg-primary text-white">
                <h4>Edit Blog</h4>
            </div>

            <!-- Hiển thị thông báo lỗi hoặc thành công -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">${errorMessage}</div>
            </c:if>
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success" role="alert">${successMessage}</div>
            </c:if>

            <form action="update-blog" method="post" enctype="multipart/form-data" onsubmit="return validateForm()">
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
                    <input class="btn btn-primary" type="submit" value="Update">
                </div>
            </form>
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
