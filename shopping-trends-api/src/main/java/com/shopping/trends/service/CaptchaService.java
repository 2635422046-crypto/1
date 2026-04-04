package com.shopping.trends.service;

import java.util.Map;

/**
 * 登录用图形验证码（内存存储，单机适用）。
 */
public interface CaptchaService {

    Map<String, String> newCaptcha();

    boolean verifyAndRemove(String captchaId, String userInput);
}
