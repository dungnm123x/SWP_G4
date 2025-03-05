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
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f4f4f4;
        }

        h1 {
            color: #333;
            text-align: center;
        }

        form {
            width: 400px;
            margin: 20px auto;
            padding: 20px;
            border: 1px solid #ddd;
            background-color: #fff;
            border-radius: 5px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        input[type="text"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }

        button[type="submit"], a {
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            margin-right: 10px;
        }

        button[type="submit"] {
            background-color: #4CAF50;
            color: white;
        }

        a {
            background-color: #f44336;
            color: white;
        }

        button[type="submit"]:hover {
            background-color: #45a049;
        }

        a:hover {
            background-color: #d32f2f;
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
