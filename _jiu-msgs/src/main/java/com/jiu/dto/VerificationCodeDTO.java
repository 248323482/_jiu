package com.jiu.dto;

import com.jiu.base.entity.SuperEntity;
import com.jiu.entity.enumeration.VerificationCodeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 验证码发送验证DTO
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "VerificationCodeDTO", description = "验证码发送验证DTO")
public class VerificationCodeDTO implements Serializable {
    @ApiModelProperty(value = "手机号")
    @NotEmpty(message = "手机号不能为空")
    private String mobile;
    @ApiModelProperty(value = "类型")
    @NotNull(message = "类型不能为空")
    private VerificationCodeType type;

    @ApiModelProperty(value = "验证码")
    @NotEmpty(groups = SuperEntity.Update.class, message = "验证码不能为空")
    private String code;
}
