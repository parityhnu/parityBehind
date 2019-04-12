<%@ page import="com.binqing.parity.Service.HttpService" %>
<%@ page import="org.apache.http.util.TextUtils" %>
<%@ page import="com.binqing.parity.Model.*" %>
<%@ page import="com.binqing.parity.Enum.CommentType" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %><%--
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
    String url_noindex = url_prensent.toString();
    url_prensent.append("&index=").append(index);
    String url_default = url_prensent.toString();
    CommentReturnModel returnModel =
            HttpService.getComments(ids, index);
    int maxPage = returnModel.getMaxPage();
    List<BaseCommentModel> commentModelList = new ArrayList<>();
    List<JDCommentModel> jdCommentModels = returnModel.getJdCommentModels();
    List<TBCommentModel> tbCommentModels = returnModel.getTbCommentModels();
    List<TMCommentModel> tmCommentModels = returnModel.getTmCommentModels();
    commentModelList.addAll(jdCommentModels);
    commentModelList.addAll(tbCommentModels);
    commentModelList.addAll(tmCommentModels);
    Collections.sort(commentModelList);

%>
<div class="all">
    <div class="content_guide">
        <div class="guide">
            <a href="<%=session.getAttribute("user") == null ? "/login?href=" + url_default : "/modify?href=" + url_default%>"><%=session.getAttribute("user") == null ? "请登录" : "欢迎您," + session.getAttribute("name")%>
            </a>
            <% if (session.getAttribute("user") != null) {%>
            <a href="<%="/signout?href=" + url_default%>">退出账户</a>
            <% }%>
            <a href="<%=session.getAttribute("user") == null ? "/login?href=" + url_default : ""%>">我的收藏</a>
        </div>
    </div>

    <div class="search_content">
        <div class="searchbox">
            <img class="image1" src="img/title.png" height="129" width="270"/>
            <form action="/search" onsubmit="return checkName()">
                <input type="text" class="shuru" id="name" name="name" placeholder="输入你要比价的商品名称"/>
                <input type="submit" class="ok" value="比价吧" style="cursor: pointer">
            </form>
        </div>
    </div>

    <div class="container">

        <div class="item_content">


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
                    <%--<% if (commentModel instanceof JDCommentModel) { %>--%>
                    <div class="comment-star"></div>
                    <%--<% }%>--%>
                </div>

                <div class="comment">

                    <div class="rateContent">
                        <font style="color: #3F3F3F;"><%=rateContent%>
                        </font>
                    </div>


                    <%if (pics != null && !pics.isEmpty()) { %>
                    <div class="pics">
                        <% for (String src : pics) {
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
                        <a class="nav1" href=<%=url_noindex + "&index=1"%>>1</a>
                        <a class="nav2" href=<%=url_noindex + "&index=2"%>>2</a>
                        <a class="nav3" href=<%=url_noindex + "&index=3"%>>3</a>
                        <a class="nav4" href=<%=url_noindex + "&index=4"%>>4</a>
                        <a class="nav5" href=<%=url_noindex + "&index=5"%>>5</a>
                        <%
                            if (maxPage > 5) {
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


                        <%if (!String.valueOf(maxPage).equals(index)) {%>
                        <a href=<%=url_noindex + "&index=" + String.valueOf(Integer.parseInt(index) + 1)%>>下一页</a>
                        <%} else {%>
                        <font style="font-size: 16px">下一页</font>
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
            for (var j = 0; j < ld; j++) {
                divs[j].onclick = function () {
                    var img = this.getElementsByClassName("pic_img")[0];
                    if (img.name == undefined || "" == img.name || "close" == img.name) {
                        img.name = "on";
                        console.log(father);
                        father.style.height = img.naturalHeight;
                        img.style.height = img.naturalHeight;
                        img.style.width = img.naturalWidth;
                    } else {
                        img.name = "close";
                        father.style.height = 48 + "px";
                        img.style.height = 48 + "px";
                        img.style.width = 48 + "px";
                    }
                }
            }
        }

    }


</script>
</html>
