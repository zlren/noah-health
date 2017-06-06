<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>登录首页</title>
</head>
<body>

<div style="text-align: center;">
    <form id="login_form" action="${pageContext.request.contextPath}/api/auth/login" method="post">
        <div>
            <label>用户名</label>
            <div>
                <input type="text" id="username" name="username">
            </div>
        </div>
        <div>
            <label>密码</label>
            <div>
                <input type="password" id="password" name="password">
            </div>
        </div>
        <div>
            <div>
                <button type="submit">登录</button>
            </div>
        </div>
    </form>
</div>
</body>
</html>