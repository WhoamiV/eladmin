package me.zhengjie;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import me.zhengjie.domain.EmailConfig;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.modules.miracase.util.JavaMailUtil;
import me.zhengjie.modules.miracase.vo.MailVo;
import me.zhengjie.utils.EncryptUtils;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by fantastyJ on 2020/6/22 2:06 下午
 */

public class TestSendEmail {

    @Test
    public void t(){
        MailAccount account = new MailAccount();
        account.setHost("smtp.ibreezee.cn");
        account.setPort(25);
        account.setUser("liangyingqi@ibreezee.cn");
        account.setAuth(true);
        // ssl方式发送
        account.setSslEnable(false);
        try {
            // 对称解密
            account.setPass("viki1230.i");
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom("Lyq"+"<liangyingqi@ibreezee.cn>");
        String content = "你好，我是你的老同学。";
        // 发送
        try {
            int size = 1;
            Mail.create(account)
                    .setTos("liangyingqi@ibreezee.cn")
                    .setTitle("Hello")
                    .setContent(content)
                    .setHtml(true)
                    //关闭session
                    .setUseGlobalSession(false)
                    .send();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Test
    public void t2() throws Exception{
        JavaMailUtil.saveAsDraft(EmailConfig.builder().hostImap("smtp.ibreezee.cn").user("liangyingqi@ibreezee.cn")
                        .pass(EncryptUtils.desEncrypt("viki1230.i")).build(),
                EmailVo.builder().num(1).subject("hhehe ...")
                .content("测试2324").tos(Stream.of("465612025@qq.com", "323293232@qq.com").collect(Collectors.toList())).build());
    }

    @Test
    public void t3() throws Exception{
        List<MailVo> mailVoList = JavaMailUtil.getMailList(
                EmailConfig.builder().hostImap("imap.ibreezee.cn").user("liangyingqi@ibreezee.cn").pass(EncryptUtils.desEncrypt("viki1230.i")).build(),
//                EmailConfig.builder().hostImap("imap.qq.com").user("465612025@qq.com").pass(EncryptUtils.desEncrypt("szhtazqrqerecace")).build(),
                JavaMailUtil.FOLDER_INBOX,10);
        System.out.println("");
    }

    @Test
    public void t4() throws Exception{
        JavaMailUtil.delete(
                EmailConfig.builder().hostImap("imap.ibreezee.cn").user("liangyingqi@ibreezee.cn").pass(EncryptUtils.desEncrypt("viki1230.i")).build(),
//                EmailConfig.builder().hostImap("imap.qq.com").user("465612025@qq.com").pass(EncryptUtils.desEncrypt("szhtazqrqerecace")).build(),
                "1",JavaMailUtil.FOLDER_DRAFT,false);
    }
}
