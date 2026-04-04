package com.shopping.trends.controller;

import com.shopping.trends.common.Result;
import com.shopping.trends.service.AdminOperationLogService;
import com.shopping.trends.service.AdminService;
import com.shopping.trends.service.AdminSessionService;
import com.shopping.trends.service.CaptchaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员接口：读写 admins.txt；登录校验密码后签发会话；敏感操作需会话令牌。
 */
@Api(tags = "管理员")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private static final String HDR_SESSION = "X-Admin-Session";

    private final AdminService adminService;
    private final AdminSessionService adminSessionService;
    private final AdminOperationLogService adminOperationLogService;
    private final CaptchaService captchaService;

    /** 不向前端返回与手机短信验证相关的历史日志行 */
    private static boolean isPhoneOrSmsVerificationLog(Map<String, String> row) {
        String action = row.get("action");
        String detail = row.get("detail");
        if (action == null) {
            action = "";
        }
        if (detail == null) {
            detail = "";
        }
        String a = action.toLowerCase();
        String combined = (action + " " + detail).toLowerCase();
        if (a.contains("mfa") || a.contains("sms") || a.contains("otp")) {
            return true;
        }
        if (combined.contains("短信") || combined.contains("手机验证") || combined.contains("短信验证")) {
            return true;
        }
        if (detail.contains("手机") && (detail.contains("验证") || detail.contains("绑定") || detail.contains("验证码"))) {
            return true;
        }
        return false;
    }

    /** 去掉详情字段中括号形式的冗余后缀（仅影响接口返回展示） */
    private static String scrubDetailParentheticalTags(String detail) {
        if (detail == null || detail.isEmpty()) {
            return detail == null ? "" : detail;
        }
        String s = detail;
        s = s.replace("（含MFA）", "").replace("(含MFA)", "");
        s = s.replace("（含mfa）", "").replace("(含mfa)", "");
        return s.trim();
    }

    /** 个人中心「操作日志」仅展示：登录、退出、修改密码、图表下载 */
    private static boolean isUserFacingOperationLogAction(String action) {
        if (action == null) {
            return false;
        }
        action = action.trim();
        switch (action) {
            case "LOGIN":
            case "LOGOUT":
            case "PASSWORD_CHANGE":
            case "DATA_EXPORT":
                return true;
            default:
                return false;
        }
    }

    private static String clientIp(HttpServletRequest req) {
        String x = req.getHeader("X-Forwarded-For");
        if (x != null && !x.isEmpty()) {
            return x.split(",")[0].trim();
        }
        return req.getRemoteAddr() != null ? req.getRemoteAddr() : "-";
    }

    private String requireSessionLoginId(HttpServletRequest req) {
        String token = req.getHeader(HDR_SESSION);
        return adminSessionService.validateSession(token);
    }

    @GetMapping("/admin/captcha")
    @ApiOperation("登录用图形验证码：四位字母 PNG（Base64）")
    public Result<Map<String, String>> captcha() {
        return Result.success(captchaService.newCaptcha());
    }

    @GetMapping("/admins")
    @ApiOperation("获取管理员列表（用于登录校验）")
    public Result<List<String>> getAdmins() {
        return Result.success(adminService.getAllLines());
    }

    @PostMapping("/admin/login/password")
    @ApiOperation("管理员登录：校验图形验证码、账号密码后签发会话")
    public Result<Map<String, String>> loginPassword(@RequestBody Map<String, String> body, HttpServletRequest req) {
        String loginId = body.get("loginId");
        String password = body.get("password");
        String captchaId = body.get("captchaId");
        String captchaText = body.get("captchaText");
        String ip = clientIp(req);
        if (loginId == null || password == null) {
            return Result.validateFailed("参数不完整");
        }
        String logUser = loginId.trim().isEmpty() ? "-" : loginId.trim();
        if (!captchaService.verifyAndRemove(captchaId, captchaText)) {
            adminOperationLogService.append(logUser, "LOGIN_FAILED", "图形验证码错误或已过期", ip);
            return Result.failed("验证码错误或已过期，请点击图片刷新");
        }
        if (!adminService.verifyPassword(loginId, password)) {
            adminOperationLogService.append(loginId, "LOGIN_FAILED", "账号或密码错误", ip);
            return Result.failed("登录ID或密码错误");
        }
        return Result.success(adminSessionService.createSessionAfterLogin(loginId, ip));
    }

    @PostMapping("/admin/logout")
    @ApiOperation("退出登录（作废会话）")
    public Result<Void> logout(HttpServletRequest req) {
        String token = req.getHeader(HDR_SESSION);
        adminSessionService.invalidateSession(token, clientIp(req));
        return Result.success(null, "已退出");
    }

    @PostMapping("/admin/change-password")
    @ApiOperation("修改密码：需验证旧密码与会话，新密码不能为空")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, HttpServletRequest req) {
        String sessionLogin = requireSessionLoginId(req);
        if (sessionLogin == null) {
            return Result.unauthorized(null);
        }
        String loginId = body.get("loginId");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (loginId == null || oldPassword == null || newPassword == null) {
            return Result.validateFailed("参数不完整");
        }
        if (!sessionLogin.equals(loginId)) {
            return Result.forbidden(null);
        }
        if (newPassword.isEmpty()) {
            return Result.validateFailed("新密码不能为空");
        }
        boolean ok = adminService.changePassword(loginId, oldPassword, newPassword);
        if (ok) {
            adminOperationLogService.append(loginId, "PASSWORD_CHANGE", "修改密码", clientIp(req));
            return Result.success(null, "修改成功");
        }
        return Result.failed("旧密码错误或账号不存在");
    }

    /**
     * 注销：从 admins.txt 中删除该管理员
     */
    @PostMapping("/admin/deregister")
    @ApiOperation("注销：从 admins.txt 中删除该管理员")
    public Result<Void> deregister(@RequestBody Map<String, String> body, HttpServletRequest req) {
        String sessionLogin = requireSessionLoginId(req);
        if (sessionLogin == null) {
            return Result.unauthorized(null);
        }

        String loginId = body.get("loginId");
        String password = body.get("password");
        if (loginId == null || password == null) {
            return Result.validateFailed("参数不完整");
        }

        if (!sessionLogin.equals(loginId)) {
            return Result.forbidden(null);
        }

        boolean ok = adminService.deregister(loginId, password);
        if (ok) {
            adminOperationLogService.append(loginId, "DEREGISTER", "管理员注销账号", clientIp(req));
            String token = req.getHeader(HDR_SESSION);
            adminSessionService.invalidateSession(token, clientIp(req));
            return Result.success(null, "已注销");
        }
        return Result.failed("账号或密码错误");
    }

    @PostMapping("/admin/operation-log")
    @ApiOperation("前端上报（图表下载等，写入操作日志）")
    public Result<Void> clientOperationLog(@RequestBody Map<String, String> body, HttpServletRequest req) {
        String sessionLogin = requireSessionLoginId(req);
        if (sessionLogin == null) {
            return Result.unauthorized(null);
        }
        String action = body.get("action");
        String detail = body.get("detail");
        if (action == null || action.isEmpty()) {
            return Result.validateFailed("缺少 action");
        }
        adminOperationLogService.append(sessionLogin, action, detail != null ? detail : "", clientIp(req));
        return Result.success(null);
    }

    @GetMapping("/admin/operation-logs")
    @ApiOperation("查询本人最近操作日志（登录、退出、修改密码、图表下载）")
    public Result<List<Map<String, String>>> operationLogs(
            @RequestParam(defaultValue = "80") int limit,
            HttpServletRequest req) {
        String sessionLogin = requireSessionLoginId(req);
        if (sessionLogin == null) {
            return Result.unauthorized(null);
        }
        List<Map<String, String>> all = adminOperationLogService.readRecent(Math.min(limit, 500));
        List<Map<String, String>> mine = new ArrayList<>();
        for (Map<String, String> row : all) {
            if (sessionLogin.equals(row.get("loginId"))
                    && !isPhoneOrSmsVerificationLog(row)
                    && isUserFacingOperationLogAction(row.get("action"))) {
                Map<String, String> copy = new LinkedHashMap<>(row);
                copy.put("detail", scrubDetailParentheticalTags(copy.get("detail")));
                mine.add(copy);
            }
        }
        return Result.success(mine);
    }
}
