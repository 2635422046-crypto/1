package com.shopping.trends.util;

import com.shopping.trends.entity.ShoppingRecord;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物数据清洗与标准化工具（仅内存处理，不写库）。
 * 对从数据库读出的数据做：去空格、枚举标准化、空值统一、数值类型保证，
 * 保证与前端 displayLabels 的 key 一致，不改变页面展示效果。
 */
public final class ShoppingDataNormalizer {

    private ShoppingDataNormalizer() {}

    // ---------- 字符串：去首尾空格、空转统一 ----------
    public static String normalizeString(String s) {
        if (s == null) return "";
        String t = s.trim();
        return t.isEmpty() ? "" : t;
    }

    // ---------- 性别：与 displayLabels genderToZh 一致 ----------
    public static String normalizeGender(String s) {
        String t = normalizeString(s);
        if (t.isEmpty()) return "";
        switch (t.toLowerCase()) {
            case "male":
            case "m":
                return "Male";
            case "female":
            case "f":
                return "Female";
            default:
                return capitalizeFirst(t);
        }
    }

    // ---------- 季节：与 displayLabels seasonToZh 一致 ----------
    public static String normalizeSeason(String s) {
        String t = normalizeString(s);
        if (t.isEmpty()) return "";
        switch (t.toLowerCase()) {
            case "spring":
                return "Spring";
            case "summer":
                return "Summer";
            case "fall":
            case "autumn":
                return "Fall";
            case "winter":
                return "Winter";
            default:
                return capitalizeFirst(t);
        }
    }

    // ---------- 商品品类(category)：与 displayLabels categoryToZh 一致 ----------
    public static String normalizeCategory(String s) {
        String t = normalizeString(s);
        if (t.isEmpty()) return "";
        switch (t.toLowerCase()) {
            case "clothing":
                return "Clothing";
            case "footwear":
                return "Footwear";
            case "outerwear":
                return "Outerwear";
            case "accessories":
                return "Accessories";
            default:
                return capitalizeFirst(t);
        }
    }

    // ---------- 地区：仅 trim，与 displayLabels locationToZh 键一致（保持原大小写如 InnerMongolia） ----------
    public static String normalizeLocation(String s) {
        return normalizeString(s);
    }

    // ---------- 购买商品名 item_purchased：trim，T-shirt 统一 ----------
    public static String normalizeItemPurchased(String s) {
        String t = normalizeString(s);
        if (t.isEmpty()) return "";
        if ("t-shirt".equalsIgnoreCase(t) || "t shirt".equalsIgnoreCase(t.replace("-", " "))) {
            return "T-shirt";
        }
        return capitalizeFirst(t);
    }

    private static String capitalizeFirst(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    // ---------- 数值：安全取 Number，null 或非法则 0 ----------
    public static Number normalizeNumber(Object v) {
        if (v == null) return 0;
        if (v instanceof Number) return (Number) v;
        if (v instanceof String) {
            String s = ((String) v).trim();
            if (s.isEmpty()) return 0;
            try {
                if (s.contains(".")) return Double.parseDouble(s);
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    // ---------- 统计行 Map 清洗（JdbcTemplate queryForList 结果） ----------
    public static Map<String, Object> normalizeStatsRow(Map<String, Object> row, String dimensionKey) {
        if (row == null) return new HashMap<>();
        Map<String, Object> out = new HashMap<>(row);
        for (Map.Entry<String, Object> e : row.entrySet()) {
            String k = e.getKey();
            Object v = e.getValue();
            if (v == null) continue;
            if (v instanceof String) {
                String s = (String) v;
                if ("gender".equals(k)) out.put(k, normalizeGender(s));
                else if ("season".equals(k)) out.put(k, normalizeSeason(s));
                else if ("category".equals(k)) out.put(k, normalizeCategory(s));
                else if ("location".equals(k)) out.put(k, normalizeLocation(s));
                else if ("name".equals(k) || "item_purchased".equals(k) || "item".equals(k)) out.put(k, normalizeItemPurchased(s));
                else if ("age_group".equals(k)) out.put(k, normalizeString(s));
                else if ("date".equals(k)) out.put(k, normalizeString(s));
                else out.put(k, normalizeString(s));
            } else if (v instanceof Number) {
                out.put(k, v);
            } else {
                out.put(k, v);
            }
        }
        // 数值字段统一为 Number 类型，避免前端拿到字符串
        if (out.containsKey("total_amount")) out.put("total_amount", normalizeNumber(out.get("total_amount")));
        if (out.containsKey("purchase_count")) out.put("purchase_count", normalizeNumber(out.get("purchase_count")));
        if (out.containsKey("value")) out.put("value", normalizeNumber(out.get("value")));
        return out;
    }

    public static List<Map<String, Object>> normalizeStatsRows(List<Map<String, Object>> rows, String dimensionKey) {
        if (rows == null) return Collections.emptyList();
        return rows.stream()
                .map(row -> normalizeStatsRow(row, dimensionKey))
                .collect(Collectors.toList());
    }

    // ---------- 实体 ShoppingRecord 清洗（用于 UserStats 等基于实体的统计） ----------
    public static ShoppingRecord normalizeRecord(ShoppingRecord r) {
        if (r == null) return null;
        r.setCustomerId(normalizeString(r.getCustomerId()));
        r.setGender(normalizeGender(r.getGender()));
        r.setCategory(normalizeCategory(r.getCategory()));
        r.setLocation(normalizeLocation(r.getLocation()));
        r.setSeason(normalizeSeason(r.getSeason()));
        r.setItem(normalizeItemPurchased(r.getItem()));
        r.setSize(normalizeString(r.getSize()));
        r.setColor(normalizeString(r.getColor()));
        r.setSubscriptionStatus(normalizeString(r.getSubscriptionStatus()));
        r.setDiscountApplied(normalizeString(r.getDiscountApplied()));
        r.setPromoCodeUsed(normalizeString(r.getPromoCodeUsed()));
        if (r.getAge() != null && (r.getAge() < 0 || r.getAge() > 150)) {
            r.setAge(0);
        }
        if (r.getPurchaseAmount() != null && r.getPurchaseAmount().doubleValue() < 0) {
            r.setPurchaseAmount(BigDecimal.ZERO);
        }
        if (r.getRating() != null && (r.getRating() < 0 || r.getRating() > 5)) {
            r.setRating(0.0);
        }
        return r;
    }

    public static List<ShoppingRecord> normalizeRecords(List<ShoppingRecord> records) {
        if (records == null) return Collections.emptyList();
        return records.stream()
                .map(ShoppingDataNormalizer::normalizeRecord)
                .collect(Collectors.toList());
    }
}
