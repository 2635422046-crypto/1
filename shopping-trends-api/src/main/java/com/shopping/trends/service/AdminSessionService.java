package com.shopping.trends.service;

import java.util.Map;

/**
 * 管理员会话：内存存储 token，单机适用。
 */
public interface AdminSessionService {

    Map<String, String> createSessionAfterLogin(String loginId, String clientIp);

    String validateSession(String sessionToken);

    void invalidateSession(String sessionToken, String clientIp);
}
