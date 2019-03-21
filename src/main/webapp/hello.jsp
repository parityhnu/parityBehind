<%@ page import="com.binqing.parity.Controller.UserController" %>
<%@ page import="com.binqing.parity.Model.UserModel" %><%--
  Created by IntelliJ IDEA.
  User: Lenovo
  Date: 2019/2/2
  Time: 10:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"  language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<style type="text/css">

    body{
        background: linear-gradient(120deg, #fdfbfb 0%, #ebedee 100%);
        height: 100%;
        width: 100%;
    }

    *{
        padding: 0;
        margin: 0;
    }

    a{
        text-decoration: none;
    }

    a:link, a:visited {
        color: #5a5a5a;
    }

    a:hover, a:active{
        color: #E62652;
    }

    .all .guide a{
        font-size: smaller ;
        font-weight:bold ;
    }

    .all .guide a:link, .all .guide a:active{
        color: black;
    }

    .all{
        width: 100%;
    }

    .all .content_guide{
        height: 25px;
        -webkit-box-shadow: #E0E0E0 0px 0px 1px;
        -moz-box-shadow: #E0E0E0 0px 0px 1px;
        box-shadow: #E0E0E0 0px 0px 1px;
    }

    .all .content_guide .guide{
        position: absolute;
        top: 0;
        right: 30px;
    }


    .all .searchbox{
        height:350px;
        width: 640px;
        margin: auto;
        position: absolute;
        top: 0;
        bottom: 0;
        left: 0;
        right: 0;
    }

    .all .searchbox .shuru{
        width: 539px;
        height: 36px;
        position: absolute;
        left:0
    }

    .all .searchbox .ok{
        width: 100px;
        height: 36px;
        color: white;
        background: #b20a2c;
        border: 0;
        font-size:medium;
        position: absolute;
        left: 541px;
        text-align:center
    }

</style>

<body>
<div class="all">
    <div class="content_guide">
        <div class="guide">
            <a href="<%=session.getAttribute("user") == null ? "/login" : "/modify"%>"><%=session.getAttribute("user") == null ? "请登录" : "欢迎您," + session.getAttribute("user")%></a>  
            <% if (session.getAttribute("user") != null) {%>
            <a href="/signout">退出账户</a>
            <% }%>
            <a href="<%=session.getAttribute("user") == null ? "/login" : ""%>">我的收藏</a>
        </div>
    </div>
    <div class="searchbox" align="center">
        <img class="image1" src="img/title.png" height="129" width="270"/>
        <form action = "/search" onsubmit="return checkName()">
            <input type="text" class="shuru" id = "name" name = "name"/>
            <input type="submit" class="ok" value="比价吧"  style="cursor: pointer"  >
        </form>
    </div>
    

    </div>
</body>
<script>
    function checkName() {
        var name = document.getElementById("name").value;
        if(name == ""){
            return false;
        }
        return true;
    }
</script>
</html>
