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
    String admin = String.valueOf(session.getAttribute("admin"));
    if (admin == null || "".equals(admin)) {
        return;
    }
    String index = String.valueOf(request.getAttribute("index"));
    if (index == null || "".equals(index)) {
        index = "1";
    }
    int count = HttpService.getUserNumber();
    int maxPage = count/20;
    if (maxPage*20>count || maxPage == 0) {
        maxPage+=1;
    }
    if (Integer.parseInt(index) > maxPage) {
        index = String.valueOf(maxPage);
    }
    List<UserModel> userModels = HttpService.getUsers(index);
    if (userModels == null) {
        return;
    }
    String url_noindex = "http://localhost:9090/admin/admin";
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
            if (userModels != null && !userModels.isEmpty()) {
        %>
            <div class="sort_tab">
                <table width="800px" style="text-align: center;margin: 0 auto" border="1px" cellspacing="0">
                        <tr style="height: 30px">
                        <th bgcolor="#ffffff">用户ID</th>
                        <th bgcolor="#ffffff">账户名</th>
                        <th bgcolor="#ffffff">昵称</th>
                        <th bgcolor="#ffffff">手机号</th>
                        <th bgcolor="#ffffff"></th>
                        </tr>
                    <%
                        for (UserModel userModel : userModels) {
                            %>
                        <tr style="height: 30px">
                            <td bgcolor="#ffffff"><%=userModel.getUid()%></td>
                            <td bgcolor="#ffffff"><%=userModel.getAccount()%></td>
                            <td bgcolor="#ffffff"><%=userModel.getUname()%></td>
                            <td bgcolor="#ffffff"><%=userModel.getPhone()%></td>
                            <td bgcolor="#ffffff"><a style="color:#5a5a5a"
                                                     href="http://localhost:9090/admin/userdetail<%="?user="+userModel.getUid()%>" class="tab1">查看详情</a></td>
                        </tr>
                    <%
                        }
                    %>

                </table>
        </div>
        <%
            }
        %>

    </div>

    <div class="nav_tab">
        <table align="center" style="margin: 0 auto; width: auto">
            <tbody>
            <tr>
                <td>
                    <%if (!"1".equals(index)) {%>
                    <a href=<%=url_noindex + "&index=" + String.valueOf(Integer.parseInt(index) - 1)%>>上一页</a>
                    <%} else {%>
                    <a href="javascript:return false;">上一页 </a>
                    <%}%>


                    <%if (Integer.parseInt(index) - 4 < 0) {%>

                    <%
                        if (maxPage > 5) { %>
                    <a class="nav1" href=<%=url_noindex + "&index=1"%>>1</a>
                    <a class="nav2" href=<%=url_noindex + "&index=2"%>>2</a>
                    <a class="nav3" href=<%=url_noindex + "&index=3"%>>3</a>
                    <a class="nav4" href=<%=url_noindex + "&index=4"%>>4</a>
                    <a class="nav5" href=<%=url_noindex + "&index=5"%>>5</a>
                    <%
                        if (maxPage == 6) {
                    %>
                    <a class="nav6" href=<%=url_noindex + "&index=6"%>>6</a>
                    <%
                    } else {
                    %>
                    <a href="">...</a>
                    <a class=<%="nav" + maxPage%> href=<%=url_noindex + "&index=" + maxPage%>><%=maxPage%>
                    </a>
                    <%
                        }
                    } else {
                        for (int i = 1; i<=maxPage; i ++) { %>
                    <a class="nav<%=i%>" href=<%=url_noindex + "&index=" + i%>>i</a>
                    <%
                            }
                        }
                    } else {
                        //                            在中间，需要首页和尾页
                        //首页
                    %>
                    <a class="nav1" href=<%=url_noindex + "&index=1"%>>1</a>
                    <% if (Integer.parseInt(index) != 4) { %>
                    <a href="">...</a>

                    <% }     //尾页


                        if (maxPage >= Integer.parseInt(index) + 2) {
                            if (maxPage != Integer.parseInt(index) + 3 && maxPage != Integer.parseInt(index) + 2) {
                                for (int i = Integer.parseInt(index) - 2; i <= Integer.parseInt(index) + 2; i++) {
                    %>
                    <a class=<%="nav" + i%> href=<%=url_noindex + "&index=" + i%>><%=i%>
                    </a>
                    <% } %>
                    <a href="">...</a>
                    <a class=<%="nav" + maxPage%> href=<%=url_noindex + "&index=" + maxPage%>><%=maxPage%>
                    </a>
                    <%

                    } else {
                        for (int i = Integer.parseInt(index) - 2; i <= maxPage; i++) {
                    %>
                    <a class=<%="nav" + i%> href=<%=url_noindex + "&index=" + i%>><%=i%>
                    </a>
                    <% }
                    }
                    } else {
                        for (int i = maxPage - 4; i <= maxPage; i++) {
                    %>
                    <a class=<%="nav" + i%> href=<%=url_noindex + "&index=" + i%>><%=i%>
                    </a>
                    <% }
                    }
                    }%>


                    <%if (maxPage> Integer.parseInt(index)) {%>
                    <a href=<%=url_noindex + "&index=" + String.valueOf(Integer.parseInt(index) + 1)%>>下一页</a>
                    <%} else {%>
                    <a href="javascript:return false;">下一页 </a>
                    <%}%>
                </td>
            </tr>
            </tbody>
        </table>
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

    function checkName() {
        var name = document.getElementById("name").value;
        if (name === "") {
            return false;
        }
        return true;
    }

</script>
</html>
