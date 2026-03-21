package com.xushu.campus.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户地址DTO
 */
@Data
@Schema(description = "用户地址信息")
public class UserAddressDTO {

    @Schema(description = "地址ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @NotBlank(message = "收货人姓名不能为空")
    @Schema(description = "收货人姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Schema(description = "收货人电话", requiredMode = Schema.RequiredMode.REQUIRED)
    private String receiverPhone;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区县")
    private String district;

    @NotBlank(message = "详细地址不能为空")
    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detailAddress;

    @Schema(description = "邮政编码")
    private String postalCode;

    @Schema(description = "是否默认地址：0-否，1-是")
    private Integer isDefault;

}