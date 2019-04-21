<%@ page import="com.binqing.parity.Model.GoodsListModel" %>
<%@ page import="com.binqing.parity.Model.GoodsModel" %>
<%@ page import="com.binqing.parity.Model.ParityModel" %>
<%@ page import="com.binqing.parity.Service.HttpService" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
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
    String name = (String) request.getAttribute("name");
    String index = (String) request.getAttribute("page");
    String sort = (String) request.getAttribute("sort");
    if (index == null || "".equals(index)) {
        index = "0";
    }
    if (sort == null || "".equals(sort)) {
        sort = "0";
    }
    GoodsListModel goodsListModel =
            HttpService.getGoods(name, index, sort);
    List<ParityModel> parity = goodsListModel.getParityGoodsList();
    List<List<ParityModel>> listList = new ArrayList<>();
    if (parity != null && !parity.isEmpty()) {
        int num = 0;
        int index2 = 0;

        for (ParityModel parityModel : parity) {
            if (parityModel == null) {
                continue;
            }
            if (parityModel.getName().length() > 38) {
                parityModel.setName(parityModel.getName().substring(0, 38) + "...");
            }
            if (index2 == 0) {
                listList.add(new ArrayList<ParityModel>());
                listList.get(num).add(parityModel);
                index2 = 1;
            } else {
                if (parityModel.getOrder() == listList.get(num).get(0).getOrder()) {
                    listList.get(num).add(parityModel);
                    num++;
                    index2 = 0;
                } else {
                    num++;
                    listList.add(new ArrayList<ParityModel>());
                    listList.get(num).add(parityModel);
                    index2 = 1;
                }
            }
        }
    }

    List<GoodsModel> goodsList = goodsListModel.getGoodsModelList();
    String url_noindex = "/search?name=" + name + "&sort=" + sort + "&page=";
    url_noindex = url_noindex.replace(' ', '+');

    String url_default = "/search?name=" + name + "&page=" + index + "&sort=0";
    String url_sale_comment = "/search?name=" + name + "&page=" + index + "&sort=1";
    String url_price_asc = "/search?name=" + name + "&page=" + index + "&sort=2";
    String url_price_desc = "/search?name=" + name + "&page=" + index + "&sort=3";
    String url_login = "/search?name=" + name + "_page=" + index + "_sort=" + sort;
    int maxPage = goodsListModel.getMaxPage() + 1;
    if (goodsListModel.getMaxPage() == 0) {
        maxPage = 0;
    }
    int index3 = Integer.parseInt(index) + 1;
    if (index3 > maxPage) {
        if (maxPage == 0) {
            index3 = 1;
        } else {
            index3 = maxPage;
        }
    }

%>
<div class="all">
    <div class="content_guide">
        <div class="guide">
            <a href="<%=session.getAttribute("user") == null ? "/login?href="+url_login : "/modify?href="+url_login%>"><%=session.getAttribute("user") == null ? "请登录" : "欢迎您," + session.getAttribute("name")%>
            </a>
            <% if (session.getAttribute("user") != null) {%>
            <a href="<%="/signout?href=" + url_login%>">退出账户</a>
            <% }%>
            <a href="<%=session.getAttribute("user") == null ? "/login?href="+ url_login : "/favorite"%>">我的收藏</a>
        </div>
    </div>

    <div class="search_content">
        <div class="searchbox">
            <a href="/hello"><img class="image1" src="img/title.png" height="129" width="270"/></a>
            <form action="/search" onsubmit="return checkName()">
                <input type="text" class="shuru" id="name" name="name" value="<%=name%>"/>
                <input type="submit" class="ok" value="比价吧" style="cursor: pointer">
            </form>
        </div>
    </div>

    <div class="container">
        <div class="item_content">
            <div class="sort_tab">
                <table width="300px" border="1px" cellspacing="0">
                    <tr>
                        <th bgcolor="<%="0".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a
                                style="color: <%="0".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_default%>"
                                class="tab1">默认排序</a></th>
                        <th bgcolor="<%="1".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a
                                style="color: <%="1".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_sale_comment%>"
                                class="tab2" title="按销量从多到少排序">销量</a></th>
                        <th bgcolor="<%="2".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a
                                style="color: <%="2".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_price_asc%>"
                                class="tab3" title="按价格从低到高排序">价格(升序)</a>
                        <th bgcolor="<%="3".equals(sort)? "#5a5a5a" : "#ffffff"%>"><a
                                style="color: <%="3".equals(sort)? "#ffffff" : "#5a5a5a"%>" href="<%=url_price_desc%>"
                                class="tab4" title="按价格从高到低排序">价格(降序)</a></th>
                    </tr>
                </table>

            </div>

            <% if (parity != null && !parity.isEmpty()) {%>
            <p class="tip_product_detail">快速查看产品详情</p>
            <div class="parity_goods">
                <%
                    for (List<ParityModel> parityModels : listList) {
                        if (parityModels == null || parityModels.isEmpty()) {
                            continue;
                        }
                        StringBuilder ids = new StringBuilder();
                        for (ParityModel parityModel : parityModels) {
                            ids.append(parityModel.getTypeGid()).append(",");
                        }
                        ids.deleteCharAt(ids.length()-1);
                %>
                <div class="item_parity_goods">
                    <div class="pic">
                        <a target="_blank" href="/detail?ids=<%=ids.toString()%>&name=<%=name%>&sort=<%=sort%>">
                            <p style="text-align: center">
                                <img class="product" data-img="1"
                                     src="<%=parityModels.size() == 2 ? parityModels.get(1).getImage() : parityModels.get(0).getImage()%>"/>
                            </p>

                        </a>
                    </div>

                    <div class="title">
                        <a class="title" style="font-size: 14px" target="_blank" href="/detail?ids=<%=ids.toString()%>&name=<%=name%>&sort=<%=sort%>">
                            <%=parityModels.size() == 2 ? parityModels.get(1).getName() : parityModels.get(0).getName()%>
                        </a>
                    </div>

                    <div class="price">
                        <p style="color: #cc0000;font-size:16px;font-weight: bold">
                            ￥<%=parityModels.size() == 2 ? parityModels.get(1).getPrice() : parityModels.get(0).getPrice()%>
                        </p>
                    </div>

                    <p class="count" style="color : #7b7b7b;font-size:13px;"><%=parityModels.size()%>个商城比价</p>
                </div>

                <%
                    }
                %>
            </div>

            <%}%>

            <div style="margin-top:4px;height:1px;width:auto;border-top:1px solid #ccc;"></div>


            <%
                if (goodsList == null) {
                    return;
                }
                for (GoodsModel goodsModel : goodsList) {
                    boolean over = false;
                    int sc = goodsModel.getSalecomment();
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
                    switch (goodsModel.getType()) {
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
            %>
            <div class="item_goods">
                <div class="pic">
                    <a target="_blank" href="<%=goodsModel.getHref()%>">
                        <img class="product" data-img="1"
                             src="<%=goodsModel.getImage()%>"/>
                    </a>
                </div>

                <div class="title">
                    <a style="font-size: 18px" target="_blank" href="<%=goodsModel.getHref()%>">
                        <%=goodsModel.getName()%>
                    </a>
                </div>

                <div class="price">
                    <p style="color: #cc0000;font-size:22px;font-weight: bold">￥<%=goodsModel.getPrice()%>
                    </p>
                </div>

                <div class="sale_comment">
                    <p class="text_sale_comment">有
                        <a href="<%=goodsModel.getHref()%>"><%=saleComment%>
                        </a>人<%=goodsModel.getType() == 0 ? "评论" : "收货"%>
                    </p>
                </div>

                <div class="shop">
                    <p class="shop_title"><%=goodsModel.getShop()%>
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
