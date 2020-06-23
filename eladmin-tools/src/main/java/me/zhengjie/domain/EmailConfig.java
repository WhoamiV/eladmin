package me.zhengjie.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 邮件配置类，存入数据存
 * IMAP： 默认bai端口为：143 （如勾选ssl安全链du接，端口号为993）
 * POP3： 默认端口为：110 （如勾选ssl安全链接，端口号为995）
 * SMTP： 默认端口为：25 （如勾选ssl安全链接，端口号为994
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "邮箱服务器配置")
@Table(name = "email_config")
public class EmailConfig implements Serializable {

    /** 用户ID */
    @Id
    @ApiModelProperty(value = "用户ID")
    private Long id;

    /** 发件服务器SMTP地址 */
    @NotBlank
    @ApiModelProperty(value = "收件服务器IMAP地址,例 smtp.ibreezee.cn")
    private String host;

    /** 收件服务器IMAP地址 */
    @NotBlank
    @Column(name = "host_imap")
    @ApiModelProperty(value = "收件服务器IMAP地址,例 imap.ibreezee.cn")
    private String hostImap;

    /** 发件人 */
    @NotBlank
    @Column(name = "user")
    @ApiModelProperty(value = "邮箱服务器账号名")
    private String user;

    /** 邮箱密码 */
    @NotBlank
    @ApiModelProperty(value = "邮箱服务器密码")
    private String pass;
}
