<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Add New Category Rule</title>

        <!-- Bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

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
        </style>
    </head>
    <body>
        <div class="slider-container">
            <h2 class="slider-title">Thêm tiêu đề quy định mới</h2>              
        </div>
        <div class="container rounded mt-5 mb-5">
                <div class="card-header bg-primary text-white text-center">
                    <h4>Thêm tiêu đề quy định mới</h4>
                </div>
                <div class="card-body">

                    <!-- Hiển thị thông báo thành công/thất bại -->
                    <c:if test="${not empty message}">
                        <div class="alert ${message.contains('thành công') ? 'alert-success' : 'alert-danger'}" role="alert">
                            ${message}
                        </div>
                    </c:if>

                    <!-- Form gửi request đến Servlet -->
                    <form action="add-categoryRule" method="post">
                        <div class="row">
                            <div class="col-md-8">
                                <div class="mb-3">
                                    <label for="categoryRuleName" class="form-label">Category Rule Name</label>
                                    <input type="text" name="categoryRuleName" class="form-control" required>
                                </div>

                                <div class="mb-3">
                                    <label for="content" class="form-label">Content</label>
                                    <textarea name="content" id="editor" class="form-control"></textarea>
                                </div>
                            </div>
                            <div class="col-md-4">
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
                            </div>
                            <div class="mt-4 text-center">
                                <a href="category-rule"><button class="btn btn-outline-custom" type="button">Again</button></a>
                                <input class="btn btn-primary" type="submit" value="Add"/>
                            </div>
                        </div>
                    </form>

                </div>
            </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>

        <!-- CKEditor -->
        <script src="https://cdn.ckeditor.com/4.16.2/full-all/ckeditor.js"></script>
        <script>
            CKEDITOR.replace('editor');
        </script>

    </body>
</html>
