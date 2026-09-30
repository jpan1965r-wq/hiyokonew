package com.example.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.dao.RecipeDAO;
import com.example.model.Recipe;
import com.example.model.User;

@WebServlet("/RecipeListServlet")
public class RecipeListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }
        User user = (User) session.getAttribute("user");
        String filter = request.getParameter("filter");
        // ---------- ページングパラメータ ----------
        final int PAGE_SIZE = 6;                         // 1ページあたりの表示件数
        String pageParam = request.getParameter("page");
        int page = 1;                                    // デフォルトは 1 ページ目
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignore) { /* そのまま 1 */ }
        }
        int offset = (page - 1) * PAGE_SIZE;
        try {
            RecipeDAO recipeDAO = new RecipeDAO();
            // ① ページングされたレシピ取得
            List<Recipe> recipes;
            // ② 総件数を取得し、ページ数を算出
            int totalCount;
            // ③ お気に入りフィルタの処理（この画面ではページングは行わないが、表示は維持）
            if ("favorite".equals(filter)) {
                // ページ指定なしでお気に入り一覧を開いた場合は、
                // 前回の解除情報をクリアして新しい一覧を開始する
                if (pageParam == null || pageParam.isEmpty()) {
                    session.removeAttribute("removedFavoriteIds");
                }
            	@SuppressWarnings("unchecked")
            	List<Integer> removedFavoriteIds =
            		(List<Integer>) session.getAttribute("removedFavoriteIds");
            	if (removedFavoriteIds == null) {
            		removedFavoriteIds = new ArrayList<>();
            	}
            	recipes = recipeDAO.findFavoriteRecipesIncludingRemoved(
            			user.getUserId(),
            			PAGE_SIZE,
            			offset,
            			removedFavoriteIds);

            	totalCount = recipeDAO.countFavoriteRecipesIncludingRemoved(
            			user.getUserId(),
            			removedFavoriteIds);

            	request.setAttribute("isFavoriteOnly", true);


            } else {
            	recipes = recipeDAO.findRecipes(
                    user.getUserId(), PAGE_SIZE, offset);
            totalCount = recipeDAO.countRecipes(user.getUserId());
            request.setAttribute("isFavoriteOnly", false);
        }
        int totalPages = (int) Math.ceil((double) totalCount / PAGE_SIZE);
        if (totalPages == 0) {
            totalPages = 1;
        }
            // ④ JSP へ渡す属性設定
            request.setAttribute("recipes", recipes);
            request.setAttribute("commonPageName", "レシピ一覧");
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", PAGE_SIZE);   // デバッグ用に残すだけ
            // 共通フッター用 アクティブ時にフッターボタンを無効化          
            request.setAttribute("commonFooterPage", "recipe");
           
            request.getRequestDispatcher("/WEB-INF/jsp/S005_recipe_list.jsp")
                    .forward(request, response);
        } catch (Exception e) {
            throw new ServletException("RecipeListServlet error: " + e.getMessage(), e);
        }
    }
          

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        String recipeIdStr = request.getParameter("recipeId");
        String filter = request.getParameter("filter");
        //ページング
        String page = request.getParameter("page");

        try {
            if (recipeIdStr != null && !recipeIdStr.isEmpty()) {
                int recipeId = Integer.parseInt(recipeIdStr);
                RecipeDAO recipeDAO = new RecipeDAO();
                if ("addFavorite".equals(action)) {

                    recipeDAO.addFavorite(user.getUserId(), recipeId);

                    // 再度お気に入り登録した場合は、
                    // 「解除済み表示」の対象から外す
                    @SuppressWarnings("unchecked")
                    List<Integer> removedFavoriteIds =
                            (List<Integer>) session.getAttribute("removedFavoriteIds");

                    if (removedFavoriteIds != null) {
                        removedFavoriteIds.remove(Integer.valueOf(recipeId));
                        session.setAttribute("removedFavoriteIds", removedFavoriteIds);
                    }

                } else if ("removeFavorite".equals(action)) {

                    recipeDAO.removeFavorite(user.getUserId(), recipeId);

                    // この画面にいる間は解除したレシピも表示する
                    @SuppressWarnings("unchecked")
                    List<Integer> removedFavoriteIds =
                            (List<Integer>) session.getAttribute("removedFavoriteIds");

                    if (removedFavoriteIds == null) {
                        removedFavoriteIds = new ArrayList<>();
                    }

                    if (!removedFavoriteIds.contains(recipeId)) {
                        removedFavoriteIds.add(recipeId);
                    }

                    session.setAttribute("removedFavoriteIds", removedFavoriteIds);
                }
            String redirectUrl = request.getContextPath() + "/RecipeListServlet";
            //ページング
            if (page != null && !page.isEmpty()) {
                redirectUrl += "?page=" + page;
            }
            
            if ("favorite".equals(filter)) {
            	if (redirectUrl.contains("?")) {
                redirectUrl += "&filter=favorite";
            } else {
                redirectUrl += "?filter=favorite";
            }
        }
            response.sendRedirect(redirectUrl);
        }
            } catch (Exception e) {
            throw new ServletException("RecipeListServlet post error: " + e.getMessage(), e);
        }
    }
}
