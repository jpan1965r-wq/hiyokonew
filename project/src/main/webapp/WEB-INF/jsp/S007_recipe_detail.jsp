<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>レシピ詳細 - 冷蔵庫コンシェルジュ</title>
	<link rel="stylesheet"
    	  href="${pageContext.request.contextPath}/css/common.css">

	<link rel="stylesheet"
    	  href="${pageContext.request.contextPath}/css/S007_recipe_detail.css">

	<script src="${pageContext.request.contextPath}/js/common.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
</head>
<body data-unsaved="false">
    <div class="container">
        <div class="header">
    <div class="recipe-title-box">
        <div class="recipe-title-info">
            <div class="recipe-subtitle">
                冷蔵庫コンシェルジュ
            </div>

            <h1 class="recipe-title">
                ${recipe.recipeName}
            </h1>
        </div>
    </div>

    <span class="recipe-info">
        日付: ${commonDate}<br>
        調理時間: ${recipe.cookingTime}分
    </span>
</div>
    <div class="favorite-image-area">

        <img src="${pageContext.request.contextPath}/${recipe.imageUrl != null ? recipe.imageUrl : 'img/reizoukokun.jpg'}" 
        alt="${recipe.recipeName}" 
        class="recipe-image-large" 
        onerror="this.src='https://via.placeholder.com/800x350?text=Recipe+Detail';">

    <div class="favorite-area">

        <form action="${pageContext.request.contextPath}/RecipeDetailServlet"
              method="post"
              class="favorite-form">
  
            <input type="hidden" name="recipeId" value="${recipe.recipeId}">

            <c:choose>
                <c:when test="${recipe.favorite}">
                    <input type="hidden" name="action" value="removeFavorite">
                    <button type="submit"
                            class="btn-fav"
                            title="お気に入り解除">★</button>
                </c:when>

                <c:otherwise>
                    <input type="hidden" name="action" value="addFavorite">
                    <button type="submit"
                            class="btn-fav btn-fav-off"
                            title="お気に入り登録">☆</button>
                </c:otherwise>
            </c:choose>

        </form>
    </div>
</div>        

        <div class="section-title">材料</div>
        <ul class="ingredients-list">
            <c:forEach var="ing" items="${recipe.ingredients}">
                <li>
                    <span>${ing.foodName}</span>
                    <span>${ing.quantity} ${ing.defaultUnit}</span>
                </li>
            </c:forEach>
        </ul>

        <div class="section-title">作り方</div>
        <div class="instructions-box">${recipe.instructions}</div>

        <div class="action-buttons">
            <c:choose>
                <c:when test="${sessionScope.recipeDetailSource == 'main'}">
                    <a href="${pageContext.request.contextPath}/MainServlet" class="btn btn-gray">戻る</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/RecipeListServlet?page=${sessionScope.recipeDetailPage}" class="btn btn-gray">戻る</a>
                </c:otherwise>
            </c:choose>
            
            <a href="${pageContext.request.contextPath}/StockDeleteServlet?recipeId=${recipe.recipeId}" class="btn btn-green">作った！</a>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/common_footer.jspf" %>
</body>
</html>
