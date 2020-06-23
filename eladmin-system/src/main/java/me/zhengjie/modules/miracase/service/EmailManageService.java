package me.zhengjie.modules.miracase.service;

import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.modules.miracase.vo.MailVo;

import java.util.List;

/**
 * @author Zheng Jie
 * @date 2018-12-13
 */
public interface EmailManageService {

    /**
     * 获取收件箱列表
     */
    List<MailVo> getInBoxMail(int readCount);

    List<MailVo> getSendBoxMail(int readCount);

    List<MailVo> getDraftBoxMail(int readCount);

    List<MailVo> getTrashBoxMail(int readCount);

    List<MailVo> getRubbishBoxMail(int readCount);

    void sendEmail(EmailVo emailVo) throws Exception;

    void saveAsDraft(EmailVo emailVo) throws Exception;
}
