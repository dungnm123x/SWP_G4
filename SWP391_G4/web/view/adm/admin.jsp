<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Quản Lý Vé Tàu</title>
        <link rel="stylesheet" href="./css/admin/admin.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <script src="./admin-scripts.js"></script>
    </head>
    <body>
        <div class="container">
            <div class="sidebar">
                <div class="logo">
                    <img src="./img/logo.jpg" alt="trainpicture">
                </div>
                <ul class="menu">
                    <li><a href="admin?view=dashboard">Dashboard</a></li>
                    <li><a href="admin?view=employees">Quản lý nhân viên</a></li>
                    <li><a href="admin?view=customers">Quản lý khách hàng</a></li>
                    <li><a href="trip">Quản lý chuyến tàu</a></li>
                    <li><a class="nav-link" href="updateuser">Hồ sơ của tôi</a></li>
                </ul>
                <form action="logout" method="GET">
                    <button type="submit" class="logout-button">Logout</button>
                </form>
            </div>

            <div class="main-content">
                <div class="header">
                    <h1>Trang Quản Trị Hệ Thống Vé Tàu</h1>
                </div>
                <div class="content">
                    <c:choose>
                        <c:when test="${type == 'dashboard'}">
                            <h2>Dashboard</h2>
                            <div class="dashboard">
                                <div class="dashboard-item">
                                    <h3>Thống kê tàu</h3>
                                    <p>Tổng số tàu: ${totalTrains}</p>
                                </div>
                                <div class="dashboard-item">
                                    <h3>Thống kê đặt vé</h3>
                                    <p>Tổng số đặt vé: ${totalBookings}</p>
                                </div>
                                <div class="dashboard-item">
                                    <h3>Thống kê chuyến đi</h3>
                                    <p>Tổng số chuyến đi: ${totalTrips}</p>
                                </div>
                            </div>
                            <div class="dashboard">
                                <div class="dashboard-item">
                                    <h3>Thống kê Blog</h3>
                                    <p>Tổng số Blog: ${totalBlogs}</p>
                                </div>
                                <div class="dashboard-item">
                                    <h3>Thống kê Quy định</h3>
                                    <p>Tổng số Quy định: <span class="math-inline">${totalRules}</span></p>
                                </div>
                            </div>
                            <div class="dashboard">
                                <div class="dashboard-item">
                                    <h3>Thống kê doanh thu</h3>
                                    <div class="revenue-chart-container">
                                        <div class="revenue-options">
                                            <button class="revenue-option active" data-type="month">Monthly</button>
                                            <button class="revenue-option" data-type="week">Weekly</button>
                                            <button class="revenue-option" data-type="year">Yearly</button>
                                        </div>
                                        <canvas id="revenueChart" width="600" height="300"></canvas>
                                    </div>
                                </div>
                                <div class="dashboard-item">
                                    <h3>Thống kê người dùng</h3>
                                    <canvas id="userChart" width="400" height="200"></canvas>
                                </div>
                            </div>

                            <script>
                                const revenueData = {
                                    week: ${revenueThisWeek},
                                    month: ${revenueThisMonth},
                                    year: ${revenueThisYear}
                                };

                                const ctxRevenue = document.getElementById('revenueChart').getContext('2d');
                                let revenueChart;

                                function renderRevenueChart(selectedType) {
                                    if (revenueChart) {
                                        revenueChart.destroy();
                                    }

                                    let labels = [];
                                    let data = [];

                                    if (selectedType === 'year') {
                                        const currentYear = new Date().getFullYear();
                                        for (let i = 4; i >= 0; i--) {
                                            const year = currentYear - i;
                                            labels.push(year.toString());
                                            data.push(getRevenueForYear(year));
                                        }
                                    } else if (selectedType === 'week') {
                                        const today = new Date();
                                        for (let i = 6; i >= 0; i--) {
                                            const date = new Date(today);
                                            date.setDate(today.getDate() - i);
                                            labels.push(date.toLocaleDateString('vi-VN', {weekday: 'short'}));
                                            data.push(getRevenueForDate(date));
                                        }
                                    } else if (selectedType === 'month') {
                                        for (let i = 0; i < 12; i++) {
                                            labels.push('Tháng ' + (i + 1));
                                            data.push(getRevenueForMonth(i + 1));
                                        }
                                    }

                                    revenueChart = new Chart(ctxRevenue, {
                                        type: 'line',
                                        data: {
                                            labels: labels,
                                            datasets: [{
                                                    label: 'Doanh thu',
                                                    data: data,
                                                    borderColor: 'rgba(75, 192, 192, 1)',
                                                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                                                    borderWidth: 1,
                                                    fill: true,
                                                }]
                                        },
                                        options: {
                                            scales: {
                                                y: {
                                                    beginAtZero: true,
                                                    title: {
                                                        display: true,
                                                        text: 'Doanh thu (VNĐ)'
                                                    }
                                                },
                                                x: {
                                                    title: {
                                                        display: true,
                                                        text: selectedType === 'year' ? 'Năm' : 'Thời gian'
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }

                                // Hàm giả định để lấy doanh thu theo ngày
                                function getRevenueForDate(date) {
                                    // Thay thế bằng logic thực tế để lấy doanh thu từ server
                                    return Math.floor(Math.random() * 1000000);
                                }

                                // Hàm giả định để lấy doanh thu theo tháng
                                function getRevenueForMonth(month) {
                                    // Thay thế bằng logic thực tế để lấy doanh thu từ server
                                    return Math.floor(Math.random() * 5000000);
                                }

                                // Hàm giả định để lấy doanh thu theo năm
                                function getRevenueForYear(year) {
                                    // Thay thế bằng logic thực tế để lấy doanh thu từ server
                                    const baseRevenue = 10000000;
                                    const randomFactor = Math.random() * 5000000;
                                    return baseRevenue + year * 100000 + randomFactor;
                                }

                                // Các tùy chọn "Monthly", "Weekly", "Yearly"
                                const revenueOptions = document.querySelectorAll('.revenue-option');

                                revenueOptions.forEach(option => {
                                    option.addEventListener('click', () => {
                                        revenueOptions.forEach(opt => opt.classList.remove('active'));
                                        option.classList.add('active');
                                        const dataType = option.getAttribute('data-type');
                                        renderRevenueChart(dataType);
                                    });
                                });

                                // Khởi tạo biểu đồ với giá trị mặc định là 'year'
                                renderRevenueChart('year');

                                // Biểu đồ thống kê người dùng
                                var ctxUser = document.getElementById('userChart').getContext('2d');
                                var userChart = new Chart(ctxUser, {
                                    type: 'bar',
                                    data: {
                                        labels: ['Người dùng', 'Nhân viên', 'Khách hàng'],
                                        datasets: [{
                                                label: 'Số lượng',
                                                data: [${totalUsers}, ${totalEmployees}, ${totalCustomers}],
                                                backgroundColor: [
                                                    'rgba(255, 99, 132, 0.2)',
                                                    'rgba(54, 162, 235, 0.2)',
                                                    'rgba(255, 206, 86, 0.2)'
                                                ],
                                                borderColor: [
                                                    'rgba(255, 99, 132, 1)',
                                                    'rgba(54, 162, 235, 1)',
                                                    'rgba(255, 206, 86, 1)'
                                                ],
                                                borderWidth: 1
                                            }]
                                    },
                                    options: {
                                        scales: {
                                            y: {
                                                beginAtZero: true
                                            }
                                        }
                                    }
                                });
                            </script>
                        </c:when>
                        <c:when test="${not empty list}">
                            <c:if test="${not empty list}">
                                <div class="search-container">
                                    <form method="get" action="admin">
                                        <input type="hidden" name="view" value="${type}">
                                        <input type="text" name="search" class="search-input" placeholder="Tìm kiếm...">
                                        <button type="submit" class="search-btn"><i class="bi bi-search"></i></button>
                                        <a href="admin?view=${type}" class="reset-btn"><i class="bi bi-arrow-counterclockwise"></i></a>
                                    </form>
                                </div>

                                <table border="1">
                                    <thead>
                                        <tr>
                                            <c:choose>
                                                <c:when test="${type == 'employees' || type == 'customers'}">
                                                    <th></th>
                                                    <th>Username</th>
                                                    <th>Full Name</th>
                                                    <th>Email</th>
                                                    <th>Phone</th>
                                                    <th>Action</th>
                                                    </c:when>
                                                </c:choose>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${list}" varStatus="status">
                                            <tr>
                                                <c:choose>
                                                    <c:when test="${type == 'employees' || type == 'customers'}">
                                                        <td>${status.index + 1}</td>
                                                        <td>${item.username}</td>
                                                        <td>${item.fullName}</td>
                                                        <td>${item.email}</td>
                                                        <td>${item.phoneNumber}</td>
                                                    </c:when>
                                                </c:choose>
                                                <td>
                                                    <button class="btn btn-outline-primary btn-sm"
                                                            onclick="location.href = 'admin?view=details&type=${type}&id=${item.userId}'">
                                                        <i class="bi bi-eye"></i> Chi Tiết
                                                    </button>

                                                    <c:choose>
                                                        <c:when test="${item.status}">
                                                            <button class="btn btn-outline-danger btn-sm" style="color: red; border-color: red;"
                                                                    onclick="location.href = 'admin?view=disable&type=${type}&id=${item.userId}'">
                                                                <i class="bi bi-x-circle"></i> Vô Hiệu
                                                            </button>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <button class="btn btn-outline-success btn-sm" style="color: green; border-color: green;"
                                                                    onclick="location.href = 'admin?view=restore&type=${type}&id=${item.userId}'">
                                                                <i class="bi bi-check-circle"></i> Khôi Phục
                                                            </button>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>

                                <c:if test="${type eq 'employees'}">
                                    <div class="add-button-container">
                                        <a href="admin?view=addEmployee" class="btn-add">
                                            <i class="bi bi-plus-circle"></i> Thêm Nhân Viên
                                        </a>
                                    </div>
                                </c:if>
                            </c:if>

                        </c:when>
                        <c:otherwise>
                            <p>Chọn chức năng từ menu bên trái để bắt đầu quản lý hệ thống.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </body>
</html>