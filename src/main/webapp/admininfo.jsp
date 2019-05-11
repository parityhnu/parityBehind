<%@ page import="com.binqing.parity.Model.InformationModel" %>
<%@ page import="com.binqing.parity.Service.HttpService" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.binqing.parity.Model.String_CountModel" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="com.binqing.parity.Consts.TimeConsts" %>
<%@ page import="java.text.SimpleDateFormat" %><%--
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
<script src = "../js/Chart.js"></script>
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

    table {
        text-align: center;
        border-color: #000;
        table-layout:fixed;
        word-break:break-all; word-wrap:break-all;
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

    canvas{
        display: block;
        width: 400px;
        height: 400px;
    }

    div .canvas{
        height: 400px;
        width: 400px;
        margin: 0 auto;
    }

    div .infor_content {
        text-align: center;
        margin-top: 80px;
        height: 450px;
        min-width: 850px;
    }

    div .floatdiv {
        width: 50%;
        float: left;
    }

</style>

<body>

<%
    String admin = String.valueOf(session.getAttribute("admin"));
    if (admin == null || "".equals(admin)) {
        return;
    }
    InformationModel informationModel = HttpService.getInformation();
    if (informationModel == null) {
        return;
    }
    long currentTime = System.currentTimeMillis();
    currentTime = currentTime - currentTime % TimeConsts.MILLS_OF_ONE_DAY;
    Map<Date, Integer> activeUserWeek = informationModel.getActiveUserWeek();
    StringBuilder activeUserWeekData = new StringBuilder();
    StringBuilder activeUserWeekLabel = new StringBuilder();
    Map<Date, Integer> searchWeek =informationModel.getSearchWeek();
    StringBuilder searchWeekData = new StringBuilder();
    StringBuilder searchWeekLabel = new StringBuilder();

    for (int i = 0 ; i < 7 ; i ++) {
        Date date = new Date(currentTime - TimeConsts.MILLS_OF_ONE_DAY * i - TimeConsts.MILLS_OF_ONE_HOUR * 8);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        activeUserWeekLabel.append("\"").append(format.format(date)).append("\"").append(",");
        searchWeekLabel.append("\"").append(format.format(date)).append("\"").append(",");

        if (activeUserWeek == null || activeUserWeek.get(date) == null) {
            activeUserWeekData.append(0).append(",");
        } else {
            activeUserWeekData.append(activeUserWeek.get(date)).append(",");
        }

        if (searchWeek == null || searchWeek.get(date) == null) {
            searchWeekData.append(0).append(",");
        } else {
            searchWeekData.append(searchWeek.get(date)).append(",");
        }
    }

    activeUserWeekLabel.deleteCharAt(activeUserWeekLabel.length()-1);
    activeUserWeekData.deleteCharAt(activeUserWeekData.length()-1);
    searchWeekLabel.deleteCharAt(searchWeekLabel.length()-1);
    searchWeekData.deleteCharAt(searchWeekData.length()-1);

    List<String_CountModel> keywordAll = informationModel.getKeywordAll();
    StringBuilder keywordAllData = new StringBuilder();
    StringBuilder keywordAllLabel = new StringBuilder();
    if (keywordAll != null) {
        for (String_CountModel string_countModel : keywordAll) {
            if (string_countModel == null) {
                continue;
            }
            keywordAllLabel.append("\"").append(string_countModel.getString()).append("\"").append(",");
            keywordAllData.append(string_countModel.getCount()).append(",");
        }
        keywordAllLabel.deleteCharAt(keywordAllLabel.length()-1);
        keywordAllData.deleteCharAt(keywordAllData.length()-1);
    }

    List<String_CountModel> keywordWeek = informationModel.getKeywordWeek();
    StringBuilder keywordWeekData = new StringBuilder();
    StringBuilder keywordWeekLabel  = new StringBuilder();
    if (keywordWeek != null) {
        for (String_CountModel string_countModel : keywordWeek) {
            if (string_countModel == null) {
                continue;
            }
            keywordWeekLabel.append("\"").append(string_countModel.getString()).append("\"").append(",");
            keywordWeekData.append(string_countModel.getCount()).append(",");
        }
        keywordWeekLabel.deleteCharAt(keywordWeekLabel.length()-1);
        keywordWeekData.deleteCharAt(keywordWeekData.length()-1);
    }

    List<String_CountModel> sortAll = informationModel.getSortAll();
    StringBuilder sortAllData = new StringBuilder();
    StringBuilder sortAllLabel = new StringBuilder();
    if (sortAll != null) {
        for (String_CountModel string_countModel : sortAll) {
            if (string_countModel == null) {
                continue;
            }
            sortAllLabel.append("\"").append(string_countModel.getSort()).append("\"").append(",");
            sortAllData.append(string_countModel.getCount()).append(",");
        }
        sortAllLabel.deleteCharAt(sortAllLabel.length()-1);
        sortAllData.deleteCharAt(sortAllData.length()-1);
    }

    List<String_CountModel> sortWeek = informationModel.getSortWeek();
    StringBuilder sortWeekData = new StringBuilder();
    StringBuilder sortWeekLabel = new StringBuilder();
    if (sortWeek != null) {
        for (String_CountModel string_countModel : sortWeek) {
            if (string_countModel == null) {
                continue;
            }
            sortWeekLabel.append("\"").append(string_countModel.getSort()).append("\"").append(",");
            sortWeekData.append(string_countModel.getCount()).append(",");
        }
        sortWeekLabel.deleteCharAt(sortWeekLabel.length()-1);
        sortWeekData.deleteCharAt(sortWeekData.length()-1);
    }

    int favoriteData = informationModel.getFavoriteCount();
    String favoriteLabel = "收藏功能使用数";

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


    <div class="infor_content">
        <div class="floatdiv">
            <font style="font: bold; font-size: large">过去一周的活跃用户数</font>
            <div class="canvas">
                <canvas id="activeUserWeek" width="100px" height="100px"></canvas>
            </div>
        </div>

        <div class="floatdiv">
            <font style="font: bold; font-size: large">过去一周的搜索量</font>
            <div class="canvas">
                <canvas id="searchWeek" width="100px" height="100px"></canvas>
            </div>
        </div>
    </div>

    <div class="infor_content">
        <font style="font: bold; font-size: large">收藏功能的使用量</font>
        <div class="canvas">
            <canvas id="favoriteCount" width="100px" height="100px"></canvas>
        </div>
    </div>


    <div class="infor_content">
        <div class="floatdiv">
            <font style="font: bold; font-size: large">过去一周的搜索关键字分布</font>
            <div class="canvas">
                <canvas id="keywordWeek" width="100px" height="100px"></canvas>
            </div>
        </div>

        <div class="floatdiv">
            <font style="font: bold; font-size: large">搜索关键字总量</font>
            <div class="canvas">
                <canvas id="keywordAll" width="100px" height="100px"></canvas>
            </div>
        </div>
    </div>

    <div class="infor_content">
        <div class="floatdiv">
            <font style="font: bold; font-size: large">过去一周的搜索排序方式分布</font>
            <div class="canvas">
                <canvas id="sortWeek" width="100px" height="100px"></canvas>
            </div>
        </div>

        <div class="floatdiv">
            <font style="font: bold; font-size: large">搜索排序方式总量</font>
            <div class="canvas">
                <canvas id="sortAll" width="100px" height="100px"></canvas>
            </div>
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
    var activeUserWeek = document.getElementById("activeUserWeek").getContext("2d");
    var activeUserWeekBar = new Chart(activeUserWeek, {
        type: 'bar',
        data: {
            labels:[<%=activeUserWeekLabel.toString()%>],
            datasets: [{
                label :"过去一周的活跃用户数",
                data: [<%=activeUserWeekData.toString()%>,0],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)',
                    'rgba(255, 159, 64, 0.6)',
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)'
                ]
            }],
            options: {
                responsive: true
            }
        }
    });

   var popCanvas = document.getElementById("searchWeek").getContext("2d");
   var barChart = new Chart(popCanvas, {
        type: 'bar',
        data: {
            labels:[<%=searchWeekLabel.toString()%>],
            datasets: [{
                label :"过去一周的搜索量",
                data: [<%=searchWeekData.toString()%>,0],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.6)',
                    'rgba(54, 162, 235, 0.6)',
                    'rgba(153, 102, 255, 0.6)',
                    'rgba(255, 159, 64, 0.6)',
                    'rgba(255, 206, 86, 0.6)',
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)'
                ]
            }]
        }
    });

   popCanvas = document.getElementById("favoriteCount").getContext("2d");
    barChart = new Chart(popCanvas, {
        type: 'bar',
        data: {
            labels:["<%=favoriteLabel%>"],
            datasets: [{
                label :"收藏功能的使用量",
                data: [<%=favoriteData%>,0],
                backgroundColor: [
                    'rgba(75, 192, 192, 0.6)',
                    'rgba(153, 102, 255, 0.6)'
                ]
            }]
        }
    });


    popCanvas = document.getElementById("keywordAll").getContext("2d");
    barChart = new Chart(popCanvas, {
        type: 'pie',
        data: {
            labels:[<%=keywordAllLabel.toString()%>],
            datasets: [{
                data: [<%=keywordAllData.toString()%>],
                backgroundColor: [
                    '#73f8dd',
                    '#2bc4ca',
                    '#1872a4',
                    '#2e3988',
                    '#73f8dd',
                    '#2bc4ca',
                    '#1872a4',
                    '#2e3988',
                    '#73f8dd',
                    '#2bc4ca'
                ]
            }]
        }
    });

    popCanvas = document.getElementById("keywordWeek").getContext("2d");
    barChart = new Chart(popCanvas, {
        type: 'pie',
        data: {
            labels:[<%=keywordWeekLabel.toString()%>],
            datasets: [{

                data: [<%=keywordWeekData.toString()%>],
                backgroundColor: [
                    '#45a298',
                    '#d8f8b7',
                    '#3d6272',
                    '#3c899b',
                    '#45a298',
                    '#d8f8b7',
                    '#3d6272',
                    '#3c899b',
                    '#45a298',
                    '#d8f8b7'
                ]
            }]
        }
    });

    popCanvas = document.getElementById("sortAll").getContext("2d");
    barChart = new Chart(popCanvas, {
        type: 'pie',
        data: {
            labels:[<%=sortAllLabel.toString()%>],
            datasets: [{

                data: [<%=sortAllData.toString()%>],
                backgroundColor: [
                    'rgba(255, 99, 132, 0.2)',
                    'rgba(54, 162, 235, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)',
                ]
            }]
        }
    });

    popCanvas = document.getElementById("sortWeek").getContext("2d");
    barChart = new Chart(popCanvas, {
        type: 'pie',
        data: {
            labels:[<%=sortWeekLabel.toString()%>],
            datasets: [{

                data: [<%=sortWeekData.toString()%>],
                backgroundColor: [
                    'rgba(153, 102, 255, 0.2)',
                    'rgba(255, 159, 64, 0.2)',
                    'rgba(255, 206, 86, 0.2)',
                    'rgba(75, 192, 192, 0.2)'
                ]
            }]
        }
    });


</script>
</html>
