<%@ page import="com.binqing.parity.Model.GoodsListModel" %>
<%@ page import="java.util.List" %>
<%@ page import="com.binqing.parity.Model.TBModel" %>
<%--
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
        height: 100%;
        width: 100%;
    }

    *{
        padding: 0;
        margin: 0;
    }
    .all .guide a{
        font-family: "微软雅黑";
        font-size: smaller ;
        color: black;
        font-weight:bold ;
    }

    .all .guide a:hover{
        color: blue;
    }

    .all{
        width: 100%;
    }

    .all .guide{
        position: absolute;
        top: 0;
        right: 0;
        width:450px;
    }

    .all .searchbox{
        height:130px;
        width: 950px;
        margin: auto;
        position: relative;
        top: 0;
        left: 0;
        right: 0;
    }

    .all .guide .product{
        background:cornflowerblue;
        display: inline-block;
        height: 20px;
        width: 58px;

    }

    .all .guide .product a{
        color: white;
        text-decoration: none;

    }

    .all .searchbox .image1{
        position: absolute;
        left: 0
    }

    .all .searchbox .shuru{
        width: 339px;
        height: 36px;
        position: absolute;
        left:300px;
        top:60%;
    }

    .all .searchbox .ok{
        width: 100px;
        height: 36px;
        color: white;
        background: #317ef3;
        border: 0;
        font-size:medium;
        position: absolute;
        left: 641px;
        top:60%;
        text-align:center
    }

    .all .container{
        background: #E0E0E0;
        position: relative;
        top: 10px;
    }

    .all .container .box
    {
        box-sizing:border-box;
        -moz-box-sizing:border-box; /* Firefox */
        width:25%;
        border:1em solid #E0E0E0;
        float:left;
    }

</style>

<body>
<div class="all">
    <div class="guide">
        <a href="https://www.baidu.com">新闻</a>  
        <a href="">hao123</a>  
        <a href="">地图</a>  
        <a href="">视频</a>  
        <a href="">贴吧</a>  
        <a href="">学术</a>  
        <a href="">登录</a>  
        <a href="">设置</a>  
        <span class="product"><a href="">更多产品</a></span>
    </div>
    <% List<GoodsListModel> goodsListModelList = (List<GoodsListModel>) request.getAttribute("goodsList");%>
    <div class="searchbox">
        <img class="image1" src="img/baidu.png" height="129" width="270"/>
        <form action = "ip/searchandredict" onsubmit="return checkName()">
            <input type="text" class="shuru" id = "name" name = "name"/>
            <input type="submit" class="ok" value="百度一下"  style="cursor: pointer"  >
        </form>
    </div>

    <div class="container">
        <% for(GoodsListModel goodsListModel : goodsListModelList) {
            %>
                <div class="box">
                    <a target="_blank" href="<%=goodsListModel.getHref()%>" >
                        <img width="220" height="220" class="err-product" data-img="1"
                             src="<%=goodsListModel.getImage()%>" />
                        <a/>
                    <p><%=goodsListModel.getName()%></p>

                </div>
            <%
        }%>
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
