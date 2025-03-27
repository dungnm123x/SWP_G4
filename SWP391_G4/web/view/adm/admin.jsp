<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Quản Lý Vé Tàu</title>
        <link rel="stylesheet" href="./css/admin/admin.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css" />
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
        <link href='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.css' rel='stylesheet' />
        <script src='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.js'></script>
        <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
        <link href='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.css' rel='stylesheet' />
        <script src='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.js'></script>
        <script>
            function submitRoleForm(userId) {
            document.getElementById('roleForm' + userId).submit();
            }

            // Client-side validation for the Add Event form
            function validateAddEventForm() {
            const title = document.getElementById('title').value.trim();
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            const description = document.getElementById('description').value.trim();
            const currentDate = new Date().toISOString().slice(0, 16);
            // Validate title
            if (!title) {
            alert('Tiêu đề không được để trống!');
            return false;
            }
            if (title.length > 100) {
            alert('Tiêu đề không được dài quá 100 ký tự!');
            return false;
            }

            // Validate description
            if (description.length > 500) {
            alert('Mô tả không được dài quá 500 ký tự!');
            return false;
            }

            // Validate start date
            if (!startDate) {
            alert('Ngày bắt đầu không được để trống!');
            return false;
            }
            if (startDate < currentDate) {
            alert('Ngày bắt đầu không được là ngày trong quá khứ!');
            return false;
            }

            // Validate end date (if provided)
            if (endDate && startDate >= endDate) {
            alert('Ngày bắt đầu phải trước ngày kết thúc!');
            return false;
            }

            return true;
            }

            // Client-side validation for the Edit Event form
            function validateEditEventForm() {
            const title = document.getElementById('editTitle').value.trim();
            const startDate = document.getElementById('editStartDate').value;
            const endDate = document.getElementById('editEndDate').value;
            const description = document.getElementById('editDescription').value.trim();
            const currentDate = new Date().toISOString().slice(0, 16);
            // Validate title
            if (!title) {
            alert('Tiêu đề không được để trống!');
            return false;
            }
            if (title.length > 100) {
            alert('Tiêu đề không được dài quá 100 ký tự!');
            return false;
            }

            // Validate description
            if (description.length > 500) {
            alert('Mô tả không được dài quá 500 ký tự!');
            return false;
            }

            // Validate start date
            if (!startDate) {
            alert('Ngày bắt đầu không được để trống!');
            return false;
            }
            if (startDate < currentDate) {
            alert('Ngày bắt đầu không được là ngày trong quá khứ!');
            return false;
            }

            // Validate end date (if provided)
            if (endDate && startDate >= endDate) {
            alert('Ngày bắt đầu phải trước ngày kết thúc!');
            return false;
            }

            return true;
            }
        </script>
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

            <div class="main-content">
                <div class="header">
                    <h1>Trang Quản Trị Hệ Thống Vé Tàu</h1>
                </div>
                <div class="content">
                    <c:choose>
                        <c:when test="${type == 'dashboard'}">
                            <h2>Dashboard</h2>
                            <div class="dashboard">
                                <div class="dashboard-row">
                                    <div class="dashboard-item trains">
                                        <h3>Thống kê tàu</h3>
                                        <p>Tổng số tàu: ${totalTrains}</p>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #F5E6C2;">
                                            <a href="train" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                    <div class="dashboard-item bookings">
                                        <h3>Thống kê đơn</h3>
                                        <p>Tổng số đơn: ${totalBookings}</p>
                                        <p>Tổng số vé: </p>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #96B4AA;">
                                            <a href="order" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                    <div class="dashboard-item routes" style="background-color: #DBE1E1">
                                        <h3>Thống kê tuyến tàu</h3>
                                        <br>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #A9C2D8;">
                                            <a href="route" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                    <div class="dashboard-item rules" >
                                        <h3>Thống kê Quy định</h3>
                                        <p>Tổng số Quy định: ${totalRules}</p>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #C2B0D8;">
                                            <a href="category-rule" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                </div>
                                <div class="dashboard-row">
                                    <div class="dashboard-item stations" style="background-color: #E0BBE4">
                                        <h3>Thống kê Ga</h3>
                                        <p>Tổng số ga: ${totalStations}</p>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #E6C2F5;">
                                            <a href="station" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                    <div class="dashboard-item trips" style="background-color: #A9DEF9">
                                        <h3>Thống kê chuyến đi</h3>
                                        <p>Tổng số chuyến đi: ${totalTrips}</p>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #A9C2D8;">
                                            <a href="trip" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                    <div class="dashboard-item category-blogs" style="background-color: #FCE6C9">
                                        <h3>Tiêu đề blog</h3>
                                        <br>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #C2B0D8;">
                                            <a href="category-blog" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                    <div class="dashboard-item blogs" style="background-color: #D4E9B9">
                                        <h3>Thống kê Blog</h3>
                                        <p>Tổng số blog: ${totalBlogs}</p>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #D8B4A9;">
                                            <a href="posts-list" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                    <div class="dashboard-item category-rules" style="background-color: #CDFBBD">
                                        <h3>Tiêu đề quy định:</h3>
                                        <br>
                                        <button class="more-info" style="width: 100%; margin-bottom: 0px; background-color: #D8B4A9;">
                                            <a href="category-rule" style="text-decoration: none;">More info <span class="arrow">→</span></a>
                                        </button>
                                    </div>
                                </div>
                                <div class="dashboard-row">
                                    <div class="dashboard-item" style="width: 60%;">
                                        <h3>Thống kê doanh thu</h3>
                                        <div class="revenue-filter">
                                            <form method="get" action="admin">
                                                <input type="hidden" name="view" value="dashboard">
                                                <label for="period">Chọn khoảng thời gian:</label>
                                                <select name="period" id="period" onchange="updateDateInput()">
                                                    
                                                    <option value="monthly" ${period == 'monthly' ? 'selected' : ''}>Hàng tháng</option>
                                                    <option value="yearly" ${period == 'yearly' ? 'selected' : ''}>Hàng năm</option>
                                                </select>

                                                <label for="selectedDate">Chọn thời gian:</label>
                                                <input type="date" id="selectedDate" name="selectedDate" value="${selectedDate}" required>

                                                <button type="submit" class="btn-custom btn-primary-custom">Xem</button>
                                            </form>
                                        </div>

                                        <canvas id="revenueChart" width="400" height="200"></canvas>
                                    </div>
                                    <div class="dashboard-item" style="width: 40%;">
                                        <h3>Thống kê người dùng</h3>
                                        <canvas id="userChart" width="400" height="200"></canvas>
                                    </div>
                                </div>
                                <div class="dashboard-row">
                                    <div class="feedback-container">
                                        <h3>Phản hồi gần đây</h3>
                                        <c:choose>
                                            <c:when test="${not empty feedbackList}">
                                                <ul class="feedback-list">
                                                    <c:forEach var="feedback" items="${feedbackList}">
                                                        <li class="feedback-item" style="with: 50%">
                                                            <strong class="feedback-rating">
                                                                <span class="feedback-email">Email: ${feedback.user.email}</span>
                                                                <c:forEach var="i" begin="1" end="${feedback.rating}" step="1">
                                                                    <i class="bi bi-star-fill text-warning" ></i>
                                                                </c:forEach>
                                                                <c:forEach var="i" begin="${feedback.rating + 1}" end="5" step="1">
                                                                    <i class="bi bi-star text-warning"></i>
                                                                </c:forEach>
                                                            </strong>
                                                            <br>
                                                            <span class="feedback-content" style="color: black">${feedback.content}</span>
                                                        </li>
                                                    </c:forEach>
                                                </ul>
                                            </c:when>
                                            <c:otherwise>
                                                <p class="no-feedback">Không có phản hồi nào.</p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>  
                                    <div class="dashboard-item" >
                                        <h3>Phân Bố Phản Hồi Sao</h3>
                                        <canvas id="starDistributionChart" width="250" height="250"></canvas>
                                    </div>
                                    <div class="dashboard-item" style="position: relative; height: 400px;">
                                        <h3>Bản đồ Ga Tàu Việt Nam</h3>
                                        <div id="map" style="width: 100%; height: 100%;"></div>
                                    </div>
                                </div>
                            </div>

                            <script>
                                document.addEventListener('DOMContentLoaded', function () {
                                var map = L.map('map').setView([16.0471, 108.2062], 6); // Center of Vietnam

                                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                                attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                                }).addTo(map);
                                // Add station markers dynamically from database
                                var stations = [
                                <c:forEach var="station" items="${stationsWithCoords}" varStatus="loop">
                                {
                                name: '${station.stationName}',
                                        lat: ${station.latitude},
                                        lng: ${station.longitude},
                                        address: '${station.address}'
                                }<c:if test="${!loop.last}">,</c:if>
                                </c:forEach>
                                ];
                                stations.forEach(function(station) {
                                if (station.lat && station.lng) { // Check if coordinates exist
                                L.marker([station.lat, station.lng])
                                        .addTo(map)
                                        .bindPopup('<b>' + station.name + '</b><br>' + station.address);
                                }
                                });
                                });
                            </script>

                            <script>
                                document.addEventListener('DOMContentLoaded', function () {
                                const starDistribution = [${starDistribution[0]}, ${starDistribution[1]}, ${starDistribution[2]}, ${starDistribution[3]}, ${starDistribution[4]}];
                                const ctx = document.getElementById('starDistributionChart').getContext('2d');
                                const starDistributionChart = new Chart(ctx, {
                                type: 'pie',
                                        data: {
                                        labels: ['1 Sao', '2 Sao', '3 Sao', '4 Sao', '5 Sao'],
                                                datasets: [{
                                                data: starDistribution,
                                                        backgroundColor: [
                                                                'rgba(255, 99, 132, 0.2)',
                                                                'rgba(54, 162, 235, 0.2)',
                                                                'rgba(255, 206, 86, 0.2)',
                                                                'rgba(75, 192, 192, 0.2)',
                                                                'rgba(153, 102, 255, 0.2)'
                                                        ],
                                                        borderColor: [
                                                                'rgba(255, 99, 132, 1)',
                                                                'rgba(54, 162, 235, 1)',
                                                                'rgba(255, 206, 86, 1)',
                                                                'rgba(75, 192, 192, 1)',
                                                                'rgba(153, 102, 255, 1)'
                                                        ],
                                                        borderWidth: 1
                                                }]
                                        },
                                        options: {
                                        responsive: true,
                                                plugins: {
                                                legend: {
                                                display: true,
                                                        position: 'top',
                                                },
                                                        title: {
                                                        display: true,
                                                                text: 'Phân Bố Sao Từ Phản Hồi Khách Hàng'
                                                        }
                                                }
                                        }
                                });
                                });
                            </script>
                            <script>
                                // Hàm cập nhật kiểu input ngày dựa trên period
                                function updateDateInput() {
                                const period = document.getElementById('period').value;
                                const dateInput = document.getElementById('selectedDate');
                                if (period === 'yearly') {
                                dateInput.type = 'number';
                                dateInput.min = new Date().getFullYear() - 4;
                                dateInput.max = new Date().getFullYear();
                                dateInput.value = dateInput.value || new Date().getFullYear();
                                } else {
                                dateInput.type = 'date';
                                dateInput.value = dateInput.value || new Date().toISOString().split('T')[0];
                                }
                                }

                                // Gọi hàm khi trang tải
                                document.addEventListener('DOMContentLoaded', updateDateInput);
                                // Dữ liệu cho biểu đồ
                                const period = '${period}';
                                let labels = [];
                                const unusedData = [];
                                const usedData1 = [];
                                const usedData2 = [];
                                <c:choose>
                                    <c:when test="${period == 'weekly'}">
                                // Tạo nhãn cho 7 ngày
                                const selectedDate = new Date('${selectedDate}');
                                for (let i = 6; i >= 0; i--) {
                                const date = new Date(selectedDate);
                                date.setDate(selectedDate.getDate() - i);
                                labels.push(date.toISOString().split('T')[0]);
                                }

                                // Điền dữ liệu doanh thu
                                        <c:forEach var="data" items="${unusedRevenue}">
                                unusedData.push(${data.totalRevenue});
                                        </c:forEach>
                                        <c:forEach var="data" items="${usedRevenue1}">
                                usedData1.push(${data.totalRevenue});
                                        </c:forEach>
                                        <c:forEach var="data" items="${usedRevenue2}">
                                usedData2.push(${data.totalRevenue});
                                        </c:forEach>
                                    </c:when>
                                    <c:when test="${period == 'monthly'}">
                                // Tạo nhãn cho các ngày trong tháng
                                const daysInMonth = new Date(new Date('${selectedDate}').getFullYear(), new Date('${selectedDate}').getMonth() + 1, 0).getDate();
                                for (let i = 1; i <= daysInMonth; i++) {
                                labels.push(i);
                                }

                                // Điền dữ liệu doanh thu
                                        <c:forEach var="data" items="${unusedRevenue}">
                                unusedData[${data.timePeriod - 1}] = ${data.totalRevenue};
                                        </c:forEach>
                                        <c:forEach var="data" items="${usedRevenue1}">
                                usedData1[${data.timePeriod - 1}] = ${data.totalRevenue};
                                        </c:forEach>
                                        <c:forEach var="data" items="${usedRevenue2}">
                                usedData2[${data.timePeriod - 1}] = ${data.totalRevenue};
                                        </c:forEach>
                                    </c:when>
                                    <c:when test="${period == 'yearly'}">
                                // Tạo nhãn cho 12 tháng
                                labels = ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7', 'Tháng 8', 'Tháng 9', 'Tháng 10', 'Tháng 11', 'Tháng 12'];
                                // Điền dữ liệu doanh thu
                                        <c:forEach var="data" items="${unusedRevenue}">
                                unusedData[${data.timePeriod - 1}] = ${data.totalRevenue};
                                        </c:forEach>
                                        <c:forEach var="data" items="${usedRevenue1}">
                                usedData1[${data.timePeriod - 1}] = ${data.totalRevenue};
                                        </c:forEach>
                                        <c:forEach var="data" items="${usedRevenue2}">
                                usedData2[${data.timePeriod - 1}] = ${data.totalRevenue};
                                        </c:forEach>
                                    </c:when>
                                </c:choose>

                                // Đảm bảo dữ liệu đầy đủ
                                for (let i = 0; i < labels.length; i++) {
                                unusedData[i] = unusedData[i] || 0;
                                usedData1[i] = usedData1[i] || 0;
                                usedData2[i] = usedData2[i] || 0;
                                }

                                // Vẽ biểu đồ đường
                                const ctxRevenue = document.getElementById('revenueChart').getContext('2d');
                                const revenueChart = new Chart(ctxRevenue, {
                                type: 'line',
                                        data: {
                                        labels: labels,
                                                datasets: [
                                                {
                                                label: 'Doanh thu (Chưa dùng)',
                                                        data: unusedData,
                                                        borderColor: 'rgba(255, 99, 132, 1)',
                                                        backgroundColor: 'rgba(255, 99, 132, 0.2)',
                                                        fill: false,
                                                        tension: 0.1
                                                },
                                                {
                                                label: 'Doanh thu (Dùng)',
                                                        data: usedData1,
                                                        borderColor: 'rgba(54, 162, 235, 1)',
                                                        backgroundColor: 'rgba(54, 162, 235, 0.2)',
                                                        fill: false,
                                                        tension: 0.1
                                                },
                                                {
                                                label: 'Doanh thu (Đã trả)',
                                                        data: usedData2,
                                                        borderColor: 'rgba(75, 192, 192, 1)',
                                                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                                                        fill: false,
                                                        tension: 0.1
                                                }
                                                ]
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
                                                        text: 'Thời gian'
                                                }
                                                }
                                        }
                                        }
                                });
                            </script>
                            <script>
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
                        <c:when test="${type == 'calendar'}">
                            <h2>Quản Lý Lịch</h2>
                            <div class="calendar-container">
                                <!-- Form to add a new event -->
                                <div class="calendar-form">
                                    <h3>Thêm Sự Kiện Mới</h3>
                                    <form action="admin" method="post" onsubmit="return validateAddEventForm()">
                                        <input type="hidden" name="action" value="addCalendarEvent">
                                        <div class="input-group">
                                            <label for="title">Tiêu đề:</label>
                                            <input type="text" id="title" name="title" class="input-field" required>
                                        </div>
                                        <div class="input-group">
                                            <label for="startDate">Ngày bắt đầu:</label>
                                            <input type="datetime-local" id="startDate" name="startDate" class="input-field" required>
                                        </div>
                                        <div class="input-group">
                                            <label for="endDate">Ngày kết thúc (tùy chọn):</label>
                                            <input type="datetime-local" id="endDate" name="endDate" class="input-field">
                                        </div>
                                        <div class="input-group">
                                            <label for="allDay">Cả ngày:</label>
                                            <input type="checkbox" id="allDay" name="allDay">
                                        </div>
                                        <div class="input-group">
                                            <label for="description">Mô tả:</label>
                                            <textarea id="description" name="description" class="input-field textarea-field" rows="3"></textarea>
                                        </div>
                                        <button type="submit" class="btn-custom btn-primary-custom">Thêm Sự Kiện</button>
                                    </form>
                                </div>
                                <!-- Calendar display -->
                                <div class="calendar-display">
                                    <h3>Lịch Sự Kiện</h3>
                                    <div id="calendar"></div>
                                </div>
                            </div>

                            <!-- Custom Modal for editing events -->
                            <div class="modal-custom" id="editEventModal">
                                <div class="modal-content-custom">
                                    <div class="modal-header-custom">
                                        <h5>Chỉnh Sửa Sự Kiện</h5>
                                        <button type="button" class="close-custom" onclick="closeModal('editEventModal')">×</button>
                                    </div>
                                    <div class="modal-body-custom">
                                        <form id="editEventForm" action="admin" method="post" onsubmit="return validateEditEventForm()">
                                            <input type="hidden" name="action" value="updateCalendarEvent">
                                            <input type="hidden" id="editEventId" name="eventId">
                                            <div class="input-group">
                                                <label for="editTitle">Tiêu đề:</label>
                                                <input type="text" id="editTitle" name="title" class="input-field" required>
                                            </div>
                                            <div class="input-group">
                                                <label for="editStartDate">Ngày bắt đầu:</label>
                                                <input type="datetime-local" id="editStartDate" name="startDate" class="input-field" required>
                                            </div>
                                            <div class="input-group">
                                                <label for="editEndDate">Ngày kết thúc (tùy chọn):</label>
                                                <input type="datetime-local" id="editEndDate" name="endDate" class="input-field">
                                            </div>
                                            <div class="input-group">
                                                <label for="editAllDay">Cả ngày:</label>
                                                <input type="checkbox" id="editAllDay" name="allDay">
                                            </div>
                                            <div class="input-group">
                                                <label for="editDescription">Mô tả:</label>
                                                <textarea id="editDescription" name="description" class="input-field textarea-field" rows="3"></textarea>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <script>
                                function openModal(modalId) {
                                document.getElementById(modalId).style.display = 'block';
                                }

                                function closeModal(modalId) {
                                document.getElementById(modalId).style.display = 'none';
                                }

                                document.addEventListener('DOMContentLoaded', function () {
                                const calendarEl = document.getElementById('calendar');
                                const calendar = new FullCalendar.Calendar(calendarEl, {
                                initialView: 'dayGridMonth',
                                        headerToolbar: {
                                        left: 'prev,next today',
                                                center: 'title',
                                                right: 'dayGridMonth,timeGridWeek,timeGridDay'
                                        },
                                        editable: true,
                                        selectable: true,
                                        events: [
                                <c:forEach var="event" items="${calendarEvents}" varStatus="loop">
                                        {
                                        id: '${event.eventID}',
                                                title: '${event.title}',
                                                start: <c:choose>
                                        <c:when test="${event.allDay}">
                                        '<fmt:formatDate value="${event.startDate}" pattern="yyyy-MM-dd"/>'
                                        </c:when>
                                        <c:otherwise>
                                        '<fmt:formatDate value="${event.startDate}" pattern="yyyy-MM-dd'T'HH:mm:ss"/>'
                                        </c:otherwise>
                                    </c:choose>,
                                                end: <c:choose>
                                        <c:when test="${not empty event.endDate}">
                                            <c:choose>
                                                <c:when test="${event.allDay}">
                                        '<fmt:formatDate value="${event.endDate}" pattern="yyyy-MM-dd"/>'
                                                </c:when>
                                                <c:otherwise>
                                        '<fmt:formatDate value="${event.endDate}" pattern="yyyy-MM-dd'T'HH:mm:ss"/>'
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                        null
                                        </c:otherwise>
                                    </c:choose>,
                                                allDay: ${event.allDay},
                                                description: '${event.description != null ? event.description : ""}'
                                        }<c:if test="${!loop.last}">,</c:if>
                                </c:forEach>
                                        ],
                                        dateClick: function (info) {
                                        document.getElementById('startDate').value = info.dateStr + 'T00:00';
                                        document.getElementById('endDate').value = info.dateStr + 'T23:59';
                                        document.getElementById('allDay').checked = true;
                                        }
                                ,
                                        eventClick: function (info) {
                                        document.getElementById('editEventId').value = info.event.id;
                                        document.getElementById('editTitle').value = info.event.title;
                                        document.getElementById('editStartDate').value = info.event.start.toISOString().slice(0, 16);
                                        if (info.event.end) {
                                        document.getElementById('editEndDate').value = info.event.end.toISOString().slice(0, 16);
                                        } else {
                                        document.getElementById('editEndDate').value = '';
                                        }
                                        document.getElementById('editAllDay').checked = info.event.allDay;
                                        document.getElementById('editDescription').value = info.event.extendedProps.description || '';
                                        openModal('editEventModal');
                                        document.getElementById('deleteEventBtn').onclick = function () {
                                        if (confirm('Bạn có chắc chắn muốn xóa sự kiện này?')) {
                                        const form = document.createElement('form');
                                        form.method = 'post';
                                        form.action = 'admin';
                                        form.innerHTML = `
                                                        <input type="hidden" name="action" value="deleteCalendarEvent">
                                                        <input type="hidden" name="eventId" value="${info.event.id}">
                                                    `;
                                        document.body.appendChild(form);
                                        form.submit();
                                        }
                                        };
                                        }
                                }
                                );
                                calendar.render();
                                });
                            </script>
                        </c:when>
                        <c:when test="${type == 'userauthorization'}">
                            <div class="search-container">
                                <form method="get" action="admin">
                                    <input type="hidden" name="view" value="userauthorization">
                                    <input type="text" name="search" class="search-input" placeholder="Tìm kiếm...">
                                    <button type="submit" class="search-btn"><i class="bi bi-search"></i></button>
                                    <a href="admin?view=userauthorization" class="reset-btn"><i class="bi bi-arrow-counterclockwise"></i></a>
                                </form>
                            </div>
                            <h2>Phân quyền người dùng</h2>
                            <table border="1">
                                <thead>
                                    <tr>
                                        <th></th>
                                        <th>Tên người dùng</th>
                                        <th>Họ và tên</th>
                                        <th>Email</th>
                                        <th>Vai trò</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="userItem" items="${list}" varStatus="status">
                                        <tr>
                                            <td>${status.index + 1}</td>
                                            <td>${userItem.username}</td>
                                            <td>${userItem.fullName}</td>
                                            <td>${userItem.email}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${userItem.roleID == 1}">Admin</c:when>
                                                    <c:when test="${userItem.roleID == 2}">Employee</c:when>
                                                    <c:when test="${userItem.roleID == 3}">Customer</c:when>
                                                    <c:otherwise>Unknown</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <form id="roleForm${userItem.userId}" method="post" action="admin">
                                                    <input type="hidden" name="action" value="setUserRole">
                                                    <input type="hidden" name="userId" value="${userItem.userId}">
                                                    <select name="roleId" class="form-select" onchange="submitRoleForm('${userItem.userId}')">
                                                        <option value="1" ${userItem.roleID == 1 ? 'selected' : ''}><i class="bi bi-person-fill-gear"></i> Admin</option>
                                                        <option value="2" ${userItem.roleID == 2 ? 'selected' : ''}><i class="bi bi-person-badge-fill"></i> Employee</option>
                                                        <option value="3" ${userItem.roleID == 3 ? 'selected' : ''}><i class="bi bi-person-fill"></i> Customer</option>
                                                    </select>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <a href="<c:url value="admin"><c:param name="view" value="${type}" /><c:param name="page" value="${currentPage - 1}" /><c:if test="${not empty param.search}"><c:param name="search" value="${param.search}" /></c:if></c:url>">Previous</a>
                                </c:if>
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <c:choose>
                                        <c:when test="${i eq currentPage}">
                                            <span>${i}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="<c:url value="admin"><c:param name="view" value="${type}" /><c:param name="page" value="${i}" /><c:if test="${not empty param.search}"><c:param name="search" value="${param.search}" /></c:if></c:url>">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:if test="${currentPage < totalPages}">
                                    <a href="<c:url value="admin"><c:param name="view" value="${type}" /><c:param name="page" value="${currentPage + 1}" /><c:if test="${not empty param.search}"><c:param name="search" value="${param.search}" /></c:if></c:url>">Next</a>
                                </c:if>
                            </div>
                        </c:when>
                        <c:when test="${not empty list}">
                            <div class="search-container">
                                <form method="get" action="admin">
                                    <input type="hidden" name="view" value="${type}">
                                    <input type="text" name="search" class="search-input" placeholder="Tìm kiếm...">
                                    <button type="submit" class="search-btn"><i class="bi bi-search"></i></button>
                                    <a href="admin?view=${type}" class="reset-btn"><i class="bi bi-arrow-counterclockwise"></i></a>
                                </form>
                            </div>
                            <c:choose>
                                <c:when test="${type == 'employees'}">
                                    <h2>Danh sách nhân viên</h2>
                                </c:when>
                                <c:otherwise>
                                    <h2>Danh sách khách hàng</h2>
                                </c:otherwise>
                            </c:choose>
                            <table border="1">
                                <thead>
                                    <tr>
                                        <c:choose>
                                            <c:when test="${type == 'employees' || type == 'customers'}">
                                                <th></th>
                                                <th>Tên người dung</th>
                                                <th>Họ và tên</th>
                                                <th>Email</th>
                                                <th>SDT</th>
                                                <th>Hành động</th>
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
                                                        <button class="btn btn-outline-danger btn-sm" style="color: green; border-color: green;"
                                                                onclick="location.href = 'admin?view=disable&type=${type}&id=${item.userId}'">
                                                            <i class="bi bi-check-circle"></i> Đang hoạt động
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button class="btn btn-outline-success btn-sm" style="color: red; border-color: red;"
                                                                onclick="location.href = 'admin?view=restore&type=${type}&id=${item.userId}'">
                                                            <i class="bi bi-x-circle"></i> Đã tắt hoạt động
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <div class="pagination">
                                <c:if test="${currentPage > 1}">
                                    <a href="<c:url value="admin"><c:param name="view" value="${type}" /><c:param name="page" value="${currentPage - 1}" /><c:if test="${not empty param.search}"><c:param name="search" value="${param.search}" /></c:if></c:url>">Previous</a>
                                </c:if>
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <c:choose>
                                        <c:when test="${i eq currentPage}">
                                            <span>${i}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="<c:url value="admin"><c:param name="view" value="${type}" /><c:param name="page" value="${i}" /><c:if test="${not empty param.search}"><c:param name="search" value="${param.search}" /></c:if></c:url>">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:if test="${currentPage < totalPages}">
                                    <a href="<c:url value="admin"><c:param name="view" value="${type}" /><c:param name="page" value="${currentPage + 1}" /><c:if test="${not empty param.search}"><c:param name="search" value="${param.search}" /></c:if></c:url>">Next</a>
                                </c:if>
                            </div>

                            <c:if test="${type eq 'employees'}">
                                <div class="add-button-container">
                                    <a href="admin?view=addEmployee" class="btn-add">
                                        <i class="bi bi-plus-circle"></i> Thêm Nhân Viên
                                    </a>
                                </div>
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