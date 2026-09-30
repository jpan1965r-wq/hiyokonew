<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>レシピ一覧 - 冷蔵庫コンシェルジュ</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet"      href="${pageContext.request.contextPath}/css/S005_recipe_list.css">
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
</head>
<body data-unsaved="false">
<%@ include file="/WEB-INF/jsp/common_header.jspf" %>
    <div class="container">
        <div class="header">
            <div>
                <h1 class="header-title">レシピを探す</h1>
            </div>
            <c:choose>
                <c:when test="${isFavoriteOnly}">
                    <a href="${pageContext.request.contextPath}/RecipeListServlet" class="btn btn-blue">全レシピ表示</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/RecipeListServlet?filter=favorite" class="btn btn-blue">お気に入り</a>
                </c:otherwise>
            </c:choose>
        </div>

        <table class="recipe-table">
            <thead>
                <tr>
                    <th class="favorite-column">お気に入り</th>
                    <th class="image-column">イメージ</th>
                    <th>レシピ名</th>
                    <th>カテゴリ</th>
                    <th>調理時間</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty recipes}">
                        <c:forEach var="recipe" items="${recipes}">
                            <tr>
                                <td class="favorite-cell">
                                    <form action="${pageContext.request.contextPath}/RecipeListServlet"
      method="post"
      class="favorite-form">
                                        <input type="hidden" name="recipeId" value="${recipe.recipeId}">
                                            <input type="hidden" name="page" value="${currentPage}">
                                        
                                        <c:if test="${isFavoriteOnly}">
                                            <input type="hidden" name="filter" value="favorite">
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${recipe.favorite}">
                                                <input type="hidden" name="action" value="removeFavorite">
                                                <button type="submit" class="btn-fav" title="お気に入り解除">★</button>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="hidden" name="action" value="addFavorite">
                                                <button type="submit" class="btn-fav btn-fav-off" title="お気に入り登録">☆</button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </td>
                                <td>
                                    <img src="${pageContext.request.contextPath}/${recipe.imageUrl != null ? recipe.imageUrl : 'img/reizoukokun.jpg'}" alt="${recipe.recipeName}" class="thumb-img" onerror="this.src='https://via.placeholder.com/80x60?text=Recipe';">
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/RecipeDetailServlet?recipeId=${recipe.recipeId}&from=recipeList&page=${currentPage}" class="recipe-link">${recipe.recipeName}</a>
                                </td>
                                <td>${recipe.recipeCategory}</td>
                                <td>${recipe.cookingTime}分</td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="5" class="empty-message">該当するレシピはありません。</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
<!-- ==== ページングナビゲーション ==== -->
<div class="pagination" style="margin-top: 1rem; text-align: center;">
    <c:if test="${currentPage > 1}">
        <c:choose>
            <c:when test="${isFavoriteOnly}">
                <a href="${pageContext.request.contextPath}/RecipeListServlet?page=${currentPage - 1}&amp;filter=favorite">
                    Prev
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/RecipeListServlet?page=${currentPage - 1}">
                    Prev
                </a>
            </c:otherwise>
        </c:choose>
    </c:if>

    <!-- 現在のページ -->
    <span style="margin: 0 0.5rem;">
        Page ${currentPage} of ${totalPages}
    </span>

    <!-- 次のページ -->
    <c:if test="${currentPage < totalPages}">
        <c:choose>
            <c:when test="${isFavoriteOnly}">
                <a href="${pageContext.request.contextPath}/RecipeListServlet?page=${currentPage + 1}&amp;filter=favorite">
                    Next
                </a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/RecipeListServlet?page=${currentPage + 1}">
                    Next
                </a>
            </c:otherwise>
        </c:choose>
    </c:if>

</div>
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/MainServlet" class="btn btn-gray">戻る</a>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/common_footer.jspf" %>
</body>
</html>
