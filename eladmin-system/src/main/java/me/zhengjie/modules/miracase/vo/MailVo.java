package me.zhengjie.modules.miracase.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by fantastyJ on 2020/6/19 10:29 上午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailVo {
    private String num;
    private String subject;
    private String sendTime;
    //是否已读
    private boolean isNew;
    //是否包含附件
    private boolean containAttach;
    //发送人地址
    private String from;
    //收件人地址
    private String to;
    //抄送
    private String cc;
    //正文
    private String text;
}
