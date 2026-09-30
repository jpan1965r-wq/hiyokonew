package com.example.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.dao.RecipeDAO;
import com.example.dao.StockDAO;
import com.example.model.Recipe;
import com.example.model.RecipeIngredient;
import com.example.model.Stock;
import com.example.model.User;

@WebServlet("/StockDeleteServlet")
public class StockDeleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        String recipeIdStr = request.getParameter("recipeId");
        String status = request.getParameter("status");

        if (recipeIdStr == null || recipeIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/MainServlet");
            return;
        }

        try {
            int recipeId = Integer.parseInt(recipeIdStr);
            RecipeDAO recipeDAO = new RecipeDAO();
            StockDAO stockDAO = new StockDAO();

            Recipe recipe = recipeDAO.findRecipeById(recipeId, user.getUserId());
            List<Stock> userStocks = stockDAO.findAllStocksByUserId(user.getUserId());

            Set<Integer> userStockFoodIds = new HashSet<>();
            for (Stock s : userStocks) {
                userStockFoodIds.add(s.getFoodId());
            }

            List<RecipeIngredient> consumeIngredients = new ArrayList<>();
            if (recipe != null) {
                for (RecipeIngredient ri : recipe.getIngredients()) {
                    if (userStockFoodIds.contains(ri.getFoodId())) {
                        consumeIngredients.add(ri);
                    }
                }
            }

            Set<Integer> alertFoodIds = new HashSet<>();
            List<Stock> alertStocks = stockDAO.findAlertStocksByUserId(user.getUserId());
            for (Stock s : alertStocks) {
                alertFoodIds.add(s.getFoodId());
            }

            request.setAttribute("recipe", recipe);
            request.setAttribute("consumeIngredients", consumeIngredients);
            request.setAttribute("alertFoodIds", alertFoodIds);
            request.setAttribute("updateStatus", status);
            //食材削除を追加
            request.setAttribute("commonPageName", "食材削除");

            request.getRequestDispatcher("/WEB-INF/jsp/S006_stock_delete.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("StockDeleteServlet error: " + e.getMessage(), e);
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
        String[] selectedFoodIds = request.getParameterValues("selectedFoodId");
        String recipeIdStr = request.getParameter("recipeId");

        try {
            if (selectedFoodIds == null || selectedFoodIds.length == 0) {
                // 既存実装の遷移先は変更せず、未選択状態のみ許容する。
                response.sendRedirect(request.getContextPath() + "/MainServlet");
                return;
            }

            StockDAO stockDAO = new StockDAO();
            for (String fidStr : selectedFoodIds) {
                int foodId = Integer.parseInt(fidStr);
                stockDAO.deleteStocksByFoodId(user.getUserId(), foodId);
            }
            response.sendRedirect(request.getContextPath() + "/MainServlet?status=success");
        } catch (Exception e) {
            String redirect = request.getContextPath() + "/StockDeleteServlet?status=failure";
            if (recipeIdStr != null && !recipeIdStr.isEmpty()) redirect += "&recipeId=" + recipeIdStr;
            response.sendRedirect(redirect);
        }
    }
}
