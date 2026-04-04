package com.shopping.trends.service.impl;

import com.shopping.trends.service.CaptchaService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 内存存储验证码（单机适用）；过期自动剔除。
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final int TTL_MS = 5 * 60 * 1000;
    /** 易混淆的 I、O 去掉 */
    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    private static final class Entry {
        final String codeUpper;
        final long expiresAt;

        Entry(String codeUpper, long expiresAt) {
            this.codeUpper = codeUpper;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    @Override
    public Map<String, String> newCaptcha() {
        purgeExpired();
        String code = randomFourLetters();
        String id = UUID.randomUUID().toString().replace("-", "");
        long exp = System.currentTimeMillis() + TTL_MS;
        store.put(id, new Entry(code.toUpperCase(), exp));
        String pngBase64 = renderPngBase64(code);
        Map<String, String> out = new HashMap<>();
        out.put("captchaId", id);
        out.put("imageBase64", pngBase64);
        return out;
    }

    @Override
    public boolean verifyAndRemove(String captchaId, String userInput) {
        if (captchaId == null || captchaId.isEmpty() || userInput == null) {
            return false;
        }
        Entry e = store.remove(captchaId.trim());
        if (e == null || System.currentTimeMillis() > e.expiresAt) {
            return false;
        }
        String u = userInput.trim();
        if (u.length() != 4) {
            return false;
        }
        return e.codeUpper.equalsIgnoreCase(u);
    }

    private void purgeExpired() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Entry>> it = store.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue().expiresAt < now) {
                it.remove();
            }
        }
    }

    private static String randomFourLetters() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            sb.append(LETTERS.charAt(r.nextInt(LETTERS.length())));
        }
        return sb.toString();
    }

    private static String renderPngBase64(String code) {
        int w = 120;
        int h = 44;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        try {
            g2.setColor(new Color(245, 247, 250));
            g2.fillRect(0, 0, w, h);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            g2.setStroke(new BasicStroke(1.2f));
            for (int i = 0; i < 6; i++) {
                g2.setColor(new Color(rnd.nextInt(180) + 40, rnd.nextInt(180) + 40, rnd.nextInt(180) + 40));
                g2.drawLine(rnd.nextInt(w), rnd.nextInt(h), rnd.nextInt(w), rnd.nextInt(h));
            }
            Font font = new Font(Font.SANS_SERIF, Font.BOLD, 30);
            g2.setFont(font);
            FontMetrics fm = g2.getFontMetrics();
            int baseY = (h + fm.getAscent() - fm.getDescent()) / 2;
            AffineTransform defaultAt = g2.getTransform();
            for (int i = 0; i < code.length(); i++) {
                g2.setTransform(defaultAt);
                char c = code.charAt(i);
                int x = 14 + i * 24;
                double rot = (rnd.nextDouble() - 0.5) * 0.45;
                g2.rotate(rot, x + 10, baseY - 6);
                g2.setColor(new Color(rnd.nextInt(80), rnd.nextInt(80), rnd.nextInt(80)));
                g2.drawString(String.valueOf(c), x, baseY);
            }
            g2.setTransform(defaultAt);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("captcha png", e);
        } finally {
            g2.dispose();
        }
    }
}
