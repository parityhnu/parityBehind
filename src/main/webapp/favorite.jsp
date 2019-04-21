<%@ page import="com.binqing.parity.Model.GoodsListModel" %>
<%@ page import="com.binqing.parity.Model.GoodsModel" %>
<%@ page import="com.binqing.parity.Model.ParityModel" %>
<%@ page import="com.binqing.parity.Service.HttpService" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.binqing.parity.Model.StringModel" %>
<%@ page import="org.attoparser.util.TextUtil" %>
<%@ page import="org.apache.http.util.TextUtils" %>
<%--
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
    <title>Title</title>
    <link rel="stylesheet" href="css/style.css" type="text/css"/>
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
</style>

<body>

<%
    int uid = (int) session.getAttribute("user");
    String user = String.valueOf(uid);
    String index = (String) request.getAttribute("page");
    if (user == null || "".equals(user)) {
        return;
    }
    if (index == null || TextUtils.isEmpty(index)) {
        index = "1";
    }

    List<ParityModel> parity = HttpService.getFavorites(user);
    if (parity == null) {
        parity = new ArrayList<>();
    }
    int size = parity.size();

    String url_noindex = "/favorite?page=";

    String url_present = "/favorite?page=" + index;

    int maxPage = size/20;
    if (maxPage * 20 < size) {
        maxPage += 1;
    }

    int index3 = Integer.parseInt(index);
    if (index3 > maxPage) {
        if (maxPage == 0) {
            index3 = 1;
        } else {
            index3 = maxPage;
        }
    }
    if (maxPage != 0) {
        if (index3 >= maxPage) {
            index3 = maxPage;
            parity = parity.subList((index3 - 1) * 20, size);
        } else {
            parity = parity.subList((index3 - 1) * 20, index3 * 20);
        }
    }

%>
<div class="all">
    <div class="content_guide">
        <div class="guide">
            <a href="<%=session.getAttribute("user") == null ? "/login?href="+url_present : "/modify?href="+url_present%>"><%=session.getAttribute("user") == null ? "请登录" : "欢迎您," + session.getAttribute("name")%>
            </a>
            <% if (session.getAttribute("user") != null) {%>
            <a href="<%="/signout?href=" + url_present%>">退出账户</a>
            <% }%>
            <a href="<%=session.getAttribute("user") == null ? "/login?href="+ url_present : "/favorite"%>">我的收藏</a>
        </div>
    </div>

    <div class="search_content">
        <div class="searchbox">
            <a href="/hello"><img class="image1" src="img/title.png" height="129" width="270"/></a>
            <form action="/search" onsubmit="return checkName()">
                <input type="text" class="shuru" id="name" name="name" placeholder="输入你要比价的商品名称"/>
                <input type="submit" class="ok" value="比价吧" style="cursor: pointer">
            </form>
        </div>
    </div>

    <div class="container">
        <div class="item_content">

            <div style="margin-top:4px;height:1px;width:auto;border-top:1px solid #ccc;"></div>


            <%
                if (parity.isEmpty()) {
                    return;
                }
                for (ParityModel parityModel : parity) {
                    boolean over = false;
                    int sc = parityModel.getSalecomment();
                    double saleOrComment = sc;
                    if (sc >= 10000) {
                        saleOrComment /= 10000;
                        over = true;
                    }
                    String saleComment = String.valueOf(sc);
                    if (over) {
                        saleComment = String.valueOf(saleOrComment);
                        saleComment += "万";
                    }
                    String img = "";
                    String root = "";
                    switch (parityModel.getType()) {
                        case 0:
                            img = "img/jd.png";
                            root = "京东商城";
                            break;
                        case 1:
                            img = "img/tb.png";
                            root = "淘宝网";
                            break;
                        case 2:
                            img = "img/tmall.jpg";
                            root = "天猫商城";
                            break;
                        default:
                            img = "img/jd.png";
                            root = "京东商城";
                            break;
                    }
                    StringBuilder href = new StringBuilder("/detail?ids=");

                    href.append(parityModel.getTypeGid())
                            .append("&name=")
                            .append(parityModel.getKeyword())
                            .append("&sort=")
                            .append(String.valueOf(parityModel.getSort()));
            %>
            <div class="item_goods">
                <div class="pic">
                    <a target="_blank" href="<%=href.toString()%>">
                        <img class="product" data-img="1"
                             src="<%=parityModel.getImage()%>"/>
                    </a>
                </div>

                <div class="title">
                    <a style="font-size: 18px" target="_blank" href="<%=href.toString()%>">
                        <%=parityModel.getName()%>
                    </a>
                </div>

                <div class="price">
                    <p style="color: #cc0000;font-size:22px;font-weight: bold">￥<%=parityModel.getPrice()%>
                    </p>
                </div>

                <div class="sale_comment">
                    <p class="text_sale_comment">有
                        <a href="<%=href.toString()%>"><%=saleComment%>
                        </a>人<%=parityModel.getType() == 0 ? "评论" : "收货"%>
                    </p>
                </div>

                <div class="shop">
                    <p class="shop_title"><%=parityModel.getShop()%>
                    </p>
                    <img class="icon" src="<%=img%>">
                    <p class="shop_origin"><%=root%>
                    </p>

                </div>

            </div>

            <div style="height:1px;width:auto;border-top:1px solid #ccc;"></div>
            <%
                }%>
        </div>

        <div class="nav_tab">
            <table align="center" style="margin: 0 auto; width: auto">
                <tbody>
                <tr>
                    <td>
                        <%if (!(1 == index3)) {%>
                        <a href=<%=url_noindex + (index3 - 2)%>>上一页</a>
                        <%} else {%>
                        <a href="javascript:return false;">上一页 </a>
                        <%}%>


                        <%if (index3 - 4 < 0) {%>

                        <%
                            if (maxPage > 5) { %>
                        <a class="nav1" href=<%=url_noindex + "0"%>>1</a>
                        <a class="nav2" href=<%=url_noindex + "1"%>>2</a>
                        <a class="nav3" href=<%=url_noindex + "2"%>>3</a>
                        <a class="nav4" href=<%=url_noindex + "3"%>>4</a>
                        <a class="nav5" href=<%=url_noindex + "4"%>>5</a>

                        <%
                                if (maxPage == 6) {
                        %>
                        <a class="nav6" href=<%=url_noindex + "5"%>>6</a>
                        <%
                        } else {
                        %>
                        <a href="">...</a>
                        <a class=<%="nav" + maxPage%> href=<%=url_noindex + (maxPage - 1)%>><%=maxPage%>
                        </a>
                        <%
                                }
                            } else {
                                for(int i=1;i<=maxPage;i++) {%>
                        <a class=<%="nav" + i%> href=<%=url_noindex + i%>><%=i%>

                                <%}}
                        } else {
//                            在中间，需要首页和尾页
                            //首页
                        %>
                        <a class="nav1" href=<%=url_noindex + "0"%>>1</a>
                        <% if (index3 != 4) { %>
                        <a href="">...</a>

                        <% }     //尾页
                            if (maxPage >= index3 + 2) {
                                if (maxPage != index3 + 3 && maxPage != index3 + 2) {
                                    for (int i = index3 - 2; i <= index3 + 2; i++) {
                        %>
                        <a class=<%="nav" + i%> href=<%=url_noindex + i%>><%=i%>
                        </a>
                        <% } %>
                        <a href="">...</a>
                        <a class=<%="nav" + maxPage%> href=<%=url_noindex + (maxPage - 1)%>><%=maxPage%>
                        </a>
                        <%
                        } else {
                            for (int i = index3 - 2; i <= maxPage; i++) {
                        %>
                        <a class=<%="nav" + i%> href=<%=url_noindex + (i-1)%>><%=i%>
                        </a>
                        <% }
                        }
                        } else {
                            for (int i = maxPage - 4; i <= maxPage; i++) {
                        %>
                        <a class=<%="nav" + i%> href=<%=url_noindex + (i-1)%>><%=i%>
                        </a>
                        <% }
                        }
                        }%>


                        <%if (maxPage > index3) {%>
                        <a href=<%=url_noindex + index3%>>下一页</a>
                        <%} else {%>
                        <a href="javascript:return false;">下一页 </a>
                        <%}%>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

    </div>

</div>
</body>
<script>

    var current = document.getElementsByClassName("<%="nav" + index3%>")[0];
    current.style.color = "#E62652";

    function checkName() {
        var name = document.getElementById("name").value;
        if (name === "") {
            return false;
        }
        return true;
    }

</script>
</html>
