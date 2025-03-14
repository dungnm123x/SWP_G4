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
            /* Optional: Add padding to the surrounding div to create spacing */
            .position-relative {
                padding-top: 10px; /* Space above the image */
            }

        </style>
    </head>
    <body>
        <div class="slider-container">
            <h2 class="slider-title">Chỉnh sửa quy định</h2>              
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
                    <input class="btn btn-primary" type="submit" value="Update">
                </div>
            </form>
        </div>

        <script>
            CKEDITOR.replace('editor');
        </script>
    </body>
</html>
