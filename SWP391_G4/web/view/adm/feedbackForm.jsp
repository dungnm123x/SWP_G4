<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8" />
        <meta http-equiv="X-UA-Compatible" content="IE=edge" />
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
        <meta name="description" content="Feedback Form" />
        <title>Gửi phản hồi</title>

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

        <script src="https://use.fontawesome.com/releases/v6.1.0/js/all.js" crossorigin="anonymous"></script>

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
                padding: 0;
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
                box-shadow: none;
            }
            .col-md-12 {
                margin-bottom: 15px;
            }
            .ck-editor__editable {
                min-height: 300px;
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

            .star {
                font-size: 1.5rem;
                cursor: pointer;
                color: #ccc;
            }
            .star.filled {
                color: gold;
            }
        </style>
    </head>
    <body>
        <div class="slider-container">
            <h2 class="slider-title">Gửi phản hồi</h2>
        </div>
        <div class="container rounded mt-5 mb-5">
            <div class="header">
                <h4>Gửi phản hồi</h4>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    ${error}
                </div>
            </c:if>

            <form action="feedback" method="POST" class="inner-form">
                <div class="row">
                    <div class="col-md-12">
                        <div class="mb-3">
                            <label for="content" class="form-label">Nội dung phản hồi:</label>
                            <textarea id="content" name="content" rows="4" class="form-control" required></textarea>
                        </div>
                        <div class="mb-3">
                            <label for="rating" class="form-label">Đánh giá:</label>
                            <div id="rating-stars">
                                <i class="bi bi-star star" data-value="1"></i>
                                <i class="bi bi-star star" data-value="2"></i>
                                <i class="bi bi-star star" data-value="3"></i>
                                <i class="bi bi-star star" data-value="4"></i>
                                <i class="bi bi-star star" data-value="5"></i>
                            </div>
                            <input type="hidden" id="rating" name="rating" value="0">
                            <span id="rating-text"></span>  </div>
                    </div>
                </div>
        </div>

        <div class="mt-4 text-center">
            <a href="home"><button class="btn btn-outline-custom" type="button">Trang chủ</button></a>
            <input class="btn btn-primary" type="submit" value="Gửi"/>
        </div>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const stars = document.querySelectorAll('.star');
        const ratingInput = document.getElementById('rating');
        const ratingText = document.getElementById('rating-text');

        const ratingLabels = {
            1: "Tệ",
            2: "Không hài lòng",
            3: "Bình thường",
            4: "Hài lòng",
            5: "Tuyệt vời"
        };

        stars.forEach(star => {
            star.addEventListener('click', function () {
                const value = parseInt(this.dataset.value);
                ratingInput.value = value;
                ratingText.textContent = ratingLabels[value];

                stars.forEach((s, index) => {
                    if (index < value) {
                        s.classList.add('bi-star-fill', 'filled');
                        s.classList.remove('bi-star');
                        // Áp dụng gradient cho màu vàng
                        s.style.color = `rgb(255, ${255 - (value - index - 1) * 50}, 0)`; // Tạo gradient màu vàng nhạt dần
                    } else {
                        s.classList.remove('bi-star-fill', 'filled');
                        s.classList.add('bi-star');
                        s.style.color = '#ccc'; // Màu xám
                    }
                });
            });
        });
    });
</script>
</body>
</html>