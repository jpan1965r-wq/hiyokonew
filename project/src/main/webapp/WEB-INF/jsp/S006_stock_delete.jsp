<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>食材削除 - 冷蔵庫コンシェルジュ</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet"   href="${pageContext.request.contextPath}/css/S006_stock_delete.css">
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
</head>
<body data-unsaved="false">
<%@include file="/WEB-INF/jsp/common_header.jspf" %>
    <div class="container">
        <h1 class="header-title">使った食材を選択してください</h1>
        <p class="description">※押下すると選択状態（削除対象）になります。賞味期限が近いものは赤字で表示されます。</p>

        <form action="${pageContext.request.contextPath}/StockDeleteServlet" method="post" id="deleteForm">
            <input type="hidden" name="recipeId" value="${recipe.recipeId}">
            <div id="hiddenInputsContainer"></div>
            
            <div class="grid-buttons">
                <c:choose>
                    <c:when test="${not empty consumeIngredients}">
                        <c:forEach var="ing" items="${consumeIngredients}">
                            <div class="btn-ingredient ${alertFoodIds.contains(ing.foodId) ? 'alert' : ''}" id="ing_btn_${ing.foodId}" onclick="toggleIngredient(${ing.foodId})">
                                ${ing.foodName} (${ing.quantity} ${ing.defaultUnit})
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="empty-message">在庫と一致するレシピ食材はありません。</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/RecipeDetailServlet?recipeId=${recipe.recipeId}" class="btn btn-gray">戻る</a>
                <button type="button" class="btn btn-red" onclick="handleNextClick()">次へ</button>
            </div>
        </form>
    </div>

    <!-- Confirm Modal -->
    <div class="modal-overlay" id="confirmModal">
        <div class="modal-card">
            <h3 id="modalTitle">削除確認</h3>
            <p id="modalMessage">選択した食材を在庫から削除します。よろしいですか？</p>
            <div class="modal-buttons">
                <button type="button" class="btn btn-gray" onclick="closeModal()">いいえ</button>
                <button type="button" class="btn btn-red" onclick="submitDeleteForm()">はい</button>
            </div>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/common_dialogs.jspf" %>
    <c:if test="${updateStatus == 'failure'}"><script>openCommonModal('commonFailureModal');</script></c:if>
    <script>
        var selectedFoodIds = new Set();

        function toggleIngredient(foodId) {
            var btn = document.getElementById('ing_btn_' + foodId);
            if (selectedFoodIds.has(foodId)) {
                selectedFoodIds.delete(foodId);
                btn.classList.remove('selected');
            } else {
                selectedFoodIds.add(foodId);
                btn.classList.add('selected');
            }
        }

        function handleNextClick() {
            var modalMsg = document.getElementById('modalMessage');
            if (selectedFoodIds.size === 0) {
                document.getElementById('modalTitle').innerText = "削除対象なし";
                modalMsg.innerText = "使用した食材が選択されていませんがよろしいですか？";
            } else {
                document.getElementById('modalTitle').innerText = "削除確認";
                modalMsg.innerText = "選択した食材を在庫から削除します。よろしいですか？";
            }
            document.getElementById('confirmModal').style.display = 'flex';
        }

        function closeModal() {
            document.getElementById('confirmModal').style.display = 'none';
        }

        function submitDeleteForm() {
            var container = document.getElementById('hiddenInputsContainer');
            container.innerHTML = '';
            selectedFoodIds.forEach(function(foodId) {
                var input = document.createElement('input');
                input.type = 'hidden';
                input.name = 'selectedFoodId';
                input.value = foodId;
                container.appendChild(input);
            });
            closeModal();
            openCommonModal('commonLoadingModal');
            document.getElementById('deleteForm').submit();
        }
    </script>
    <%@ include file="/WEB-INF/jsp/common_footer.jspf" %>
</body>
</html>
