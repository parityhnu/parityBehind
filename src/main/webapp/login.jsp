<!DOCTYPE html>
<html lang="en">
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"  language="java" %>
<head>
    <meta charset="UTF-8">
    <link rel="icon" type="image/x-icon" href="#" />
    <link type="text/css" rel="styleSheet"  href="css/main.css" />
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

        @css.font-face {
            font-family: 'neo';
            src: url("font/NEOTERICc.ttf");
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
            height: 0;
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
        #login_wrap{
            width: 980px;
            min-height: 500px;
            border-radius: 10px;
            font-family: "neo";
            overflow: hidden;
            box-shadow: 0px 0px 120px rgba(0, 0, 0, 0.25);
            position: fixed;
            top: 50%;
            right: 50%;
            margin-top: -250px;
            margin-right: -490px;
        }
        #login{
            width: 50%;
            height: 100%;
            min-height: 500px;
            background: linear-gradient(to top, #cfd9df 0%, #e2ebf0 100%);
            position: relative;
            float: right;
        }
        #login #status{
            width: 90px;
            height: 35px;
            margin: 40px auto;
            color: rgba(0,0,0,0.40);
            font-size: 30px;
            font-weight: 600;
            position: relative;
            overflow: hidden;
        }
        #login #status i{
            font-style: normal;
            position: absolute;
            transition: .5s
        }
        #login span{
            text-align: center;
            position: absolute;
            left: 50%;
            margin-left: -150px;
            top: 52%;
            margin-top: -140px;
        }
        #login span a{
            text-decoration: none;
            color: rgba(0,0,0,0.40);
            display: block;
            margin-top: 80px;
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
            #login_wrap{
                width: 490px;
                margin-right: -245px;
            }
            #login{
                width: 100%;

            }
        }
        @media screen and (max-width:560px ) {
            #login_wrap{
                width: 330px;
                margin-right: -165px;
            }
            #login span{
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
            #login_wrap {
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
%>

<body>
<div id="bg">
    <div id="hint"><!-- 提示框 -->
        <p></p>
    </div>
    <div id="login_wrap">
        <div id="login"><!-- 登录注册切换动画 -->
            <div id="status">
                <i style="top: 0">Log</i>
                <i style="top: 35px">Sign</i>
                <i style="right: 5px">in</i>
            </div>
            <span>
                    <form action="post">
                        <p class="form"><input type="text" style="color: rgba(0,0,0,0.40)" id="user" placeholder="账户名"></p>
                        <p class="form"><input type="password" style="color: rgba(0,0,0,0.40)" id="passwd" placeholder="密码"></p>
                        <p class="form confirm"><input type="password" style="color: rgba(0,0,0,0.40)" id="confirm-passwd" placeholder="重复确定密码"></p>
                        <p class="form confirm"><input type="number" style="color: rgba(0,0,0,0.40)" id="phone" placeholder="手机号"></p>

                        <input type="button" value="登录" class="btn" onclick="login()" style="margin-right: 20px;">
                        <input type="button" value="注册" class="btn" onclick='signin()' id="btn">
                    </form>
                    <a href="/forgetPassword?href=/login?href=<%=href%>">忘记密码?</a>
                </span>
        </div>


    </div>
</div>
</body>
<script>
    var onoff = true;//根据此布尔值判断当前为注册状态还是登录状态
    var confirm = document.getElementsByClassName("confirm")[0];
    var input_phone = document.getElementsByClassName("confirm")[1];
    var user = document.getElementById("user");
    var passwd = document.getElementById("passwd");
    var con_pass = document.getElementById("confirm-passwd");
    var phone = document.getElementById("phone");

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
    function submit(callback) {
        if (passwd.value == con_pass.value) {
            var request = new XMLHttpRequest();
            var url = "/user/register";
            request.open("post", url, true);
            var data = new FormData();
            data.append("account", user.value);
            data.append("password", passwd.value);
            data.append("phone", phone.value);
            request.onreadystatechange = function() {
                if (this.readyState == 4) {
                    callback.call(this, this.responseText)
                }
            };
            request.send(data)
        } else {
            hit.innerHTML = "两次密码不同";
            hitting();
            }
    }
    //注册按钮
    function signin() {
        var status = document.getElementById("status").getElementsByTagName("i");
        var hit = document.getElementById("hint").getElementsByTagName("p")[0];
        if (onoff) {
            confirm.style.height = 51 + "px";
            input_phone.style.height = 51 + "px";
            status[0].style.top = 35 + "px";
            status[1].style.top = 0;
            onoff = !onoff
        } else {
            if (!/^[A-Za-z][A-Za-z0-9]+$/.test(user.value))
                hit.innerHTML = "账号只能为英文开头的英文和数字";
            else if (user.value.length < 6)
                hit.innerHTML = "账号长度必须大于6位";
            else if (passwd.value.length < 6 || passwd.value.length > 12)
                hit.innerHTML = "密码长度必须大于6位，小于12位";
            else if (!(/^[A-Za-z0-9]+$/.test(passwd.value) ||
                /[a-zA-Z~!@#$%^&*.]+/.test(passwd.value) ||
                /[\d~!@#$%^&*.]*/.test(passwd.value) ||
                /[\da-zA-Z~!@#$%^&*.]+/.test(passwd.value)))
                hit.innerHTML = "密码必须由英文、数字或特殊字符的两种或以上组合";
            else if (passwd.value != con_pass.value)
                hit.innerHTML = "两次密码不相等";
            else if (phone.value.length != 11)
                hit.innerHTML = "手机号必须为11位"
            else if (passwd.value = con_pass.value) {
                submit(function(res) {
                    var json = JSON.parse(res);
                    if (json.account == user.value) {
                        hit.innerHTML = "账号注册成功，两秒后自动刷新页面";
                        setTimeout("window.location.reload()", 2000);
                    } else  {
                        hit.innerHTML = "该账号已存在";
                    }
                })
            }
            hint()
        }
    }

    //登录按钮
    function login() {
        if (onoff) {
            var hit = document.getElementById("hint").getElementsByTagName("p")[0];
            var request = new XMLHttpRequest();
            var url = "/user/login";
            request.open("post", url, true);
            var data = new FormData();
            data.append("account", user.value);
            data.append("password", passwd.value);
            request.onreadystatechange = function() {
                if (this.readyState == 4) {
                    if (this.responseText == false)
                        hit.innerHTML = "登录失败";
                    }
                    else {
                        var json = JSON.parse(this.responseText);
                        if (json.uid > 0) {
                            hit.innerHTML = "登录成功";
                            setTimeout(window.location.href = "<%=href%>", 1000);
                        } else {
                            if (json.uid == -1) {
                                hit.innerHTML = "账户名或者密码错误";
                            } else if (json.uid == -2) {
                                hit.innerHTML = "错误次数过多，请过5分钟后再试";
                            }
                        }
                    }
                hint();
            };
            request.send(data);
            } else {
            var status = document.getElementById("status").getElementsByTagName("i");
            confirm.style.height = 0;
            input_phone.style.height = 0;
            status[0].style.top = 0;
            status[1].style.top = 35 + "px";
            onoff = !onoff
        }
    }

</script>
</html>
