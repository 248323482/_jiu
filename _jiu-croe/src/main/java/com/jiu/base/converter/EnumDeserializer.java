package com.jiu.base.converter;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.jiu.base.BaseEnum;
import com.jiu.database.mybatis.auth.DataScopeType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.util.Objects;

import static com.jiu.base.converter.EnumSerializer.ALL_ENUM_KEY_FIELD;

/**
 * enum反序列化工具
 *
 */
@Slf4j
@JsonComponent
@Setter
//https://zhuanlan.zhihu.com/p/150370826
public class EnumDeserializer extends JsonDeserializer<Enum<?>> implements ContextualDeserializer {
    public final static EnumDeserializer INSTANCE = new EnumDeserializer();
    private Class<?> clazz;


    @Override
    public Enum<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Class<?> enumType = clazz;
        if (Objects.isNull(enumType) || !enumType.isEnum()) {
            return null;
        }
        try {
            // 读取
            JsonNode node = jp.getCodec().readTree(jp);
            // 当前字段
            String currentName = jp.currentName();
            // 当前对象
            Object currentValue = jp.getCurrentValue();
            // 在对象中找到改字段
            JsonNode code = node.get(ALL_ENUM_KEY_FIELD);
            String val = code != null ? code.asText() : node.asText();
            if (StrUtil.isBlank(val)) {
                return null;
            }
            return Enum.valueOf((Class<Enum>) enumType, val);
        } catch (Exception e) {
            log.warn("解析枚举失败", e);
            return null;
        }
    }


    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctx, BeanProperty property) throws JsonMappingException {
        Class<?> rawCls = ctx.getContextualType().getRawClass();
        EnumDeserializer converter = new EnumDeserializer();
        converter.setClazz(rawCls);
        return converter;
    }
}
