package me.zhengjie.modules.miracase.util;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.EmailConfig;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.modules.miracase.vo.MailVo;
import me.zhengjie.utils.EncryptUtils;
import me.zhengjie.utils.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * @author yh
 */
@Slf4j
public class JavaMailUtil {

    private MimeMessage mimeMessage = null;
    private String saveAttachPath = ""; // 附件下载后的存放目录
    private StringBuffer bodyText = new StringBuffer(); // 存放邮件内容的StringBuffer对象
    private String dateFormat = "yyyy-MM-dd HH:mm"; // 默认的日前显示格式

    public static final String FOLDER_INBOX = "INBOX";
    public static final String FOLDER_OUTBOX = "Sent";//已发送
    public static final String FOLDER_TRASH = "Trash";//已删除
    public static final String FOLDER_DRAFT = "Drafts";//草稿
    public static final String FOLDER_SPAM = "Spam";//垃圾箱

    /**
     * 　ReceiveEmail类测试
     */
    public static void main(String args[]) throws Exception {
        String host = "imap.ibreezee.cn";
        String username = "liangyingqi@ibreezee.cn";
        String password = "viki1230.i";

        List<MailVo> mails = JavaMailUtil.getMailList(host,username,EncryptUtils.desEncrypt(password),FOLDER_INBOX,10);
        System.out.println("邮件数量:　" + mails.size());

        System.out.println(mails);

    }

    public static List<MailVo> getMailList(String host,String user, String pass, String folderName, int readCount){
        return getMailList(EmailConfig.builder().hostImap(host).user(user).pass(pass).build(),folderName,readCount);
    }

    /**
     * 读取邮箱列表
     * @param folderName 列表种类
     * @param readCount 读取数量
     * @return
     */
    public static List<MailVo> getMailList(EmailConfig config, String folderName, int readCount){
        List<MailVo> mails = new ArrayList<>();
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imap");
            store.connect(config.getHostImap(), config.getUser(), EncryptUtils.desDecrypt(config.getPass()));
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            int msg = folder.getNewMessageCount();
            int unreadCount = folder.getUnreadMessageCount();
            Message[] messages = folder.getMessages(Math.max(0,folder.getMessageCount()-readCount+1),folder.getMessageCount());
            log.info(String.format("%s,新消息%d条,未读消息:%d条,总条数:%d条，想要读取%d条",folderName,msg,unreadCount,messages.length,readCount));
            for (int i = messages.length-1; i >= 0; i--) {
                Message message = messages[i];
                boolean seen = message.getFlags().contains(Flags.Flag.SEEN);

                //解析邮件内容
                Object content = message.getContent();
                String text = "";
                if (content instanceof MimeMultipart) {
                    MimeMultipart multipart = (MimeMultipart) content;
                    StringBuilder stringBuilder = new StringBuilder();
                    parseMultipart(multipart,stringBuilder);
                    text = stringBuilder.toString();
                }else if(content instanceof String){
                    text = (String) content;
                }
                if(!seen) message.setFlag(Flags.Flag.SEEN,seen);//如果未读，不修改这个状态
                mails.add(
                        MailVo.builder().sendTime(getSentDate(message.getSentDate())).subject(message.getSubject()).text(text)
                                .to(getMailAddress(message,"to"))
                                .from(getFrom(message))
                                .num(message.getMessageNumber()+"")
                                .build()
                );
            }
            folder.close(false);//expunge 擦去；删掉
            store.close();
        }catch (Exception e){
            log.error("getInboxMails exception....",e);
        }
        return mails;
    }

    /**
     * 　*　获得发件人的地址和姓名
     */
    private static String getFrom(Message message) throws Exception {
        InternetAddress address[] = (InternetAddress[]) message.getFrom();
        String from = address[0].getAddress();
        if (from == null) {
            from = "";
//            System.out.println("无法知道发送者.");
        }
        String personal = address[0].getPersonal();

        if (personal == null) {
            personal = "";
//            System.out.println("无法知道发送者的姓名.");
        }

        String fromAddr = null;
        if (personal != null || from != null) {
            fromAddr = personal + "<" + from + ">";
//            System.out.println("发送者是：" + fromAddr);
        } else {
//            System.out.println("无法获得发送者信息.");
        }
        return fromAddr;
    }

    /**
     * 　*　获得邮件发送日期
     */
    private static String getSentDate(Date date) throws Exception {
        if(date == null) return StringUtils.EMPTY;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strSentDate = format.format(date);
        return strSentDate;
    }

    /**
     * 　*　获得邮件的收件人，抄送，和密送的地址和姓名，根据所传递的参数的不同
     * 　*　"to"----收件人　"cc"---抄送人地址　"bcc"---密送人地址
     */
    private static String getMailAddress(Message message, String type) throws Exception {
        String mailAddr = "";
        String addType = type.toUpperCase();

        InternetAddress[] address = null;
        if (addType.equals("TO") || addType.equals("CC")
                || addType.equals("BCC")) {

            if (addType.equals("TO")) {
                address = (InternetAddress[]) message
                        .getRecipients(Message.RecipientType.TO);
            } else if (addType.equals("CC")) {
                address = (InternetAddress[]) message
                        .getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) message
                        .getRecipients(Message.RecipientType.BCC);
            }

            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String emailAddr = address[i].getAddress();
                    if (emailAddr == null) {
                        emailAddr = "";
                    } else {
                        emailAddr = MimeUtility.decodeText(emailAddr);
                    }
                    String personal = address[i].getPersonal();
                    if (personal == null) {
                        personal = "";
                    } else {
                        personal = MimeUtility.decodeText(personal);
                    }
                    String compositeto = personal + "<" + emailAddr + ">";
//                    System.out.println("完整的邮件地址：" + compositeto);
                    mailAddr += "," + compositeto;
                }
                mailAddr = mailAddr.substring(1);
            }
        } else {
            throw new Exception("错误的电子邮件类型!");
        }
        return mailAddr;
    }

    /**
     * 对复杂邮件的解析
     * @param multipart
     * @throws MessagingException
     * @throws IOException
     */
    public static void parseMultipart(Multipart multipart,StringBuilder stringBuilder) throws MessagingException, IOException {
        int count = multipart.getCount();
        System.out.println("count =  "+count);
        for (int idx=0;idx<count;idx++) {
            BodyPart bodyPart = multipart.getBodyPart(idx);
            System.out.println(bodyPart.getContentType());
            if (bodyPart.isMimeType("text/plain")) {
                stringBuilder.append(bodyPart.getContent());
            } else if(bodyPart.isMimeType("text/html")) {
                stringBuilder.append(bodyPart.getContent());
            } else if(bodyPart.isMimeType("multipart/*")) {
                Multipart mpart = (Multipart)bodyPart.getContent();
                parseMultipart(mpart,stringBuilder);

            } else if (bodyPart.isMimeType("application/octet-stream")) {
                String disposition = bodyPart.getDisposition();
                System.out.println(disposition);
                if (disposition.equalsIgnoreCase(BodyPart.ATTACHMENT)) {
                    String fileName = bodyPart.getFileName();
                    InputStream is = bodyPart.getInputStream();
                    copy(is, new FileOutputStream("D:\\"+fileName));
                }
            }
        }
    }

    /**
     * 文件拷贝，在用户进行附件下载的时候，可以把附件的InputStream传给用户进行下载
     * @param is
     * @param os
     * @throws IOException
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len=is.read(bytes)) != -1 ) {
            os.write(bytes, 0, len);
        }
        if (os != null)
            os.close();
        if (is != null)
            is.close();
    }

    public static void saveAsDraft(EmailConfig config, EmailVo email) {
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            Store store = session.getStore("imap");
            store.connect(config.getHostImap(), config.getUser(), EncryptUtils.desDecrypt(config.getPass()));
            Folder folder = store.getFolder("Drafts");
            if(email.getNum() != null && folder.getMessageCount() > email.getNum()){
                //如果是修改草稿邮件，先删掉之前的
                folder.open(Folder.READ_WRITE);
                Message msg = folder.getMessage(email.getNum());
                if(msg != null) msg.setFlag(Flags.Flag.DELETED,true);
            }
            //新增插入草稿邮件
            MimeMessage mmessage = new MimeMessage(session);
            mmessage.setFrom(new InternetAddress(config.getUser()));
            InternetAddress[] toAddress = new InternetAddress[email.getTos().size()];
            for(int i = 0; i < email.getTos().size();i++){
                toAddress[i] = new InternetAddress(email.getTos().get(i));
            }

            mmessage.setRecipients(Message.RecipientType.TO,toAddress);
            mmessage.setSubject(email.getSubject());
            Multipart mainPart = new MimeMultipart();
            BodyPart html = new MimeBodyPart();
            html.setContent(email.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            mmessage.setContent(mainPart);
            mmessage.setSentDate(new Date());
            mmessage.saveChanges();
            mmessage.setFlag(Flags.Flag.DRAFT, true);
            MimeMessage[] draftMessages = {mmessage};
            folder.appendMessages(draftMessages);
            if(email.getNum() != null && folder.isOpen()){
                folder.close(true);
            }
            store.close();
        }catch (Exception e){
            log.error("邮箱服务器异常,",e);
        }
    }

    /**
     * 删除邮件
     * @param id 邮件的ID
     * @param forever 是否彻底删除
     */
    public static void delete(EmailConfig config, String id,String folderName, boolean forever){
        try {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imap");
            store.connect(config.getHostImap(), config.getUser(), EncryptUtils.desDecrypt(config.getPass()));
            Folder folder = store.getFolder(folderName);
            folder.open(Folder.READ_WRITE);
            Message message = folder.getMessage(Integer.parseInt(id));
            if(message != null){
                message.setFlag(Flags.Flag.DELETED,true);
            }
            //(非彻底删除功能还未调通)
            folder.close(forever);
            store.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

