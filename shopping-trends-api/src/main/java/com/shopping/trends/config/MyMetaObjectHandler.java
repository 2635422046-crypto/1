package com.shopping.trends.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器：
 * 在执行 INSERT / UPDATE 时自动为 createTime、updateTime 字段赋值当前时间，
 * 前提是实体字段上标注了 {@code @TableField(fill = FieldFill.INSERT)} 等注解。
 * 本项目 shopping_data 表为已有数据（只读查询），此处为扩展预留。
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * INSERT 时自动填充：createTime 和 updateTime 均设为当前时间
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * UPDATE 时自动填充：仅更新 updateTime 为当前时间
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}