<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.RailwayDTO" %>
<%
    List<RailwayDTO> tickets = (List<RailwayDTO>) request.getAttribute("tickets");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Danh sách vé đã mua</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <style>
        body {
            padding-top: 70px; /* Để tránh bị navbar che mất phần đầu nội dung */
            background-color: #f8f9fa;
        }
        .table-container {
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
            border-radius: 10px;
            overflow: hidden;
            background: white;
            padding: 15px;
            margin-top: 20px;
        }
        .pagination {
            justify-content: center;
        }
        .no-results {
            color: red;
            font-weight: bold;
        }
    </style>
    <jsp:include page="/navbar.jsp"/>
</head>
<body>
<div class="container mt-4">
    <h2 class="text-center">Danh sách vé đã mua</h2>
    <div class="mb-3 d-flex justify-content-between">
        <select id="rowsPerPage" class="form-select w-25">
            <option value="5">5 per page</option>
            <option value="10">10 per page</option>
            <option value="20">20 per page</option>
        </select>
        <select id="sortOrder" class="form-select w-25" >
            <option value="asc">Sort by Oldest</option>
            <option value="desc">Sort by Newest</option>
        </select>
        <select id="statusFilter" class="form-select w-25">
            <option value="">All</option>
            <option value="Used">Used</option>
            <option value="Unused">Unused</option>
            <option value="Refunded">Refunded</option>
        </select>
        <input type="text" id="searchInput" class="form-control w-25" placeholder="Search chuyến đi, tên tàu">
    </div>
    <div class="table-container">
        <table class="table table-bordered table-hover text-center" id="ticketTable">
            <thead class="bg-dark text-white">
                <tr>
                    <th>Mã vé</th>
                    <th>CCCD</th>
                    <th>Hành trình</th>
                    <th>Tàu</th>
                    <th>Thời gian khởi hành</th>
                    <th>Toa</th>
                    <th>Chỗ ngồi</th>
                    <th>Giá vé</th>
                    <th>Trạng thái vé</th>
                </tr>
            </thead>
            <tbody id="ticketBody">
                <% if (tickets != null && !tickets.isEmpty()) {
                    for (RailwayDTO ticket : tickets) { %>
                    <tr>
                        <td><%= ticket.getTicketID() %></td>
                        <td><%= ticket.getCccd() %></td>
                        <td><%= ticket.getRoute() %></td>
                        <td><%= ticket.getTrainCode() %></td>
                        <td><%= ticket.getDepartureTime() %></td>
                        <td><%= ticket.getCarriageNumber() %></td>
                        <td><%= ticket.getSeatNumber() %></td>
                        <td><%= ticket.getTicketPrice() %> $</td>
                        <td><%= ticket.getTicketStatus() %></td>
                    </tr>
                <% }
                } else { %>
                    <tr><td colspan="9" class="no-results">Không có vé nào.</td></tr>
                <% } %>
            </tbody>
        </table>
        <nav>
            <ul class="pagination">
                <li class="page-item"><a class="page-link" href="#" id="prevPage">Previous</a></li>
                <li class="page-item"><a class="page-link" href="#" id="nextPage">Next</a></li>
            </ul>
        </nav>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener("DOMContentLoaded", function() {
        const rowsPerPageSelect = document.getElementById("rowsPerPage");
        const sortOrderSelect = document.getElementById("sortOrder");
        let rowsPerPage = parseInt(rowsPerPageSelect.value);
        let currentPage = 1;
        let allRows = Array.from(document.getElementById("ticketTable").getElementsByTagName("tr")).slice(1);

        function renderTable() {
            const searchValue = document.getElementById("searchInput").value.toLowerCase();
            const statusValue = document.getElementById("statusFilter").value;
            const sortOrder = sortOrderSelect.value;
            const table = document.getElementById("ticketTable");
            const tbody = document.getElementById("ticketBody");
            const filteredRows = allRows.filter(row => {
                const cells = row.getElementsByTagName("td");
                if (cells.length > 0) {
                    const route = cells[2].innerText.toLowerCase();
                    const trainCode = cells[3].innerText.toLowerCase();
                    const status = cells[8].innerText;
                    return (route.includes(searchValue) || trainCode.includes(searchValue)) &&
                           (statusValue === "" || status === statusValue);
                }
                return false;
            }).sort((a, b) => {
                const idA = parseInt(a.getElementsByTagName("td")[0].innerText);
                const idB = parseInt(b.getElementsByTagName("td")[0].innerText);
                return sortOrder === "asc" ? idA - idB : idB - idA;
            });

            tbody.innerHTML = "";
            if (filteredRows.length === 0) {
                tbody.innerHTML = `<tr><td colspan="9" class="no-results">Không tồn tại vé bạn tìm kiếm.</td></tr>`;
            } else {
                const start = (currentPage - 1) * rowsPerPage;
                const end = Math.min(start + rowsPerPage, filteredRows.length);
                for (let i = start; i < end; i++) {
                    tbody.appendChild(filteredRows[i]);
                }
                document.getElementById("prevPage").parentNode.classList.toggle("disabled", currentPage === 1);
                document.getElementById("nextPage").parentNode.classList.toggle("disabled", end === filteredRows.length);
            }
        }

        rowsPerPageSelect.addEventListener("change", function() {
            rowsPerPage = parseInt(this.value);
            currentPage = 1;
            renderTable();
        });

        sortOrderSelect.addEventListener("change", function() {
            currentPage = 1;
            renderTable();
        });

        document.getElementById("searchInput").addEventListener("input", function() {
            currentPage = 1;
            renderTable();
        });

        document.getElementById("statusFilter").addEventListener("change", function() {
            currentPage = 1;
            renderTable();
        });

        document.getElementById("prevPage").addEventListener("click", function(event) {
            event.preventDefault();
            if (currentPage > 1) {
                currentPage--;
                renderTable();
            }
        });

        document.getElementById("nextPage").addEventListener("click", function(event) {
            event.preventDefault();
            const totalPages = Math.ceil(allRows.length / rowsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                renderTable();
            }
        });

        renderTable();
    });
</script>
</body>
</html>
