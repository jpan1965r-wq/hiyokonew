<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>メイン画面 - 冷蔵庫コンシェルジュ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/common.css">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/css/S002_main.css">
<script src="${pageContext.request.contextPath}/js/common.js"></script>
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="header-title-box">
                <img src="${pageContext.request.contextPath}/img/reizoukokun.jpg" alt="ロゴ" onerror="this.src='https://via.placeholder.com/50?text=Fridge';">
                <h1 class="header-title">冷蔵庫コンシェルジュ</h1>
            </div>
            <div class="user-info">
                <span>日付: ${currentDate}</span> | 
                <span>${user.userName != null && !user.userName.isEmpty() ? user.userName : user.loginId}さん</span>
                <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">ログアウト</a>
            </div>
        </div>

        <!-- 賞味期限の近い食材 -->
        <fmt:parseDate value="${currentDate}" pattern="yyyy/MM/dd" var="todayDate" />
        
        <div class="section-title">賞味期限の近い食材（アラート食材）</div>
        <c:choose>
            <c:when test="${not empty alertStocks}">
                <table class="table">
                    <thead>
                        <tr>
                            <th>食材名</th>
                            <th>数量</th>
                            <th>賞味期限</th>
                            <th>状態</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="stock" items="${alertStocks}">
                            <tr>
                                <td>${stock.foodName}</td>
                                <td>${stock.quantity} ${stock.defaultUnit}</td>
                                <td>${stock.expirationDate}</td>
                                <td>
                               
                                <c:choose>
                                <c:when test="${stock.expirationDate lt todayDate}">
                                <span class="expired-badge">賞味期限切れ</span>
                                </c:when>
                                <c:otherwise>
                                <span class="alert-badge">賞味期限間近</span>
                                </c:otherwise>
                                </c:choose>
                                
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p class="empty-message">現在、賞味期限の近い食材はありません。</p>
            </c:otherwise>
        </c:choose>

        <!-- おすすめレシピ -->
        <div class="section-title">おすすめレシピ</div>
        <c:choose>
            <c:when test="${not empty recommendedRecipes}">
                <div class="recipe-grid">
                    <c:forEach var="recipe" items="${recommendedRecipes}">
                        <div class="recipe-card">
                            <img src="${pageContext.request.contextPath}/${recipe.imageUrl != null ? recipe.imageUrl : 'img/reizoukokun.jpg'}" alt="${recipe.recipeName}" class="recipe-img" onerror="this.src='https://via.placeholder.com/300x160?text=Recipe';">
                            <div class="recipe-content">
                                <h3 class="recipe-name">
                                    <a href="${pageContext.request.contextPath}/RecipeDetailServlet?recipeId=${recipe.recipeId}&from=main" class="recipe-link">${recipe.recipeName}</a>
                                </h3>
                                <p class="recipe-info">調理時間: ${recipe.cookingTime}分 | カテゴリ: ${recipe.recipeCategory}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p class="empty-message">おすすめレシピがありません。</p>
            </c:otherwise>
        </c:choose>

    </div>
    <c:if test="${param.status == 'success'}"><%@ include file="/WEB-INF/jsp/common_dialogs.jspf" %><script>openCommonModal('commonSuccessModal');</script></c:if>
    <%@ include file="/WEB-INF/jsp/common_footer.jspf" %>
</body>
</html>
