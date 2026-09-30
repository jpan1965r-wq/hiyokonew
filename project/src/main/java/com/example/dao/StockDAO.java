package com.example.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Stock;
import com.example.util.DBUtil;

public class StockDAO {

    public List<Stock> findAlertStocksByUserId(int userId) throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, s.user_id, s.food_id, s.quantity, s.created_at, s.expiration_date, s.last_checked_at, "
                + "f.food_name, f.basic_unit, c.food_category_name "
                + "FROM stocks s "
                + "JOIN food_master f ON s.food_id = f.food_id "
                + "LEFT JOIN food_categories c ON f.food_category_id = c.food_category_id "
                + "WHERE s.user_id = ? AND s.expiration_date <= CURRENT_DATE + INTERVAL '3 days' "
                + "ORDER BY s.expiration_date ASC, s.stock_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Stock stock = mapResultSetToStock(rs);
                    list.add(stock);
                }
            }
        }
        return list;
    }

    public List<Stock> findAllStocksByUserId(int userId) throws SQLException {
        List<Stock> list = new ArrayList<>();
        String sql = "SELECT s.stock_id, s.user_id, s.food_id, s.quantity, s.created_at, s.expiration_date, s.last_checked_at, "
                + "f.food_name, f.basic_unit, c.food_category_name "
                + "FROM stocks s "
                + "JOIN food_master f ON s.food_id = f.food_id "
                + "LEFT JOIN food_categories c ON f.food_category_id = c.food_category_id "
                + "WHERE s.user_id = ? "
                + "ORDER BY s.expiration_date ASC, s.stock_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Stock stock = mapResultSetToStock(rs);
                    list.add(stock);
                }
            }
        }
        return list;
    }

    public boolean insertStock(int userId, int foodId, int quantity, Date expirationDate) throws SQLException {
        String sql = "INSERT INTO stocks (user_id, food_id, quantity, created_at, expiration_date, last_checked_at) "
                + "VALUES (?, ?, ?, CURRENT_DATE, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, foodId);
            ps.setInt(3, quantity);
            ps.setDate(4, expirationDate);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateStock(int stockId, int userId, int quantity, Date expirationDate) throws SQLException {
        String sql = "UPDATE stocks SET quantity = ?, expiration_date = ?, last_checked_at = CURRENT_TIMESTAMP "
                + "WHERE stock_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setDate(2, expirationDate);
            ps.setInt(3, stockId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStock(int stockId, int userId) throws SQLException {
        String sql = "DELETE FROM stocks WHERE stock_id = ? AND user_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStocksByFoodId(int userId, int foodId) throws SQLException {
        String sql = "DELETE FROM stocks WHERE user_id = ? AND food_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, foodId);
            return ps.executeUpdate() > 0;
        }
    }

    private Stock mapResultSetToStock(ResultSet rs) throws SQLException {
        Stock stock = new Stock();
        stock.setStockId(rs.getInt("stock_id"));
        stock.setUserId(rs.getInt("user_id"));
        stock.setFoodId(rs.getInt("food_id"));
        stock.setQuantity(rs.getInt("quantity"));
        stock.setCreatedAt(rs.getDate("created_at"));
        stock.setExpirationDate(rs.getDate("expiration_date"));
        stock.setLastCheckedAt(rs.getTimestamp("last_checked_at"));
        stock.setFoodName(rs.getString("food_name"));
        stock.setDefaultUnit(rs.getString("basic_unit"));
        stock.setFoodCategoryName(rs.getString("food_category_name"));
        return stock;
    }
}
