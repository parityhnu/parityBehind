<%@ page import="com.binqing.parity.Service.HttpService" %>
<%@ page import="org.apache.http.util.TextUtils" %>
<%@ page import="com.binqing.parity.Model.*" %>
<%@ page import="com.binqing.parity.Enum.CommentType" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="org.attoparser.util.TextUtil" %><%--
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
    <link rel="stylesheet" href="css/style2.css" type="text/css"/>
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

    ul{
        list-style:none;
    }

    font {
        font-size: 14px;
        line-height: 1.4;
        vertical-align: inherit;
        word-wrap: break-word;
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
    name = name.replace(' ', '+');
    String sort = (String) request.getAttribute("sort");
    List<String> ids = (List<String>) request.getAttribute("ids");
    if (ids == null || ids.isEmpty()) {
        return;
    }
    String index = (String) request.getAttribute("index");
    if (index == null || TextUtils.isEmpty(index) || Integer.parseInt(index) < 1) {
        index = "1";
    }
    StringBuilder url_prensent = new StringBuilder("/detail?ids=");
    int size = ids.size();
    for (int i = 0; i < size; i++) {
        String id = ids.get(i);
        if (TextUtils.isEmpty(id)) {
            continue;
        }
        if (i == size - 1) {
            url_prensent.append(id);
        } else {
            url_prensent.append(id).append(',');
        }
    }
    url_prensent.append("&name=").append(name).append("&sort=").append(sort);
    String url_noindex = url_prensent.toString();
    url_prensent.append("&index=").append(index);
    String url_default = url_prensent.toString();
    String url_login = url_default.replace('&', '_');

    //评论相关
    CommentReturnModel returnModel =
            HttpService.getComments(ids, index);
    if (returnModel == null) {
        return;
    }
    int maxPage = returnModel.getMaxPage();
    if (Integer.parseInt(index) > maxPage) {
        if (maxPage == 0) {
            index = "1";
        } else {
            index = String.valueOf(maxPage);
        }
    }
    List<BaseCommentModel> commentModelList = new ArrayList<>();
    List<JDCommentModel> jdCommentModels = returnModel.getJdCommentModels();
    List<TBCommentModel> tbCommentModels = returnModel.getTbCommentModels();
    List<TMCommentModel> tmCommentModels = returnModel.getTmCommentModels();
    if (jdCommentModels != null && !jdCommentModels.isEmpty()) {
        commentModelList.addAll(jdCommentModels);
    }
    if (tbCommentModels != null && !tbCommentModels.isEmpty()) {
        commentModelList.addAll(tbCommentModels);
    }
    if (tmCommentModels != null && !tmCommentModels.isEmpty()) {
        commentModelList.addAll(tmCommentModels);
    }
    Collections.sort(commentModelList);

    //参数相关
    List<AttributeModel> attributeModelList = HttpService.getAttributes(ids);
    Map<String, List<AttributeModel>> attributesMap = new HashMap<>();

    if (attributeModelList != null && !attributeModelList.isEmpty()) {
        for (AttributeModel attributeModel : attributeModelList) {
            if (attributeModel == null) {
                continue;
            }
            if (attributesMap.get(attributeModel.getGid()) == null) {
                List<AttributeModel> list = new ArrayList<>();
                attributesMap.put(attributeModel.getGid(), list);
            }
            attributesMap.get(attributeModel.getGid()).add(attributeModel);
        }
    }

    //商品本身
    List<ParityModel> parityModelList = HttpService.getGoods(ids);

%>
<div class="all">
    <div class="content_guide">
        <div class="guide">
            <a href="<%=session.getAttribute("user") == null ? "/login?href=" + url_login : "/modify?href=" + url_login%>"><%=session.getAttribute("user") == null ? "请登录" : "欢迎您," + session.getAttribute("name")%>
            </a>
            <% if (session.getAttribute("user") != null) {%>
            <a href="<%="/signout?href=" + url_login%>">退出账户</a>
            <% }%>
            <a href="<%=session.getAttribute("user") == null ? "/login?href=" + url_login : "/favorite"%>">我的收藏</a>
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
            <p class="tip_parity">商城比价</p>
            <%
                if (parityModelList == null) {
                    return;
                }
                for (ParityModel parityModel : parityModelList) {
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
                    String img_name = parityModel.getTypeGid() + "_";
                    String img_src = "img/tofavorite.png";
                    StringModel stringModel = HttpService.checkFavorite(String.valueOf(session.getAttribute("user")), parityModel.getTypeGid(), name, sort);
                    if (stringModel != null && parityModel.getGid().equals(stringModel.getString())) {
                        img_src = "img/favorite.png";
                        img_name += "true";
                    } else {
                        img_name += "false";
                    }
            %>
            <div class="item_goods">

                <div class="pic">
                    <a target="_blank" href="<%=parityModel.getHref()%>">
                        <img class="product" data-img="1"
                             src="<%=parityModel.getImage()%>"/>
                    </a>
                </div>

                <div class="goods_up">
                    <div class="title">
                        <a style="font-size: 18px" target="_blank" href="<%=parityModel.getHref()%>">
                            <%=parityModel.getName()%>
                        </a>
                    </div>

                    <div class="price">
                        <p style="color: #cc0000;font-size:22px;font-weight: bold">￥<%=parityModel.getPrice()%>
                        </p>
                    </div>

                    <div class="sale_comment">
                        <p class="text_sale_comment">有
                            <a href="<%=parityModel.getHref()%>"><%=saleComment%>
                            </a>人<%=parityModel.getType() == 0 ? "评论" : "收货"%>
                        </p>
                    </div>

                    <div class="shop">
                        <p class="shop_title"><%=parityModel.getShop()%>
                        </p>
                        <img class="icon" src="<%=img%>">
                        <p class="shop_origin"><%=root%></p>
                        <img class="favorite" name="<%=img_name%>" src="<%=img_src%>"
                             style="width: 18px; height: 18px;margin-left: 6px;margin-top: 6px;float: left;">

                    </div>
                </div>

                <div class="goods_attribute">
                    <ul class="attribute_list">
                        <%
                            List<AttributeModel> models = attributesMap.get(parityModel.getGid());
                            if (models != null && !models.isEmpty()) {
                                for (AttributeModel attributeModel : models) {
                                %>
                                <li class="item_attribute"><%=attributeModel.getAttribute()%></li>
                                <%
                                }
                            }
                        %>
                    </ul>
                </div>

            </div>

            <div style="height:1px;width:auto;border-top:1px solid #ccc;"></div>
            <%
                }%>
        </div>

        <div class="item_content">
            <p class="tip_parity_comment">用户评价</p>
            <%
                if (commentModelList == null) {
            %>
            <%--设置没有评论时的提示信息--%>
            <%
            } else {
                for (BaseCommentModel commentModel : commentModelList) {

                    boolean hasAttend = true;
                    List<String> pics = commentModel.getPics();
                    String rateContent = commentModel.getRateContent();
                    String content = commentModel.getContent();
                    if ("{ }".equals(content)) {
                        hasAttend = false;
                    }
                    long time = commentModel.getCtime();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                    Date date = new Date(time);
                    String resultTime = simpleDateFormat.format(date);

                    String shop = "";
                    CommentType commentType = CommentType.JD;

                    //TM and taobao
                    List<String> attendPics = new ArrayList<>();
                    String auctionSku = "";

                    //jd
                    String productSize = "";
                    String productColor = "";
                    int score = 0;

                    if (commentModel instanceof TBCommentModel) {
                        commentType = CommentType.TAOBAO;
                        shop = "淘宝网";
                        attendPics = ((TBCommentModel) commentModel).getAttendpics();
                        auctionSku = ((TBCommentModel) commentModel).getAuctionSku();
                    } else if (commentModel instanceof TMCommentModel) {
                        commentType = CommentType.TMALL;
                        shop = "天猫商城";
                        attendPics = ((TMCommentModel) commentModel).getAttendpics();
                        auctionSku = ((TMCommentModel) commentModel).getAuctionSku();
                    } else if (commentModel instanceof JDCommentModel) {
                        shop = "京东商城";
                        commentType = CommentType.JD;
                        productSize = ((JDCommentModel) commentModel).getProductSize();
                        productColor = ((JDCommentModel) commentModel).getProductColor();
                        score = ((JDCommentModel) commentModel).getScore();
                    }

            %>
            <div class="item_comment">
                <div class="user">
                    <div class="nick"><font><%=commentModel.getDisplayUserNick()%>
                    </font></div>
                    <div class="shop"><font style="color: #AAA"><%=shop%>
                    </font></div>
                    <% if (commentModel instanceof JDCommentModel) { %>
                    <div class="comment-star"></div>
                    <% }%>
                </div>

                <div class="comment">

                    <div class="rateContent">
                        <font style="color: #3F3F3F;"><%=rateContent%>
                        </font>
                    </div>


                    <%if (pics != null && !pics.isEmpty()) { %>
                    <div class="pics">
                        <% for (String src : pics) {
                            if (commentModel instanceof JDCommentModel) {
                                try {
                                    String [] strings = src.split("/");
                                    strings[3] = "shaidan";
                                    strings[4] = "s616x405_jfs";
                                    int length = strings.length;
                                    StringBuilder builder = new StringBuilder();
                                    for (int i = 0;i<length; i++) {
                                        if (i < length - 1) {
                                            builder.append(strings[i]).append("/");
                                        } else {
                                            builder.append(strings[i]);
                                        }
                                    }
                                    src = builder.toString();
                                } catch (Exception e) {

                                }
                            }
                        %>

                        <div class="pic">
                            <img class="pic_img" src=<%=src%>>
                        </div>
                        <%}%>
                    </div>
                    <div class="viewer" style="display: none">
                        <img src=<%=pics.get(0)%>>
                    </div>
                    <%}%>

                    <div class="timeandsku">
                        <span>
                            <font style="float: left;font-size: 12px;color: #B0B0B0;"><%=resultTime%></font>
                        </span>
                        <font style="margin-left:16px;float: left;font-size: 12px;color: #B0B0B0;">
                            <%=commentType == CommentType.JD ?
                                    productColor + "        " + productSize :
                                    auctionSku%>
                        </font>
                    </div>

                    <% if (hasAttend) { %>

                    <div style="margin-top:8px;height:1px;width:auto;border-top:1px #e2dce6 dashed;"></div>

                    <div class="attend">
                        <div class="attend_content">
                                <span>
                                    <font style=" color:#AAA;"> [追加评论]</font></span><font
                                style=" font-size: 14px;line-height: 1.4;color: #3F3F3F;"><%=content%>
                        </font>
                        </div>
                        <% if (commentType != CommentType.JD) { %>
                        <%if (attendPics != null && !attendPics.isEmpty()) { %>
                        <div class="pics">
                            <%
                                for (String src : attendPics) {
                            %>
                            <div class="pic">
                                <img class="pic_img" src=<%=src%>>
                            </div>

                            <% }
                            %>
                        </div>
                        <%
                                }
                            }%>
                    </div>
                    <% } %>

                </div>

            </div>

            <div style="height:1px;width:auto;border-top:1px solid #ccc;"></div>
            <%
                    }
                }%>
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

</div>
</body>
<script>

    var current = document.getElementsByClassName("<%="nav" + index%>")[0];
    current.style.color = "#E62652";

    function checkName() {
        var name = document.getElementById("name").value;
        if (name === "") {
            return false;
        }
        return true;
    }

    window.onload = function () {
        var pics = document.getElementsByClassName("pics");
        var length = pics.length;
        for (var i = 0; i < length; i++) {
            var divs = pics[i].getElementsByClassName("pic");
            var ld = divs.length;
            let father = pics[i];
            let checkdivs = divs;
            for (var j = 0; j < ld; j++) {
                divs[j].onclick = function () {
                    var img = this.getElementsByClassName("pic_img")[0];
                    if (img.name == undefined || "" == img.name || "close" == img.name) {
                        var lc = checkdivs.length;
                        for (var s = 0; s < lc; s ++ ) {
                            var checkimg = checkdivs[s].getElementsByClassName("pic_img")[0];
                            checkimg.name = "close";
                            checkimg.style.height = 48 + "px";
                            checkimg.style.width = 48 + "px";
                        }
                        img.name = "on";
                        var natH = img.naturalHeight;
                        var natW = img.naturalWidth;
                        var WcH = natW/natH * 400;
                        father.style.height = 400 + "px";
                        img.style.height = 400 + "px";
                        img.style.width = WcH + "px";
                    } else {
                        img.name = "close";
                        img.style.height = 48 + "px";
                        img.style.width = 48 + "px";
                        father.style.height = 48 + "px";
                    }
                }
            }
        }

        var favorites = document.getElementsByClassName("favorite");
        var lf = favorites.length;
        var request = new XMLHttpRequest();
        for (var i = 0; i < lf; i ++) {
            let img = favorites[i];
            let id = img.name.split('_')[0];
            let todo = img.name.split('_')[1];
            let user = "<%=session.getAttribute("user")%>";
            let name = "<%=name%>";
            let sort = "<%=sort%>";

            var url = "/user/checkfavorite";

            img.onclick = function () {
                if ("null" == user) {
                    setTimeout(window.location.href = "/login?href=<%=url_login%>", 0);
                    return;
                }
                var cancel = true;
                if (todo == "false") {
                    cancel = false;
                }
                var url = "/user/favorite";
                request.open("post", url, true);
                var data = new FormData();
                data.append("id", id);
                data.append("user", user);
                data.append("name", name);
                data.append("sort", sort);
                data.append("cancel", cancel);
                request.onreadystatechange = function() {
                    if (this.readyState == 4) {
                        var json = JSON.parse(this.responseText);
                        var result = json.string;
                        console.log(result);
                        if (result != null) {
                            var s1 = result.split("_")[0];
                            var s2 = result.split("_")[1];
                            console.log(s1)
                            console.log(s2)
                            if (s1 == id.split(":")[1]) {

                                if (s2 == "true") {
                                    img.src = "img/tofavorite.png";
                                    todo = "false";
                                    img.name = id + "_" + todo;
                                } else {
                                    img.src = "img/favorite.png";
                                    todo = "true";
                                    img.name = id + "_" + todo;
                                }
                            }
                        }
                    }
                };
                request.send(data);
            }
        }
    }


</script>
</html>
