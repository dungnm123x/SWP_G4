<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Manage Category Blog</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/css/bootstrap.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha3/dist/js/bootstrap.bundle.min.js"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="css/employee.css">
        <style>
            .manager-title {
                text-align: center;
                margin-bottom: 1rem;
            }
            .filter-row {
                display: flex;
                flex-wrap: wrap; /* Cho phép xuống dòng khi màn hình nhỏ */
                gap: 1rem;
                margin-bottom: 1.5rem;
            }
            .filter-group {
                flex: 1; /* Mỗi nhóm form sẽ dàn đều */
                min-width: 120px; /* Đảm bảo không quá nhỏ ở màn hẹp */
            }
            .admin-back-button {
                display: inline-block;
                padding: 10px 20px;
                background-color: #2C3E50;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                border: none;
                font-size: 16px;
                transition: background-color 0.3s ease
            }

            .admin-back-button:hover {
                background-color: #00509E;
            }
            .admin-back-button i {
                margin-right: 8px;
            }
        </style>
    </head>
    <body class="bg-light">
        <div class="main-container">
            <c:if test="${sessionScope.user.roleID == 2}">
                <div class="sidebar">
                    <div class="logo">
                        <img src="./img/logo.jpg" alt="avatar">
                    </div>
                    <ul>
                        <li><a href="employeeCalendar">Lịch làm việc</a></li>
                        <li><a href="train">Quản lý tàu</a></li>
                        <li><a href="trip">Quản lý chuyến</a></li>
                        <li><a href="route">Quản lý tuyến tàu</a></li>
                        <li><a href="station">Quản lý ga</a></li>
                        <li><a href="order">Quản lý hóa đơn</a></li>
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
                        <li><a href="admin?view=calendar">Lịch</a></li>
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
            <div class="container">
                <div class="manager-title">
                    <h2 class="slider-title">Quản lý Tiêu đề Blog</h2>              
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

                <div class="filter-row">
                    <!-- Chọn số lượng hiển thị trên mỗi trang -->
                    <div class="filter-group">
                        <form id="pageSizeForm" action="category-blog" method="get" >
                            <label for="page-size-select" class="form-label me-2 mb-0">Show</label>
                            <select name="pageSize" id="page-size-select" class="form-select me-2" onchange="document.getElementById('pageSizeForm').submit()">
                                <option value="5" ${pageSize == 5 ? 'selected' : ''}>5</option>
                                <option value="10" ${pageSize == 10 ? 'selected' : ''}>10</option>
                                <option value="15" ${pageSize == 15 ? 'selected' : ''}>15</option>
                                <option value="20" ${pageSize == 20 ? 'selected' : ''}>20</option>
                                <option value="25" ${pageSize == 25 ? 'selected' : ''}>25</option>
                            </select>
                            <input type="hidden" name="index" value="${currentPage}" />
                            <input type="hidden" name="categoryName" value="${categoryName}" />
                            <input type="hidden" name="status" value="${status}" />
                            <input type="hidden" name="sortBy" value="${sortBy}" />
                        </form>
                    </div>
                    <div class="filter-group">
                        <form action="category-blog" method="get" >
                            <label for="sortBy" class="form-label me-2 mb-0">Sort By</label>
                            <select id="sortBy" name="sortBy" class="form-select" onchange="this.form.submit()">
                                <option value="0" ${sortBy == 0 ? 'selected' : ''}>Default</option>
                                <option value="1" ${sortBy == 1 ? 'selected' : ''}>Name (A-Z)</option>
                                <option value="2" ${sortBy == 2 ? 'selected' : ''}>Name (Z-A)</option>
                                <option value="3" ${sortBy == 3 ? 'selected' : ''}>ID (Ascending)</option>
                                <option value="4" ${sortBy == 4 ? 'selected' : ''}>ID (Descending)</option>
                            </select>
                            <input type="hidden" name="categoryName" value="${categoryName}" />
                            <input type="hidden" name="pageSize" value="${pageSize}" />
                            <input type="hidden" name="status" value="${status}" />
                        </form>
                    </div>
                    <!-- Trạng thái -->
                    <div class="filter-group">
                        <form action="category-blog" method="get">
                            <label for="statusFilter" class="form-label me-2 mb-0">Status</label>
                            <select id="statusFilter" name="status" class="form-select" onchange="this.form.submit()">
                                <option value="-1" ${status == -1 ? 'selected' : ''}>All</option>
                                <option value="1" ${status == 1 ? 'selected' : ''}>Active</option>
                                <option value="0" ${status == 0 ? 'selected' : ''}>Inactive</option>
                            </select>
                            <input type="hidden" name="categoryName" value="${categoryName}" />
                            <input type="hidden" name="pageSize" value="${pageSize}" />
                            <input type="hidden" name="sortBy" value="${sortBy}" />
                        </form>
                    </div>
                    <!-- Tìm kiếm -->
                    <div class="filter-group">
                        <form action="category-blog" method="GET">
                            <label for="search" class="form-label me-2 mb-0">Search</label>
                            <div class="input-group">
                                <input id="search" type="text" name="categoryName" class="form-control" placeholder="Tìm kiếm" value="${categoryName != null ? categoryName : ''}">
                                <input type="hidden" name="pageSize" value="${pageSize}" />
                                <input type="hidden" name="status" value="${status}" />
                                <input type="hidden" name="sortBy" value="${sortBy}" />
                                <button type="submit" class="btn btn-outline-danger">
                                    <i class="fas fa-search"></i>
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
                <div class="card shadow">
                    <div class="card-header text-white" style="background-color: #2C3E50;">
                        <h5 class="mb-0">Category Blog List</h5>
                    </div>

                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty categories}">
                                <!-- Nếu không có danh mục, hiển thị thông báo -->
                                <div class="alert alert-warning text-center" role="alert">
                                    No categories found. Please try again with different filters.
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:set var="currentPage" value="${currentPage != null ? currentPage : 1}" />
                                <c:set var="pageSize" value="${pageSize != null ? pageSize : 10}" />

                                <table class="table table-bordered">
                                    <thead class="bg-light">
                                        <tr>
                                            <th style="color: black;">STT</th>
                                            <th style="color: black;">Name</th>
                                            <th style="color: black;">Status</th>
                                            <th style="color: black;">Toggle Switch</th>
                                            <th style="color: black;">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="category" items="${categories}" varStatus="loop">
                                            <tr>
                                                <td>${(currentPage - 1) * pageSize + loop.index + 1}</td>
                                                <td>${category.categoryBlogName}</td>

                                                <!-- Cột Status -->
                                                <td>
                                                    <span id="statusLabel${category.categoryBlogId}" 
                                                          class="badge ${category.status == 1 ? 'bg-success' : 'bg-danger'}">
                                                        ${category.status == 1 ? 'Active' : 'Inactive'}
                                                    </span>
                                                </td>

                                                <!-- Cột Toggle Switch -->
                                                <td>
                                                    <form method="post" action="update-cate-status" style="display: inline;">
                                                        <input type="hidden" name="categoryId" value="${category.categoryBlogId}">
                                                        <input type="hidden" name="status" value="${category.status == 1 ? 0 : 1}">
                                                        <div class="form-check form-switch">
                                                            <input class="form-check-input" type="checkbox" 
                                                                   name="status" 
                                                                   value="1"
                                                                   id="switch${category.categoryBlogId}"
                                                                   ${category.status == 1 ? "checked" : ""}
                                                                   onchange="this.form.submit()">
                                                        </div>
                                                    </form>
                                                </td>

                                                <!-- Các cột Actions -->
                                                <td>
                                                    <button class="btn btn-warning btn-sm" 
                                                            onclick="editCategory(${category.categoryBlogId}, '${category.categoryBlogName}', ${category.status})">
                                                        <i class="bi bi-pencil-fill"></i>
                                                    </button>

                                                    <form action="category-blog" method="post" style="display:inline;">
                                                        <input type="hidden" name="action" value="delete">
                                                        <input type="hidden" name="id" value="${category.categoryBlogId}">
                                                        <button type="submit" class="btn btn-danger btn-sm">
                                                            <i class="bi bi-trash-fill"></i>
                                                        </button>
                                                    </form>

                                                    <button class="btn btn-primary btn-sm" onclick="openAddModal()">
                                                        <i class="bi bi-plus-lg"></i>
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>

                                    </tbody>
                                </table>

                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <nav>
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="?index=${currentPage - 1}&categoryName=${categoryName != null ? categoryName : ''}&sortBy=${sortBy}&pageSize=${pageSize}">Previous</a>
                        </li>
                        <c:forEach var="i" begin="1" end="${num}">
                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                <a class="page-link" href="?index=${i}&categoryName=${categoryName != null ? categoryName : ''}&sortBy=${sortBy}&pageSize=${pageSize}">${i}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == num ? 'disabled' : ''}">
                            <a class="page-link" href="?index=${currentPage + 1}&categoryName=${categoryName != null ? categoryName : ''}&sortBy=${sortBy}&pageSize=${pageSize}">Next</a>
                        </li>
                    </ul>
                </nav>
            </div>

            <!-- Modal thêm/sửa danh mục -->
            <div class="modal fade" id="categoryModal" tabindex="-1" aria-labelledby="categoryModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <form method="post" action="category-blog">
                            <div class="modal-header">
                                <h5 class="modal-title" id="categoryModalLabel">Category Form</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="id" id="categoryId">
                                <input type="hidden" name="action" id="formAction">
                                <div class="mb-3">
                                    <label for="categoryName" class="form-label">Category Name</label>
                                    <input type="text" class="form-control" name="name" id="categoryName" placeholder="Enter category name" required>
                                </div>
                                <div class="mb-3">
                                    <label for="categoryStatus" class="form-label">Status</label>
                                    <select class="form-select" name="status" id="categoryStatus" required>
                                        <option value="1">Active</option>
                                        <option value="0">Inactive</option>
                                    </select>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-primary">Save changes</button>
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script>
            function openAddModal() {
                document.getElementById('formAction').value = 'add';
                document.getElementById('categoryId').value = '';
                document.getElementById('categoryName').value = '';
                document.getElementById('categoryStatus').value = '1'; // Default: Active
                new bootstrap.Modal(document.getElementById('categoryModal')).show();
            }

            function editCategory(id, name, status) {
                document.getElementById('formAction').value = 'edit';
                document.getElementById('categoryId').value = id;
                document.getElementById('categoryName').value = name;
                document.getElementById('categoryStatus').value = status; // Set current status
                new bootstrap.Modal(document.getElementById('categoryModal')).show();
            }

            function deleteCategory(id) {
                if (!id) {
                    alert("Invalid category ID!");
                    return;
                }

                if (confirm("Are you sure you want to delete this category?")) {
                    console.log("Deleting category with ID:", id); // Debugging
                    const form = document.createElement("form");
                    form.method = "post";
                    form.action = "category-blog";
                    form.innerHTML = `
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" value="${id}">
        `;
                    document.body.appendChild(form);
                    form.submit();
                }
            }


        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
