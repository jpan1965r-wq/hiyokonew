package com.example.servlet;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.dao.FoodDAO;
import com.example.dao.StockDAO;
import com.example.model.FoodCategory;
import com.example.model.FoodMaster;
import com.example.model.User;

@WebServlet("/StockRegisterServlet")
public class StockRegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        String action = request.getParameter("action");
        String status = request.getParameter("status");
        if (status != null && !status.isEmpty()) {
            request.setAttribute("updateStatus", status);
        }
        if ("reset".equals(action)) {
            session.removeAttribute("reg_step");
            session.removeAttribute("reg_categoryId");
            session.removeAttribute("reg_categoryName");
            session.removeAttribute("reg_foodId");
            session.removeAttribute("reg_foodName");
            session.removeAttribute("reg_quantity");
            session.removeAttribute("reg_expirationDate");
        }

        Integer step = (Integer) session.getAttribute("reg_step");
        if (step == null) {
            step = 1;
            session.setAttribute("reg_step", 1);
        }

        try {
            FoodDAO foodDAO = new FoodDAO();// 食品マスタのDAOクラスのインスタンスを生成	
            List<FoodCategory> categories = foodDAO.findAllCategories();
            request.setAttribute("categories", categories);

            Integer categoryId = (Integer) session.getAttribute("reg_categoryId");
            if (categoryId != null) {
                List<FoodMaster> foods = foodDAO.findFoodsByCategoryId(categoryId);// 食品マスタのDAOクラスのメソッドを呼び出して、指定されたカテゴリIDに基づいて食品のリストを取得
                request.setAttribute("foods", foods);// 取得した食品のリストをリクエスト属性に設定
            }
            // 共通ヘッダー用
            request.setAttribute("commonPageName", "食材登録");// リクエスト属性にページ名を設定
            request.setAttribute("commonDate",// リクエスト属性に現在の日付を設定
                    LocalDate.now().toString().replace('-', '/'));// 
                     
            request.setAttribute("commonFooterPage", "register");// リクエスト属性にフッターページ名を設定

            request.getRequestDispatcher("/WEB-INF/jsp/S003_stock_register.jsp").forward(request, response);// JSPファイルにリクエストを転送して、画面を表示
        } catch (Exception e) {// 例外が発生した場合の処理
            throw new ServletException("StockRegisterServlet error: " + e.getMessage(), e);// 例外をラップしてServletExceptionとしてスロー
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {// POSTリクエストの処理
        request.setCharacterEncoding("UTF-8");// リクエストの文字エンコーディングをUTF-8に設定
        HttpSession session = request.getSession(false);// セッションを取得
        if (session == null || session.getAttribute("user") == null) {// セッションが存在しないか、ユーザーがログインしていない場合の処理
            response.sendRedirect(request.getContextPath() + "/LoginServlet");// ログインページにリダイレクト
            return;// 以降の処理を中断
        }

        User user = (User) session.getAttribute("user");// セッションからユーザー情報を取得
        String action = request.getParameter("action");// リクエストパラメータからアクションを取得

        try {// 例外処理の開始
            FoodDAO foodDAO = new FoodDAO();// 食品マスタのDAOクラスのインスタンスを生成
            StockDAO stockDAO = new StockDAO();// 在庫管理のDAOクラスのインスタンスを生成

            if ("selectCategory".equals(action)) {
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));// リクエストパラメータからカテゴリIDを取得
                FoodCategory category = foodDAO.findCategoryById(categoryId);// 食品マスタのDAOクラスのメソッドを呼び出して、指定されたカテゴリIDに基づいてカテゴリ情報を取得
                session.setAttribute("reg_categoryId", categoryId);// セッションにカテゴリIDを設定
                session.setAttribute("reg_categoryName", category != null ? category.getFoodCategoryName() : "");// セッションにカテゴリ名を設定
                session.setAttribute("reg_step", 2);// セッションにステップを設定
            } else if ("selectFood".equals(action)) {// リクエストパラメータから食品IDを取得
                int foodId = Integer.parseInt(request.getParameter("foodId"));// 食品マスタのDAOクラスのメソッドを呼び出して、指定された食品IDに基づいて食品情報を取得
                FoodMaster food = foodDAO.findFoodById(foodId);// 食品マスタのDAOクラスのメソッドを呼び出して、指定された食品IDに基づいて食品情報を取得
                session.setAttribute("reg_foodId", foodId);// セッションに食品IDを設定
                session.setAttribute("reg_foodName", food != null ? food.getFoodName() : "");// セッションに食品名を設定
                session.setAttribute("reg_quantity", 1);// セッションに数量を設定（初期値は1）
                session.setAttribute("reg_step", 3);// セッションにステップを設定
            } else if ("updateQuantity".equals(action)) {// リクエストパラメータから数量を取得
                int quantity = Integer.parseInt(request.getParameter("quantity"));// 数量が1未満の場合は1に設定
                if (quantity < 1) quantity = 1;// セッションに数量を設定
                session.setAttribute("reg_quantity", quantity);// セッションにステップを設定
            } else if ("step3ToStep4".equals(action)) {// リクエストパラメータから数量を取得
                int quantity = Integer.parseInt(request.getParameter("quantity"));// 数量が1未満の場合は1に設定
                if (quantity < 1) quantity = 1;// セッションに数量を設定
                session.setAttribute("reg_quantity", quantity);// セッションにステップを設定

                String categoryName = (String) session.getAttribute("reg_categoryName");// セッションからカテゴリ名を取得
                LocalDate defaultExpDate = LocalDate.now();// デフォルトの賞味期限を現在の日付に設定
                if ("野菜".equals(categoryName)) {// カテゴリ名が「野菜」の場合は7日後に設定
                    defaultExpDate = defaultExpDate.plusDays(7);// カテゴリ名が「肉」の場合は5日後に設定????
                }
                session.setAttribute("reg_expirationDate", defaultExpDate.toString());// セッションにデフォルトの賞味期限を設定
                session.setAttribute("reg_step", 4);// セッションにステップを設定
            } else if ("updateExpirationDate".equals(action)) {// リクエストパラメータから賞味期限を取得
                String expDate = request.getParameter("expirationDate");// セッションに賞味期限を設定
                session.setAttribute("reg_expirationDate", expDate);// セッションにステップを設定
            } else if ("prevStep".equals(action)) {// リクエストパラメータからアクションを取得
                Integer currentStep = (Integer) session.getAttribute("reg_step");// セッションから現在のステップを取得
                if (currentStep != null && currentStep > 1) {// 現在のステップが1より大きい場合は1つ前のステップに戻す
                    session.setAttribute("reg_step", currentStep - 1);// セッションにステップを設定
                }
            } else if ("nextStep".equals(action)) {
                Integer currentStep = (Integer) session.getAttribute("reg_step");
                if (currentStep != null && currentStep < 4) {
                    session.setAttribute("reg_step", currentStep + 1);
                }
            } else if ("confirmSubmit".equals(action)) {
                Integer foodId = (Integer) session.getAttribute("reg_foodId");
                Integer quantity = (Integer) session.getAttribute("reg_quantity");

                // STEP4で入力された賞味期限を取得
                String expDateStr = request.getParameter("expirationDate");

                if (foodId != null && quantity != null && expDateStr != null && !expDateStr.isEmpty()) {
                    Date expDate = Date.valueOf(expDateStr);
                    boolean inserted = stockDAO.insertStock(user.getUserId(), foodId, quantity, expDate);
                    if (!inserted) {
                        response.sendRedirect(request.getContextPath() + "/StockRegisterServlet?status=failure");
                        return;
                    }

                    // セッションにも最新の賞味期限を保持
                    session.setAttribute("reg_expirationDate", expDateStr);
                    session.setAttribute("reg_step", 5);
                    response.sendRedirect(request.getContextPath() + "/StockRegisterServlet?status=success");
                    return;
                } else {
                    response.sendRedirect(request.getContextPath() + "/StockRegisterServlet?status=failure");
                    return;
                }
            } else if ("reset".equals(action)) {
                session.removeAttribute("reg_step");
                session.removeAttribute("reg_categoryId");
                session.removeAttribute("reg_categoryName");
                session.removeAttribute("reg_foodId");
                session.removeAttribute("reg_foodName");
                session.removeAttribute("reg_quantity");
                session.removeAttribute("reg_expirationDate");
                session.setAttribute("reg_step", 1);
            }

            response.sendRedirect(request.getContextPath() + "/StockRegisterServlet");
        } catch (Exception e) {
            session.setAttribute("reg_status", "failure");
            response.sendRedirect(request.getContextPath() + "/StockRegisterServlet?status=failure");
        }
    }
}
