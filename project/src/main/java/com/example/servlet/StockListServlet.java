package com.example.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.dao.StockDAO;
import com.example.model.Stock;
import com.example.model.User;

@WebServlet("/StockListServlet")
public class StockListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        User user = (User) session.getAttribute("user");
        String mode = request.getParameter("mode");
        boolean isEditMode = "edit".equals(mode);
        String status = request.getParameter("status");

        try {
            StockDAO stockDAO = new StockDAO();
            List<Stock> stocks = stockDAO.findAllStocksByUserId(user.getUserId());
            // 共通ヘッダー用
            request.setAttribute("commonPageName", "食材一覧");
            request.setAttribute("commonDate",
                    LocalDate.now().toString().replace('-', '/'));
            request.setAttribute("stocks", stocks);
            request.setAttribute("isEditMode", isEditMode);
            request.setAttribute("updateStatus", status);
            // 共通フッター用 アクティブ時にフッターボタンを無効化           
            request.setAttribute("commonFooterPage", "list");

            request.getRequestDispatcher("/WEB-INF/jsp/S004_stock_list.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException("StockListServlet error: " + e.getMessage(), e);
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
            if ("updateBatch".equals(action)) {
                StockDAO stockDAO = new StockDAO();
                String[] stockIds = request.getParameterValues("stockId");
                String[] deleteIds = request.getParameterValues("deleteStockId");

                if (deleteIds != null) {
                    for (String dId : deleteIds) {
                        int stockId = Integer.parseInt(dId);
                        stockDAO.deleteStock(stockId, user.getUserId());
                    }
                }

                if (stockIds != null) {
                    for (String sId : stockIds) {
                        int stockId = Integer.parseInt(sId);
                        boolean isDeleted = false;
                        if (deleteIds != null) {
                            for (String dId : deleteIds) {
                                if (Integer.parseInt(dId) == stockId) {
                                    isDeleted = true;
                                    break;
                                }
                            }
                        }
                        if (!isDeleted) {
                            String qtyStr = request.getParameter("quantity_" + stockId);
                            String expStr = request.getParameter("expirationDate_" + stockId);
                            if (qtyStr != null && expStr != null) {
                                int qty = Integer.parseInt(qtyStr);
                                java.sql.Date expDate = java.sql.Date.valueOf(expStr);
                                stockDAO.updateStock(stockId, user.getUserId(), qty, expDate);
                            }
                        }
                    }
                }
            }
            response.sendRedirect(request.getContextPath() + "/StockListServlet?status=success");
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/StockListServlet?mode=edit&status=failure");
        }
    }
}
