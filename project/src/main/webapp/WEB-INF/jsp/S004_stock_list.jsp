<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>食材一覧 - 冷蔵庫コンシェルジュ</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet"      href="${pageContext.request.contextPath}/css/S004_stock_list.css">
    <script src="${pageContext.request.contextPath}/js/common.js"></script>
</head>
<body data-unsaved="${isEditMode}">
<%@ include file="/WEB-INF/jsp/common_header.jspf" %>
    <div class="container">
        <div class="header">
            <h1 class="header-title" id="pageTitle">${isEditMode ? '編集中' : '冷蔵庫内に格納されている食材'}</h1>
            <c:if test="${not isEditMode}">
                <button type="button" class="btn btn-orange" onclick="enableEditMode()">編集</button>
            </c:if>
        </div>

        <form action="${pageContext.request.contextPath}/StockListServlet" method="post" id="stockForm">
            <input type="hidden" name="action" value="updateBatch">
            
            <table class="table">
                <thead>
                    <tr>
                        <c:if test="${isEditMode}">
                            <th class="delete-column">削除</th>
                        </c:if>
                        <th>カテゴリ</th>
                        <th>食材名</th>
                        <th>数量</th>
                        <th>賞味期限</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty stocks}">
                            <c:forEach var="stock" items="${stocks}">
                                <tr id="row_${stock.stockId}">
                                    <input type="hidden" name="stockId" value="${stock.stockId}">
                                    <c:if test="${isEditMode}">
                                        <td>
                                            <input type="checkbox" name="deleteStockId" value="${stock.stockId}" onchange="toggleRowState(${stock.stockId}, this.checked)">
                                        </td>
                                    </c:if>
                                    <td>${stock.foodCategoryName}</td>
                                    <td>${stock.foodName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${isEditMode}">
                                                <input type="number" name="quantity_${stock.stockId}" value="${stock.quantity}" data-initial="${stock.quantity}" min="1" class="input-qty" id="qty_${stock.stockId}"> ${stock.defaultUnit}
                                            </c:when>
                                            <c:otherwise>
                                                ${stock.quantity} ${stock.defaultUnit}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${isEditMode}">
                                                <input type="date" name="expirationDate_${stock.stockId}" value="${stock.expirationDate}" data-initial="${stock.expirationDate}" class="input-date" id="exp_${stock.stockId}">
                                            </c:when>
                                            <c:otherwise>
                                                ${stock.expirationDate}
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="${isEditMode ? 5 : 4}" class="empty-message">在庫食材はありません。</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>

            <div class="action-buttons">
                <c:choose>
                    <c:when test="${isEditMode}">
                        <a href="${pageContext.request.contextPath}/StockListServlet" class="btn btn-gray" data-nav-target="${pageContext.request.contextPath}/StockListServlet">キャンセル</a>
                        <button type="button" class="btn btn-green" onclick="openUpdateConfirm()">更新</button>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/MainServlet" class="btn btn-gray">戻る</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </form>
    </div>

    <script>
        function openUpdateConfirm() {
            var editCount = 0, deleteCount = document.querySelectorAll('input[name="deleteStockId"]:checked').length;
            document.querySelectorAll('tr[id^="row_"]').forEach(function(row){
                var qty = row.querySelector('.input-qty');
                var exp = row.querySelector('.input-date');
                var deleted = row.querySelector('input[name="deleteStockId"]:checked');
                if (!deleted && ((qty && qty.value !== qty.getAttribute('data-initial')) || (exp && exp.value !== exp.getAttribute('data-initial')))) {
                    editCount++;
                }
            });
            var msg = 'この内容で登録してよろしいですか？';
            if (editCount > 0 && deleteCount > 0) msg += ' 編集' + editCount + '件／削除' + deleteCount + '件';
            else if (editCount > 0) msg += ' 編集' + editCount + '件';
            else if (deleteCount > 0) msg += ' 削除' + deleteCount + '件';
            document.getElementById('updateConfirmMessage').textContent = msg;
            openCommonModal('updateConfirmModal');
        }
        function submitUpdate() {
            closeCommonModal('updateConfirmModal');
            openCommonModal('commonLoadingModal');
            document.getElementById('stockForm').submit();
        }

        function enableEditMode() {
            window.location.href = "${pageContext.request.contextPath}/StockListServlet?mode=edit";
        }
        
        function toggleRowState(stockId, isChecked) {
            var row = document.getElementById('row_' + stockId);
            var qtyInput = document.getElementById('qty_' + stockId);
            var expInput = document.getElementById('exp_' + stockId);
            if (isChecked) {
                row.classList.add('disabled-row');
                if (qtyInput) qtyInput.disabled = true;
                if (expInput) expInput.disabled = true;
            } else {
                row.classList.remove('disabled-row');
                if (qtyInput) qtyInput.disabled = false;
                if (expInput) expInput.disabled = false;
            }
        }
    </script>
    <div class="common-modal" id="updateConfirmModal">
        <div class="common-modal-card">
            <p id="updateConfirmMessage">この内容で登録してよろしいですか？</p>
            <div class="common-modal-actions">
                <button type="button" class="common-modal-btn secondary" onclick="closeCommonModal('updateConfirmModal')">キャンセル</button>
                <button type="button" class="common-modal-btn" onclick="submitUpdate()">保存する</button>
            </div>
        </div>
    </div>
    <c:if test="${updateStatus == 'success'}"><script>document.body.setAttribute('data-unsaved','false'); openCommonModal('commonSuccessModal');</script></c:if>
    <c:if test="${updateStatus == 'failure'}"><script>openCommonModal('commonFailureModal');</script></c:if>
    <%@ include file="/WEB-INF/jsp/common_dialogs.jspf" %>
    <%@ include file="/WEB-INF/jsp/common_footer.jspf" %>
</body>
</html>
