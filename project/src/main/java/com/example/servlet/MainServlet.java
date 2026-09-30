package com.example.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.dao.RecipeDAO;
import com.example.dao.StockDAO;
import com.example.model.Recipe;
import com.example.model.Stock;
import com.example.model.User;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getUserId();

        try {
            StockDAO stockDAO = new StockDAO();
            RecipeDAO recipeDAO = new RecipeDAO();

            List<Stock> alertStocks = stockDAO.findAlertStocksByUserId(userId);
            List<Recipe> recommendedRecipes = recipeDAO.findRecommendedRecipes(userId);

            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            request.setAttribute("currentDate", formattedDate);
            request.setAttribute("alertStocks", alertStocks);
            request.setAttribute("recommendedRecipes", recommendedRecipes);
            //共通フッター アクティブ時にフッターボタンを無効化
            request.setAttribute("commonFooterPage", "main");

            request.getRequestDispatcher("/WEB-INF/jsp/S002_main.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("MainServlet error: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
