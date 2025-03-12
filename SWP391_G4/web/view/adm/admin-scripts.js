/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


const revenueData = {
    day: revenueToday, // Doanh thu của ngày (cần được truyền từ JSP)
    week: revenueThisWeek, // Doanh thu của tuần (cần được truyền từ JSP)
    month: revenueThisMonth, // Doanh thu của tháng (cần được truyền từ JSP)
    year: revenueThisYear // Doanh thu của năm (cần được truyền từ JSP)
};

const ctx = document.getElementById('revenueChart').getContext('2d');
let revenueChart;

function renderChart(selectedType) {
    if (revenueChart) {
        revenueChart.destroy();
    }

    revenueChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: [selectedType],
            datasets: [{
                label: 'Doanh thu',
                data: [revenueData[selectedType]],
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
                        text: 'Thời gian'
                    }
                }
            }
        }
    });
}

document.getElementById('revenueType').addEventListener('change', (event) => {
    renderChart(event.target.value);
});

renderChart('day');

var ctxUser = document.getElementById('userChart').getContext('2d');
var myChart = new Chart(ctxUser, {
    type: 'bar',
    data: {
        labels: ['Người dùng', 'Nhân viên', 'Khách hàng'],
        datasets: [{
            label: 'Số lượng',
            data: [totalUsers, totalEmployees, totalCustomers], // Cần được truyền từ JSP
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