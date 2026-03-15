package com.shopping.trends.service;

import java.util.List;

/**
 * 管理员文件服务（不涉及数据库，仅读写 admins.txt）
 */
public interface AdminService {

    /**
     * 获取所有管理员列表，每项为 "id,password"
     */
    List<String> getAllLines();

    /**
     * 修改密码：校验旧密码后替换为新密码
     * @return true 成功
     */
    boolean changePassword(String loginId, String oldPassword, String newPassword);

    /**
     * 注销：校验 id+密码 后从文件中删除该行
     * @return true 成功
     */
    boolean deregister(String loginId, String password);
}
