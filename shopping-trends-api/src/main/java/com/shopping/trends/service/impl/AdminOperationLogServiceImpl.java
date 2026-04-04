package com.shopping.trends.service.impl;

import com.shopping.trends.service.AdminOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志追加到 data/admin_operation.log（与 admins.txt 同目录策略）。
 */
@Slf4j
@Service
public class AdminOperationLogServiceImpl implements AdminOperationLogService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Value("${shopping-trends.admin.operation-log-file:}")
    private String operationLogConfigured;

    private Path logPath;

    @PostConstruct
    public void init() {
        logPath = resolveLogPath();
        try {
            Path parent = logPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            log.warn("无法创建操作日志目录", e);
        }
        log.info("管理员操作日志路径: {}", logPath.toAbsolutePath());
    }

    private Path resolveLogPath() {
        Path cwd = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        if (operationLogConfigured != null && !operationLogConfigured.trim().isEmpty()) {
            Path p = Paths.get(operationLogConfigured.trim());
            return p.isAbsolute() ? p.normalize() : cwd.resolve(p).normalize();
        }
        Path underCwd = cwd.resolve("data").resolve("admin_operation.log");
        Path parentData = cwd.resolve("..").resolve("data").resolve("admin_operation.log").normalize();
        if (Files.exists(underCwd.getParent()) && Files.exists(underCwd.getParent().resolve("admins.txt"))) {
            return underCwd;
        }
        if (Files.exists(parentData.getParent()) && Files.exists(parentData.getParent().resolve("admins.txt"))) {
            return parentData;
        }
        String dirName = cwd.getFileName() != null ? cwd.getFileName().toString() : "";
        if ("shopping-trends-api".equalsIgnoreCase(dirName)) {
            return parentData;
        }
        return underCwd;
    }

    @Override
    public synchronized void append(String loginId, String action, String detail, String clientIp) {
        if (loginId == null) {
            loginId = "-";
        }
        if (action == null) {
            action = "";
        }
        if (detail == null) {
            detail = "";
        }
        detail = detail.replace("|", " ");
        String ip = clientIp != null ? clientIp : "-";
        String line = TS.format(LocalDateTime.now()) + "|" + loginId + "|" + action + "|" + detail + "|" + ip + "\n";
        try {
            Files.write(logPath, line.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("写入操作日志失败", e);
        }
    }

    @Override
    public synchronized List<Map<String, String>> readRecent(int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }
        if (!Files.exists(logPath)) {
            return Collections.emptyList();
        }
        try {
            List<String> lines = Files.readAllLines(logPath, StandardCharsets.UTF_8);
            List<Map<String, String>> out = new ArrayList<>();
            for (int i = lines.size() - 1; i >= 0 && out.size() < limit; i--) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split("\\|", 5);
                if (parts.length < 5) {
                    continue;
                }
                Map<String, String> row = new LinkedHashMap<>();
                row.put("time", parts[0]);
                row.put("loginId", parts[1]);
                row.put("action", parts[2]);
                row.put("detail", parts[3]);
                row.put("ip", parts[4]);
                out.add(row);
            }
            return out;
        } catch (IOException e) {
            log.error("读取操作日志失败", e);
            return Collections.emptyList();
        }
    }
}
