package org.jeecg.modules.zzj.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.zzj.entity.Race;
import org.jeecg.modules.zzj.entity.Reservation;
import org.jeecg.modules.zzj.service.GonganService;
import org.jeecg.modules.zzj.service.RaceService;
import org.jeecg.modules.zzj.service.ReservationService;
import org.jeecg.modules.zzj.util.Base64Img;
import org.jeecg.modules.zzj.util.Http.HttpUtil;
import org.jeecg.modules.zzj.util.JaxbUtil;
import org.jeecg.modules.zzj.util.Publicsecurity.clientele;
import org.jeecg.modules.zzj.util.Publicsecurity.gongan;
import org.jeecg.modules.zzj.util.Returned3.R;
import org.jeecg.modules.zzj.util.SignUtils;
import org.jeecg.modules.zzj.util.UuidUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Api(tags = "公安旅店录入信息接口")
@RestController
@RequestMapping("/zzj/Thepublic")
public class Thepublic {

    @Value("${fuyi.appKey}")
    private String appKey;
    @Value("${fuyi.appSecret}")
    private String appSecret;
    @Value("${fuyi.MobileNo}")
    private String MobileNo;
    @Value("${fuyi.Password}")
    private String Password;
    @Value("${fuyi.HotelNo}")
    private String HotelNo;
    @Value("${fuyiaddress}")
    private String fuyiaddress;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RaceService raceService;

    @Autowired
    private GonganService gonganService;

    /**
     *
     * @param  descript 民族
     * @return
     */
    @ApiOperation(value = "民族代码  code 转换")
    @RequestMapping(value = "/GetRace", method = RequestMethod.GET)
    public String GetRace(String descript) {
        if (StringUtils.isEmpty(descript)){
            return "转换失败，缺少参数";
        }
        Race race=raceService.getOne(new QueryWrapper<Race>().eq("descript",descript));
        return race.getRaceid();
    }

    /**
     *  公安一键入住
     */
    @ApiOperation(value = "公安一键入住", httpMethod = "GET")
    @RequestMapping(value = "/GongAnCheckIn", method = RequestMethod.GET)
    public R GongAnCheckIn(String ConfirmNo) throws Exception {
        List<clientele> list=(List<clientele>)redisUtil.get(ConfirmNo);
        for (clientele clientele : list){
            R r=information(clientele,ConfirmNo);
            gongan gongan=new gongan();
            gongan.setId(clientele.getID());
            gongan.setAddrdetail(clientele.getADDRDETAIL());
            gongan.setBirthday(clientele.getBIRTHDAY());
            gongan.setCardno(clientele.getCARD_NO());
            gongan.setCreatetime(clientele.getCREATE_TIME());
            gongan.setCardtype(clientele.getCARD_TYPE());
            gongan.setCreditcardno(clientele.getCREDITCARD_NO());
            gongan.setCreditcardtype(clientele.getCREDITCARD_TYPE());
            gongan.setEntertime(clientele.getENTER_TIME());
            gongan.setExittime(clientele.getEXIT_TIME());
            gongan.setFlag(clientele.getFLAG());
            gongan.setName(clientele.getNAME());
            gongan.setNation(clientele.getNATION());
            gongan.setOccupation(clientele.getOCCUPATION());
            gongan.setOperatetype(clientele.getOPERATETYPE());
            gongan.setReason(clientele.getREASON());
            gongan.setRoomno(clientele.getROOM_NO());
            gongan.setSex(clientele.getSEX());
            gongan.setConfirmno(ConfirmNo);
            gonganService.save(gongan);
        }
        return R.ok();
    }
    private String gongAnNewImgurl="D:\\gonganimage\\";
    /**
     *  公安一键续住 or 退房
     */
    @ApiOperation(value = "公安一键续住 or 退房", httpMethod = "GET")
    @RequestMapping(value = "/GongAnUpdateOrCheckOut", method = RequestMethod.GET)
    public R GongAnUpdateOrCheckOut(String ConfirmNo,String OPERATETYPE) throws Exception {
        log.info("ConfrimNo:"+ConfirmNo+",OPERATETYPE:"+OPERATETYPE);
        List<gongan> list=gonganService.list(new QueryWrapper<gongan>().eq("confirmno",ConfirmNo));
        if (null!=list && list.size()>0){
            for (gongan gongan :list){
                clientele clientele=new clientele();
                clientele.setID(gongan.getId());
                clientele.setADDRDETAIL(gongan.getAddrdetail());
                clientele.setBIRTHDAY(gongan.getBirthday());
                clientele.setCARD_NO(gongan.getCardno());
                clientele.setCARD_TYPE(gongan.getCardtype());
                clientele.setCREDITCARD_NO(gongan.getCreditcardno());
                clientele.setCREDITCARD_TYPE(gongan.getCreditcardtype());
                clientele.setFLAG(gongan.getFlag());
                clientele.setNAME(gongan.getName());
                clientele.setNATION(gongan.getNation());
                clientele.setOCCUPATION(gongan.getOccupation());
                clientele.setOPERATETYPE(OPERATETYPE);
                clientele.setREASON(gongan.getReason());
                clientele.setROOM_NO(gongan.getRoomno());
                clientele.setSEX(gongan.getSex());
                clientele.setPHOTO(gongAnNewImgurl+gongan.getCardno()+".jpg");
                information(clientele,ConfirmNo);
            }
        }else{
            return  R.error("该客人是未在自助机入住的客人，通知前台用公安软件续房");
        }
        return R.ok();
    }

    @ApiOperation(value = "公安旅店录入信息接口", httpMethod = "GET")
    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public R information(clientele clientele,String ConfirmNo) throws Exception {
        System.out.println("client:"+clientele);
        String path = "D:\\img\\";//本地xml文件的路径
        String pathname;
        String fullpath;
        File newFile = null;
        String uuid = UuidUtils.getUUID();
        pathname = uuid + ".tmp";
        fullpath = path + pathname;
        System.out.println("fullPath:"+fullpath);
        File filename = new File(fullpath);
        try {
            if (!filename.exists()) {
                filename.createNewFile();
            }
        } catch (IOException e) {
            log.info("创建文件失败");
            return R.error("创建文件失败");
        }

        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        clientele user = new clientele();
        String resStates=null;
        switch (clientele.getOPERATETYPE()){
            case "0":
            case "2":
            case "3":
                resStates="I";
                break;
            case "1":
                resStates="O";
                break;
        }
        if (clientele.getCARD_TYPE().equals("ID")) {
            user.setCARD_TYPE("11");//身份证
        }
        if (clientele.getCARD_TYPE().equals("PSP")) {
            user.setCARD_TYPE("93");//中华人民共和国护照
        }
        if (clientele.getCARD_TYPE().equals("HKP")) {
            user.setCARD_TYPE("20");//中华人民共和国往来港澳通行证
        }
        if (clientele.getCARD_TYPE().equals("TWP")) {
            user.setCARD_TYPE("台湾居民往来大陆许可证");
        }
        if (clientele.getCARD_TYPE().equals("FRP")) {
            user.setCARD_TYPE("外宾居留证");
        }
        if (clientele.getCARD_TYPE().equals("DP")) {
            user.setCARD_TYPE("外交护照");
        }
        if (clientele.getCARD_TYPE().equals("FTP")) {
            user.setCARD_TYPE("外宾旅游证");
        }
        if (clientele.getCARD_TYPE().equals("SP")) {
            user.setCARD_TYPE("公务护照");
        }
        if (clientele.getCARD_TYPE().equals("MO")) {
            user.setCARD_TYPE("90");//中国人民解放军军官证
        }
        if (clientele.getCARD_TYPE().equals("MS")) {
            user.setCARD_TYPE("92");//中国人民解放军士兵证
        }
        if (clientele.getCARD_TYPE().equals("OC")) {
            user.setCARD_TYPE("91");//中国人民武装警察部队警官证
        }
        if (clientele.getCARD_TYPE().equals("PTP")) {
            user.setCARD_TYPE("证照办理证明");
        }
        Reservation reservation=getOneReservation(ConfirmNo,resStates).get(0);
        System.out.println("reservation:"+reservation);
        //Reservation reservation=reservationService.getOne(new QueryWrapper<Reservation>().eq("Confirm_no",ConfirmNo));

        String checkintime = null;
        String checkouttime = null;
        String birthdaytime = null;

        checkintime = reservation.getArrivalTime().replaceAll("-","/").replaceAll("T"," ").substring(0,19);
        checkouttime = reservation.getDepartTime().replaceAll("-","/").replaceAll("T"," ").substring(0,19);
        birthdaytime = clientele.getBIRTHDAY();


        String race=GetRace(clientele.getNATION());
        if(race==null || "".equals(race)){
            race="01";//默认汉族
        }
        SimpleDateFormat formats = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date=new Date();
        Calendar calendar= Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE,calendar.get(Calendar.MINUTE)-30);
        date=calendar.getTime();
        String nowDate=formats.format(date);

        user.setENTER_TIME(nowDate+":00");//入住时间
        user.setEXIT_TIME(checkouttime);//离店时间
        user.setBIRTHDAY(birthdaytime);//出生日期
        user.setFLAG("CHINESE");
        user.setCREATE_TIME(nowDate);
        user.setID(clientele.getID());
        user.setOPERATETYPE(clientele.getOPERATETYPE());//对该旅客的操作类型 只有入住
        user.setROOM_NO(reservation.getRoomNo());//房间号
        user.setNAME(clientele.getNAME());//入住人姓名
        if ("男".equals(clientele.getSEX())){
            user.setSEX("1");//性别 nan
        }else{
            user.setSEX("2");//性别 nv
        }
        user.setNATION(race); //民族
        user.setCARD_NO(clientele.getCARD_NO());//入住人身份证号码
        user.setREASON("22");//来京理由 默认22
        user.setOCCUPATION("22");//职业 默认22
        user.setADDRDETAIL(clientele.getADDRDETAIL());//地址
        user.setCREDITCARD_NO(" ");//旅客信用卡号 可以为空
        user.setCREDITCARD_TYPE(" ");//旅客信用卡类型 可以为空
        System.out.println("user:"+user);
        user.setPHOTO(Base64Img.GetImageStr(clientele.getPHOTO()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(fullpath)),"utf-8"));
        String dataXml = JaxbUtil.convertToXml(user);
        out.write(dataXml); // \r\n即为换行
        out.flush();
        out.close(); // 最后记得关闭文件
        File parentDir = new File("\\\\192.168.100.8\\exchange\\data");  //部署时需要修改IP地址
        //File parentDir = new File("\\\\10.10.4.31\\data");  //部署时需要修改IP地址
        File targetpath = new File(parentDir, uuid + ".tmp");
        SAXReader reader = new SAXReader();
        Document read = reader.read(fullpath);
        //创建输出流
        FileOutputStream outStream = new FileOutputStream(targetpath);
        //设置输出格式
        OutputFormat format = OutputFormat.createCompactFormat();
        format.setEncoding("utf-8");
        XMLWriter writers = new XMLWriter(outStream, format);
        writers.write(read);
        outStream.close();//释放流

        return R.ok();
    }


    @RequestMapping(value = "/getOneReservation", method = RequestMethod.GET)
    @ApiOperation(value = "查询订单", httpMethod = "GET")
    public List<Reservation> getOneReservation(String ConfirmNo, String ResStates) throws Exception {
        log.info("getOneReservation()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ConfirmNo",ConfirmNo);
        map.put("ResStates",ResStates);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/SSMForQueryReservation";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        System.out.println("data:"+data);
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            JSONArray items=jsonObj.getJSONArray("data");
            List<Reservation> list= JSON.parseObject(items.toJSONString(), new TypeReference<List<Reservation>>() {});
            return list;
        }else{
            return null;
        }
    }
    //获取ticketId
    public String getTicketIdString() {
        log.info("getTicketId()方法");
        Map<String,String> map=new HashMap<>();
        map.put("appKey",appKey);
        map.put("appSecret",appSecret);
        String param= HttpUtil.getMapToString(map);
        System.out.println(param);
        String url=fuyiaddress+"/api/Ticket/GetTicketID";
        String returnResult= HttpUtil.sendGet(url,param);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        JSONObject data=jsonObj.getJSONObject("data");
        String ticketId=data.getString("TicketID");
        return ticketId;
    }
    //每个请求中都包含该 map集合中的参数
    public Map<String,String> getMap(String nowDate) throws Exception {
        Map<String,String> map=new HashMap<>();
        String ticketId=getTicketIdString();
        String sign= SignUtils.CreateSign(ticketId,nowDate);
        String ip= InetAddress.getLocalHost().getHostAddress();
        map.put("TicketID",ticketId);
        map.put("Sign",sign);
        map.put("InvokeIp",ip);
        return map;
    }
}
