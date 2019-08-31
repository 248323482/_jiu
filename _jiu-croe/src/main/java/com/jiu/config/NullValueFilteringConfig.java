package com.jiu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

public class NullValueFilteringConfig {

	@Bean
	@Primary
	@ConditionalOnMissingBean(ObjectMapper.class)
	public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		

		// 通过该方法对mapper对象进行设置，所有序列化的对象都将按改规则进行系列化
		// Include.Include.ALWAYS 默认
		// Include.NON_DEFAULT 属性为默认值不序列化
		// Include.NON_EMPTY 属性为 空（""） 或者为 NULL
		// 都不序列化，则返回的json是没有这个字段的。这样对移动端会更省流量
		// Include.NON_NULL 属性为NULL 不序列化,就是为null的字段不参加序列化
		 objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		 objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		 // 允许出现特殊字符和转义符
//		 objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		 // 允许出现单引号
//		 objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);//统一加下划线
		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);//首字母大写
//		objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);//首字母大写


		// // 字段保留，将null值转为""
		// objectMapper.getSerializerProvider().setNullValueSerializer(new
		// JsonSerializer<Object>() {
		// @Override
		// public void serialize(Object o, JsonGenerator
		// jsonGenerator,SerializerProvider serializerProvider)
		// throws IOException, JsonProcessingException {
		// jsonGenerator.writeString("");
		// }
		// });
		return objectMapper;
	}
}
