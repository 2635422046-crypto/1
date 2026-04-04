package com.shopping.trends.service.impl;

import com.shopping.trends.service.AdminOperationLogService;
import com.shopping.trends.service.AdminSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AdminSessionServiceImpl implements AdminSessionService {

    private final AdminOperationLogService adminOperationLogService;

    private static final long SESSION_TTL_MS = 24 * 60 * 60 * 1000L;

    private static final class Session {
        final String loginId;
        final long expiresAt;

        Session(String loginId, long expiresAt) {
            this.loginId = loginId;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> createSessionAfterLogin(String loginId, String clientIp) {
        String token = UUID.randomUUID().toString().replace("-", "");
        long exp = System.currentTimeMillis() + SESSION_TTL_MS;
        sessions.put(token, new Session(loginId, exp));
        adminOperationLogService.append(loginId, "LOGIN", "登录", clientIp);
        Map<String, String> out = new HashMap<>();
        out.put("sessionToken", token);
        out.put("loginId", loginId);
        return out;
    }

    @Override
    public String validateSession(String sessionToken) {
        if (sessionToken == null || sessionToken.isEmpty()) {
            return null;
        }
        Session s = sessions.get(sessionToken);
        if (s == null) {
            return null;
        }
        if (System.currentTimeMillis() > s.expiresAt) {
            sessions.remove(sessionToken);
            return null;
        }
        return s.loginId;
    }

    @Override
    public void invalidateSession(String sessionToken, String clientIp) {
        if (sessionToken == null) {
            return;
        }
        Session removed = sessions.remove(sessionToken);
        if (removed != null) {
            adminOperationLogService.append(removed.loginId, "LOGOUT", "退出",
                    clientIp != null ? clientIp : "-");
        }
    }
}
