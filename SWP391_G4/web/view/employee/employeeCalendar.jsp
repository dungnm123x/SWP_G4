<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch Nhân Viên</title>
        <!-- FullCalendar CSS and JS -->
        <link href='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.css' rel='stylesheet' />
        <script src='https://cdn.jsdelivr.net/npm/fullcalendar@5.10.1/main.min.js'></script>
        <link rel="stylesheet" href="css/employee.css">
        <!-- Custom CSS -->
        <style>




            .calendar-display {
                background-color: #fff;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            }

            #calendar {
                max-width: 100%;
                margin: 0 auto;
            }

            .message {
                padding: 10px;
                margin-bottom: 20px;
                border-radius: 4px;
                text-align: center;
            }

            .message.success {
                background-color: #d4edda;
                color: #155724;
            }

            .message.error {
                background-color: #f8d7da;
                color: #721c24;
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
    <body>
        <div class="container" method="get" action="employeeCalendar">
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
            <div class="header">
                <h2>Lịch Sự Kiện Của Bạn</h2>

            </div>

            <!-- Display success or error messages -->
            <c:if test="${not empty sessionScope.message2}">
                <div class="message ${sessionScope.message2.startsWith('✅') ? 'success' : 'error'}">
                    ${sessionScope.message2}
                    <c:remove var="message2" scope="session"/>
                </div>
            </c:if>

            <!-- Calendar display -->
            <div class="calendar-display">
                <div id="calendar"></div>
            </div>
        </div>

        <script>
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
                    },
            });
            calendar.render();
            });
        </script>
    </body>
</html>