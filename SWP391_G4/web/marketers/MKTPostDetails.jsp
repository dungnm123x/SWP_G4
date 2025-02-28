<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Post Details</title>
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            /* CSS tùy chỉnh */
            body {
                background-color: #f8f9fa;
            }
            .card {
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            }
            .table th {
                width: 25%;
                background-color: #e9ecef;
            }
            .table td, .table th {
                vertical-align: middle;
            }
            .img-thumbnail {
                max-width: 200px;
            }
            /* CSS cho chức năng read more */
            .content-preview {
                overflow: hidden;
                transition: max-height 0.5s ease;
            }
            input[type="text"] {
                width: 200px; /* Hoặc điều chỉnh theo nhu cầu */
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
    </head>
    <body>
        <div class="container mt-5">
            <div class="slider-container">
                <h2 class="slider-title">View Details Blog</h2>              
            </div>
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">Details Blog</h4>
                </div>
                <div class="card-body">
                    <table class="table table-bordered">
                        <tr>
                            <th>Title:</th>
                            <td>${blog.title}</td>
                        </tr>
                        <!-- Hiển thị Danh mục -->
                        <tr>
                            <th>Category Blog:</th>
                            <td>
                                <c:set var="categoryName" value="Không xác định"/>
                                <c:forEach items="${categories}" var="item">
                                    <c:if test="${blog.categoryBlog_id == item.categoryBlogId}">
                                        <c:set var="categoryName" value="${item.categoryBlogName}"/>
                                    </c:if>
                                </c:forEach>
                                ${categoryName}
                            </td>
                        </tr>
                        <!-- Hiển thị Trạng thái -->
                        <tr>
                            <th>Status:</th>
                            <td>
                                <input type="text" class="form-control-plaintext" name="status" value="${blog.status ? 'Không hoạt động' : 'Hoạt động'}" readonly>
                            </td>
                        </tr>
                        <!-- Hiển thị Hình ảnh -->
                        <tr>
                            <th>Picture:</th>
                            <td>
                                <img src="${blog.thumbnail}" alt="Thumbnail" class="img-thumbnail">
                            </td>
                        </tr>
                        <!-- Hiển thị Nội dung có chức năng read more -->
                        <tr>
                            <th>Content:</th>
                            <td>
                                <!-- Nội dung được bao bọc trong 1 div để áp dụng hiệu ứng read more -->
                                <div id="contentContainer" class="content-preview">
                                    ${blog.content}
                                </div>
                                <!-- Nút read more/read less -->
                                <button id="toggleBtn" class="btn btn-link p-0 mt-2">Read More</button>
                            </td>
                        </tr>
                    </table>
                    <div class="text-end">
                        <a href="posts-list" class="btn btn-secondary">Again</a>
                    </div>
                </div>
            </div>
        </div>
        <!-- Bootstrap JS Bundle -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
        <!-- JavaScript cho chức năng read more/read less -->
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const contentContainer = document.getElementById("contentContainer");
                const toggleBtn = document.getElementById("toggleBtn");

                // Chiều cao ban đầu của khối nội dung (có thể điều chỉnh theo ý muốn)
                const collapsedHeight = 150; // pixels

                // Lưu lại chiều cao ban đầu của nội dung sau khi render
                const fullHeight = contentContainer.scrollHeight;

                // Nếu nội dung quá ngắn, ẩn nút toggle
                if (fullHeight <= collapsedHeight) {
                    toggleBtn.style.display = "none";
                } else {
                    // Ban đầu hiển thị nội dung thu gọn
                    contentContainer.style.maxHeight = collapsedHeight + "px";
                }

                toggleBtn.addEventListener("click", function () {
                    // Nếu đang ở trạng thái thu gọn thì mở rộng, ngược lại thu gọn lại
                    if (toggleBtn.textContent === "Read More") {
                        contentContainer.style.maxHeight = fullHeight + "px";
                        toggleBtn.textContent = "Short";
                    } else {
                        contentContainer.style.maxHeight = collapsedHeight + "px";
                        toggleBtn.textContent = "Read More";
                    }
                });
            });
        </script>
    </body>
</html>
