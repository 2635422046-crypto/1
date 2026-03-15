package com.shopping.trends.service.impl;

import com.shopping.trends.service.AdminService;
import lombok.extern.slf4j.Slf4j;
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
 * 基于文件的管理员服务：不涉及数据库，仅读写项目根目录下的 data/admins.txt。
 * 文件格式每行一条，格式为 "loginId,password"。
 * 启动时（{@code @PostConstruct}）自动检测并创建文件，若 classpath 中有初始数据则优先使用。
 */
@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    /** 存放 admins.txt 的目录名（相对于 IDEA 运行时工作目录，即项目根目录） */
    private static final String DATA_DIR  = "data";
    private static final String FILE_NAME = "admins.txt";

    /** 文件实际路径，在 init() 中确定 */
    private Path filePath;

    /**
     * 应用启动后立即执行：确保 data/admins.txt 存在。
     * 优先从 classpath:admins.txt 复制初始内容，
     * 若 classpath 中不存在则写入内置的默认账号（admin1~admin5，密码均为 123456）。
     */
    @PostConstruct
    public void init() throws IOException {
        Path dataDir = Paths.get(DATA_DIR);
        // 若 data/ 目录不存在则递归创建
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }
        filePath = dataDir.resolve(FILE_NAME);

        // 文件已存在则跳过初始化，保留运行时修改的密码
        if (Files.exists(filePath)) {
            return;
        }

        // 内置兜底账号（当 classpath 中也没有 admins.txt 时使用）
        String defaultContent = "admin1,123456\nadmin2,123456\nadmin3,123456\nadmin4,123456\nadmin5,123456\n";

        ClassPathResource resource = new ClassPathResource("admins.txt");
        if (resource.exists()) {
            // 使用 try-with-resources 自动关闭 InputStream，避免资源泄漏
            try (InputStream is = resource.getInputStream()) {
                byte[] bytes = toByteArray(is);
                Files.write(filePath, bytes);
                log.info("Initialized admins.txt from classpath resource");
            } catch (IOException e) {
                log.warn("Failed to copy classpath admins.txt, falling back to default content", e);
                Files.write(filePath, defaultContent.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            // classpath 中没有初始文件，写入默认账号
            Files.write(filePath, defaultContent.getBytes(StandardCharsets.UTF_8));
            log.info("Created default admins.txt (no classpath resource found)");
        }
    }

    /** Java 8 兼容：从 InputStream 读取全部字节 */
    private static byte[] toByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int n;
        while ((n = is.read(chunk)) != -1) {
            buffer.write(chunk, 0, n);
        }
        return buffer.toByteArray();
    }

    /**
     * 读取 admins.txt 全部有效行（过滤空行和首尾空格）。
     * @return 每项为 "loginId,password" 格式的字符串列表；文件不存在或异常时返回空列表
     */
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

    /**
     * 从行字符串 "loginId,password" 中解析 loginId（逗号前部分）。
     */
    private static String parseId(String line) {
        int i = line.indexOf(',');
        return i == -1 ? line.trim() : line.substring(0, i).trim();
    }

    /**
     * 从行字符串 "loginId,password" 中解析 password（逗号后部分）。
     */
    private static String parsePassword(String line) {
        int i = line.indexOf(',');
        return i == -1 ? "" : line.substring(i + 1).trim();
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
            if (parseId(line).equals(loginId)) {
                // 账号匹配，验证旧密码
                if (!parsePassword(line).equals(oldPassword)) {
                    return false;
                }
                // 替换该行为新密码
                lines.set(i, loginId + "," + newPassword);
                return writeLines(lines);
            }
        }
        // 未找到该账号
        return false;
    }

    /**
     * 注销指定账号：验证 id + 密码均正确后，从文件中删除该行。
     * @return true 表示注销成功；false 表示账号不存在或密码错误
     */
    @Override
    public boolean deregister(String loginId, String password) {
        if (loginId == null || password == null) {
            return false;
        }
        List<String> lines = getAllLines();
        // 过滤掉匹配的行（id 和密码均相等才删除）
        List<String> kept = lines.stream()
                .filter(line -> !(parseId(line).equals(loginId) && parsePassword(line).equals(password)))
                .collect(Collectors.toList());

        // 若过滤前后行数相同，说明没有找到匹配账号
        if (kept.size() == lines.size()) {
            return false;
        }
        return writeLines(kept);
    }

    /**
     * 将行列表以换行符拼接后写入 admins.txt（UTF-8 编码）。
     * @return true 表示写入成功；false 表示发生 IO 异常
     */
    private boolean writeLines(List<String> lines) {
        try {
            Files.write(filePath, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            log.error("Failed to write admins.txt", e);
            return false;
        }
    }
}
