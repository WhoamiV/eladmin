package me.zhengjie.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 发送邮件时，接收参数的类
 * @author 郑杰
 * @date 2018/09/28 12:02:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailVo {

    private Integer num;

    /** 收件人，支持多个收件人 */
    @NotEmpty
    private List<String> tos;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    public EmailVo(@NotEmpty List<String> tos, @NotBlank String subject, @NotBlank String content) {
        this.tos = tos;
        this.subject = subject;
        this.content = content;
    }
}
