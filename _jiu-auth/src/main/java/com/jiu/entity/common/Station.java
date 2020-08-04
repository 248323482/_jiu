package com.jiu.entity.common;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jiu.base.entity.Entity;
import com.jiu.base.model.RemoteData;
import com.jiu.injection.annonation.InjectionField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.SqlCondition.LIKE;
import static com.jiu.common.constant.InjectionFieldConstants.ORG_ID_CLASS;
import static com.jiu.common.constant.InjectionFieldConstants.ORG_ID_METHOD;

/**
 * <p>
 * 实体类
 * 岗位
 * </p>
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("c_core_station")
@ApiModel(value = "Station", description = "岗位")
public class Station extends Entity<Long> {

    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @NotEmpty(message = "名称不能为空")
    @Length(max = 255, message = "名称长度不能超过255")
    @TableField(value = "name", condition = LIKE)
    @Excel(name = "名称")
    private String name;

    /**
     * 组织ID
     * #c_core_org
     */
    @ApiModelProperty(value = "组织ID")
    @TableField("org_id")
    @InjectionField(api = ORG_ID_CLASS, method = ORG_ID_METHOD)
    @Excel(name = "组织")
    @ExcelEntity(name = "")
    private RemoteData<Long, Org> org;


    /**
     * 状态
     */
    @ApiModelProperty(value = "状态")
    @TableField("status")
    @Excel(name = "状态", replace = {"启用_true", "禁用_false", "_null"})
    private Boolean status;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    @Length(max = 255, message = "描述长度不能超过255")
    @TableField(value = "describe_", condition = LIKE)
    @Excel(name = "描述", width = 50)
    private String describe;


    @Builder
    public Station(Long id, LocalDateTime createTime, Long createUser, LocalDateTime updateTime, Long updateUser,
                   String name, RemoteData<Long, Org> orgId, Boolean status, String describe) {
        this.id = id;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.name = name;
        this.org = orgId;
        this.status = status;
        this.describe = describe;
    }

}
