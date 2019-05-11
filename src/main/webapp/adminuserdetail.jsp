<%@ page import="com.binqing.parity.Model.UserModel" %>
<%@ page import="java.util.List" %>
<%@ page import="com.binqing.parity.Service.HttpService" %><%--
=======
<%@ page import="java.util.List" %>
<%--
>>>>>>> master
  Created by IntelliJ IDEA.
  User: Lenovo
  Date: 2019/2/2
  Time: 10:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <title>比价网管理员系统</title>
    <link rel="stylesheet" href="../css/styleadmin.css" type="text/css"/>
</head>
<style type="text/css">
    body {
        background: linear-gradient(120deg, #fdfbfb 0%, #ebedee 100%);
        height: 100%;
        width: 100%;
    }

    * {
        padding: 0;
        margin: 0;
    }

    a {
        text-decoration: none;
    }

    a:link, a:visited {
        color: #5a5a5a;
    }

    a:hover, a:active {
        color: #E62652;
    }

    table tr th {
        font-weight: 500;

        font-size: 14px
    }
    table td {
        text-align: center;

        font-size: 16px;
    }

    button {
        display: inline-block;
        outline: none;
        cursor: pointer;
        text-align: center;
        text-decoration: none;
        font: 14px/100% Arial, Helvetica, sans-serif;
        padding: .5em 2em .55em;
        text-shadow: 0 1px 1px rgba(0,0,0,.3);
        -webkit-border-radius: .5em;
        -moz-border-radius: .5em;
        border-radius: .5em;
        -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.2);
        -moz-box-shadow: 0 1px 2px rgba(0,0,0,.2);
        box-shadow: 0 1px 2px rgba(0,0,0,.2);
    }

    button:hover {
        text-decoration: none;
    }
    button:active {
        position: relative;
        top: 1px;
    }

</style>

<body>

<%

    String user = String.valueOf(request.getAttribute("user"));
    if (user == null || "".equals(user)) {
        return;
    }
    UserModel userModel = HttpService.getUser(user);
    if (userModel == null) {
        return;
    }
%>
<div class="all">
    <div class="content_guide">
        <div class="guide">
            <a href="/admin/hello"><img class="image1" src="../img/title.png" height="95" width="200"/></a>
        </div>
        <div class="guide" style="margin-top: 25px">
            <a href="/admin/crawler"><button type="button">爬虫状态</button></a>
        </div>
        <%
            if ("0".equals(String.valueOf(session.getAttribute("admin")))) {
        %>
        <div class="guide" style="margin-top: 25px">
            <a href="/admin/admin"><button type="button">管理员管理</button></a>
        </div>
        <%
            }
        %>
        <div class="guide" style="margin-top: 25px">
            <a href="/admin/user"><button type="button">用户管理</button></a>
        </div>
        <div class="guide" style="margin-top: 25px">
            <a href="/admin/information"><button type="button">信息统计</button></a>
        </div>
    </div>

    <div class="feature_content">
        <%
            if (userModel != null) {
        %>
        <div class="sort_tab">
            <table width="400px"  style="height:200px;text-align: center;margin: 0 auto" border="1px" cellspacing="0">
                <tr>
                    <td bgcolor="#ffffff">用户ID</td>
                    <td bgcolor="#ffffff"><%=userModel.getUid()%></td>
                </tr>
                <tr>
                    <td bgcolor="#ffffff">账户名</td>
                    <td bgcolor="#ffffff"><%=userModel.getAccount()%></td>
                </tr>
                <tr>
                    <td bgcolor="#ffffff">昵称</td>
                    <td bgcolor="#ffffff"><%=userModel.getUname()%></td>
                </tr>
                <tr>
                    <td bgcolor="#ffffff">手机号</td>
                    <td bgcolor="#ffffff"><%=userModel.getPhone()%></td>
                </tr>

                <tr>
                    <td bgcolor="#ffffff">用户状态</td>
                    <td bgcolor="#ffffff"><a id="user_state" name="<%=userModel.getState()<0?0:1%>" style="color:#5a5a5a" onclick="userstate()" href="javascript:void(0)"
                                             class="tab1"><%=userModel.getState()<0 ?"解封该用户":"封禁该用户"%></a></td>
                </tr>
            </table>
        </div>
        <%
            }
        %>

    </div>



</div>
</body>
<script>
    var admin = "<%=session.getAttribute("admin")%>";
    if ("null" == admin) {
        window.location.href = "/hello";
    }
    if (!0 == admin && !1 == admin) {
        window.location.href = "/hello";
    }

    function userstate() {
        var request = new XMLHttpRequest();
        // 0说明当前的状态是被封禁，所以要解封
        var ban = document.getElementById("user_state").name;
        if ("0" == ban) {
            var url = "/admin/releaseuser";
            var account = "<%=userModel.getAccount()%>";
            request.open("post", url, true);
            var data = new FormData();
            data.append("account", account);
            request.onreadystatechange = function() {
                if (this.readyState == 4) {
                    var json = JSON.parse(this.responseText);
                    if (json.string == account) {
                        document.getElementById("user_state").name = "1";
                        document.getElementById("user_state").innerText = "封禁该用户";
                    }
                }
            };
            request.send(data)
        } else {
            var url = "/controller/admin/banuser";
            var account = "<%=userModel.getAccount()%>";
            request.open("post", url, true);
            var data = new FormData();
            data.append("account", account);
            request.onreadystatechange = function() {
                if (this.readyState == 4) {
                    var json = JSON.parse(this.responseText);
                    if (json.string == account) {
                        document.getElementById("user_state").name = "0";
                        document.getElementById("user_state").innerText = "解封该用户";
                    }
                }
            };
            request.send(data)
        }
        return false;
    }

</script>
</html>
