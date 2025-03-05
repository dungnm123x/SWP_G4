<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Manager Blog</title>

        <!-- Bootstrap CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <!-- SweetAlert2 -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <link rel="stylesheet" href="css/employee.css">
        <!-- CSS tuỳ chỉnh -->
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
            <div class="sidebar">
                <div class="logo">
                    <img src="./img/logo.jpg" alt="avatar">
                </div>
                <ul>
                    <li><a href="train">Quản lý tàu</a></li>
                    <li><a href="trip">Quản lý chuyến</a></li>
                    <li><a href="route">Quản lý tuyến tàu</a></li>
                    <li><a href="station">Quản lý ga</a></li>
                    <li><a href="category-blog">Quản lý tiêu đề Blog</a></li>
                    <li><a href="posts-list">Quản lý Blog</a></li>
                    <li><a href="category-rule">Quản lý tiêu đề quy định</a></li>
                    <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                </ul>
                <form action="logout" method="GET">
                    <button type="submit" class="logout-button">Logout</button>
                </form>

            </div>
            <!-- Tiêu đề chính -->
            <h2 class="manager-title">Manager Blog</h2>

            <!-- Nhóm nút Add / Refresh -->
            <div class="action-buttons">
                <!-- Nút "Add Blog" -->
                <a class="btn btn-primary" href="add-post">
                    <i class="fas fa-plus-circle"></i> Add Blog
                </a>
            </div>

            <script>
                function refreshPage() {
                    window.location.href = 'posts-list';
                }
            </script>

            <!-- Hàng ngang chứa các bộ lọc -->
            <div class="filter-row">
                <!-- Per Page -->
                <div class="filter-group">
                    <form action="posts-list" method="GET">
                        <label for="page-size-select" class="form-label">Per Page</label>
                        <select name="pageSize" id="page-size-select" class="form-select" onchange="this.form.submit();">
                            <option value="5" ${param.pageSize == '5' ? 'selected' : ''}>5</option>
                            <option value="10" ${param.pageSize == '10' ? 'selected' : ''}>10</option>
                            <option value="15" ${param.pageSize == '15' ? 'selected' : ''}>15</option>
                            <option value="20" ${param.pageSize == '20' ? 'selected' : ''}>20</option>
                            <option value="25" ${param.pageSize == '25' ? 'selected' : ''}>25</option>
                        </select>
                        <input type="hidden" name="page" value="${param.currentPage}">
                    </form>
                </div>

                <!-- Chọn Danh Mục -->
                <div class="filter-group">
                    <form action="posts-list" method="GET">
                        <label class="form-label">Category Name</label>
                        <select class="form-select" name="categoryId" onchange="this.form.submit();">
                            <option value="">Find All</option>
                            <c:forEach var="category" items="${categories}">
                                <option value="${category.categoryBlogId}" ${category.categoryBlogId == param.categoryId ? 'selected' : ''}>
                                    ${category.categoryBlogName}
                                </option>
                            </c:forEach>
                        </select>
                    </form>
                </div>

                <!-- Sắp xếp -->
                <div class="filter-group">
                    <form action="posts-list" method="GET">
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

                <!-- Trạng thái -->
                <div class="filter-group">
                    <form action="posts-list" method="GET">
                        <label class="form-label">Status</label>
                        <select class="form-select" name="status" onchange="this.form.submit();">
                            <option value="all" ${empty param.status || param.status == 'all' ? 'selected' : ''}>All</option>
                            <option value="true" ${param.status == 'true' ? 'selected' : ''}>Active</option>
                            <option value="false" ${param.status == 'false' ? 'selected' : ''}>Inactive</option>
                        </select>
                    </form>
                </div>

                <!-- Tìm kiếm -->
                <div class="filter-group">
                    <form action="posts-list" method="GET">
                        <label class="form-label">Search</label>
                        <div class="input-group">
                            <input type="text" name="key" placeholder="Tìm kiếm blog" class="form-control" value="${param.key}">
                            <button type="submit" class="btn btn-outline-danger">
                                <i class="fas fa-search"></i>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            <!-- Kết thúc phần filter-row -->
            <c:if test="${empty blogs}">
                <script>
                    Swal.fire({
                        title: 'No Data Found',
                        text: 'Please check again or try another search.',
                        icon: 'warning',
                        confirmButtonText: 'OK'
                    });
                </script>
            </c:if>


            <!-- Hiển thị thông báo nếu xóa thành công -->
            <c:if test="${not empty success}">
                <script>
                    Swal.fire({
                        title: 'Xóa thành công!',
                        text: '${success}',
                        icon: 'success',
                        confirmButtonText: 'OK'
                    });
                </script>
            </c:if>

            <!-- Hiển thị thông báo nếu không có dữ liệu -->
            <c:if test="${empty blogs}">
                <script>
                    Swal.fire({
                        title: 'No Data',
                        text: 'Please search again to find the data you want :))',
                        icon: 'warning',
                        confirmButtonText: 'OK'
                    });
                </script>
            </c:if>

            <!-- Bảng danh sách Blog -->
            <div class="card shadow ">
                <div class="card-header text-white" style="background-color: #2C3E50;">
                    <h5 class="mb-0">Blog List</h5>
                </div>

                <div class="card-body">
                    <table class="table table-bordered">
                        <thead class="bg-light">
                            <tr>
                                <th>STT</th>
                                <th>Thumbnail</th>
                                <th>Title</th>
                                <th>Category Blog</th>
                                <th>Update Date</th>
                                <th>Status</th>
                                <th>Toggle</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="b" items="${blogs}" varStatus="status">
                                <tr>
                                    <td>${status.index + 1}</td>
                                    <td><img src="${b.thumbnail}" alt="Thumbnail" width="80" height="80"></td>
                                    <td>${b.title}</td>
                                    <td>
                                        <c:forEach items="${categories}" var="c">
                                            <c:if test="${b.categoryBlog_id == c.categoryBlogId}">
                                                ${c.categoryBlogName}
                                            </c:if>
                                        </c:forEach>
                                    </td>

                                    <td>${b.updated_date}</td>
                                    <td>
                                        <span class="badge ${b.status ? 'bg-success' : 'bg-danger'}">
                                            ${b.status ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <form action="toggle-status" method="POST">
                                            <input type="hidden" name="blogId" value="${b.blog_id}">
                                            <!-- Chuyển đổi giá trị status -->
                                            <input type="hidden" name="status" value="${b.status ? 'false' : 'true'}">
                                            <div class="form-check form-switch">
                                                <input class="form-check-input" type="checkbox" 
                                                       id="statusSwitch${b.blog_id}" 
                                                       name="status" ${b.status ? 'checked' : ''} 
                                                       onchange="this.form.submit();">
                                                <label class="form-check-label" for="statusSwitch${b.blog_id}"></label>
                                            </div>
                                        </form>
                                    </td>
                                    <td>
                                        <!-- Nút Xem Chi Tiết -->
                                        <a href="view-blog?blog_id=${b.blog_id}" class="btn btn-primary btn-sm">
                                            <i class="fas fa-eye"></i> 
                                        </a>

                                        <!-- Nút Sửa -->
                                        <a href="edit-blog?blog_id=${b.blog_id}" class="btn btn-warning btn-sm">
                                            <i class="fas fa-edit"></i> 
                                        </a>

                                        <!-- Nút Xóa -->
                                        <a href="delete-blog?blog_id=${b.blog_id}" class="btn btn-danger btn-sm"
                                           onclick="return confirm('Bạn có chắc muốn xóa bài viết này không?');">
                                            <i class="fas fa-trash"></i> 
                                        </a>
                                    </td>

                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>

            <c:if test="${empty blogs}">
                <p>Không có dữ liệu Blog!</p>
            </c:if>

            <!-- Phân trang -->
            <nav aria-label="Page navigation" class="mt-3">
                <ul class="pagination justify-content-center">
                    <c:set var="safePageSize" value="${empty param.pageSize ? 10 : param.pageSize}" />
                    <c:set var="safeSortBy" value="${empty param.sortBy ? '' : param.sortBy}" />
                    <c:set var="safeStatus" value="${empty param.status ? 'all' : param.status}" />
                    <c:set var="safeKey" value="${empty param.key ? '' : param.key}" />
                    <c:set var="safeCategoryId" value="${empty param.categoryId ? '' : param.categoryId}" />

                    <!-- Nút Previous -->
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a href="posts-list?page=${currentPage - 1}&pageSize=${safePageSize}
                           &sortBy=${safeSortBy}&status=${safeStatus}&key=${safeKey}
                           &categoryId=${safeCategoryId}" 
                           class="page-link" aria-label="Previous">
                            <span aria-hidden="true">«</span>
                        </a>
                    </li>

                    <!-- Các trang -->
                    <c:forEach var="i" begin="1" end="${totalPages}" step="1">
                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                            <a class="page-link" href="posts-list?page=${i}&pageSize=${safePageSize}
                               &sortBy=${safeSortBy}&status=${safeStatus}&key=${safeKey}
                               &categoryId=${safeCategoryId}">${i}</a>
                        </li>
                    </c:forEach>

                    <!-- Nút Next -->
                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                        <a href="posts-list?page=${currentPage + 1}&pageSize=${safePageSize}
                           &sortBy=${safeSortBy}&status=${safeStatus}&key=${safeKey}
                           &categoryId=${safeCategoryId}" 
                           class="page-link" aria-label="Next">
                            <span aria-hidden="true">»</span>
                        </a>
                    </li>
                </ul>
            </nav>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>