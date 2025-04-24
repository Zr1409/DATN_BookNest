<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Kiểm tra loại tam giác</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css">
</head>
<body>
<div class="container">
    <h2 class="mt-5">Nhập 3 cạnh của tam giác</h2>
    <form action="/checktamgiac" method="post">
        <div class="form-group">
            <label for="a">Cạnh a:</label>
            <input type="number" class="form-control" id="a" name="a" step="any" required>
        </div>
        <div class="form-group">
            <label for="b">Cạnh b:</label>
            <input type="number" class="form-control" id="b" name="b" step="any" required>
        </div>
        <div class="form-group">
            <label for="c">Cạnh c:</label>
            <input type="number" class="form-control" id="c" name="c" step="any" required>
        </div>
        <button type="submit" class="btn btn-primary">Kiểm tra loại tam giác</button>
        <a href="${pageContext.request.contextPath}/tamgiac" class="btn btn-secondary">Nhập lại</a>
    </form>
</div>
</body>
</html>
