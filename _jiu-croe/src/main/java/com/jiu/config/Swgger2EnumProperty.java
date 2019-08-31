package com.jiu.config;

import com.fasterxml.classmate.ResolvedType;
import com.google.common.base.Optional;
import com.jiu.entity.vo.Result;
import io.swagger.annotations.ApiModelProperty;
import springfox.documentation.schema.Annotations;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.schema.ApiModelProperties;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Swgger2EnumProperty implements ModelPropertyBuilderPlugin {
    @Override
    public void apply(ModelPropertyContext context) {
        Optional<ApiModelProperty> annotation = Optional.absent();

        if (context.getAnnotatedElement().isPresent()) {
            annotation = annotation.or(ApiModelProperties.findApiModePropertyAnnotation(context.getAnnotatedElement().get()));
        }
        if (context.getBeanPropertyDefinition().isPresent()) {
            annotation = annotation.or(Annotations.findPropertyAnnotation(
                    context.getBeanPropertyDefinition().get(),
                    ApiModelProperty.class));
        }
        final Class<?> rawPrimaryType = context.getBeanPropertyDefinition().get().getRawPrimaryType();
        //过滤得到目标类型
        if (annotation.isPresent() && Result.SYSTEM_CODE.class.isAssignableFrom(rawPrimaryType)) {
            //获取CodedEnum的code值
        	Result.SYSTEM_CODE[] values = (Result.SYSTEM_CODE[]) rawPrimaryType.getEnumConstants();
            final List<String> displayValues = Arrays.stream(values).map(SYSTEM_CODE -> Integer.toString(SYSTEM_CODE.getCode())+"-"+SYSTEM_CODE.name()).collect(Collectors.toList());
            final AllowableListValues allowableListValues = new AllowableListValues(displayValues, rawPrimaryType.getTypeName());
            //固定设置为int类型
            final ResolvedType resolvedType = context.getResolver().resolve(String.class);
            context.getBuilder().allowableValues(allowableListValues).type(resolvedType);
        }
    }

    @Override
    public boolean supports(DocumentationType documentationType) {
        return true;
    }
}