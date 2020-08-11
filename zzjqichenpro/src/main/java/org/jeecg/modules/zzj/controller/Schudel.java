package org.jeecg.modules.zzj.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.License;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.zzj.util.Base64Img;
import org.jeecg.modules.zzj.util.Publicsecurity.clientele;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/Schudel")
public class Schudel {

    @Autowired
    private RedisUtil redisUtil;

    private String gongAnImgurl="D:\\demo\\photo.bmp";
    private String gongAnNewImgurl="D:\\gonganimage\\";

    /**
     * 公安上传身份证上图片留存
     */
    @ApiOperation(value = "gongAnImgSave")
    @RequestMapping(value = "/gongAnImgSave", method = RequestMethod.GET)
    public void gongAnImgSave(String accnt) throws Exception {
        log.info("gongAnImgSave()方法");
        copy(gongAnImgurl, gongAnNewImgurl+accnt+".jpg");
    }

    @ApiOperation(value = "setRedis")
    @RequestMapping(value = "/setRedis", method = RequestMethod.GET)
    public void setRedis(String cardType,String birthdaytime,String OPERATETYPE,
                          String name,String sex,String race,String idEntityCard,
                          String street,String ConfirmNo){
        List<clientele> list=(List<clientele>)redisUtil.get(ConfirmNo);
        if (list==null){
            list=new ArrayList<>();
        }
        clientele clientele= new clientele();
        clientele.setCARD_TYPE(cardType);
        clientele.setBIRTHDAY(birthdaytime);//出生日期
        clientele.setFLAG("CHINESE");
        clientele.setID(UUID.randomUUID().toString().replaceAll("-",""));
        clientele.setOPERATETYPE(OPERATETYPE);//对该旅客的操作类型 只有入住
        clientele.setNAME(name);//入住人姓名
        clientele.setSEX(sex);//性别
        clientele.setNATION(race); //民族
        clientele.setCARD_NO(idEntityCard);//入住人身份证号码
        clientele.setADDRDETAIL(street);//地址
        String imgurl=gongAnNewImgurl+idEntityCard+".jpg";
        clientele.setPHOTO(imgurl);
        list.add(clientele);
        redisUtil.set(ConfirmNo,list,0);
    }

    @ApiOperation(value = "setOneRedis")
    @RequestMapping(value = "/setOneRedis", method = RequestMethod.GET)
    public void setOneRedis(String cardType,String birthdaytime,String OPERATETYPE,
                         String name,String sex,String race,String idEntityCard,
                         String street){
        clientele clientele= new clientele();
        clientele.setCARD_TYPE(cardType);
        clientele.setBIRTHDAY(birthdaytime);//出生日期
        clientele.setFLAG("CHINESE");
        clientele.setID(UUID.randomUUID().toString().replaceAll("-",""));
        clientele.setOPERATETYPE(OPERATETYPE);//对该旅客的操作类型 只有入住
        clientele.setNAME(name);//入住人姓名
        clientele.setSEX(sex);//性别
        clientele.setNATION(race); //民族
        clientele.setCARD_NO(idEntityCard);//入住人身份证号码
        clientele.setADDRDETAIL(street);//地址
        String imgurl=gongAnNewImgurl+idEntityCard+".jpg";
        clientele.setPHOTO(imgurl);
        redisUtil.set(idEntityCard,clientele,0);
    }

    @ApiOperation(value = "getOneRedis")
    @RequestMapping(value = "/getOneRedis", method = RequestMethod.GET)
    public Object getOneRedis(String idEntityCard){
        return redisUtil.get(idEntityCard);
    }

    @ApiOperation(value = "cleanOneRedis")
    @RequestMapping(value = "/cleanOneRedis", method = RequestMethod.GET)
    public void cleanOneRedis(String idEntityCard) throws Exception {
        redisUtil.del(idEntityCard);
    }

    @ApiOperation(value = "getRedis")
    @RequestMapping(value = "/getRedis", method = RequestMethod.GET)
    public List<clientele> getRedis(String ConfirmNo) throws Exception {
        List<clientele> list=(List<clientele>)redisUtil.get(ConfirmNo);
        return  list;
    }




    @ApiOperation(value = "cleanRedis")
    @RequestMapping(value = "/cleanRedis", method = RequestMethod.GET)
    public void cleanRedis(String ConfirmNo) throws Exception {
        redisUtil.del(ConfirmNo);
    }


    public void copy(String oldPath, String newPath) throws Exception {
        InputStream in=new FileInputStream(oldPath);
        byte[] b=new byte[in.available()];
        in.read(b);
        OutputStream out=new FileOutputStream(newPath);
        out.write(b);
        in.close();
        out.close();
    }
}
