<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Employee</title>
    <style>
    body {
        font-family: Arial, sans-serif;
    }

    h2 {
        text-align: center;
        color: #333;
    }

    form {
        width: 50%;
        margin: 0 auto;
        padding: 20px;
        border: 1px solid #ccc;
        border-radius: 10px;
        background-color: #f9f9f9;
    }

    label {
        display: block;
        margin: 10px 0 5px;
        font-weight: bold;
    }

    input[type="text"], select {
        width: 100%;
        padding: 8px;
        margin-bottom: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
    }

    button {
        padding: 10px 15px;
        margin-right: 10px;
        border: none;
        border-radius: 5px;
        background-color: #4CAF50;
        color: white;
        cursor: pointer;
    }

    button[type="button"] {
        background-color: #f44336;
    }

    button:hover {
        opacity: 0.8;
    }
</style>

</head>
<body>

<h2>Edit Employee</h2>

<form action="update" method="post">
    <input type="hidden" name="uid" value="${employee.id}">

    <label>Name:</label>
    <input type="text" name="name" value="${employee.name}" required>

    <label>Phone Number:</label>
    <input type="text" name="phoneNumber" value="${employee.phoneNumber}" required>

    <label>Address:</label>
    <input type="text" name="address" value="${employee.address}" required>

    <label>Department:</label>
    <select name="departmentId">
        <c:forEach var="department" items="${departments}">
            <option value="${department.id}" ${department.id == employee.department.id ? 'selected' : ''}>
                ${department.name}
            </option>
        </c:forEach>
    </select>

    <label>Salary Level:</label>
    <select name="salaryId">
        <c:forEach var="salary" items="${salaries}">
            <option value="${salary.id}" ${salary.id == employee.salary.id ? 'selected' : ''}>
                ${salary.amount}
            </option>
        </c:forEach>
    </select>

    <button type="submit">Update</button>
    <button type="button" onclick="window.location.href='view'">Cancel</button>
</form>

</body>
</html>
