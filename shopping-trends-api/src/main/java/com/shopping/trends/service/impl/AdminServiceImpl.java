package com.shopping.trends.service.impl;

import com.shopping.trends.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于文件的管理员服务：读写 admins.txt（默认仓库根目录 data/admins.txt）。
 * 每行格式：loginId,password（忽略第三列及以后，保持向前兼容）。
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    private static final String FILE_NAME = "admins.txt";

    @Value("${shopping-trends.admin.admins-file:}")
    private String adminsFileConfigured;

    private Path filePath;

    @PostConstruct
    public void init() throws IOException {
        filePath = resolveAdminsFilePath();
        Path dataDir = filePath.getParent();
        if (dataDir != null && !Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        log.info("管理员信息表路径 admins.txt: {}", filePath.toAbsolutePath());

        if (Files.exists(filePath)) {
            return;
        }

        String defaultContent = "admin1,123456\nadmin2,123456\nadmin3,123456\nadmin4,123456\nadmin5,123456\n";

        ClassPathResource resource = new ClassPathResource("admins.txt");
        if (resource.exists()) {
            try (InputStream is = resource.getInputStream()) {
                byte[] bytes = toByteArray(is);
                Files.write(filePath, bytes);
                log.info("Initialized admins.txt from classpath resource");
            } catch (IOException e) {
                log.warn("Failed to copy classpath admins.txt, falling back to default content", e);
                Files.write(filePath, defaultContent.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            Files.write(filePath, defaultContent.getBytes(StandardCharsets.UTF_8));
            log.info("Created default admins.txt (no classpath resource found)");
        }
    }

    private Path resolveAdminsFilePath() {
        Path cwd = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        if (adminsFileConfigured != null && !adminsFileConfigured.trim().isEmpty()) {
            Path p = Paths.get(adminsFileConfigured.trim());
            return p.isAbsolute() ? p.normalize() : cwd.resolve(p).normalize();
        }
        Path underCwd = cwd.resolve("data").resolve(FILE_NAME);
        Path parentData = cwd.resolve("..").resolve("data").resolve(FILE_NAME).normalize();
        if (Files.exists(underCwd)) {
            return underCwd;
        }
        if (Files.exists(parentData)) {
            return parentData;
        }
        String dirName = cwd.getFileName() != null ? cwd.getFileName().toString() : "";
        if ("shopping-trends-api".equalsIgnoreCase(dirName)) {
            return parentData;
        }
        return underCwd;
    }

    private static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int n;
        while ((n = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, n);
        }
        return buffer.toByteArray();
    }

    @Override
    public List<String> getAllLines() {
        try {
            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }
            return Files.readAllLines(filePath, StandardCharsets.UTF_8).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to read admins.txt", e);
            return new ArrayList<>();
        }
    }

    private static final class ParsedLine {
        final String id;
        final String password;

        ParsedLine(String id, String password) {
            this.id = id;
            this.password = password;
        }
    }

    private static ParsedLine parseLine(String line) {
        if (line == null) {
            return new ParsedLine("", "");
        }
        String[] parts = line.split(",", 3);
        String id = parts[0].trim();
        String pwd = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedLine(id, pwd);
    }

    private static String toStorageLine(String loginId, String password) {
        return loginId + "," + password;
    }

    /**
     * 修改指定账号的密码：校验旧密码正确后，将对应行替换为新密码并回写文件。
     * @return true 表示修改成功；false 表示账号不存在或旧密码错误
     */
    @Override
    public boolean changePassword(String loginId, String oldPassword, String newPassword) {
        // 基本参数校验
        if (loginId == null || oldPassword == null || newPassword == null || newPassword.isEmpty()) {
            return false;
        }
        List<String> lines = getAllLines();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            ParsedLine pl = parseLine(line);
            if (pl.id.equals(loginId)) {
                // 账号匹配，验证旧密码
                if (!pl.password.equals(oldPassword)) {
                    return false;
                }
                // 替换该行为新密码
                lines.set(i, toStorageLine(loginId, newPassword));
                return writeLines(lines);
            }
        }
        // 未找到该账号
        return false;
    }

    @Override
    public boolean deregister(String loginId, String password) {
        if (loginId == null || password == null) {
            return false;
        }
        List<String> lines = getAllLines();
        List<String> kept = lines.stream()
                .filter(line -> {
                    ParsedLine pl = parseLine(line);
                    return !(pl.id.equals(loginId) && pl.password.equals(password));
                })
                .collect(Collectors.toList());

        if (kept.size() == lines.size()) {
            return false;
        }
        return writeLines(kept);
    }

    private boolean writeLines(List<String> lines) {
        try {
            String content = lines.isEmpty() ? "" : String.join("\n", lines) + "\n";
            Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
            log.info("已更新管理员信息表 admins.txt: {} （{} 条记录）", filePath.toAbsolutePath(), lines.size());
            return true;
        } catch (IOException e) {
            log.error("Failed to write admins.txt", e);
            return false;
        }
    }

    @Override
    public boolean verifyPassword(String loginId, String password) {
        if (loginId == null || password == null) {
            return false;
        }
        for (String line : getAllLines()) {
            ParsedLine pl = parseLine(line);
            if (pl.id.equals(loginId) && pl.password.equals(password)) {
                return true;
            }
        }
        return false;
    }
}
