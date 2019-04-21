<!DOCTYPE html>
<html lang="en">
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"  language="java" %>
<head>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="#" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>账户登录</title>
    <style>
        *{
            margin: 0;
            padding: 0;
        }

        html,
        body {
            height: 100%;
        }

        input:focus{
            outline: none;
        }
        .form input{
            width: 300px;
            height: 30px;
            font-size: 18px;
            background: none;
            border: none;
            border-bottom: 1px solid rgba(0,0,0,0.40);;
            color: rgba(0,0,0,0.40);
            margin-bottom: 20px;
        }
        .form input::placeholder{
            color: rgba(0,0,0,0.40);
            font-size: 18px;
            font-family: "neo";
        }
        .confirm{
            height: 51px;
            overflow: hidden;
            transition: .25s;
        }
        .btn{
            width:140px;
            height: 40px;
            border: 1px solid rgba(0,0,0,0.40);;
            background: none;
            font-size:20px;
            color: rgba(0,0,0,0.40);
            cursor: pointer;
            margin-top: 25px;
            font-family: "neo";
            transition: .25s;
        }
        .btn:hover{
            background: rgba(255,255,255,.25);
        }
        #modify_wrap{
            width: 980px;
            min-height: 600px;
            border-radius: 10px;
            font-family: "neo";
            overflow: hidden;
            box-shadow: 0px 0px 120px rgba(0, 0, 0, 0.25);
            position: fixed;
            top: 40%;
            right: 50%;
            margin-top: -250px;
            margin-right: -490px;
        }
        #modify{
            width: 50%;
            height: 100%;
            min-height: 600px;
            background: linear-gradient(to top, #cfd9df 0%, #e2ebf0 100%);
            position: relative;
            float: right;
        }
        #modify #status{
            width: 110px;
            height: 35px;
            margin: 40px auto;
            color: rgba(0,0,0,0.40);
            font-size: 26px;
            font-weight: 600;
            position: relative;
            overflow: hidden;
        }
        #modify #status i{
            font-style: normal;
            position: absolute;
            transition: .5s
        }
        #modify span{
            text-align: center;
            position: absolute;
            left: 45%;
            margin-left: -150px;
            top: 45%;
            margin-top: -140px;
        }
        #modify span a{
            text-decoration: none;
            color: rgba(0,0,0,0.40);
            display: block;
            margin-top: 40px;
            font-size: 18px;
        }
        #bg{
            background: linear-gradient(120deg, #fdfbfb 0%, #ebedee 100%);
            height: 100%;
        }

        /*提示*/
        #hint{
            width: 100%;
            line-height: 70px;
            background: linear-gradient(to top, #cfd9df 0%, #e2ebf0 100%);
            text-align: center;
            font-size: 25px;
            color: rgba(0,0,0,0.40);
            background-blend-mode: multiply,multiply;
            display: none;
            opacity: 0;
            transition: .5s;
            position: absolute;
            top: 0;
            z-index: 999;
        }
        /* 响应式 */
        @media screen and (max-width:2000px ) {
            #login_img{
                display: none;
            }
            #modify_wrap{
                width: 490px;
                margin-right: -245px;
            }
            #modify{
                width: 100%;

            }
        }
        @media screen and (max-width:560px ) {
            #modify_wrap{
                width: 330px;
                margin-right: -165px;
            }
            #modify span{
                margin-left: -125px;
            }
            .form input{
                width: 250px;
            }
            .btn{
                width: 113px;
            }
        }
        @media screen and (max-width:345px ) {
            #modify_wrap {
                width: 290px;
                margin-right: -145px;
            }
        }
    </style>
</head>

<%
    String href = (String) request.getAttribute("href");
    if (href == null || "".equals(href)) {
        href = "/hello";
    }
    href = href.replace('_', '&');
%>

<body>
<div id="bg">
    <div id="hint"><!-- 提示框 -->
        <p></p>
    </div>
    <div id="modify_wrap">
        <div id="modify">
            <div id="status">
                <i style="right: 5px">修改资料</i>
            </div>

            <span>
                    <form action="post">
                        <p class="form"><input type="text" style="color: rgba(0,0,0,0.40)" id="uname" placeholder="<%="现昵称:" + session.getAttribute("name")%>"></p>
                        <p class="form"><input type="password" style="color: rgba(0,0,0,0.40)" id="passwd_present" placeholder="原密码"></p>
                        <p class="form"><input type="password" style="color: rgba(0,0,0,0.40)" id="passwd" placeholder="修改密码"></p>
                        <p class="form confirm"><input type="password" style="color: rgba(0,0,0,0.40)" id="confirm_passwd" placeholder="重复确定密码"></p>
                        <p class="form confirm"><input type="number" style="color: rgba(0,0,0,0.40)" id="phone_present" placeholder="<%="原手机号:" + session.getAttribute("ph")%>"></p>
                        <p class="form confirm"><input type="number" style="color: rgba(0,0,0,0.40); font-style: normal" id="phone" placeholder="新手机号"></p>
                        <p style="color: rgba(0,0,0,0.40); font-size: 14px">tip:请填写您需要更改的资料，密码和手机号需要填写验证</p>
                        <input type="button" value="返回" class="btn" onclick="back()" style="margin-right: 20px;">
                        <input type="button" value="修改" class="btn" onclick='modify()' id="btn">
                    </form>

                    <a href="/forgetPassword?href=/modify?href=<%=href%>">忘记密码?</a>
                </span>
        </div>


    </div>
</div>
</body>


<script>
    var user = "<%=session.getAttribute("user")%>";
    if ("null" == user) {
        window.location.href = "/login";
    }
    //引用hint()在最上方弹出提示
    function hint() {
        var hit = document.getElementById("hint");
        hit.style.display = "block";
        setTimeout(function () {
            hit.style.opacity = 1;
        }, 0);
        setTimeout(function () {
            hit.style.opacity = 0;
        }, 2000);
        setTimeout(function () {
            hit.style.display = "none";
        }, 3000);
    }
    //回调函数
    function submit(callback, user, s1, s2, modifyType) {
        var request = new XMLHttpRequest();
        var url = "/user/modify";
        request.open("post", url, true);
        var data = new FormData();
        data.append("user", user);
        data.append("s1", s1);
        data.append("s2", s2);
        data.append("modifyType", modifyType);
        request.onreadystatechange = function() {
            if (this.readyState == 4) {
                callback.call(this, this.responseText)
            }
        };
        request.send(data)
    }
    //修改按钮
    function modify() {
        var hit = document.getElementById("hint").getElementsByTagName("p")[0];
        hit.innerHTML = "";
        var uname = document.getElementById("uname");
        var passwd_present = document.getElementById("passwd_present");
        var passwd = document.getElementById("passwd");
        var con_pass = document.getElementById("confirm_passwd");
        var phone_present = document.getElementById("phone_present");
        var phone = document.getElementById("phone");
        var modifyName = false;
        var modifyPwd = false;
        var modifyPhone = false;
        var checkPwd = false;
        var checkPhone = false;

        if (uname.value != "") {
            modifyName = true;
        }

        if (passwd_present.value != "" || passwd.value != "" || con_pass.value != "") {
            modifyPwd = true;
            //每次只需要check输入的新密码
            if (passwd.value != con_pass.value) {
                hit.innerHTML += " 两次密码不相等";
            } else if (!/((?=.*[a-z])(?=.*\d)|(?=[a-z])(?=.*[#@!~%^&*])|(?=.*\d)(?=.*[#@!~%^&*]))[a-z\d#@!~%^&*]{6,12}/i.test(passwd.value)) {
                hit.innerHTML += " 密码的长度为6-12，且必须由英文、数字或特殊字符的两种或以上组合";
            } else if (!/((?=.*[a-z])(?=.*\d)|(?=[a-z])(?=.*[#@!~%^&*])|(?=.*\d)(?=.*[#@!~%^&*]))[a-z\d#@!~%^&*]{6,12}/i.test(con_pass.value)) {
                hit.innerHTML += " 密码的长度为6-12，且必须由英文、数字或特殊字符的两种或以上组合";
            } else if (passwd_present.value == passwd.value) {
                hit.innerHTML += "密码不能和原密码相同";
            } else{
                checkPwd = true;
            }
        }

        if (phone_present.value != "" || phone.value != "") {
            modifyPhone = true;
            if (phone.value.length == 11 && phone_present.value.length == 11) {
                checkPhone = true;
            } else if (phone.value == phone_present.value) {
                hit.innerHTML += "手机号不能和原手机号相同";
            } else {
                hit.innerHTML += " 手机号必须为11位"
            }
        }

        if ((modifyPhone == checkPhone) && (modifyPwd == checkPwd)) {
            hit.innerHTML = "";
            //需要更改的都check
            if (modifyName) {
                submit(function(res) {
                    console.log(res);
                    if (res === uname.value) {
                        hit.innerHTML += "修改昵称成功";
                    }
                },"<%=session.getAttribute("user")%>", uname.value, null, "0");
            }

            if (modifyPhone) {
                submit(function(res) {
                    if (res === phone.value) {
                        hit.innerHTML += " 修改手机成功";
                    }
                },"<%=session.getAttribute("user")%>", phone_present.value, phone.value, "1");
            }

            if (modifyPwd) {
                submit(function(res) {
                    if (res === passwd.value) {
                        hit.innerHTML += " 修改密码成功";
                    }
                },"<%=session.getAttribute("user")%>", passwd_present.value, passwd.value, "2");
            }
            setTimeout("window.location.reload()", 2000);
        } else if (!modifyPwd && !modifyPhone && !modifyName) {
            hit.innerHTML = "没有需要更改的";
        }
        hint();


    }

    //返回按钮
    function back() {
        window.location.href = "<%=href%>";
    }

</script>
</html>
