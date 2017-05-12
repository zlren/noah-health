<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>admin！</title>
</head>
<body>
<div style="text-align: center;">
    <h1>user.jsp !</h1>
</div>

<div>
    <shiro:hasRole name="ADMIN">
        这句话只有角色为ADMIN的用户可以看到<br>
        用户[<shiro:principal property="username"/>]的角色是ADMIN<br>
    </shiro:hasRole>
</div>

<div>
    <shiro:hasRole name="USER_1">
        这句话只有角色为USER_1的用户可以看到<br>
        用户[<shiro:principal property="username"/>]的角色是USER1<br>
    </shiro:hasRole>
</div>

<div>
    <c:url value="/logout.action" var="logout_url"/>
    <li><a href="${logout_url}">退出</a></li>
</div>


<li><a href="${pageContext.request.contextPath}/member/search/1.action">查找id为1的member（这个操作只有ADMIN可以执行）</a></li>

</body>
</html>
