<%@ page import="com.binqing.parity.Model.GoodsListModel" %>
<%@ page import="com.binqing.parity.Model.GoodsModel" %>
<%@ page import="com.binqing.parity.Service.HttpService" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.util.Comparator" %>
<%@ page import="java.util.List" %><%--
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
    <link rel="stylesheet" href="css/style.css" type="text/css" />
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

    a{
        text-decoration: none;
    }

    a:link, a:visited {
        color: #5a5a5a;
    }

    a:hover, a:active{
        color: #E62652;
    }

    table  tr th{

        font-weight:500;

        font-size:14px

    }
</style>

<body>
<div class="all">
    <div class="content_guide">
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
    </div>

    <%
        String name = (String) request.getAttribute("name");
        String index = (String) request.getAttribute("page");
        String sort = (String) request.getAttribute("sort");
        if (index == null || "".equals(index)) {
            index = "0";
        }
        if (sort == null || "".equals(sort)) {
            sort = "0";
        }
        GoodsListModel goodsListModel = HttpService.getGoods(name, index, sort);
        List<GoodsModel> goodsList = new ArrayList<>();
        if (goodsListModel.getGoodsModelList() != null && !goodsListModel.getGoodsModelList().isEmpty()) {
            goodsList.addAll(goodsListModel.getGoodsModelList());
        }
        switch (sort){
            case "1":
                Collections.sort(goodsList, new Comparator<GoodsModel>() {
                    @Override
                    public int compare(GoodsModel o1, GoodsModel o2) {
                        return o2.getSalecomment() - o1.getSalecomment();
                    }
                });
                break;
            case "2":
                Collections.sort(goodsList, new Comparator<GoodsModel>() {
                    @Override
                    public int compare(GoodsModel o1, GoodsModel o2) {
                        double d = Double.parseDouble(o1.getPrice()) - Double.parseDouble(o2.getPrice());
                        return (int) (d * 100);
                    }
                });
                break;
            case "3":
                Collections.sort(goodsList, new Comparator<GoodsModel>() {
                    @Override
                    public int compare(GoodsModel o1, GoodsModel o2) {
                        double d = Double.parseDouble(o2.getPrice()) - Double.parseDouble(o1.getPrice());
                        return (int) (d * 100);
                    }
                });
                break;
            default:
                Collections.sort(goodsList, new Comparator<GoodsModel>() {
                    @Override
                    public int compare(GoodsModel o1, GoodsModel o2) {
                        return (int) (o2.getScore() - o1.getScore());
                    }
                });
                break;
        }


        String url_default = "/search?name="+name+"&page="+index+"&sort=0";
        String url_sale_comment = "/search?name="+name+"&page="+index+"&sort=1";
        String url_price_asc = "/search?name="+name+"&page="+index+"&sort=2";
        String url_price_desc = "/search?name="+name+"&page="+index+"&sort=3";
    %>
    <div class="search_content">
        <div class="searchbox">
            <img class="image1" src="img/baidu.png" height="129" width="270"/>
            <form action = "/search" onsubmit="return checkName()">
                <input type="text" class="shuru" id = "name" name = "name"/>
                <input type="submit" class="ok" value="百度一下"  style="cursor: pointer"  >
            </form>
        </div>
    </div>

    <div class="container">
        <div class="item_content">
            <div class="sort_tab">
                <table width="300px" border="1px" cellspacing="0">
                    <tr>
                        <th bgcolor="<%="0".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a style="color: <%="0".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_default%>" class="tab1">默认排序</a></th>
                        <th bgcolor="<%="1".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a style="color: <%="1".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_sale_comment%>" class="tab2" title="按销量从多到少排序">销量</a></th>
                        <th bgcolor="<%="2".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a style="color: <%="2".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_price_asc%>" class="tab3" title="按价格从低到高排序">价格(升序)</a>
                        <th bgcolor="<%="3".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a style="color: <%="3".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_price_desc%>" class="tab4" title="按价格从高到低排序">价格(降序)</a></th>
                    </tr>
                </table>

            </div>
            <%
                for(GoodsModel goodsModel : goodsList) {
                    boolean over = false;
                    int sc = goodsModel.getSalecomment();
                    double saleOrComment = sc;
                    if (sc >= 10000) {
                        saleOrComment/=10000;
                        over = true;
                    }
                    String saleComment = String.valueOf(sc);
                    if (over) {
                        saleComment = String.valueOf(saleOrComment);
                        saleComment += "万";
                    }
            %>
            <div class="item_goods">
                <div class="pic">
                    <a  target="_blank" href="<%=goodsModel.getHref()%>" >
                        <img  class="product" data-img="1"
                             src="<%=goodsModel.getImage()%>" />
                    </a>
                </div>

                <div class = "title">
                    <a style="font-size: 18px"  target="_blank" href="<%=goodsModel.getHref()%>" >
                        <%=goodsModel.getName()%>
                    </a>
                </div>

                <div class= "price">
                    <p style="color: #cc0000;font-size:22px;font-weight: bold"><%=goodsModel.getPrice()%></p>
                </div>

                <div class="sale_comment" >
                    <p class="text_sale_comment" >有
                        <a href="<%=goodsModel.getHref()%>" ><%=saleComment%></a>人<%=goodsModel.getType() == 0?"评论":"收货"%></p>
                </div>

                <div class="shop" >
                    <p class="shop_title" ><%=goodsModel.getShop()%></p>
                    <img class = "icon" src="<%=goodsModel.getType() == 0?"img/jd.png":"img/tb.png"%>">
                    <p class="shop_origin" ><%=goodsModel.getType() == 0?"京东商城":"淘宝商城"%></p>

                </div>

            </div>

            <div style="height:1px;width:auto;border-top:1px solid #ccc;"></div>
            <%
                }%>
        </div>

        <div class = "nav_tab">
            <% if (!"0".equals(index)) {%>
            <a class="nav_before" href="<%="/search?name="+name+"&page="+(Integer.parseInt(index)-1)+"&sort="+sort%>">上一页</a>
            <%}%>
            <p class="current">当前第<%=Integer.parseInt(index)+1%>页</p>
            <a class="nav_after" href="<%="/search?name="+name+"&page="+(Integer.parseInt(index)+1)+"&sort="+sort%>">下一页</a>
        </div>

    </div>

    </div>
</body>
<script>
    function checkName() {
        var name = document.getElementById("name").value;
        if(name === ""){
            return false;
        }
        return true;
    }

</script>
</html>
