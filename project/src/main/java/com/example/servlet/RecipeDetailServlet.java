package com.example.servlet;

import java.io.IOException;
//importを追加
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.dao.RecipeDAO;
import com.example.model.Recipe;
import com.example.model.User;

@WebServlet("/RecipeDetailServlet")
public class RecipeDetailServlet extends HttpServlet {
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
        String from = request.getParameter("from");
        //ページング処理
        String page = request.getParameter("page");

        if (recipeIdStr == null || recipeIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/RecipeListServlet");
            return;
        }

        if (from != null && !from.isEmpty()) {
            session.setAttribute("recipeDetailSource", from);
        }
        //ページング処理
        if (page != null && !page.isEmpty()) {
            session.setAttribute("recipeDetailPage", page);
        }

        try {
            int recipeId = Integer.parseInt(recipeIdStr);
            RecipeDAO recipeDAO = new RecipeDAO();
            Recipe recipe = recipeDAO.findRecipeById(recipeId, user.getUserId());
            if (recipe == null) {
                response.sendRedirect(request.getContextPath() + "/RecipeListServlet");
                return;
            }

            request.setAttribute("recipe", recipe);
            
            //jspからservletに移動　S52～S56まで
            request.setAttribute("commonDate",
                    LocalDate.now().toString().replace('-', '/'));
            request.getRequestDispatcher("/WEB-INF/jsp/S007_recipe_detail.jsp").forward(request, response);

            
        } catch (Exception e) {
            throw new ServletException("RecipeDetailServlet error: " + e.getMessage(), e);
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

        try {
            if (recipeIdStr != null && !recipeIdStr.isEmpty()) {
                int recipeId = Integer.parseInt(recipeIdStr);
                RecipeDAO recipeDAO = new RecipeDAO();
                if ("addFavorite".equals(action)) {
                    recipeDAO.addFavorite(user.getUserId(), recipeId);
                } else if ("removeFavorite".equals(action)) {
                    recipeDAO.removeFavorite(user.getUserId(), recipeId);
                }
                
                String page = (String) session.getAttribute("recipeDetailPage");

                String redirectUrl = request.getContextPath()
                        + "/RecipeDetailServlet?recipeId=" + recipeId;

                if (page != null && !page.isEmpty()) {
                    redirectUrl += "&page=" + page;
                }

                response.sendRedirect(redirectUrl);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/RecipeListServlet");
        } catch (Exception e) {
            throw new ServletException("RecipeDetailServlet post error: " + e.getMessage(), e);
        }
    }
}
