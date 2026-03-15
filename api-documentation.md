# Shopping Trends API 文档

## 基础信息

- 基础URL: `http://localhost:8080`
- 所有响应都遵循以下格式：

```json
{
    "code": 200,
    "message": "操作成功",
    "data": [...],
    "success": true
}
```

## API 端点

### 1. 性别统计

- **URL**: `/api/shopping-records/stats/gender`
- **方法**: GET
- **描述**: 获取按性别分组的购物统计数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "gender": "Male",
      "total_amount": 698,
      "purchase_count": 12
    },
    {
      "gender": "Female",
      "total_amount": 1490,
      "purchase_count": 24
    }
  ],
  "success": true
}
```

### 2. 年龄段统计

- **URL**: `/api/shopping-records/stats/age-group`
- **方法**: GET
- **描述**: 获取按年龄段分组的购物统计数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "age_group": "18-24",
      "total_amount": 187,
      "purchase_count": 3
    },
    {
      "age_group": "25-34",
      "total_amount": 793,
      "purchase_count": 10
    },
    {
      "age_group": "35-44",
      "total_amount": 287,
      "purchase_count": 5
    },
    {
      "age_group": "45-54",
      "total_amount": 338,
      "purchase_count": 8
    },
    {
      "age_group": "55-64",
      "total_amount": 346,
      "purchase_count": 6
    },
    {
      "age_group": "65+",
      "total_amount": 237,
      "purchase_count": 4
    }
  ],
  "success": true
}{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "age_group": "18-24",
      "total_amount": 187,
      "purchase_count": 3
    },
    {
      "age_group": "25-34",
      "total_amount": 793,
      "purchase_count": 10
    },
    {
      "age_group": "35-44",
      "total_amount": 287,
      "purchase_count": 5
    },
    {
      "age_group": "45-54",
      "total_amount": 338,
      "purchase_count": 8
    },
    {
      "age_group": "55-64",
      "total_amount": 346,
      "purchase_count": 6
    },
    {
      "age_group": "65+",
      "total_amount": 237,
      "purchase_count": 4
    }
  ],
  "success": true
}
```

### 3. 商品类别统计

- **URL**: `/api/shopping-records/stats/category`
- **方法**: GET
- **描述**: 获取按商品类别分组的购物统计数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "category": "Clothing",
      "total_amount": 1034,
      "purchase_count": 17
    },
    {
      "category": "Footwear",
      "total_amount": 427,
      "purchase_count": 7
    },
    {
      "category": "Accessories",
      "total_amount": 407,
      "purchase_count": 7
    },
    {
      "category": "Outerwear",
      "total_amount": 320,
      "purchase_count": 5
    }
  ],
  "success": true
}
```

### 4. 季节统计

- **URL**: `/api/shopping-records/stats/season`
- **方法**: GET
- **描述**: 获取按季节分组的购物统计数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "season": "Winter",
      "total_amount": 649,
      "purchase_count": 13
    },
    {
      "season": "Spring",
      "total_amount": 312,
      "purchase_count": 5
    },
    {
      "season": "Summer",
      "total_amount": 597,
      "purchase_count": 10
    },
    {
      "season": "Fall",
      "total_amount": 630,
      "purchase_count": 8
    }
  ],
  "success": true
}
```

### 6. 地区统计

- **URL**: `/api/shopping-records/stats/location`
- **方法**: GET
- **描述**: 获取按地区分组的购物统计数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "location": "Shanghai",
      "total_amount": 386,
      "purchase_count": 5
    },
    {
      "location": "Sichuan",
      "total_amount": 252,
      "purchase_count": 5
    },
    {
      "location": "Guangdong",
      "total_amount": 236,
      "purchase_count": 3
    },
    {
      "location": "Hunan",
      "total_amount": 212,
      "purchase_count": 3
    },
    {
      "location": "Shandong",
      "total_amount": 163,
      "purchase_count": 3
    },
    {
      "location": "Zhejiang",
      "total_amount": 126,
      "purchase_count": 2
    },
    {
      "location": "Hebei",
      "total_amount": 107,
      "purchase_count": 2
    },
    {
      "location": "Liaoning",
      "total_amount": 99,
      "purchase_count": 2
    },
    {
      "location": "Chongqing",
      "total_amount": 97,
      "purchase_count": 1
    },
    {
      "location": "Guangxi",
      "total_amount": 95,
      "purchase_count": 1
    },
    {
      "location": "Hubei",
      "total_amount": 93,
      "purchase_count": 1
    },
    {
      "location": "Heilongjiang",
      "total_amount": 90,
      "purchase_count": 1
    },
    {
      "location": "Shanxi",
      "total_amount": 69,
      "purchase_count": 2
    },
    {
      "location": "Gansu",
      "total_amount": 50,
      "purchase_count": 1
    },
    {
      "location": "Jiangsu",
      "total_amount": 34,
      "purchase_count": 1
    },
    {
      "location": "Henan",
      "total_amount": 28,
      "purchase_count": 1
    },
    {
      "location": "Anhui",
      "total_amount": 26,
      "purchase_count": 1
    },
    {
      "location": "Shaanxi",
      "total_amount": 25,
      "purchase_count": 1
    }
  ],
  "success": true
}
```

### 6. 日期范围统计

- **URL**: `/api/shopping-records/stats/date-range`
- **方法**: GET
- **描述**: 获取按月份分组的购物统计数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "date": "2023-01",
      "total_amount": 171,
      "purchase_count": 5
    },
    {
      "date": "2023-02",
      "total_amount": 233,
      "purchase_count": 4
    },
    {
      "date": "2023-03",
      "total_amount": 157,
      "purchase_count": 2
    },
    {
      "date": "2023-04",
      "total_amount": 155,
      "purchase_count": 3
    },
    {
      "date": "2023-06",
      "total_amount": 197,
      "purchase_count": 4
    },
    {
      "date": "2023-07",
      "total_amount": 187,
      "purchase_count": 3
    },
    {
      "date": "2023-08",
      "total_amount": 213,
      "purchase_count": 3
    },
    {
      "date": "2023-09",
      "total_amount": 71,
      "purchase_count": 1
    },
    {
      "date": "2023-10",
      "total_amount": 163,
      "purchase_count": 2
    },
    {
      "date": "2023-11",
      "total_amount": 396,
      "purchase_count": 5
    },
    {
      "date": "2023-12",
      "total_amount": 245,
      "purchase_count": 4
    }
  ],
  "success": true
}
```

### 7. 客户购买统计

- **URL**: `/api/shopping-records/stats/customer`
- **方法**: GET
- **描述**: 获取按客户ID分组的购物统计数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "customer_id": "BH-11710",
      "total_amount": 395,
      "avg_amount": 49.375,
      "purchase_count": 8
    },
    {
      "customer_id": "GA-14725",
      "total_amount": 222,
      "avg_amount": 74,
      "purchase_count": 3
    },
    {
      "customer_id": "SO-20335",
      "total_amount": 139,
      "avg_amount": 69.5,
      "purchase_count": 2
    },
    {
      "customer_id": "SV-20935",
      "total_amount": 119,
      "avg_amount": 59.5,
      "purchase_count": 2
    },
    {
      "customer_id": "CG-12520",
      "total_amount": 117,
      "avg_amount": 58.5,
      "purchase_count": 2
    },
    {
      "customer_id": "AC-10660",
      "total_amount": 100,
      "avg_amount": 100,
      "purchase_count": 1
    },
    {
      "customer_id": "RB-19360",
      "total_amount": 95,
      "avg_amount": 95,
      "purchase_count": 1
    },
    {
      "customer_id": "DM-13345",
      "total_amount": 93,
      "avg_amount": 46.5,
      "purchase_count": 2
    },
    {
      "customer_id": "CC-12370",
      "total_amount": 93,
      "avg_amount": 93,
      "purchase_count": 1
    },
    {
      "customer_id": "SC-20380",
      "total_amount": 87,
      "avg_amount": 87,
      "purchase_count": 1
    },
    {
      "customer_id": "JB-15400",
      "total_amount": 80,
      "avg_amount": 80,
      "purchase_count": 1
    },
    {
      "customer_id": "KC-16255",
      "total_amount": 79,
      "avg_amount": 79,
      "purchase_count": 1
    },
    {
      "customer_id": "BG-11695",
      "total_amount": 79,
      "avg_amount": 79,
      "purchase_count": 1
    },
    {
      "customer_id": "XP-21865",
      "total_amount": 77,
      "avg_amount": 77,
      "purchase_count": 1
    },
    {
      "customer_id": "VF-21715",
      "total_amount": 73,
      "avg_amount": 73,
      "purchase_count": 1
    },
    {
      "customer_id": "DV-13045",
      "total_amount": 73,
      "avg_amount": 73,
      "purchase_count": 1
    },
    {
      "customer_id": "TC-21295",
      "total_amount": 50,
      "avg_amount": 50,
      "purchase_count": 1
    },
    {
      "customer_id": "DJ-13510",
      "total_amount": 48,
      "avg_amount": 48,
      "purchase_count": 1
    },
    {
      "customer_id": "CK-12325",
      "total_amount": 47,
      "avg_amount": 47,
      "purchase_count": 1
    },
    {
      "customer_id": "SN-20560",
      "total_amount": 36,
      "avg_amount": 36,
      "purchase_count": 1
    },
    {
      "customer_id": "RL-19615",
      "total_amount": 33,
      "avg_amount": 33,
      "purchase_count": 1
    },
    {
      "customer_id": "EM-13810",
      "total_amount": 28,
      "avg_amount": 28,
      "purchase_count": 1
    },
    {
      "customer_id": "AJ-10780",
      "total_amount": 25,
      "avg_amount": 25,
      "purchase_count": 1
    }
  ],
  "success": true
}
```

### 8. 分页查询购物记录

- **URL**: `/api/shopping-records/page`
- **方法**: GET
- **描述**: 分页查询购物记录数据，支持多个过滤条件
- **参数**:
  - `current`: 当前页码，默认为1
  - `size`: 每页大小，默认为10
  - `category`: 商品类别过滤，如 "Clothing"
  - `gender`: 性别过滤，如 "Male"
  - `season`: 季节过滤，如 "Summer"
- **响应示例**:

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "pageNum": 1,
        "pageSize": 10,
        "totalPage": 4,
        "total": 36,
        "list": [
            {
                "id": 1,
                "date": "2023-12-27 00:00:00",
                "customerId": "CG-12520",
                "age": 55,
                "gender": "Male",
                "item": "Blouse",
                "category": "Clothing",
                "purchaseAmount": 53.00,
                "location": "Zhejiang",
                "size": "L",
                "color": "Gray",
                "season": "Winter",
                "rating": 3.1,
                "subscriptionStatus": "Yes",
                "discountApplied": "Yes",
                "promoCodeUsed": "Yes",
                "createdAt": "2025-07-01 14:36:37",
                "updatedAt": "2025-07-01 14:36:37"
            }
        ]
    },
    "success": true
}
```

- **响应字段说明**:
  - 分页信息:
    - `pageNum`: 当前页码
    - `pageSize`: 每页大小
    - `totalPage`: 总页数
    - `total`: 总记录数
  - 记录字段:
    - `id`: 记录ID
    - `date`: 购买日期
    - `customerId`: 客户ID
    - `age`: 客户年龄
    - `gender`: 客户性别
    - `item`: 商品名称
    - `category`: 商品类别
    - `purchaseAmount`: 购买金额
    - `location`: 购买地点
    - `size`: 商品尺寸
    - `color`: 商品颜色
    - `season`: 购买季节
    - `rating`: 评分
    - `subscriptionStatus`: 订阅状态
    - `discountApplied`: 是否使用折扣
    - `promoCodeUsed`: 是否使用促销码
    - `createdAt`: 创建时间
    - `updatedAt`: 更新时间
- **示例请求**:

```
# 基础分页查询
GET http://localhost:8080/api/shopping-records/page?current=1&size=10

# 带过滤条件的查询
GET http://localhost:8080/api/shopping-records/page?current=1&size=10&category=Clothing&gender=Male&season=Summer
```

### 9. summary统计

- **URL**: `/api/shopping-records/stats/summary`
- **方法**: GET
- **描述**: 获取summary数据
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "order_count": 36,
    "avg_order_amount": 60.7777777777778,
    "total_sales": 2188,
    "customer_count": 23
  },
  "success": true
}
```


### 10. 分页查询购物记录

- **URL**: `/api/shopping-records?page=1&pageSize=10`
- **方法**: GET
- **描述**: 分页查询购物记录数据，支持多个过滤条件
- **参数**:
- **响应示例**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "totalPage": 4,
    "total": 36,
    "list": [
      {
        "id": 1,
        "date": "2023-12-27 00:00:00",
        "customerId": "CG-12520",
        "age": 55,
        "gender": "Male",
        "item": "Blouse",
        "category": "Clothing",
        "purchaseAmount": 53,
        "location": "Zhejiang",
        "size": "L",
        "color": "Gray",
        "season": "Winter",
        "rating": 3.1,
        "subscriptionStatus": "Yes",
        "discountApplied": "Yes",
        "promoCodeUsed": "Yes",
        "createdAt": "2025-06-26 11:32:12",
        "updatedAt": "2025-06-26 11:32:12"
      },
      {
        "id": 2,
        "date": "2023-03-06 00:00:00",
        "customerId": "CG-12520",
        "age": 19,
        "gender": "Male",
        "item": "Sweater",
        "category": "Clothing",
        "purchaseAmount": 64,
        "location": "Shandong",
        "size": "L",
        "color": "Maroon",
        "season": "Spring",
        "rating": 3.1,
        "subscriptionStatus": "Yes",
        "discountApplied": "Yes",
        "promoCodeUsed": "Yes",
        "createdAt": "2025-06-26 11:32:12",
        "updatedAt": "2025-06-26 11:32:12"
      },
      {
        "id": 10,
        "date": "2023-12-03 00:00:00",
        "customerId": "BH-11710",
        "age": 57,
        "gender": "Male",
        "item": "Handbag",
        "category": "Accessories",
        "purchaseAmount": 31,
        "location": "Sichuan",
        "size": "M",
        "color": "Pink",
        "season": "Winter",
        "rating": 4.8,
        "subscriptionStatus": "Yes",
        "discountApplied": "Yes",
        "promoCodeUsed": "Yes",
        "createdAt": "2025-06-26 11:32:12",
        "updatedAt": "2025-06-26 11:32:12"
      }
    ]
  },
  "success": true
}
```