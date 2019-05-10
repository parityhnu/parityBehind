<%@ page import="com.binqing.parity.Model.ConfigModel" %>
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

        font-size: 18px
    }
    table td {
        text-align: center;

        font-size: 20px;
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
    String admin = String.valueOf(session.getAttribute("admin"));
    if (admin == null || "".equals(admin)) {
        return;
    }
    ConfigModel configModel = HttpService.getConfig();
    if (configModel == null) {
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
        <div class="sort_tab">
            <table style="text-align: center;margin: 0 auto" width="300px" border="1px" cellspacing="0">
                <tr>
                    <th bgcolor="#ffffff">爬虫状态开关</th>
                    <th bgcolor="#ffffff"></th>
                </tr>
                <tr>
                    <td bgcolor="#ffffff">京东商品爬取</td>
                    <td bgcolor="#ffffff"><a id="config_jd" name="<%=configModel.getJd()==1?0:1%>" style="color:#5a5a5a" onclick="configjd()" href="javascript:void(0)"
                            class="tab1"><%=configModel.getJd()==1?"开启":"关闭"%></a></td>
                </tr>
                <tr>
                    <td bgcolor="#ffffff">淘宝商品爬取</td>
                    <td bgcolor="#ffffff"><a id="config_tb" name="<%=configModel.getTb()==1?0:1%>"
                                             style="color:#5a5a5a" onclick="configtb()" href="javascript:void(0)"
                            class="tab1"><%=configModel.getTb()==1?"开启":"关闭"%></a></td>
                </tr>
            </table>

        </div>

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

    function configjd() {
        var request = new XMLHttpRequest();
        var url = "http://localhost:9090/controller/admin/configjd";
        var signal = document.getElementById("config_jd").name;
        console.log(signal);
        request.open("post", url, true);
        var data = new FormData();
        data.append("signal", signal);
        request.onreadystatechange = function() {
            if (this.readyState == 4) {
                var json = JSON.parse(this.responseText);
                if (json.string == signal) {
                    console.log(json.string);
                    if (json.string == '1') {
                        document.getElementById("config_jd").name = "0";
                        document.getElementById("config_jd").innerText = "开启";
                    } else {
                        document.getElementById("config_jd").name = "1";
                        document.getElementById("config_jd").innerText = "关闭";
                    }
                }
            }
        };
        request.send(data)
        return false;
    }

    function configtb() {
        var request = new XMLHttpRequest();
        var url = "http://localhost:9090/controller/admin/configtb";
        var signal = document.getElementById("config_tb").name;
        request.open("post", url, true);
        var data = new FormData();
        data.append("signal", signal);
        request.onreadystatechange = function() {
            if (this.readyState == 4) {
                var json = JSON.parse(this.responseText);
                if (json.string == signal) {
                    if (json.string == '1') {
                        document.getElementById("config_tb").name = "0";
                        document.getElementById("config_tb").innerText = "开启";
                    } else {
                        document.getElementById("config_tb").name = "1";
                        document.getElementById("config_tb").innerText = "关闭";
                    }
                }
            }
        };
        request.send(data)
        return false;
    }

</script>
</html>