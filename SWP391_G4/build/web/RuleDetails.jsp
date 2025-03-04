<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi Tiết Quy Định</title>

        <!-- Bootstrap 5.3.0 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">

        <style>
            body {
                background-color: #f4f7fa;
                font-family: 'Arial', sans-serif;
            }

            /* Container chính để căn giữa nội dung */
            .main-container {
                max-width: 900px; /* Độ rộng tối đa */
                margin: 30px auto; /* Căn giữa */
                background-color: #fff;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            }

            .single_post h3 {
                color: #333;
                font-weight: 600;
            }

            .single_post p {
                font-size: 1rem;
                line-height: 1.8;
                color: #555;
            }

            .img-post img {
                border-radius: 8px;
                max-width: 100%;
                height: auto;
                margin-bottom: 20px;
            }

            .related-rule .card {
                margin-bottom: 15px;
                border: 0;
            }

            .related-rule h4 a {
                text-decoration: none;
                color: #007bff;
            }

            .related-rule h4 a:hover {
                text-decoration: underline;
            }

            /* Nút quay lại */
            .back-button {
                margin-top: 10px;
            }

            .related-rule .card {
                max-width: 100%; /* Đảm bảo card không quá rộng */
                margin: 10px auto; /* Căn giữa card */
                padding: 15px;
                box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            }

            .related-rule .img-post img {
                width: 100%; /* Ảnh rộng theo card */
                height: 150px; /* Giới hạn chiều cao ảnh */
                object-fit: cover; /* Cắt ảnh cho đẹp */
                border-radius: 5px;
            }

            .related-rule h4 {
                font-size: 1.1rem;
                font-weight: bold;
                margin-top: 10px;
            }

            .related-rule p {
                font-size: 0.9rem;
                color: #666;
            }

        </style>
    </head>
    <body>

        <!-- Thêm thanh navbar -->
        <jsp:include page="/navbar.jsp"/>

        <div class="container">
            <div class="main-container">

                <!-- Nút quay lại -->
                <button class="btn btn-secondary back-button" onclick="history.back()">⬅ Quay lại</button>

                <div class="card single_post">
                    <div class="body">
                        <h3>${rule.title}</h3>
                        <p>${rule.content}</p>
                    </div>
                  
                </div>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

    </body>
</html>
