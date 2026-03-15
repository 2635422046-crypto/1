package com.shopping.trends.controller;

import com.shopping.trends.common.Result;
import com.shopping.trends.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员接口：仅读写 admins.txt，不涉及数据库
 */
@Api(tags = "管理员")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admins")
    @ApiOperation("获取管理员列表（用于登录校验）")
    public Result<List<String>> getAdmins() {
        return Result.success(adminService.getAllLines());
    }

    @PostMapping("/admin/change-password")
    @ApiOperation("修改密码：需验证旧密码，新密码两次一致")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        String loginId = body.get("loginId");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (loginId == null || oldPassword == null || newPassword == null) {
            return Result.validateFailed("参数不完整");
        }
        if (newPassword.isEmpty()) {
            return Result.validateFailed("新密码不能为空");
        }
        boolean ok = adminService.changePassword(loginId, oldPassword, newPassword);
        if (ok) return Result.success(null, "修改成功");
        return Result.failed("旧密码错误或账号不存在");
    }

    @PostMapping("/admin/deregister")
    @ApiOperation("注销：从 admins.txt 中删除该管理员")
    public Result<Void> deregister(@RequestBody Map<String, String> body) {
        String loginId = body.get("loginId");
        String password = body.get("password");
        if (loginId == null || password == null) {
            return Result.validateFailed("参数不完整");
        }
        boolean ok = adminService.deregister(loginId, password);
        if (ok) return Result.success(null, "已注销");
        return Result.failed("账号或密码错误");
    }
}
