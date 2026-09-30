<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>食材登録 - 冷蔵庫コンシェルジュ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet"      href="${pageContext.request.contextPath}/css/S003_stock_register.css">
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
</head>
<body data-unsaved="true">
<%@ include file="/WEB-INF/jsp/common_header.jspf" %>
    <div class="container">
        <!-- Step 1 to 4 Indicator -->
        <div class="step-indicator">
            <span class="step-item ${sessionScope.reg_step == 1 ? 'active' : (sessionScope.reg_step > 1 ? 'completed' : '')}">1. カテゴリ</span>
            <span class="step-item ${sessionScope.reg_step == 2 ? 'active' : (sessionScope.reg_step > 2 ? 'completed' : '')}">2. 食材</span>
            <span class="step-item ${sessionScope.reg_step == 3 ? 'active' : (sessionScope.reg_step > 3 ? 'completed' : '')}">3. 数量</span>
            <span class="step-item ${sessionScope.reg_step == 4 ? 'active' : (sessionScope.reg_step > 4 ? 'completed' : '')}">4. 賞味期限</span>
        </div>

        <c:choose>
            <c:when test="${sessionScope.reg_step == 1}">
                <!-- STEP 1: CATEGORY -->
                <h2 class="header-title">カテゴリを選択してください</h2>
                <div class="grid-buttons">
                    <c:forEach var="cat" items="${categories}">
                        <form action="${pageContext.request.contextPath}/StockRegisterServlet" method="post" class="grid-form">
                            <input type="hidden" name="action" value="selectCategory">
                            <input type="hidden" name="categoryId" value="${cat.foodCategoryId}">
                            <button type="submit" class="btn-grid">
                             ${cat.foodCategoryName}
					        </button>
                        </form>
                    </c:forEach>
                </div>
            </c:when>

            <c:when test="${sessionScope.reg_step == 2}">
                <!-- STEP 2: FOOD -->
                <div class="selected-summary">
                    <span>選択中カテゴリ: ${sessionScope.reg_categoryName}</span>
                </div>
                <h2 class="header-title">食材を選択してください</h2>
                <div class="grid-buttons">
                    <c:forEach var="food" items="${foods}">
                        <form action="${pageContext.request.contextPath}/StockRegisterServlet" method="post" class="grid-form">
                            <input type="hidden" name="action" value="selectFood">
                            <input type="hidden" name="foodId" value="${food.foodId}">
                            <button type="submit" class="btn-grid" >${food.foodName}</button>
                        </form>
                    </c:forEach>
                </div>
            </c:when>

            <c:when test="${sessionScope.reg_step == 3}">
                <!-- STEP 3: QUANTITY -->
                <div class="selected-summary">
                    <span>カテゴリ: ${sessionScope.reg_categoryName}</span>
                    <span>食材: ${sessionScope.reg_foodName}</span>
                </div>
                <h2 class="header-title">数量を指定してください</h2>
                <form action="${pageContext.request.contextPath}/StockRegisterServlet" method="post" id="qtyForm">
                    <input type="hidden" name="action" value="step3ToStep4">
                    <div class="quantity-control">
                        <button type="button" class="btn-qty" onclick="changeQty(-1)">-</button>
                        <input type="number" id="quantityInput" name="quantity" value="${sessionScope.reg_quantity != null ? sessionScope.reg_quantity : 1}" min="1" class="qty-input">
                        <button type="button" class="btn-qty" onclick="changeQty(1)">+</button>
                    </div>
                </form>
            </c:when>

            <c:when test="${sessionScope.reg_step == 4}">
                <!-- STEP 4: EXPIRATION DATE & CONFIRMATION -->
                <div class="selected-summary">
                    <span>カテゴリ: ${sessionScope.reg_categoryName}</span>
                    <span>食材: ${sessionScope.reg_foodName}</span>
                    <span>数量: ${sessionScope.reg_quantity}</span>
                </div>
                <h2 class="header-title">賞味期限を指定してください</h2>
                <form action="${pageContext.request.contextPath}/StockRegisterServlet" method="post" id="confirmForm">
                    <input type="hidden" name="action" value="confirmSubmit">
                    <div class="date-control">
                        <input type="date" name="expirationDate" value="${sessionScope.reg_expirationDate}" class="date-input" required>
                    </div>
                    <p class="confirm-message">
                    上記の内容で登録してよろしいですか？</p>
                </form>
            </c:when>

            <c:when test="${sessionScope.reg_step == 5}">
                <!-- STEP 5: COMPLETED -->
                <div class="register-complete">
                    <h2>登録完了</h2>
                    <p>食材の登録が正常に完了しました。</p>
                    <div class="register-complete-buttons">
        <a href="${pageContext.request.contextPath}/StockRegisterServlet?action=reset"
           class="btn-nav blue">続けて入力</a>

        <a href="${pageContext.request.contextPath}/MainServlet"
           class="btn-nav green">メインに戻る</a>
    				</div>
                </div>
            </c:when>
        </c:choose>

        <!-- Navigation buttons for step 1 to 4 -->
        <c:if test="${sessionScope.reg_step >= 1 && sessionScope.reg_step <= 4}">
            <div class="nav-buttons">
                <div>
                    <c:choose>
                        <c:when test="${sessionScope.reg_step > 1}">
                            <form action="${pageContext.request.contextPath}/StockRegisterServlet" method="post" class="inline-form">
                                <input type="hidden" name="action" value="prevStep">
                                <button type="submit" class="btn-nav gray">ひとつ前に戻る</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/MainServlet" data-nav-target="${pageContext.request.contextPath}/MainServlet" class="btn-nav gray">ひとつ前に戻る</a>
                        </c:otherwise>
                    </c:choose>
                    <a href="${pageContext.request.contextPath}/StockRegisterServlet?action=reset" class="btn-nav gray reset-button">最初からやり直す</a>
                </div>

                <div>
                    <c:if test="${sessionScope.reg_step == 3}">
                        <button type="button" onclick="document.getElementById('qtyForm').submit();" class="btn-nav blue">次へ</button>
                    </c:if>
                    <c:if test="${sessionScope.reg_step == 4}">
                        <button type="button" onclick="openRegisterConfirm();" class="btn-nav green">内容を登録する</button>
                    </c:if>
                </div>
            </div>
        </c:if>
    </div>

    <script>
        function openRegisterConfirm() {
            openCommonModal('registerConfirmModal');
        }
        function submitRegister() {
            closeCommonModal('registerConfirmModal');
            openCommonModal('commonLoadingModal');
            document.getElementById('confirmForm').submit();
        }

        function changeQty(delta) {
            var input = document.getElementById('quantityInput');
            var val = parseInt(input.value) || 1;
            val += delta;
            if (val < 1) val = 1;
            input.value = val;
        }
    </script>

    <div class="common-modal" id="registerConfirmModal">
        <div class="common-modal-card">
            <p>この内容で登録してよろしいですか？</p>
            <div class="common-modal-actions">
                <button type="button" class="common-modal-btn secondary" onclick="closeCommonModal('registerConfirmModal')">キャンセル</button>
                <button type="button" class="common-modal-btn" onclick="submitRegister()">保存する</button>
            </div>
        </div>
    </div>
    <%@ include file="/WEB-INF/jsp/common_dialogs.jspf" %>
    <c:if test="${updateStatus == 'success'}"><script>document.body.setAttribute('data-unsaved','false'); openCommonModal('commonSuccessModal');</script></c:if>
    <c:if test="${updateStatus == 'failure'}"><script>openCommonModal('commonFailureModal');</script></c:if>
    <%@ include file="/WEB-INF/jsp/common_footer.jspf" %>
</body>
</html>
