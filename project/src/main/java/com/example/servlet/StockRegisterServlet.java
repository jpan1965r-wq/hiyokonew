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
            FoodDAO foodDAO = new FoodDAO();
            List<FoodCategory> categories = foodDAO.findAllCategories();
            request.setAttribute("categories", categories);

            Integer categoryId = (Integer) session.getAttribute("reg_categoryId");
            if (categoryId != null) {
                List<FoodMaster> foods = foodDAO.findFoodsByCategoryId(categoryId);
                request.setAttribute("foods", foods);
            }
            // 共通ヘッダー用
            request.setAttribute("commonPageName", "食材登録");
            request.setAttribute("commonDate",
                    LocalDate.now().toString().replace('-', '/'));
            // 共通フッター用 アクティブ時にフッターボタンを無効化          
            request.setAttribute("commonFooterPage", "register");

            request.getRequestDispatcher("/WEB-INF/jsp/S003_stock_register.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("StockRegisterServlet error: " + e.getMessage(), e);
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

        try {
            FoodDAO foodDAO = new FoodDAO();
            StockDAO stockDAO = new StockDAO();

            if ("selectCategory".equals(action)) {
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                FoodCategory category = foodDAO.findCategoryById(categoryId);
                session.setAttribute("reg_categoryId", categoryId);
                session.setAttribute("reg_categoryName", category != null ? category.getFoodCategoryName() : "");
                session.setAttribute("reg_step", 2);
            } else if ("selectFood".equals(action)) {
                int foodId = Integer.parseInt(request.getParameter("foodId"));
                FoodMaster food = foodDAO.findFoodById(foodId);
                session.setAttribute("reg_foodId", foodId);
                session.setAttribute("reg_foodName", food != null ? food.getFoodName() : "");
                session.setAttribute("reg_quantity", 1);
                session.setAttribute("reg_step", 3);
            } else if ("updateQuantity".equals(action)) {
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                if (quantity < 1) quantity = 1;
                session.setAttribute("reg_quantity", quantity);
            } else if ("step3ToStep4".equals(action)) {
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                if (quantity < 1) quantity = 1;
                session.setAttribute("reg_quantity", quantity);

                String categoryName = (String) session.getAttribute("reg_categoryName");
                LocalDate defaultExpDate = LocalDate.now();
                if ("野菜".equals(categoryName)) {
                    defaultExpDate = defaultExpDate.plusDays(7);
                }
                session.setAttribute("reg_expirationDate", defaultExpDate.toString());
                session.setAttribute("reg_step", 4);
            } else if ("updateExpirationDate".equals(action)) {
                String expDate = request.getParameter("expirationDate");
                session.setAttribute("reg_expirationDate", expDate);
            } else if ("prevStep".equals(action)) {
                Integer currentStep = (Integer) session.getAttribute("reg_step");
                if (currentStep != null && currentStep > 1) {
                    session.setAttribute("reg_step", currentStep - 1);
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
