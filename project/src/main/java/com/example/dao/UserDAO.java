package com.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.model.User;
import com.example.util.DBUtil;
import com.example.util.PasswordUtil;

public class UserDAO {

    public User findByLoginIdAndPassword(String loginId, String password) throws SQLException {
        String sql = "SELECT user_id, login_id, password_hash, created_at, user_name FROM users WHERE login_id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (!PasswordUtil.verifyPassword(password, storedHash)) {
                        return null;
                    }
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setLoginId(rs.getString("login_id"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setCreatedAt(rs.getDate("created_at"));
                    user.setUserName(rs.getString("user_name"));
                    return user;
                }
            }
        }
        return null;
    }
    
    public User registerUser(String userName, String loginId, String passwordHash) throws SQLException {

        String sql = "INSERT INTO users (user_name, login_id, password_hash) "
                   + "VALUES (?, ?, ?) "
                   + "RETURNING user_id, login_id, password_hash, created_at, user_name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userName);
            ps.setString(2, loginId);
            ps.setString(3, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = new User();

                    user.setUserId(rs.getInt("user_id"));
                    user.setLoginId(rs.getString("login_id"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setCreatedAt(rs.getDate("created_at"));
                    user.setUserName(rs.getString("user_name"));

                    return user;
                }
            }
        }

        return null;
    }
    
    public boolean existsByLoginId(String loginId) throws SQLException {

        String sql = "SELECT COUNT(*) FROM users WHERE login_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loginId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
    
}
