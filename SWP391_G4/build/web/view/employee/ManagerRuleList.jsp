<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Manager Rule</title>

        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <link rel="stylesheet" href="css/employee.css">
        <style>
            /* Căn giữa tiêu đề */
            .manager-title {
                text-align: center;
                margin-bottom: 1rem;
            }

            /* Nhóm nút Add/Refresh phía dưới tiêu đề */
            .action-buttons {
                display: flex;
                justify-content: center; /* Căn giữa cả nhóm nút */
                gap: 10px;
                margin-bottom: 2rem; /* khoảng cách dưới nhóm nút */
            }

            /* Hàng ngang chứa các form filter */
            .filter-row {
                display: flex;
                flex-wrap: wrap; /* Cho phép xuống dòng khi màn hình nhỏ */
                gap: 1rem;
                margin-bottom: 1.5rem;
            }

            /* Nếu muốn độ rộng cụ thể cho mỗi filter, 
               có thể dùng class col hoặc col-xx thay vì width cố định */
            .filter-group {
                flex: 1; /* Mỗi nhóm form sẽ dàn đều */
                min-width: 120px; /* Đảm bảo không quá nhỏ ở màn hẹp */
            }
            body {
                margin: 20px;
            }
            .search-form {
                margin-bottom: 20px;
            }
        </style>
    </head>

    <body class="bg-light">
        <div class="container">
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
            <h2 class="manager-title">Quản lý quy định </h2>
            <div class="action-buttons">
                <!-- Nút "Add Blog" -->
                <a class="btn btn-primary" href="add-rule">
                    <i class="fas fa-plus-circle"></i> Thêm quy định
                </a>
            </div>
            <%-- Hiển thị thông báo --%>
            <c:if test="${not empty sessionScope.message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% session.removeAttribute("message"); %>
            </c:if>

            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${sessionScope.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
                <% session.removeAttribute("error"); %>
            </c:if>
            <!-- Bộ lọc giữ nguyên form cũ -->
            <div class="filter-row">
                <div class="filter-group">
                    <form action="manager-rule-list" method="GET">
                        <label class="form-label">Per Page</label>
                        <select name="pageSize" class="form-select" onchange="this.form.submit();">
                            <option value="5" ${param.pageSize == '5' ? 'selected' : ''}>5</option>
                            <option value="10" ${param.pageSize == '10' ? 'selected' : ''}>10</option>
                            <option value="15" ${param.pageSize == '15' ? 'selected' : ''}>15</option>
                            <option value="20" ${param.pageSize == '20' ? 'selected' : ''}>20</option>
                        </select>
                    </form>
                </div>

                <div class="filter-group">
                    <form action="manager-rule-list" method="GET">
                        <label class="form-label">Category Name</label>
                        <select class="form-select" name="categoryRuleID" onchange="this.form.submit();">
                            <option value="">Find All</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryRuleID}" ${category.categoryRuleID == param.categoryRuleID ? 'selected' : ''}>
                                    ${category.categoryRuleName}
                                </option>
                            </c:forEach>
                        </select>
                    </form>
                </div>

                <div class="filter-group">
                    <form action="manager-rule-list" method="GET">
                        <label class="form-label">Sort By</label>
                        <select class="form-select" name="sortBy" onchange="this.form.submit();">
                            <option value="">Default</option>
                            <option value="name_asc" ${param.sortBy == 'name_asc' ? 'selected' : ''}>Name (asc)</option>
                            <option value="name_desc" ${param.sortBy == 'name_desc' ? 'selected' : ''}>Name (desc)</option>
                            <option value="id_asc" ${param.sortBy == 'id_asc' ? 'selected' : ''}>ID (asc)</option>
                            <option value="id_desc" ${param.sortBy == 'id_desc' ? 'selected' : ''}>ID (desc)</option>
                        </select>
                    </form>
                </div>

                <div class="filter-group">
                    <form action="manager-rule-list" method="GET">
                        <label class="form-label">Status</label>
                        <select class="form-select" name="status" onchange="this.form.submit();">
                            <option value="all" ${empty param.status || param.status == 'all' ? 'selected' : ''}>All</option>
                            <option value="true" ${param.status == 'true' ? 'selected' : ''}>Active</option>
                            <option value="false" ${param.status == 'false' ? 'selected' : ''}>Inactive</option>
                        </select>
                    </form>
                </div>

                <div class="filter-group">
                    <form action="manager-rule-list" method="GET">
                        <label class="form-label">Search</label>
                        <div class="input-group">
                            <input type="text" name="key" placeholder="Tìm kiếm quy định" class="form-control" value="${param.key}">
                            <button type="submit" class="btn btn-outline-danger">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Bảng danh sách quy định -->
            <div class="card shadow ">
                <div class="card-header text-white" style="background-color: #2C3E50;">
                    <h5 class="mb-0">Blog List</h5>
                </div>
                <table class="table table-bordered">
                    <thead class="bg-light">
                        <tr>
                            <th>STT</th>
                            <th>Title</th>
                            <th>Category Rule</th>
                            <th>Update Date</th>
                            <th>Status</th>
                            <th>Toggle</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="r" items="${rules}" varStatus="status">
                            <tr>
                                <td>${status.index + 1}</td>
                                <td>${r.title}</td>
                                <td>
                                    <c:forEach items="${categories}" var="c">
                                        <c:if test="${r.categoryRuleID == c.categoryRuleID}">
                                            ${c.categoryRuleName}
                                        </c:if>
                                    </c:forEach>
                                </td>
                                <td>${r.update_date}</td>
                                <td>
                                    <span class="badge ${r.status ? 'bg-success' : 'bg-danger'}">
                                        ${r.status ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                                <td>
                                    <form action="toggle-status-rule" method="POST">
                                        <input type="hidden" name="ruleID" value="${r.ruleID}">
                                        <input type="hidden" name="status" value="${r.status ? 'false' : 'true'}">
                                        <div class="form-check form-switch">
                                            <input class="form-check-input" type="checkbox" 
                                                   id="statusSwitch${r.ruleID}" 
                                                   name="status" ${r.status ? 'checked' : ''} 
                                                   onchange="this.form.submit();">
                                            <label class="form-check-label" for="statusSwitch${r.ruleID}"></label>
                                        </div>
                                    </form>

                                </td>
                                <td>
                                    <!-- Nút Sửa -->
                                    <a href="edit-rule?ruleID=${r.ruleID}" class="btn btn-warning btn-sm">
                                        <i class="fas fa-edit"></i>
                                    </a>

                                    <!-- Nút Xóa -->
                                    <a href="delete-rule?ruleID=${r.ruleID}" class="btn btn-danger btn-sm"
                                       onclick="return confirm('Bạn có chắc muốn xóa quy định này không?');">
                                        <i class="fas fa-trash"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>