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
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

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
                        <select id="rating" name="rating" class="form-select" required>
                            <option value="1">1 sao</option>
                            <option value="2">2 sao</option>
                            <option value="3">3 sao</option>
                            <option value="4">4 sao</option>
                            <option value="5">5 sao</option>
                        </select>
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
</body>
</html>