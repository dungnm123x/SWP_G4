<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, model.RailwayDTO" %>
<%
    List<RailwayDTO> tickets = (List<RailwayDTO>) request.getAttribute("tickets");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Danh sách vé đã mua</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
    <h2>Danh sách vé đã mua</h2>
    <table>
        <tr>
            <th>Mã vé</th>
<!--            <th>Loại chuyến</th>-->
            <th>Hành trình</th>
            <th>Tàu</th>
            <th>Thời gian khởi hành</th>
            <th>Toa</th>
            <th>Chỗ ngồi</th>
            <th>Giá vé</th>
            <th>Trạng thái vé</th>
        </tr>
        <% if (tickets != null && !tickets.isEmpty()) {
            for (RailwayDTO ticket : tickets) { %>
                <tr>
                    <td><%= ticket.getTicketID() %></td>
<!--                    <td><%= ticket.getTripType() %></td>  ✅ Hiển thị "Chuyến đi" hoặc "Chuyến về" -->
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
            <tr><td colspan="9">Không có vé nào.</td></tr>
        <% } %>
    </table>
</body>
</html>