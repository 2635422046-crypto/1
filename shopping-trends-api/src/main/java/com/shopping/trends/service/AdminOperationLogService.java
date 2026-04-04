package com.shopping.trends.service;

import java.util.List;
import java.util.Map;

/**
 * 管理员操作日志（文本文件追加，只读查询最近若干条）。
 */
public interface AdminOperationLogService {

    void append(String loginId, String action, String detail, String clientIp);

    List<Map<String, String>> readRecent(int limit);
}
