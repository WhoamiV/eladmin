package me.zhengjie.modules.miracase.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.EmailConfig;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.modules.miracase.service.EmailManageService;
import me.zhengjie.modules.miracase.util.JavaMailUtil;
import me.zhengjie.modules.miracase.vo.MailVo;
import me.zhengjie.service.EmailService;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmailManageServiceImpl implements EmailManageService {

    String host = "imap.ibreezee.cn";
    String username = "liangyingqi@ibreezee.cn";
    String password = "viki1230.i";

    private final EmailService emailService;

    public EmailManageServiceImpl(EmailService emailService){
        this.emailService = emailService;
    }

    @Override
    public List<MailVo> getInBoxMail(int readCount) {
        EmailConfig config = emailService.findByUserId(SecurityUtils.getCurrentUserId());
        return JavaMailUtil.getMailList(config,JavaMailUtil.FOLDER_INBOX,readCount);
    }

    @Override
    public List<MailVo> getSendBoxMail(int readCount) {
        EmailConfig config = emailService.findByUserId(SecurityUtils.getCurrentUserId());
        return JavaMailUtil.getMailList(config,JavaMailUtil.FOLDER_OUTBOX,readCount);
    }

    @Override
    public List<MailVo> getDraftBoxMail(int readCount) {
        EmailConfig config = emailService.findByUserId(SecurityUtils.getCurrentUserId());
        return JavaMailUtil.getMailList(config,JavaMailUtil.FOLDER_DRAFT,readCount);
    }

    @Override
    public List<MailVo> getTrashBoxMail(int readCount) {
        EmailConfig config = emailService.findByUserId(SecurityUtils.getCurrentUserId());
        return JavaMailUtil.getMailList(config,JavaMailUtil.FOLDER_TRASH,readCount);
    }

    @Override
    public List<MailVo> getRubbishBoxMail(int readCount) {
        EmailConfig config = emailService.findByUserId(SecurityUtils.getCurrentUserId());
        return JavaMailUtil.getMailList(config,JavaMailUtil.FOLDER_SPAM,readCount);
    }

    @Override
    public void sendEmail(EmailVo emailVo) throws Exception{
        Long userId = SecurityUtils.getCurrentUserId();
        emailService.send(emailVo,emailService.findByUserId(userId));
    }

    @Override
    public void saveAsDraft(EmailVo emailVo) throws Exception {
        EmailConfig config = emailService.findByUserId(SecurityUtils.getCurrentUserId());
        if(emailVo.getNum() != null){
            JavaMailUtil.delete(config,String.valueOf(emailVo.getNum()),JavaMailUtil.FOLDER_DRAFT,true);
        }
        JavaMailUtil.saveAsDraft(config,emailVo);
    }
}
