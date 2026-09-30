package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Recipe;
import com.example.model.RecipeIngredient;
import com.example.util.DBUtil;

public class RecipeDAO {

    public List<Recipe> findAllRecipes(int userId) throws SQLException {
        List<Recipe> recipes = new ArrayList<>();
        String sql = "SELECT r.recipe_id, r.recipe_name, r.recipe_category, r.cooking_time, r.instructions, r.image_url, "
                + "(CASE WHEN f.favorite_id IS NOT NULL THEN true ELSE false END) AS is_favorite "
                + "FROM recipes r "
                + "LEFT JOIN favorite_recipes f ON r.recipe_id = f.recipe_id AND f.user_id = ? "
                + "ORDER BY r.recipe_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
            }
        }
        
        // 全レシピの材料を1回のSQLで取得
        Map<Integer, List<RecipeIngredient>> ingredientMap = new HashMap<>();

        String ingredientSql =
                "SELECT ri.recipe_id, ri.food_id, ri.quantity, "
              + "f.food_name, f.basic_unit "
              + "FROM recipe_ingredients ri "
              + "JOIN food_master f ON ri.food_id = f.food_id "
              + "ORDER BY ri.recipe_id ASC, ri.food_id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(ingredientSql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RecipeIngredient ri = new RecipeIngredient();

                ri.setRecipeId(rs.getInt("recipe_id"));
                ri.setFoodId(rs.getInt("food_id"));
                ri.setQuantity(rs.getInt("quantity"));
                ri.setFoodName(rs.getString("food_name"));
                ri.setDefaultUnit(rs.getString("basic_unit"));

                ingredientMap
                    .computeIfAbsent(ri.getRecipeId(), k -> new ArrayList<>())
                    .add(ri);
            }
        }

        // 各レシピに材料を設定
        for (Recipe r : recipes) {
            r.setIngredients(
                ingredientMap.getOrDefault(
                    r.getRecipeId(),
                    new ArrayList<>()
                )
            );
        }

        return recipes;
    }

    public List<Recipe> findFavoriteRecipes(int userId, int limit, int offset) throws SQLException {

    List<Recipe> recipes = new ArrayList<>();

    String sql =
            "SELECT r.recipe_id, r.recipe_name, r.recipe_category, "
          + "r.cooking_time, r.instructions, r.image_url, "
          + "true AS is_favorite "
          + "FROM recipes r "
          + "JOIN favorite_recipes f ON r.recipe_id = f.recipe_id "
          + "WHERE f.user_id = ? "
          + "ORDER BY r.recipe_id ASC "
          + "LIMIT ? OFFSET ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ps.setInt(2, limit);
        ps.setInt(3, offset);

        try (ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs));
            }
        }
    }

    return recipes;
    }

    public Recipe findRecipeById(int recipeId, int userId) throws SQLException {
        String sql = "SELECT r.recipe_id, r.recipe_name, r.recipe_category, r.cooking_time, r.instructions, r.image_url, "
                + "(CASE WHEN f.favorite_id IS NOT NULL THEN true ELSE false END) AS is_favorite "
                + "FROM recipes r "
                + "LEFT JOIN favorite_recipes f ON r.recipe_id = f.recipe_id AND f.user_id = ? "
                + "WHERE r.recipe_id = ?";
        Recipe recipe = null;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    recipe = mapResultSetToRecipe(rs);
                }
            }
        }
        if (recipe != null) {
            recipe.setIngredients(findIngredientsByRecipeId(recipeId));
        }
        return recipe;
    }

    public List<RecipeIngredient> findIngredientsByRecipeId(int recipeId) throws SQLException {
        List<RecipeIngredient> list = new ArrayList<>();
        String sql = "SELECT ri.recipe_id, ri.food_id, ri.quantity, f.food_name, f.basic_unit "
                + "FROM recipe_ingredients ri "
                + "JOIN food_master f ON ri.food_id = f.food_id "
                + "WHERE ri.recipe_id = ? "
                + "ORDER BY ri.food_id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recipeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RecipeIngredient ri = new RecipeIngredient();
                    ri.setRecipeId(rs.getInt("recipe_id"));
                    ri.setFoodId(rs.getInt("food_id"));
                    ri.setQuantity(rs.getInt("quantity"));
                    ri.setFoodName(rs.getString("food_name"));
                    ri.setDefaultUnit(rs.getString("basic_unit"));
                    list.add(ri);
                }
            }
        }
        return list;
    }

    public boolean addFavorite(int userId, int recipeId) throws SQLException {
        String checkSql = "SELECT favorite_id FROM favorite_recipes WHERE user_id = ? AND recipe_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        String sql = "INSERT INTO favorite_recipes (recipe_id, user_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recipeId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeFavorite(int userId, int recipeId) throws SQLException {
        String sql = "DELETE FROM favorite_recipes WHERE user_id = ? AND recipe_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, recipeId);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Recipe> findRecommendedRecipes(int userId) throws SQLException {
        //おすすめ専用のレシピ・材料情報を取得
    	List<Recipe> allRecipes = findRecommendationRecipes();
        if (allRecipes.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, java.sql.Date> stockEarliestExpMap = new HashMap<>();

        String alertSql = "SELECT food_id, MIN(expiration_date) AS min_exp FROM stocks WHERE user_id = ? GROUP BY food_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(alertSql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    stockEarliestExpMap.put(rs.getInt("food_id"), rs.getDate("min_exp"));
                }
            }
        }

        class RecipeScore {
            Recipe recipe;
            java.sql.Date earliestExp;
            int matchingFoodTypes;

            RecipeScore(Recipe r, java.sql.Date exp, int match) {
                this.recipe = r;
                this.earliestExp = exp;
                this.matchingFoodTypes = match;
            }
        }

        List<RecipeScore> scores = new ArrayList<>();
        for (Recipe r : allRecipes) {
            java.sql.Date minExp = null;
            int matchCount = 0;
            for (RecipeIngredient ri : r.getIngredients()) {
                if (stockEarliestExpMap.containsKey(ri.getFoodId())) {
                    matchCount++;
                    java.sql.Date exp = stockEarliestExpMap.get(ri.getFoodId());
                    if (minExp == null || (exp != null && exp.before(minExp))) {
                        minExp = exp;
                    }
                }
            }
            scores.add(new RecipeScore(r, minExp, matchCount));
        }

        Collections.sort(scores, new Comparator<RecipeScore>() {
            @Override
            public int compare(RecipeScore a, RecipeScore b) {
                if (a.earliestExp != null && b.earliestExp != null) {
                    int c = a.earliestExp.compareTo(b.earliestExp);
                    if (c != 0) {
                        return c;
                    }
                } else if (a.earliestExp != null) {
                    return -1;
                } else if (b.earliestExp != null) {
                    return 1;
                }

                if (a.matchingFoodTypes != b.matchingFoodTypes) {
                    return Integer.compare(b.matchingFoodTypes, a.matchingFoodTypes);
                }

                return Integer.compare(a.recipe.getRecipeId(), b.recipe.getRecipeId());
            }
        });

        List<Recipe> result = new ArrayList<>();
        for (int i = 0; i < Math.min(3, scores.size()); i++) {
            result.add(scores.get(i).recipe);
        }
        return result;
    }

    private List<Recipe> findRecommendationRecipes() throws SQLException {

        List<Recipe> recipes = new ArrayList<>();

        // おすすめ判定に必要な情報だけ取得
        String sql =
                "SELECT r.recipe_id, r.recipe_name, r.image_url, r.recipe_category, r.cooking_time, "
              + "ri.food_id, ri.quantity, "
              + "f.food_name, f.basic_unit "
              + "FROM recipes r "
              + "LEFT JOIN recipe_ingredients ri "
              + "ON r.recipe_id = ri.recipe_id "
              + "LEFT JOIN food_master f "
              + "ON ri.food_id = f.food_id "
              + "ORDER BY r.recipe_id ASC, ri.food_id ASC";

        // レシピIDごとにRecipeを管理
        Map<Integer, Recipe> recipeMap = new HashMap<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int recipeId = rs.getInt("recipe_id");

                // 初めて出てきたレシピの場合、Recipeを作成
                Recipe recipe = recipeMap.get(recipeId);

                if (recipe == null) {
                    recipe = new Recipe();

                    recipe.setRecipeId(recipeId);
                    recipe.setRecipeName(rs.getString("recipe_name"));
                    recipe.setImageUrl(rs.getString("image_url"));
                    recipe.setRecipeCategory(rs.getString("recipe_category"));
                    recipe.setCookingTime(rs.getInt("cooking_time"));
                    recipe.setIngredients(new ArrayList<>());

                    recipeMap.put(recipeId, recipe);
                }

                // 材料が存在する場合のみ追加
                int foodId = rs.getInt("food_id");

                if (!rs.wasNull()) {
                    RecipeIngredient ri = new RecipeIngredient();

                    ri.setRecipeId(recipeId);
                    ri.setFoodId(foodId);
                    ri.setQuantity(rs.getInt("quantity"));
                    ri.setFoodName(rs.getString("food_name"));
                    ri.setDefaultUnit(rs.getString("basic_unit"));

                    recipe.getIngredients().add(ri);
                }
            }
        }

        recipes.addAll(recipeMap.values());

        return recipes;
    }
    
    
    private Recipe mapResultSetToRecipe(ResultSet rs) throws SQLException {
        Recipe r = new Recipe();
        r.setRecipeId(rs.getInt("recipe_id"));
        r.setRecipeName(rs.getString("recipe_name"));
        r.setRecipeCategory(rs.getString("recipe_category"));
        r.setCookingTime(rs.getInt("cooking_time"));
        r.setInstructions(rs.getString("instructions"));
        r.setImageUrl(rs.getString("image_url"));
        r.setFavorite(rs.getBoolean("is_favorite"));
        return r;
    }


/**
 * 指定ユーザーのレシピをページング取得する。
 *
 * @param userId ユーザーID
 * @param limit  取得件数（ページサイズ）
 * @param offset 取得開始位置（(page-1) * limit）
 * @return limit 件数以下の Recipe リスト
 * @throws SQLException DB エラー時
 */
public List<Recipe> findRecipes(int userId, int limit, int offset) throws SQLException {
    List<Recipe> recipes = new ArrayList<>();
    String sql = "SELECT r.recipe_id, r.recipe_name, r.recipe_category, r.cooking_time, "
               + "r.instructions, r.image_url, "
               + "(CASE WHEN f.favorite_id IS NOT NULL THEN true ELSE false END) AS is_favorite "
               + "FROM recipes r "
               + "LEFT JOIN favorite_recipes f ON r.recipe_id = f.recipe_id AND f.user_id = ? "
               + "ORDER BY r.recipe_id ASC "
               + "LIMIT ? OFFSET ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.setInt(2, limit);
        ps.setInt(3, offset);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                recipes.add(mapResultSetToRecipe(rs));
            }
        }
    }
    // 各レシピに食材情報を付与（既存メソッドと同様）
    for (Recipe r : recipes) {
        r.setIngredients(findIngredientsByRecipeId(r.getRecipeId()));
    }
    return recipes;
}
/**
 * ユーザーが持つレシピ総数を取得する。
 *
 * @param userId ユーザーID
 * @return 総件数
 * @throws SQLException DB エラー時
 */
public int countRecipes(int userId) throws SQLException {
    String sql = "SELECT COUNT(*) FROM recipes";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        // ← ここにあった ps.setInt(1, userId); を削除しました
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    }
    return 0; 
}

public int countFavoriteRecipes(int userId) throws SQLException {

    String sql =
            "SELECT COUNT(*) "
          + "FROM favorite_recipes "
          + "WHERE user_id = ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, userId);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    }

    return 0;
}
    /**
     * お気に入り一覧をページング取得する。
     * 現在お気に入り登録されているレシピに加えて、
     * この画面でお気に入り解除したレシピも表示対象とする。
     *
     * @param userId ユーザーID
     * @param limit  取得件数
     * @param offset 取得開始位置
     * @param removedFavoriteIds この画面でお気に入り解除したレシピID
     */
    public List<Recipe> findFavoriteRecipesIncludingRemoved(
            int userId, int limit, int offset,
            List<Integer> removedFavoriteIds) throws SQLException {

        List<Recipe> recipes = new ArrayList<>();

        StringBuilder sql = new StringBuilder();

        sql.append(
                "SELECT r.recipe_id, r.recipe_name, r.recipe_category, "
              + "r.cooking_time, r.instructions, r.image_url, "
              + "(CASE WHEN f.favorite_id IS NOT NULL "
              + "THEN true ELSE false END) AS is_favorite "
              + "FROM recipes r "
              + "LEFT JOIN favorite_recipes f "
              + "ON r.recipe_id = f.recipe_id "
              + "AND f.user_id = ? "
              + "WHERE f.favorite_id IS NOT NULL"
        );

        if (removedFavoriteIds != null && !removedFavoriteIds.isEmpty()) {

            sql.append(" OR r.recipe_id IN (");

            for (int i = 0; i < removedFavoriteIds.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }

            sql.append(")");
        }

        sql.append(
                " ORDER BY r.recipe_id ASC "
              + "LIMIT ? OFFSET ?"
        );

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            // ユーザーID
            ps.setInt(paramIndex++, userId);

            // この画面で解除したレシピID
            if (removedFavoriteIds != null) {
                for (Integer recipeId : removedFavoriteIds) {
                    ps.setInt(paramIndex++, recipeId);
                }
            }

            // ページング
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    recipes.add(mapResultSetToRecipe(rs));
                }
            }
        }

        return recipes;
    }
    
    /**
     * お気に入り一覧の総件数を取得する。
     * 現在お気に入り登録されているレシピに加えて、
     * この画面でお気に入り解除したレシピも件数に含める。
     */
    public int countFavoriteRecipesIncludingRemoved(
            int userId, List<Integer> removedFavoriteIds) throws SQLException {

        StringBuilder sql = new StringBuilder();

        sql.append(
                "SELECT COUNT(*) "
              + "FROM recipes r "
              + "LEFT JOIN favorite_recipes f "
              + "ON r.recipe_id = f.recipe_id "
              + "AND f.user_id = ? "
              + "WHERE f.favorite_id IS NOT NULL"
        );

        if (removedFavoriteIds != null && !removedFavoriteIds.isEmpty()) {

            sql.append(" OR r.recipe_id IN (");

            for (int i = 0; i < removedFavoriteIds.size(); i++) {
                if (i > 0) {
                    sql.append(", ");
                }
                sql.append("?");
            }

            sql.append(")");
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;

            ps.setInt(paramIndex++, userId);

            if (removedFavoriteIds != null) {
                for (Integer recipeId : removedFavoriteIds) {
                    ps.setInt(paramIndex++, recipeId);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }
}