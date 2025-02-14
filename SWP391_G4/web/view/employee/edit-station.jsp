<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Station" %>
<%
    Station station = (Station) request.getAttribute("station");
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Sửa Ga</title>
        <style>
            body {
                font-family: 'Arial', sans-serif;
                background-color: #f4f7f9;
                color: #333;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                height: 100vh;
            }

            h1 {
                text-align: center;
                color: #003366;
                margin-bottom: 20px;
            }

            form {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
                width: 350px;
                display: flex;
                flex-direction: column;
            }

            label {
                font-weight: bold;
                margin-top: 10px;
            }

            input {
                padding: 10px;
                margin-top: 5px;
                border: 1px solid #ddd;
                border-radius: 5px;
                font-size: 16px;
                width: 100%;
            }

            button {
                background: #00509e;
                color: white;
                border: none;
                padding: 10px;
                border-radius: 5px;
                cursor: pointer;
                font-size: 16px;
                transition: 0.3s;
                margin-top: 15px;
            }

            button:hover {
                background: #003366;
            }

            a {
                text-align: center;
                display: block;
                margin-top: 10px;
                text-decoration: none;
                color: #00509e;
                font-weight: bold;
            }

            a:hover {
                color: #003366;
            }
        </style>
    </head>
    <body>
        <h1>Sửa Ga</h1>
        <form action="station" method="POST">
            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="stationID" value="<%= station.getStationID() %>">

            <label for="stationName">Tên Ga:</label>
            <input type="text" name="stationName" value="<%= station.getStationName() %>" required>

            <label for="address">Địa Chỉ:</label>
            <input type="text" name="address" value="<%= station.getAddress() %>" required>

            <button type="submit">Lưu</button>
            <a href="station">Hủy</a>
        </form>
    </body>
</html>
