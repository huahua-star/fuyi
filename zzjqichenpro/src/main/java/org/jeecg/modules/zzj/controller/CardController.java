package org.jeecg.modules.zzj.controller;

import TTCEPackage.K7X0Util;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.zzj.entity.DoorCard;
import org.jeecg.modules.zzj.entity.Record;
import org.jeecg.modules.zzj.entity.TblTxnp;
import org.jeecg.modules.zzj.service.IRecordService;
import org.jeecg.modules.zzj.service.ITblTxnpService;
import org.jeecg.modules.zzj.util.Card.PrintUtil;
import org.jeecg.modules.zzj.util.Card.SetResultUtil;
import org.jeecg.modules.zzj.util.Http.HttpUtil;
import org.jeecg.modules.zzj.util.SignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.plaf.TableHeaderUI;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static TTCEPackage.K7X0Util.check;

/**
 * 卡Controller
 */
@Slf4j
@Api(tags = "制读卡")
@RestController
@RequestMapping("/zzj/card")
public class CardController {

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

    @Value("${sdk.ComHandle}")
    private Integer comHandle;


    @Value("${print.cityaddress}")
    private String cityaddress;
    @Value("${print.areaaddress}")
    private String areaaddress;
    @Value("${print.roadaddress}")
    private String roadaddress;
    @Value("${print.communityaddress}")
    private String communityaddress;
    @Value("${print.numberaddress}")
    private String numberaddress;
    @Value("${print.phone}")
    private String phone;
    @Value("${print.wifiname}")
    private String wifiname;
    @Value("${print.wifipass}")
    private String wifipass;
    @Value("${print.hotelname}")
    private String hotelname;


    private static String cardUrl="http://localhost:10086/api/readcard/";
    private static String CardNo="1";
    private static String MakeWay="1";


    @Autowired
    private ITblTxnpService tblTxnpService;

    @Autowired
    private IRecordService iRecordService;//发卡记录


    @ApiOperation(value = "制卡", notes = "制卡")
    @GetMapping(value = "/GuestCard")
    public static Result<Object> GuestCard(String CheckInDate,String CheckOutDate,String RoomNo){
        Map<String,String> map=new HashMap<>();
        map.put("CheckInDate",CheckInDate);
        map.put("CheckOutDate",CheckOutDate);
        map.put("RoomNo",RoomNo);
        map.put("CardNo",CardNo);
        map.put("MakeWay",MakeWay);
        String param= HttpUtil.getMapToString(map);
        String returnResult= HttpUtil.sendPost(cardUrl+"makecard",param);
        if (returnResult!=null){
            JSONObject jsonObj = JSONObject.parseObject(returnResult);
            System.out.println(jsonObj);
            String IsSuccess=jsonObj.getString("IsSuccess");
            if ("true".equals(IsSuccess)){
                return  Result.ok();
            }else {
                return Result.error("写卡失败");
            }
        }else{
            return Result.error("写卡失败");
        }
    }

    @ApiOperation(value = "checks", notes = "checks")
    @GetMapping(value = "/checks")
    public Result<Object> checks() {
        Result<Object> result = new Result<>();
        log.info("check()方法");
        try {
            // 打开发卡机
            log.info("检测发卡机是否有卡");
            K7X0Util.open(comHandle);
            //复位
            K7X0Util.reset();
            Thread.sleep(2000);
            // 检测发卡机是否预空
            boolean isEmpty = check(2, 0x31);
            if (isEmpty) {
                log.info("发卡机卡箱预空,即将无卡");
            }
            Thread.sleep(2000);
            // 检测发卡机是否有卡
            isEmpty = check(3, 0x38);
            if (isEmpty) {
                log.info("sendCard()方法结束return:{卡箱已空}");
                return SetResultUtil.setErrorMsgResult(result, "发卡机卡箱已空");
            }
            log.info("check()方法结束");
            return SetResultUtil.setSuccessResult(result, "有卡");
        } catch (Exception e) {
            log.error("sendCard()出现异常error:{}", e.getMessage());
            return SetResultUtil.setExceptionResult(result, "sendCard");
        }
    }


    @ApiOperation(value = "读卡", notes = "读卡")
    @GetMapping(value = "/ReadCard")
    public static Result<Object> ReadCard(){
        String returnResult= HttpUtil.sendPost(cardUrl+"readcardinfo",null);
        if (returnResult!=null){
            JSONObject jsonObj = JSONObject.parseObject(returnResult);
            System.out.println(jsonObj);
            String IsSuccess=jsonObj.getString("IsSuccess");
            if ("true".equals(IsSuccess)){
                String roomno=jsonObj.getString("RoomNo").substring(2,6);
                String endTime=jsonObj.getString("ETime");
                endTime=new SimpleDateFormat("yy").format(new Date())+endTime;
                if ("0".equals(roomno.substring(0,1))){
                    roomno=roomno.substring(1);
                }
                DoorCard doorCard=new DoorCard();
                doorCard.setRoomNo(roomno);
                doorCard.setEndTime(endTime);
                return  Result.ok(doorCard);
            }else {
                return Result.error("读卡失败");
            }
        }else{
            return Result.error("读卡失败");
        }
    }



    /**
     * 发卡
     */
    @ApiOperation(value = "发卡", notes = "发卡-sendCard")
    @GetMapping(value = "/sendCard")
    public Result<Object> sendCard(String CheckInTime, String CheckOutTime,
                                   String orderId, String id, String roomno,
                                   int num, boolean flag) {
        System.out.println("CheckInTime:"+CheckInTime);
        System.out.println("CheckOutTime:"+CheckOutTime);
        System.out.println("orderId:"+orderId);
        System.out.println("id:"+id);
        System.out.println("num:"+num);
        if (roomno.length()== 3) {
            roomno = "0" + roomno;
        }
        Result<Object> result = new Result<>();
        log.info("进入sendCard()方法");
        try {
            // 打开发卡机
            log.info("检测发卡机是否有卡");
            K7X0Util.open(comHandle);
            // 检测发卡机是否预空
            boolean isEmpty = check(2, 0x31);
            if (isEmpty) {
                String str = "发卡机卡箱预空";
                log.info("发卡机卡箱预空");
            }
            // 检测发卡机是否有卡
            isEmpty = check(3, 0x38);
            if (isEmpty) {
                // 发卡失败
                log.info("发卡失败失败数据加入数据库中");
                log.info("sendCard()方法结束return:{卡箱已空}");
                return SetResultUtil.setErrorMsgResult(result, "发卡机卡箱已空");
            }
            while (check(3, 0x31)) {
                System.out.println("##########有卡未取出");
                return SetResultUtil.setExceptionResult(result, "读卡位置有卡未取出");
            }
            for (int i = 0; i < num; i++) {
                System.out.println("发送到读卡位置");
                // 将卡片发送到读卡位置
                int key=K7X0Util.sendToReadToReturn(comHandle);
                //读卡
                if (0 != key){
                    return Result.error("失败");
                }
                /**
                 * 写卡
                 */
                Result<Object> writeResult=GuestCard(CheckInTime,CheckOutTime,roomno);
                if (writeResult.getCode()==200) {
                    System.out.println("开始发卡");
                    K7X0Util.sendCardToTake(comHandle);
                    Thread.sleep(2000);
                } else {
                    //记录发卡
                    Record record=new Record();
                    record.setOrderId(orderId);
                    record.setSendTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    record.setSendNum(num+"");
                    record.setRoomNum(roomno);
                    record.setState("0");
                    iRecordService.save(record);
                    K7X0Util.regain();
                    return SetResultUtil.setErrorMsgResult(result, "写卡失败");
                }
            }
            log.info("打印小票需要的数据");
            PrintUtil pu = new PrintUtil();
            //查询数据库中存储的流水记录
            String money = "";
            String reservationType = "";
            if (null != id && !"".equals(id)) {
                TblTxnp tblTxnp = tblTxnpService.getOne(new QueryWrapper<TblTxnp>().eq("id", id));
                money = tblTxnp.getPreamount().toString();//预授权金额
                reservationType = tblTxnp.getPaymethod();
                if (reservationType == "0" || reservationType.equals("0")) {
                    reservationType = "支付宝";
                } else if (reservationType == "1" || reservationType.equals("1")) {
                    reservationType = "微信";
                } else {
                    reservationType = "银联";
                }
            }
            //打印小票无 早餐数据
            pu.print(roomno, phone, wifiname, wifipass, hotelname, cityaddress, areaaddress, roadaddress, communityaddress,
                    numberaddress, money, reservationType,CheckInTime,
                    CheckOutTime, orderId);

            //记录发卡
            Record record=new Record();
            record.setOrderId(orderId);
            record.setSendTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            record.setSendNum(num+"");
            record.setRoomNum(roomno);
            record.setState("1");
            iRecordService.save(record);

            log.info("sendCard()方法结束");
            AddDoorsLog(orderId.substring(1),CheckInTime,CheckOutTime,roomno,"11");
            return SetResultUtil.setSuccessResult(result, "发卡成功");
        } catch (Exception e) {
            log.error("sendCard()出现异常error:{}", e.getMessage());
            return SetResultUtil.setExceptionResult(result, "sendCard");
        }
    }

    /**
     * 不打印发卡
     */
    @ApiOperation(value = "发卡", notes = "发卡-sendCardNoPaper")
    @GetMapping(value = "/sendCardNoPaper")
    public Result<Object> sendCardNoPaper(String CheckInTime, String CheckOutTime, String roomno,
                                          int num, boolean flag
    ) {
        if (roomno.length()==3){
            roomno="0"+roomno;
        }

        Result<Object> result = new Result<>();
        log.info("进入sendCard()方法");
        try {
            // 打开发卡机
            log.info("检测发卡机是否有卡");
            K7X0Util.open(comHandle);
            // 检测发卡机是否预空
            boolean isEmpty = K7X0Util.check(2, 0x31);
            if (isEmpty) {
                String str = "发卡机卡箱预空";
                log.info("发卡机卡箱预空");
            }
            // 检测发卡机是否有卡
            isEmpty = K7X0Util.check(3, 0x38);
            if (isEmpty) {
                // 发卡失败
                log.info("发卡失败失败数据加入数据库中");
                log.info("sendCard()方法结束return:{卡箱已空}");
                return SetResultUtil.setErrorMsgResult(result, "发卡机卡箱已空");
            }
            while (K7X0Util.check(3, 0x31)) {
                System.out.println("##########有卡未取出");
                return SetResultUtil.setExceptionResult(result, "读卡位置有卡未取出");
            }
            for (int i = 0; i < num; i++) {
                System.out.println("发送到读卡位置");
                int key=K7X0Util.sendToReadToReturn(comHandle);
                //读卡
                if (0 != key){
                    return Result.error("失败");
                }
                /**
                 * 写卡
                 */
                Result<Object> writeResult=GuestCard(CheckInTime,CheckOutTime,roomno);
                if (writeResult.getCode()==200) {
                    System.out.println("开始发卡");
                    K7X0Util.sendCardToTake(comHandle);
                    Thread.currentThread().sleep(1000);
                }else{
                    K7X0Util.regain();
                    return SetResultUtil.setErrorMsgResult(result, "写卡失败");
                }
            }
            log.info("sendCard()方法结束");
            
            return SetResultUtil.setSuccessResult(result, "发卡成功");
        } catch (Exception e) {
            log.error("sendCard()出现异常error:{}", e.getMessage());
            return SetResultUtil.setExceptionResult(result, "sendCard");
        }
    }


    /**
     *testTakeToRead
     */
    @ApiOperation(value = "testTakeToRead", notes = "testTakeToRead")
    @GetMapping(value = "/testTakeToRead")
    public Result<Object> testTakeToRead() throws InterruptedException {
        Result<Object> result = new Result<>();
        log.info("testTakeToRead()方法");
        // 回收到发卡箱
        Boolean flag = K7X0Util.regainCard(comHandle);
        if (!flag) {
            return SetResultUtil.setErrorMsgResult(result, "退卡失败");
        }
        // 将卡片发送到读卡位置
        int key=K7X0Util.sendToReadToReturn(comHandle);
        //读卡
        if (0 == key){
            return  Result.ok("成功");
        }else{
            return Result.error("失败");
        }
    }

    /**
     *checkCardPosition
     */
    @ApiOperation(value = "checkCardPosition", notes = "checkCardPosition")
    @GetMapping(value = "/checkCardPosition")
    public Result<Object> checkCardPosition() throws InterruptedException {
        if(!K7X0Util.open(comHandle)){
            System.out.println("发卡器串口打开失败+=========>com:"+comHandle);
            return Result.error("打开串口失败");
        }
        Result<Object> result = new Result<>();
        log.info("checkCardPosition()方法");
        boolean flag = check(3,0x33);
        //读卡
        return Result.ok(flag);
    }

    /**
     * reagin
     */
    @ApiOperation(value = "reagin", notes = "regain")
    @GetMapping(value = "/regain")
    public Result<Object> regain() throws InterruptedException {
        if(!K7X0Util.open(comHandle)){
            System.out.println("发卡器串口打开失败+=========>com:"+comHandle);
            return Result.error("打开串口失败");
        }
        K7X0Util.regain();
        return Result.ok("成功");
    }

    /**
     * send
     */
    @ApiOperation(value = "send", notes = "send")
    @GetMapping(value = "/send")
    public Result<Object> send() throws InterruptedException {
        K7X0Util.send();
        return Result.ok("成功");
    }


    /**
     * send
     */
    @ApiOperation(value = "testOpenAndClose", notes = "testOpenAndClose")
    @GetMapping(value = "/testOpenAndClose")
    public Result<Object> testOpenAndClose() {
        System.out.println("打开发卡机串口");
        if(!K7X0Util.open(comHandle)){
            System.out.println("发卡器串口打开失败+=========>com:"+comHandle);
            return Result.error("发卡机串口打开失败");
        }
        System.out.println("发卡机打开串口成功");
        System.out.println("关闭发卡机串口");
        K7X0Util.colse(K7X0Util.ComHandle);
        return Result.ok("成功");
    }






    /**
     * 退卡
     */
    @ApiOperation(value = "退卡", notes = "退卡-Recoverycard")
    @GetMapping(value = "/Recoverycard")
    public Result<Object> Recoverycard() throws InterruptedException {
        Result<Object> result = new Result<>();
        log.info("Recoverycard()方法");
        // 将卡片发送到读卡位置
        int key=K7X0Util.sendTakeToRead(comHandle);
        //读卡
        if (0 == key){
            result=ReadCard();
            K7X0Util.regain();
            return  result;
        }else{
            return Result.error("失败");
        }
    }

    /**
     * 检测发卡位置是否有卡
     */
    @ApiOperation(value = "检测发卡是否有卡", httpMethod = "GET")
    @RequestMapping(value = "/checkTureCard", method = RequestMethod.GET)
    public Result<Object> checkTureCard() {
        Result<Object> result = new Result<>();
        //打开发卡器
        K7X0Util.open(comHandle);
        boolean isEmpty = K7X0Util.check(3, 0x35);//0x35
        System.out.println("isEmpty:" + isEmpty);
        SetResultUtil.setSuccessResult(result, "检测是否有卡", isEmpty);
        return result;
    }



    @RequestMapping(value = "/AddDoorsLog", method = RequestMethod.GET)
    @ApiOperation(value = "AddDoorsLog", httpMethod = "GET")
    public Result<JSONObject> AddDoorsLog(String ResID,String BeginTime,
                                          String EndTime,String RoomNo,String Opareator) throws Exception {
        log.info("AddDoorsLog()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("HotelCode",HotelNo);
        map.put("RoomNo",RoomNo);
        map.put("ResID",ResID);
        map.put("BeginTime",BeginTime);
        map.put("EndTime",EndTime);
        map.put("Opareator",Opareator);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/RoomRate/AddDoorsLog";
        String returnResult= HttpUtil.sendGet(url, param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
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