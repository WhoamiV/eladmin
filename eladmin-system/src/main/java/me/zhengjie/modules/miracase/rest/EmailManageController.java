package me.zhengjie.modules.miracase.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import me.zhengjie.aop.log.Log;
import me.zhengjie.domain.EmailConfig;
import me.zhengjie.domain.vo.EmailVo;
import me.zhengjie.modules.miracase.service.EmailManageService;
import me.zhengjie.service.EmailService;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * Created by fantastyJ on 2020/6/19 9:33 上午
 */
@Api(tags = "邮件管理")
@RestController
@RequestMapping("/api/email")
public class EmailManageController {

    private final EmailManageService emailManageService;

    private final EmailService emailService;

    public EmailManageController(EmailManageService emailManageService, EmailService emailService){
        this.emailManageService = emailManageService;
        this.emailService = emailService;
    }

    @PostMapping
    @ApiOperation("获取邮箱服务器配置")
    @GetMapping("/config")
    public ResponseEntity<Object> get(){
        return new ResponseEntity<>(emailService.findByUserId(SecurityUtils.getCurrentUserId()),HttpStatus.OK);
    }

    @Log("配置邮箱服务器")
    @PutMapping("/config")
    @ApiOperation("配置邮箱服务器")
    public ResponseEntity<Object> emailConfig(@Validated @RequestBody EmailConfig emailConfig){
        if(emailConfig.getId() == null) emailConfig.setId(SecurityUtils.getCurrentUserId());
        emailService.update(emailConfig,emailService.findByUserId(SecurityUtils.getCurrentUserId()));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("发送邮件")
    @PostMapping("/send")
    @ApiOperation("发送邮件")
    public ResponseEntity<Object> send(@Validated @RequestBody EmailVo emailVo) throws Exception {
        emailManageService.sendEmail(emailVo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("保存草稿")
    @PostMapping("/saveAsDraft")
    @ApiOperation("发送邮件")
    public ResponseEntity<Object> saveDraft(@Validated @RequestBody EmailVo emailVo) throws Exception {
        emailManageService.saveAsDraft(emailVo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("收件箱列表")
    @ApiOperation("收件箱列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "readCount",value = "读取条数,默认10条")
    })
    @GetMapping(value = "/inbox")
    public ResponseEntity<Object> inbox(@RequestParam(value = "readCount", defaultValue = "10") int readCount){
        return new ResponseEntity<>(emailManageService.getInBoxMail(readCount), HttpStatus.OK);
    }

    @Log("发件箱列表")
    @ApiOperation("发件箱列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "readCount",value = "读取条数,默认10条")
    })
    @GetMapping(value = "/sendBox")
    public ResponseEntity<Object> sendbBox(@RequestParam(value = "readCount", defaultValue = "10") int readCount){
        return new ResponseEntity<>(emailManageService.getSendBoxMail(readCount), HttpStatus.OK);
    }

    @Log("草稿箱列表")
    @ApiOperation("草稿箱列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "readCount",value = "读取条数,默认10条")
    })
    @GetMapping(value = "/draftBox")
    public ResponseEntity<Object> drafBox(@RequestParam(value = "readCount", defaultValue = "10") int readCount){
        return new ResponseEntity<>(emailManageService.getDraftBoxMail(readCount), HttpStatus.OK);
    }

    @Log("垃圾邮件列表")
    @ApiOperation("垃圾邮件列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "readCount",value = "读取条数,默认10条")
    })
    @GetMapping(value = "/rubbishBox")
    public ResponseEntity<Object> rubbishBox(@RequestParam(value = "readCount", defaultValue = "10") int readCount){
        return new ResponseEntity<>(emailManageService.getRubbishBoxMail(readCount), HttpStatus.OK);
    }

    @Log("已删除列表")
    @ApiOperation("已删除列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "readCount",value = "读取条数,默认10条")
    })
    @GetMapping(value = "/trashBox")
    public ResponseEntity<Object> trashBox(@RequestParam(value = "readCount", defaultValue = "10") int readCount){
        return new ResponseEntity<>(emailManageService.getTrashBoxMail(readCount), HttpStatus.OK);
    }


}
