<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>ログイン - 冷蔵庫コンシェルジュ</title>
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/S001_login.css">
</head>
<body>
    <div class="login-card">
        <h1 class="header-title">冷蔵庫コンシェルジュ</h1>
        <img src="${pageContext.request.contextPath}/img/reizoukokun.jpg" alt="冷蔵庫コンシェルジュ" class="logo-img" onerror="this.src='https://via.placeholder.com/120?text=Fridge';">
        
        <!-- ログイン -->
       <div class="form-container"> 
        <div class="form-section">
             <h2 class="section-title">ログイン</h2>
                
        <div class="message-text">ログインIDとパスワードを入力してください</div>

        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/LoginServlet" method="post">
            <div class="form-group">
                <label for="loginId">ログインID</label>
                <input type="text" id="loginId" name="loginId" value="${loginId}" class="form-control" placeholder="例: aaaa" required>
            </div>
            <div class="form-group">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password" class="form-control" placeholder="例: 1234" required>
            </div>
            <button type="submit" class="btn-submit">ログイン</button>
        </form>
    </div>


            <!-- 新規登録 -->
            <div class="form-section">

                <h2 class="section-title">新規登録</h2>

                <div class="message-text">
                    新しくアカウントを登録します
                </div>

                <c:if test="${not empty registerErrorMessage}">
                    <div class="error-message">
                        ${registerErrorMessage}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/UserRegisterServlet"
                      method="post">

                    <div class="form-group">

                        <label for="userName">ユーザー名</label>

                        <input type="text"
                               id="userName"
                               name="userName"
                               value="${registerUserName}"
                               class="form-control"
                               required>

                    </div>

                    <div class="form-group">

                        <label for="registerLoginId">ユーザーID</label>

                        <input type="text"
                               id="registerLoginId"
                               name="loginId"
                               value="${registerLoginId}"
                               class="form-control"
                               required>

                    </div>

                    <div class="form-group">

                        <label for="registerPassword">パスワード</label>

                        <input type="password"
                               id="registerPassword"
                               name="password"
                               class="form-control"
                               required>

                    </div>

                    <div class="form-group">

                        <label for="passwordConfirm">
                            パスワード（確認用）
                        </label>

                        <input type="password"
                               id="passwordConfirm"
                               name="passwordConfirm"
                               class="form-control"
                               required>

                    </div>

                    <button type="submit"
                            class="btn-submit">
                        新規登録
                    </button>

                </form>

            </div>

        </div>

    </div>    
    </div>
    
</body>
</html>
