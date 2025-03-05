<%-- 
    Document   : EditCategoryRule
    Created on : Mar 5, 2025, 9:12:39 AM
    Author     : dung9
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Edit Category Rule</title>

        <!-- Bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

        <style>
            body {
                background-color: #f8f9fa;
            }
            .container {
                max-width: 800px;
                margin-top: 30px;
            }
            .form-group label {
                font-weight: bold;
            }
            .btn-custom {
                background-color: #007bff;
                color: white;
                border: none;
            }
            .btn-custom:hover {
                background-color: #0056b3;
            }
        </style>
    </head>
    <body>

        <div class="container">
            <div class="card">
                <div class="card-header bg-primary text-white text-center">
                    <h4>Edit Category Rule</h4>
                </div>
                <div class="card-body">

                    <!-- Hiển thị thông báo thành công/thất bại -->
                    <c:if test="${not empty message}">
                        <div class="alert ${message.contains('thành công') ? 'alert-success' : 'alert-danger'}" role="alert">
                            ${message}
                        </div>
                    </c:if>

                    <!-- Form gửi request đến Servlet -->
                    <form action="edit-categoryRule" method="post">
                        <div class="row">
                            <div class="col-md-8">
                                <input type="hidden" name="categoryRuleID" value="${categoryRule.categoryRuleID}">
                                <div class="mb-3">
                                    <label for="categoryRuleName" class="form-label">Category Rule Name</label>
                                    <input type="text" name="categoryRuleName" class="form-control" required value="${categoryRule.categoryRuleName}">
                                </div>

                                <div class="mb-3">
                                    <label for="content" class="form-label">Content</label>
                                    <textarea name="content" id="editor" class="form-control">${categoryRule.content}</textarea>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="mb-3">
                            <label class="form-label">Update Date</label>
                            <input type="text" class="form-control" value="${categoryRule.update_date}" readonly>
                        </div>
                            <div class="mb-3">
                                <label for="status" class="form-label">Status</label><br/>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="status" value="1" ${categoryRule.status ? 'checked' : ''}/>
                                    <label class="form-check-label">Show</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="status" value="0" ${!categoryRule.status ? 'checked' : ''}/>
                                    <label class="form-check-label">Hide</label>
                                </div>
                            </div>
                            </div>
                            <div class="mt-4 text-center">
                                <a href="category-rule"><button class="btn btn-outline-custom" type="button">Cancel</button></a>
                                <input class="btn btn-primary" type="submit" value="Update"/>
                            </div>
                        </div>
                    </form>

                </div>
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