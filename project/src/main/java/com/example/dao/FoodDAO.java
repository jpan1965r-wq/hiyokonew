package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.model.FoodCategory;
import com.example.model.FoodMaster;
import com.example.util.DBUtil;

public class FoodDAO {

    public List<FoodCategory> findAllCategories() throws SQLException {
        List<FoodCategory> list = new ArrayList<>();
        String sql = "SELECT food_category_id, food_category_name FROM food_categories ORDER BY food_category_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                FoodCategory cat = new FoodCategory();
                cat.setFoodCategoryId(rs.getInt("food_category_id"));
                cat.setFoodCategoryName(rs.getString("food_category_name"));
                list.add(cat);
            }
        }
        return list;
    }

    public List<FoodMaster> findFoodsByCategoryId(int categoryId) throws SQLException {
        List<FoodMaster> list = new ArrayList<>();
        String sql = "SELECT food_id, food_category_id, food_name, basic_unit, storage_type FROM food_master WHERE food_category_id = ? ORDER BY food_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FoodMaster fm = new FoodMaster();
                    fm.setFoodId(rs.getInt("food_id"));
                    fm.setFoodCategoryId(rs.getInt("food_category_id"));
                    fm.setFoodName(rs.getString("food_name"));
                    fm.setDefaultUnit(rs.getString("basic_unit"));
                    fm.setStorageType(rs.getString("storage_type"));
                    list.add(fm);
                }
            }
        }
        return list;
    }

    public FoodCategory findCategoryById(int categoryId) throws SQLException {
        String sql = "SELECT food_category_id, food_category_name FROM food_categories WHERE food_category_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FoodCategory(rs.getInt("food_category_id"), rs.getString("food_category_name"));
                }
            }
        }
        return null;
    }

    public FoodMaster findFoodById(int foodId) throws SQLException {
        String sql = "SELECT food_id, food_category_id, food_name, basic_unit, storage_type FROM food_master WHERE food_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new FoodMaster(
                            rs.getInt("food_id"),
                            rs.getInt("food_category_id"),
                            rs.getString("food_name"),
                            rs.getString("basic_unit"),
                            rs.getString("storage_type")
                    );
                }
            }
        }
        return null;
    }
}
