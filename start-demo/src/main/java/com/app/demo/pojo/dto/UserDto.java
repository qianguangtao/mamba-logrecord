package com.app.demo.pojo.dto;

import com.app.core.mvc.serialization.Dictionary;
import com.app.demo.dictionary.DictionaryEnum;
import com.app.demo.enums.SexEnum;
import com.app.demo.translator.AddressTranslatorTemplate;
import com.app.logrecord.annotation.LogRecordField;
import com.app.logrecord.enums.FieldStrategy;
import com.app.logrecord.translator.BoolTranslator;
import com.app.logrecord.translator.EnumTranslator;
import com.app.logrecord.translator.ListTranslator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 用户表
 * @author qiangt
 * @since 2022-10-24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel(description = "用户表")
public class UserDto {

    private Long id;

    @LogRecordField(value = "用户名")
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    @JsonIgnore
    private String password;

    @LogRecordField(value = "手机号")
    @ApiModelProperty(value = "手机号")
    private String cellphone;

    @LogRecordField(value = "邮箱")
    @ApiModelProperty(value = "邮箱")
    private String email;

    @Max(100)
    @Min(1)
    @ApiModelProperty(value = "年龄")
    @LogRecordField(value = "年龄", fieldStrategy = FieldStrategy.UPDATE)
    private Integer age;

    @LogRecordField(value = "用户来源")
    @Dictionary(DictionaryEnum.Names.UserSource)
    @ApiModelProperty(value = "用户来源")
    private String source;

    @LogRecordField(value = "用户类型", translator = EnumTranslator.class)
    @ApiModelProperty(value = "用户类型")
    private SexEnum sex;

    @LogRecordField(value = "用户类型", translator = ListTranslator.class)
    @ApiModelProperty(value = "用户类型")
    private List<String> roles;

    @LogRecordField(value = "用户类型", translator = BoolTranslator.class)
    @ApiModelProperty(value = "用户类型")
    private Boolean enabled;

    @LogRecordField(value = "用户类型", translatorTemplate = AddressTranslatorTemplate.class)
    @ApiModelProperty(value = "用户类型")
    private Address address;

}
