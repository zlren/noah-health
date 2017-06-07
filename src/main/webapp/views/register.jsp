<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>注册页面</title>
</head>
<body>
<h3>注册页面</h3>
<div style="text-align: center;">

    <form id="code_form" action="${pageContext.request.contextPath}/api/auth/send_sms" method="post">
        <div>
            <label>手机号码</label>
            <div>
                <input type="text" id="phoneNumber" name="phoneNumber">
            </div>
        </div>
        <%--<div>--%>
            <%--<label>密码</label>--%>
            <%--<div>--%>
                <%--<input type="text" id="inputCode" name="inputCode">--%>
            <%--</div>--%>
        <%--</div>--%>
        <div>
            <div>
                <button type="submit">发送验证码</button>
            </div>
        </div>
    </form>


</div>
</body>
</html>