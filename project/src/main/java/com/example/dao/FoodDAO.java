package com.example.dao;//食品カテゴリと食品マスタのデータアクセスオブジェクトを提供するパッケージ

import java.sql.Connection;//データベース接続を管理するためのクラス
import java.sql.PreparedStatement;//SQL文を実行するためのクラス
import java.sql.ResultSet;//SQLクエリの結果を格納するためのクラス
import java.sql.SQLException;//データベース操作中に発生する例外を処理するためのクラス
import java.util.ArrayList;//リストを作成するためのクラス
import java.util.List;//リストを操作するためのインターフェース

import com.example.model.FoodCategory;//食品カテゴリのモデルクラス
import com.example.model.FoodMaster;//食品マスタのモデルクラス
import com.example.util.DBUtil;//データベース接続を管理するユーティリティクラス

public class FoodDAO {//食品カテゴリと食品マスタのデータアクセスオブジェクト

    public List<FoodCategory> findAllCategories() throws SQLException { //データベースからすべての食品カテゴリを取得する
        List<FoodCategory> list = new ArrayList<>();//食品カテゴリのリストを作成
        String sql = "SELECT food_category_id, food_category_name FROM food_categories ORDER BY food_category_id";//SQLクエリを定義
        try (Connection conn = DBUtil.getConnection();//データベース接続を取得
             PreparedStatement ps = conn.prepareStatement(sql);//SQL文を準備
             ResultSet rs = ps.executeQuery()) {//SQLクエリを実行して結果を取得
            while (rs.next()) {//結果セットをループして各行を処理
                FoodCategory cat = new FoodCategory();//食品カテゴリのインスタンスを作成
                cat.setFoodCategoryId(rs.getInt("food_category_id"));//食品カテゴリIDを設定
                cat.setFoodCategoryName(rs.getString("food_category_name"));//食品カテゴリ名を設定
                list.add(cat);//リストに追加
            }
        }
        return list;//食品カテゴリのリストを返す
    }

    public List<FoodMaster> findFoodsByCategoryId(int categoryId) throws SQLException {//指定されたカテゴリIDに基づいて食品マスタを取得する
        List<FoodMaster> list = new ArrayList<>();//食品マスタのリストを作成
        String sql = "SELECT food_id, food_category_id, food_name, basic_unit, storage_type FROM food_master WHERE food_category_id = ? ORDER BY food_id";//SQLクエリを定義
        try (Connection conn = DBUtil.getConnection();//データベース接続を取得
             PreparedStatement ps = conn.prepareStatement(sql)) {//SQL文を準備
            ps.setInt(1, categoryId);//カテゴリIDを設定
            try (ResultSet rs = ps.executeQuery()) {		//SQLクエリを実行して結果を取得
                while (rs.next()) {		//結果セットをループして各行を処理
                    FoodMaster fm = new FoodMaster();//食品マスタのインスタンスを作成
                    fm.setFoodId(rs.getInt("food_id"));//食品IDを設定
                    fm.setFoodCategoryId(rs.getInt("food_category_id"));//食品カテゴリIDを設定
                    fm.setFoodName(rs.getString("food_name"));//食品名を設定
                    fm.setDefaultUnit(rs.getString("basic_unit"));//	基本単位を設定
                    fm.setStorageType(rs.getString("storage_type"));//保存タイプを設定
                    list.add(fm);//リストに追加
                }
            }
        }
        return list;//食品マスタのリストを返す
    }

    public FoodCategory findCategoryById(int categoryId) throws SQLException {//指定されたカテゴリIDに基づいて食品カテゴリを取得する
        String sql = "SELECT food_category_id, food_category_name FROM food_categories WHERE food_category_id = ?";//SQLクエリを定義
        try (Connection conn = DBUtil.getConnection();//データベース接続を取得
             PreparedStatement ps = conn.prepareStatement(sql)) {//SQL文を準備
            ps.setInt(1, categoryId);//カテゴリIDを設定
            try (ResultSet rs = ps.executeQuery()) {//SQLクエリを実行して結果を取得
                if (rs.next()) {//結果セットに行が存在する場合
                    return new FoodCategory(rs.getInt("food_category_id"), rs.getString("food_category_name"));//食品カテゴリのインスタンスを作成して返す
                }
            }
        }
        return null;//結果セットに行が存在しない場合はnullを返す
    }

    public FoodMaster findFoodById(int foodId) throws SQLException {//指定された食品IDに基づいて食品マスタを取得する
        String sql = "SELECT food_id, food_category_id, food_name, basic_unit, storage_type FROM food_master WHERE food_id = ?";//SQLクエリを定義
        try (Connection conn = DBUtil.getConnection();//データベース接続を取得
             PreparedStatement ps = conn.prepareStatement(sql)) {//SQL文を準備
            ps.setInt(1, foodId);//食品IDを設定
            try (ResultSet rs = ps.executeQuery()) {//SQLクエリを実行して結果を取得
                if (rs.next()) {//結果セットに行が存在する場合
                    return new FoodMaster(// 食品マスタのインスタンスを作成して返す
                            rs.getInt("food_id"),//食品IDを取得
                            rs.getInt("food_category_id"),//食品カテゴリIDを取得
                            rs.getString("food_name"),//食品名を取得
                            rs.getString("basic_unit"),//基本単位を取得
                            rs.getString("storage_type")//保存タイプを取得
                    );
                }
            }
        }
        return null;//結果セットに行が存在しない場合はnullを返す
    }
}
