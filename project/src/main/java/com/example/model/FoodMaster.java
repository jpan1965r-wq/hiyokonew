package com.example.model;

import java.io.Serializable;

public class FoodMaster implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer foodId;
    private Integer foodCategoryId;
    private String foodName;
    private String defaultUnit;
    private String storageType;

    public FoodMaster() {
    }

    public FoodMaster(Integer foodId, Integer foodCategoryId, String foodName, String defaultUnit, String storageType) {
        this.foodId = foodId;
        this.foodCategoryId = foodCategoryId;
        this.foodName = foodName;
        this.defaultUnit = defaultUnit;
        this.storageType = storageType;
    }

    public Integer getFoodId() {
        return foodId;
    }

    public void setFoodId(Integer foodId) {
        this.foodId = foodId;
    }

    public Integer getFoodCategoryId() {
        return foodCategoryId;
    }

    public void setFoodCategoryId(Integer foodCategoryId) {
        this.foodCategoryId = foodCategoryId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }

    public void setDefaultUnit(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }
}
