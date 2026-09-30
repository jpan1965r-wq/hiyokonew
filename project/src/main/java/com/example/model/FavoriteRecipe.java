package com.example.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class FavoriteRecipe implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer favoriteId;
    private Integer recipeId;
    private Integer userId;
    private Timestamp createdAt;

    public FavoriteRecipe() {
    }

    public Integer getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Integer favoriteId) {
        this.favoriteId = favoriteId;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
