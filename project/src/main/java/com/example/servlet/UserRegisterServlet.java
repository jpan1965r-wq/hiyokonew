package com.example.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.example.dao.UserDAO;
import com.example.model.User;
import com.example.util.PasswordUtil;

@WebServlet("/UserRegisterServlet")
public class UserRegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String userName = request.getParameter("userName");
        String loginId = request.getParameter("loginId");
        String password = request.getParameter("password");
        String passwordConfirm = request.getParameter("passwordConfirm");

        // 入力チェック
        if (userName == null || userName.isBlank()
                || loginId == null || loginId.isBlank()
                || password == null || password.isBlank()
                || passwordConfirm == null || passwordConfirm.isBlank()) {

            request.setAttribute("registerErrorMessage",
                    "すべての項目を入力してください");

            request.getRequestDispatcher(
                    "/WEB-INF/jsp/S001_login.jsp")
                    .forward(request, response);

            return;
        }

        // パスワード確認
        if (!password.equals(passwordConfirm)) {

            request.setAttribute("registerErrorMessage",
                    "パスワードが一致しません");

            request.setAttribute("registerUserName", userName);
            request.setAttribute("registerLoginId", loginId);

            request.getRequestDispatcher(
                    "/WEB-INF/jsp/S001_login.jsp")
                    .forward(request, response);

            return;
        }

        try {

            UserDAO userDAO = new UserDAO();

            // ユーザーID重複チェック
            if (userDAO.existsByLoginId(loginId)) {

                request.setAttribute("registerErrorMessage",
                        "そのユーザーIDは既に使用されています");

                request.setAttribute("registerUserName", userName);
                request.setAttribute("registerLoginId", loginId);

                request.getRequestDispatcher(
                        "/WEB-INF/jsp/S001_login.jsp")
                        .forward(request, response);

                return;
            }

            // パスワードをハッシュ化
            String passwordHash =
                    PasswordUtil.hashPassword(password);

            // ユーザー登録
            User user =
                    userDAO.registerUser(
                            userName,
                            loginId,
                            passwordHash);

            if (user == null) {

                throw new ServletException(
                        "ユーザー登録に失敗しました");
            }

            // 登録番号を生成
            String date =
                    new SimpleDateFormat("yyyyMMdd")
                            .format(user.getCreatedAt());

            String registrationNumber =
                    "USR-" + date + "-" + user.getUserId();

            request.setAttribute(
                    "registrationNumber",
                    registrationNumber);

            request.setAttribute(
                    "userName",
                    user.getUserName());

            request.setAttribute(
                    "loginId",
                    user.getLoginId());

            request.getRequestDispatcher(
                    "/WEB-INF/jsp/registration_complete.jsp")
                    .forward(request, response);

        } catch (Exception e) {

            request.setAttribute(
                    "registerErrorMessage",
                    "システムエラーが発生しました");

            request.setAttribute(
                    "registerUserName",
                    userName);

            request.setAttribute(
                    "registerLoginId",
                    loginId);

            request.getRequestDispatcher(
                    "/WEB-INF/jsp/S001_login.jsp")
                    .forward(request, response);
        }
    }
}