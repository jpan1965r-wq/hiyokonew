<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="ja">

<head>

    <meta charset="UTF-8">

    <title>登録完了 - 冷蔵庫コンシェルジュ</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/S001_login.css">

</head>

<body>

    <div class="login-card">

        <h1 class="header-title">冷蔵庫コンシェルジュ</h1>

        <img src="${pageContext.request.contextPath}/img/reizoukokun.jpg"
             alt="冷蔵庫コンシェルジュ"
             class="logo-img">

        <h2>登録完了</h2>

        <p>ユーザー登録が完了しました。</p>

        <p>登録番号</p>

        <div class="registration-number">
            ${registrationNumber}
        </div>

        <p>
            ユーザーID：${loginId}
        </p>

        <p>
            ユーザー名：${userName}
        </p>

        <a href="${pageContext.request.contextPath}/LoginServlet"
           class="btn-submit">
            ログイン画面へ
        </a>

    </div>

</body>

</html>