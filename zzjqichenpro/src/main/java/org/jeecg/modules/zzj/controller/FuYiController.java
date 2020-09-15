package org.jeecg.modules.zzj.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.modules.zzj.entity.OperationRecord;
import org.jeecg.modules.zzj.entity.Reservation;
import org.jeecg.modules.zzj.service.OperationRecordService;
import org.jeecg.modules.zzj.service.ReservationService;
import org.jeecg.modules.zzj.util.Card.SetResultUtil;
import org.jeecg.modules.zzj.util.Http.HttpUtil;
import org.jeecg.modules.zzj.util.SignUtils;
import org.jeecg.modules.zzj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Api(tags = "富驿接口")
@RestController
@RequestMapping("/FuYi")
public class FuYiController {
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
    private OperationRecordService operationRecordService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    @Autowired
    private ReservationService reservationService;


    /**
     * 订单留存测试
     */
    @ApiOperation(value = "订单留存测试",httpMethod = "POST")
    @RequestMapping(value = "/testSave", method = RequestMethod.POST)
    public Result<Object> testSave(){
        Result<Object> result = new Result<Object>();
        Reservation reservation=reservationService.getOne(new QueryWrapper<Reservation>().eq("Confirm_no","B2286912"));
        String sendPost=JSONObject.toJSONString(reservation);
        String url="http://192.168.11.1:8099/jeecg-boot/Reservation/fuyiResSaveOrUpdate";
        String returnResult= HttpUtil.sendPosts(url,sendPost);
        System.out.println("returnResult:"+returnResult);
        return SetResultUtil.setSuccessResult(result,"成功");
    }

    /**
     * 订单留存测试
     */
    @ApiOperation(value = "操作记录留存测试",httpMethod = "POST")
    @RequestMapping(value = "/testOperationRecord", method = RequestMethod.POST)
    public Result<Object> testOperationRecord(){
        Result<Object> result = new Result<Object>();
        OperationRecord operationRecord=operationRecordService.getById("1156e7e36898e6a173feddfdb2bbfeb1");
        String sendPost=JSONObject.toJSONString(operationRecord);
        String url="http://192.168.11.1:8099/jeecg-boot/Reservation/fuyiOperationSaveOrUpdate";
        String returnResult= HttpUtil.sendPosts(url,sendPost);
        System.out.println("returnResult:"+returnResult);
        return SetResultUtil.setSuccessResult(result,"成功");
    }



    /**
     * 获取ticketId
     */
    @RequestMapping(value = "/getTicketId", method = RequestMethod.GET)
    @ApiOperation(value = "获取ticketId", httpMethod = "GET")
    public Result<JSONObject> getTicketId() {
        log.info("getTicketId()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        Map<String,String> map=new HashMap<>();
        map.put("appKey",appKey);
        map.put("appSecret",appSecret);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Ticket/GetTicketID";
        String returnResult= HttpUtil.sendGet(url,param);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    @RequestMapping(value = "/testSaveOper", method = RequestMethod.GET)
    @ApiOperation(value = "testSaveOper", httpMethod = "GET")
    public Result testSaveOper() {
        String ConfirmNo="123";
        OperationRecord operationRecord=new OperationRecord();
        operationRecord.setName("123");
        operationRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        operationRecord.setOperation("CREATE");
        operationRecord.setOperationDes("订单确认号为:"+ConfirmNo+"姓名为:"+operationRecord.getName()+"的客人，在"+operationRecord.getCreateTime()+"在自助机生成了一个订单。");
        operationRecord.setResno(ConfirmNo);
        operationRecordService.save(operationRecord);

        return Result.ok("123");
    }



    /**
     * 会员登录，获取会员信息
     * 该接口暂时手机号 密码 写死在配置文件里了
     * @param mobileNo
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/Login", method = RequestMethod.GET)
    @ApiOperation(value = "Login", httpMethod = "GET")
    public Result<JSONObject> Login(String mobileNo,String password) throws Exception {
        log.info("Login()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("MobileNo",mobileNo);
        map.put("Password",password);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Membership/MemberLogin";
        String returnResult= HttpUtil.sendPost(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     * 获取房价代码销售条件
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/GetRateCodeRestriction", method = RequestMethod.GET)
    @ApiOperation(value = "获取房价代码销售条件", httpMethod = "GET")
    public Result<JSONObject> LoGetRateCodeRestrictiongin() throws Exception {
        log.info("LoGetRateCodeRestrictiongin()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Restriction/GetRateCodeRestriction";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     *
     * @param ArrDate
     * @param DepDate
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/GetHotelRestriction", method = RequestMethod.GET)
    @ApiOperation(value = "获取酒店销售条件", httpMethod = "GET")
    public Result<JSONObject> LoGetRateCodeRestrictiongin(String ArrDate,String DepDate) throws Exception {
        log.info("LoGetRateCodeRestrictiongin()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ArrDate",ArrDate);
        map.put("DepDate",DepDate);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Restriction/GetHotelRestriction";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     *
     * @param CheckInDate 入住日期
     * @param CheckInTime 入住时间（分钟小时）
     * @param CheckOutDate 离店日期
     * @param RateCode 房价代码
     * @param RoomRate 房价
     * @param RoomTypeNo 房型code
     * @param RoomCount 房间数
     * @param TotalPrice 总房价
     * @param BookerName 订房人姓名
     * @param BookerTel 订房人电话
     * @param BookerEmail 订房人邮箱
     * @param BookerMebNo 订房人会员号
     * @param BookerCardNo 订房人卡号
     * @param Gender 性别
     * @param GuestEmail 客人邮箱
     * @param GuestMebCardNo 客人会员卡号
     * @param GuestName 客人姓名
     * @param GuestTel 客人电话
     * @param GuestAddress 客人地址
     * @param DocumentNo
     * @param DocumentType
     * @param AdultsCount 成人数
     * @param ChildrenCount 儿童数
     * @param IsConfirmBySms
     * @param Remarks 备注
     * @param VoucherIDList 凭证编号清单
     * @param PromotionCode 促销号
     * @param Point
     * @param SalePoint
     * @param OuterConfirmNo
     * @param OTANo
     * @param PaymentType 支付方式
     * @param ResType
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/AddReservation", method = RequestMethod.GET)
    @ApiOperation(value = "生成订单", httpMethod = "GET")
    public Result<JSONObject> AddReservation(String CheckInDate,String CheckInTime,String CheckOutDate,String RateCode,String RoomRate,
                                             String RoomTypeNo,String RoomCount,String TotalPrice,String BookerName,String BookerTel,
                                             String BookerEmail,String BookerMebNo,String BookerCardNo,String Gender,String GuestEmail,
                                             String GuestMebCardNo,String GuestName,String GuestTel,String GuestAddress,String DocumentNo,
                                             String DocumentType,String AdultsCount,String ChildrenCount,String IsConfirmBySms,String Remarks,
                                             String VoucherIDList,String PromotionCode,String Point,String SalePoint,String OuterConfirmNo,
                                             String OTANo,String PaymentType,String ResType
                                             ) throws Exception {
        log.info("AddReservation()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("CheckInDate",CheckInDate);
        map.put("CheckInTime",CheckInTime);
        map.put("CheckOutDate",CheckOutDate);
        map.put("RateCode",RateCode);
        map.put("RoomRate",RoomRate);
        map.put("RoomTypeNo",RoomTypeNo);
        map.put("RoomCount",RoomCount);
        map.put("TotalPrice",TotalPrice);
        map.put("BookerName",BookerName);
        map.put("BookerTel",BookerTel);
        map.put("BookerEmail",BookerEmail);
        map.put("BookerMebNo",BookerMebNo);
        map.put("BookerCardNo",BookerCardNo);
        map.put("Gender",Gender);
        map.put("GuestEmail",GuestEmail);
        map.put("GuestMebCardNo",GuestMebCardNo);
        map.put("GuestName",GuestName);
        map.put("GuestTel",GuestTel);
        map.put("GuestAddress",GuestAddress);
        map.put("DocumentNo",DocumentNo);
        map.put("DocumentType",DocumentType);
        map.put("AdultsCount",AdultsCount);
        map.put("ChildrenCount",ChildrenCount);
        map.put("IsConfirmBySms",IsConfirmBySms);
        map.put("Remarks",Remarks);
        map.put("VoucherIDList",VoucherIDList);
        map.put("PromotionCode",PromotionCode);
        map.put("Point",Point);
        map.put("SalePoint",SalePoint);
        map.put("OuterConfirmNo",OuterConfirmNo);
        map.put("OTANo",OTANo);
        map.put("PaymentType",PaymentType);
        map.put("ResType",ResType);
        for (Map.Entry<String,String> entry : map.entrySet()){
            System.out.println("key:"+entry.getKey()+",value:"+entry.getValue());
        }
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/AddReservation";
        String returnResult= HttpUtil.sendPost(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        System.out.println("data:"+data);
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){

            String ConfirmNo=jsonObj.getJSONObject("data").getString("ConfirmNo");
            OperationRecord operationRecord=new OperationRecord();
            operationRecord.setName(GuestName);
            operationRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            operationRecord.setOperation("CREATE");
            operationRecord.setOperationDes("订单确认号为:"+ConfirmNo+"，姓名为:"+GuestName+"的客人，在"+operationRecord.getCreateTime()+",在自助机生成订单。");
            operationRecord.setResno(ConfirmNo);
            operationRecordService.save(operationRecord);

            return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
        }else{
            return SetResultUtil.setErrorMsgResult(result,"失败",jsonObj);
        }
    }


    /**
     *
     * @param CheckInDate
     * @param CheckOutDate
     * @param MemberType
     * @param RoomTypeCode
     * @param RateTypeCode
     * @param PromotionCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/GetRoomStatusByHotelNoOnSaleRestriction", method = RequestMethod.GET)
    @ApiOperation(value = "查询房价", httpMethod = "GET")
    public Result<Object> GetRoomStatusByHotelNoOnSaleRestriction(String CheckInDate,String CheckOutDate,String MemberType,
                                                                      String RoomTypeCode,String RateTypeCode,String PromotionCode) throws Exception {
        log.info("GetRoomStatusByHotelNoOnSaleRestriction()方法");
        Result<Object> result = new Result<Object>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("CheckInDate",CheckInDate);
        map.put("CheckOutDate",CheckOutDate);
        map.put("MemberType",MemberType);
        map.put("RoomTypeCode",RoomTypeCode);
        map.put("RateTypeCode",RateTypeCode);
        map.put("PromotionCode",PromotionCode);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/GetRoomStatusByHotelNoOnSaleRestriction";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        System.out.println("data:"+data);
        JSONArray newJsonArray=new JSONArray();
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            JSONObject jsonData=jsonObj.getJSONObject("data");
            JSONArray array=jsonData.getJSONArray("RoomListsale");
            System.out.println(array.size());
            for (int i=0;i<array.size();i++){
                JSONObject jo=array.getJSONObject(i);
                if("CLK".equals(jo.getString("RoomTypeNo")) || "CLT".equals(jo.getString("RoomTypeNo"))){
                    newJsonArray.add(jo);
                }
            }
            array.removeAll(newJsonArray);
            newJsonArray.addAll(array);
            jsonData.remove("RoomListsale");
            jsonData.put("RoomListsale",newJsonArray);
            jsonObj.remove("data");
            jsonObj.put("data",jsonData);
            return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
        }else{
            return SetResultUtil.setErrorMsgResult(result,"失败，无符合要求的房型");
        }
    }

    /**
     *
     * @param SellChannel
     * @param SpecialCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/GetRateCodeForHotelByHotelNo", method = RequestMethod.GET)
    @ApiOperation(value = "获取酒店房价代码信息", httpMethod = "GET")
    public Result<JSONObject> GetRateCodeForHotelByHotelNo(String SellChannel,String SpecialCode) throws Exception {
        log.info("GetRateCodeForHotelByHotelNo()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("SellChannel",SellChannel);
        map.put("SpecialCode",SpecialCode);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Restriction/GetRateCodeForHotelByHotelNo";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     *
     * @param SpecialCode
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/GetRoomTypesByHotelNo", method = RequestMethod.GET)
    @ApiOperation(value = "获取房型信息请求参数", httpMethod = "GET")
    public Result<JSONObject> GetRoomTypesByHotelNo(String SpecialCode) throws Exception {
        log.info("GetRoomTypesByHotelNo()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("SpecialCode",SpecialCode);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Restriction/GetRoomTypesByHotelNo";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    @RequestMapping(value = "/AddTrans", method = RequestMethod.GET)
    @ApiOperation(value = "入账", httpMethod = "GET")
    public Result<JSONObject> AddTrans(String TransCodeID, String ResID, String Payment, String RefNo, String Remark) throws Exception {
        log.info("AddTrans()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("TransCodeID",TransCodeID);
        map.put("ResID",ResID);
        map.put("Payment",Payment);
        map.put("RefNo",RefNo);
        map.put("Remark",Remark);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/AddTrans";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }


    /**
     *
     * @param ConfirmNo PMS系统确认号
     * @param ResStates 订单状态
                         * R:预定
                         * I：在住
                         * X：取消
                         * N：noshow
                         * O：离店
                         * (不填则默认R)
     * @param CurrencyNo 通用号码 (证件号码/预定人手机号/入住人手机号/会员卡号都可以)
     * @param GuestsName 客人姓名
     * @param RoomNo 房号
     * @param BeginArrivalBizDate 开始抵店营业日期（与离店日期区间二选一）
     * @param EndArrivalBizDate  结束抵店营业日期（与离店日期区间二选一）
     * @param BeginDepartBizDate  开始抵店营业日期 （与离店日期区间二选一）
     * @param EndDepartBizDate 结束抵店营业日期 （与离店日期区间二选一）
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/SSMForQueryReservation", method = RequestMethod.GET)
    @ApiOperation(value = "查询订单", httpMethod = "GET")
    public Result<JSONObject> SSMForQueryReservation(String ConfirmNo, String ResStates, String CurrencyNo, String GuestsName,
                                                     String RoomNo, String BeginArrivalBizDate, String EndArrivalBizDate,
                                                     String BeginDepartBizDate, String EndDepartBizDate,String GroupID) throws Exception {
        log.info("SSMForQueryReservation()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ConfirmNo",ConfirmNo);
        map.put("ResStates",ResStates);
        map.put("CurrencyNo",CurrencyNo);
        map.put("GuestsName",GuestsName);
        map.put("RoomNo",RoomNo);
        map.put("BeginArrivalBizDate",BeginArrivalBizDate);
        map.put("EndArrivalBizDate",EndArrivalBizDate);
        map.put("BeginDepartBizDate",BeginDepartBizDate);
        map.put("EndDepartBizDate",EndDepartBizDate);
        map.put("GroupID",GroupID);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/SSMForQueryReservation";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        JSONObject old = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            if(!StringUtils.isNullOrEmpty(ResStates)&&"I".equals(ResStates)){
                JSONArray datas=jsonObj.getJSONArray("data");
                JSONObject mid=datas.getJSONObject(0);
                JSONArray ResTransaction=mid.getJSONArray("ResTransaction");
                JSONArray newArray=new JSONArray();
                for(int i=0;i<ResTransaction.size();i++){
                    boolean flag=true;
                    for (int k=0;k<newArray.size();k++){
                        if (i==0){
                            break;
                        }
                        if (ResTransaction.getJSONObject(i).getString("TransType").equals(newArray.getJSONObject(k).getString("TransType"))) {
                            flag=false;
                            if (!StringUtils.isNullOrEmpty(ResTransaction.getJSONObject(i).getString("Charge"))
                                    && !StringUtils.isNullOrEmpty(newArray.getJSONObject(k).getString("Charge"))
                            ){
                                BigDecimal allCharge=new BigDecimal(newArray.getJSONObject(k).getString("Charge"));
                                BigDecimal oldCharge=new BigDecimal(ResTransaction.getJSONObject(i).getString("Charge"));
                                BigDecimal oldTaxOne=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxOne"));
                                BigDecimal oldTaxTwo=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxTwo"));
                                BigDecimal oldTaxThree=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxThree"));
                                allCharge=allCharge.add(oldCharge);
                                allCharge=allCharge.add(oldTaxOne);
                                allCharge=allCharge.add(oldTaxTwo);
                                allCharge=allCharge.add(oldTaxThree);
                                newArray.getJSONObject(k).put("Charge",allCharge.setScale(2, RoundingMode.HALF_UP));
                            }
                            if (!StringUtils.isNullOrEmpty(ResTransaction.getJSONObject(i).getString("Payment"))
                                    && !StringUtils.isNullOrEmpty(newArray.getJSONObject(k).getString("Payment"))
                            ){
                                BigDecimal allCharge=new BigDecimal(newArray.getJSONObject(k).getString("Payment"));
                                BigDecimal oldCharge=new BigDecimal(ResTransaction.getJSONObject(i).getString("Payment"));
                                allCharge=allCharge.add(oldCharge);
                                newArray.getJSONObject(k).put("Payment",allCharge.setScale(2, RoundingMode.HALF_UP));
                            }
                        }
                    }
                    if (flag){
                        newArray.add(ResTransaction.get(i));
                        JSONObject j=newArray.getJSONObject(newArray.size()-1);
                        if (!StringUtils.isNullOrEmpty(j.getString("Charge"))){
                            BigDecimal allCharge=new BigDecimal(j.getString("Charge"));
                            BigDecimal oldTaxOne=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxOne"));
                            BigDecimal oldTaxTwo=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxTwo"));
                            BigDecimal oldTaxThree=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxThree"));
                            allCharge=allCharge.add(oldTaxOne);
                            allCharge=allCharge.add(oldTaxTwo);
                            allCharge=allCharge.add(oldTaxThree);
                            j.put("Charge",allCharge.setScale(2, RoundingMode.HALF_UP));
                        }
                    }
                }
                JSONArray loddatas=old.getJSONArray("data");
                JSONObject oldmid=loddatas.getJSONObject(0);
                oldmid.put("ResTransactions",newArray);
                loddatas.set(0,oldmid);
                old.put("data",loddatas);
            }
            return SetResultUtil.setSuccessResult(result,"成功",old);
        }else{
            return SetResultUtil.setErrorMsgResult(result,"查无此订单");
        }

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


    @RequestMapping(value = "/GetQueryAvailableRoom", method = RequestMethod.GET)
    @ApiOperation(value = "可用房号查询", httpMethod = "GET")
    public Result<JSONObject> GetQueryAvailableRoom(String RoomTypeCode, String ArrivalBizDate, String DepartBizDate) throws Exception {
        log.info("GetQueryAvailableRoom()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("RoomTypeCode",RoomTypeCode);
        map.put("ArrivalBizDate",ArrivalBizDate);
        map.put("DepartBizDate",DepartBizDate);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/RoomRate/GetQueryAvailableRoom";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    @RequestMapping(value = "/GetQueryAvailableRoomType", method = RequestMethod.GET)
    @ApiOperation(value = "可用房型查询", httpMethod = "GET")
    public Result<JSONObject> GetQueryAvailableRoomType(String ArrivalBizDate, String DepartBizDate) throws Exception {
        log.info("GetQueryAvailableRoomType()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ArrivalBizDate",ArrivalBizDate);
        map.put("DepartBizDate",DepartBizDate);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/RoomRate/GetQueryAvailableRoomType";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    @RequestMapping(value = "/GetTransList", method = RequestMethod.GET)
    @ApiOperation(value = "交易项目查询", httpMethod = "GET")
    public Result<JSONObject> GetTransList() throws Exception {
        log.info("GetTransList()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/GetTransList";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     *
     * @param RoomTypeNo 房型编号
     * @param RoomNo 房间号
     * @param ResID 订单ID
     * @param Gender 性别
     * @param GuestEmail 邮箱
     * @param GuestMebCardNo 会员卡
     * @param GuestName 客人姓名
     * @param GuestAddress 地址
     * @param DocumentNo 证件号码
     * @param DocumentType 证件类型
     * @param AdultsCount 成人数量
     * @param ChildrenCount 儿童数量
     * @param ConfirmNo 外部确认号，用于外部PMS对接
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/EditReservation", method = RequestMethod.GET)
    @ApiOperation(value = "修改订单预定", httpMethod = "GET")
    public Result<JSONObject> EditReservation(String RoomTypeNo,String RoomNo,String ResID,String Gender,String GuestEmail,String GuestMebCardNo,
                                           String GuestName,String GuestAddress,String DocumentNo,String DocumentType,String AdultsCount,
                                           String ChildrenCount,String ConfirmNo,String BookerTel,String GuestTel,String BillingInstruction
    ,String Remarks) throws Exception {
        log.info("EditReservation()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("RoomTypeNo",RoomTypeNo);
        map.put("RoomNo",RoomNo);
        map.put("ResID",ResID);
        map.put("Gender",Gender);
        map.put("GuestEmail",GuestEmail);
        map.put("GuestMebCardNo",GuestMebCardNo);
        map.put("GuestName",GuestName);
        map.put("GuestAddress",GuestAddress);
        map.put("DocumentNo",DocumentNo);
        map.put("DocumentType",DocumentType);
        map.put("AdultsCount",AdultsCount);
        map.put("ChildrenCount",ChildrenCount);
        map.put("ConfirmNo",ConfirmNo);
        map.put("BookerTel",BookerTel);
        map.put("GuestTel",GuestTel);
        map.put("BillingInstruction",BillingInstruction);
        map.put("Remakes",Remarks);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/EditReservation";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     * 修改抵店/离店日期
     * @param ResID
     * @param overridRatePlan
     * @param CheckInDate
     * @param CheckOutDate
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/EditCheckInOrCheckOutDate", method = RequestMethod.GET)
    @ApiOperation(value = "修改抵店/离店日期", httpMethod = "GET")
    public Result<JSONObject> EditCheckInOrCheckOutDate(String ResID,String overridRatePlan,String CheckInDate,String CheckOutDate) throws Exception {
        log.info("EditReservation()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ResID",ResID);
        map.put("overridRatePlan",overridRatePlan);
        map.put("CheckInDate",CheckInDate);
        map.put("CheckOutDate",CheckOutDate);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/EditCheckInOrCheckOutDate";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }


    /**
     * 入住
     * @param confirmNo
     * @param roomNo
     * @param documentType
     * @param documentNo
     * @param sourceArea
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/CheckIn", method = RequestMethod.GET)
    @ApiOperation(value = "入住", httpMethod = "GET")
    public Result<JSONObject> CheckIn(String confirmNo,String roomNo,
                                      String documentType,String documentNo,
                                      String sourceArea,String Gender,
                                      String Birthday,String Address,String Nation,
                                      String Nationality,String EnFamilyName,String EnFirstName,String GuestName) throws Exception {
        log.info("CheckIn()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("confirmNo",confirmNo);
        map.put("roomNo",roomNo);
        map.put("documentType",documentType);
        map.put("documentNo",documentNo);
        map.put("sourceArea",sourceArea);

        map.put("Gender",Gender);
        map.put("Birthday",Birthday);
        map.put("Address",Address);
        map.put("Nation",Nation);
        map.put("Nationality",Nationality);
        map.put("EnFamilyName",EnFamilyName);
        map.put("EnFirstName",EnFirstName);
        map.put("GuestName",GuestName);

        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/CheckIn";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        System.out.println("data:"+data);
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            List<Reservation> list=getOneReservation(confirmNo,"I");// 获取订单 入住 状态
            for (Reservation reservation : list){
                reservation.setArrivalBizDate(reservation.getArrivalBizDate().replace("T"," "));
                reservation.setDepartBizDate(reservation.getDepartBizDate().replace("T"," "));
                reservation.setArrivalTime(reservation.getArrivalTime().replace("T"," "));
                reservation.setCreateTime(reservation.getCreateTime().replace("T"," "));
                reservation.setDepartTime(reservation.getDepartTime().replace("T"," "));
                reservation.setIsActionTime(reservation.getIsActionTime().replace("T"," "));
            }
            reservationService.saveBatch(list);

            OperationRecord operationRecord=new OperationRecord();
            operationRecord.setName(EnFamilyName+EnFirstName);
            operationRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            operationRecord.setOperation("CHECKIN");
            operationRecord.setOperationDes("订单确认号为:"+confirmNo+"，姓名为:"+operationRecord.getName()+"的客人，在"+operationRecord.getCreateTime()+"在自助机办理入住。");
            operationRecord.setResno(confirmNo);
            operationRecordService.save(operationRecord);

            return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
        }else{
            return SetResultUtil.setErrorMsgResult(result,"失败",jsonObj);
        }
    }


    @RequestMapping(value = "/AddResGuest", method = RequestMethod.GET)
    @ApiOperation(value = "添加同住", httpMethod = "GET")
    public Result<JSONObject> AddResGuest(String ResID,String ConfirmNo,String ResGuestID,String GuestName,String EnFamilyName,String EnFirstName,
                                          String Gender,String DocumentType,String DocumentNo,String Birthday,String MebCardNo,String Adrress,
                                          String Flag,String MobilePhone,String CustomerID,String EMail,String Nationality) throws Exception {
        log.info("AddResGuest()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ResID",ResID);
        map.put("ConfirmNo",ConfirmNo);
        map.put("ResGuestID",ResGuestID);
        map.put("GuestName",GuestName);
        map.put("EnFamilyName",EnFamilyName);
        map.put("EnFirstName",EnFirstName);
        map.put("Gender",Gender);
        map.put("DocumentType",DocumentType);
        map.put("DocumentNo",DocumentNo);
        map.put("Birthday",Birthday);
        map.put("MebCardNo",MebCardNo);
        map.put("Adrress",Adrress);
        map.put("Flag",Flag);// 1 启动 0  禁用
        map.put("MobilePhone",MobilePhone);
        map.put("CustomerID",CustomerID);
        map.put("EMail",EMail);
        map.put("Nationality",Nationality);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/AddResGuest";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    @RequestMapping(value = "/UpdateResGuest", method = RequestMethod.GET)
    @ApiOperation(value = "修改同住", httpMethod = "GET")
    public Result<JSONObject> UpdateResGuest(String ResID,String ConfirmNo,String ResGuestID,String GuestName,String EnFamilyName,String EnFirstName,
                                          String Gender,String DocumentType,String DocumentNo,String Birthday,String MebCardNo,String Adrress,
                                          String Flag,String MobilePhone,String CustomerID,String EMail,String Nationality) throws Exception {
        log.info("UpdateResGuest()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ResID",ResID);
        map.put("ConfirmNo",ConfirmNo);
        map.put("ResGuestID",ResGuestID);
        map.put("GuestName",GuestName);
        map.put("EnFamilyName",EnFamilyName);
        map.put("EnFirstName",EnFirstName);
        map.put("Gender",Gender);
        map.put("DocumentType",DocumentType);
        map.put("DocumentNo",DocumentNo);
        map.put("Birthday",Birthday);
        map.put("MebCardNo",MebCardNo);
        map.put("Adrress",Adrress);
        map.put("Flag",Flag);
        map.put("MobilePhone",MobilePhone);
        map.put("CustomerID",CustomerID);
        map.put("EMail",EMail);
        map.put("Nationality",Nationality);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/UpdateResGuest";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     *
     * @param RoomNo 房号
     * @param ConfirmNo 确认号
     * @param RoomTypeCode 房型编号
     * @param CheckOutDate 新的离店日期
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/ChangeRoomsToStay", method = RequestMethod.GET)
    @ApiOperation(value = "续住", httpMethod = "GET")
    public Result<JSONObject> ChangeRoomsToStay(String RoomNo,String ConfirmNo,String RoomTypeCode,String CheckOutDate) throws Exception {
        log.info("ChangeRoomsToStay()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("RoomNo",RoomNo);
        map.put("ConfirmNo",ConfirmNo);
        map.put("RoomTypeCode",RoomTypeCode);
        map.put("CheckOutDate",CheckOutDate);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/ChangeRoomsToStay";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        System.out.println("data:"+data);
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            Reservation reservations=reservationService.getOne(new QueryWrapper<Reservation>().eq("Confirm_no",ConfirmNo));
            List<Reservation> list=getOneReservation(ConfirmNo,"I");
            for (Reservation reservation : list){
                reservation.setArrivalBizDate(reservation.getArrivalBizDate().replace("T"," "));
                reservation.setDepartBizDate(reservation.getDepartBizDate().replace("T"," "));
                reservation.setArrivalTime(reservation.getArrivalTime().replace("T"," "));
                reservation.setCreateTime(reservation.getCreateTime().replace("T"," "));
                reservation.setDepartTime(reservation.getDepartTime().replace("T"," "));
                reservation.setIsActionTime(reservation.getIsActionTime().replace("T"," "));
            }
            if (reservations==null){
                reservationService.saveBatch(list);
            }else{
                list.get(0).setId(reservations.getId());
                reservationService.updateById(list.get(0));
            }

            OperationRecord operationRecord=new OperationRecord();
            operationRecord.setName(list.get(0).getGuestName());
            operationRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            operationRecord.setOperation("UPDATE");
            operationRecord.setOperationDes("订单确认号为:"+ConfirmNo+"，姓名为:"+operationRecord.getName()+"的客人，在"+operationRecord.getCreateTime()+",在自助机办理续住。");
            operationRecord.setResno(ConfirmNo);
            operationRecordService.save(operationRecord);

        }else{
            return SetResultUtil.setErrorMsgResult(result,"失败",jsonObj);
        }
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }


    /**
     *
     * @param ResId 订单号
     * @param ConfirmNo 确认号
     * @param state 0代表入住 1代表续住  2代表退房
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/CalculateTheAmountToBePaid", method = RequestMethod.GET)
    @ApiOperation(value = "计算需支付金额", httpMethod = "GET")
    public Result<JSONObject> CalculateTheAmountToBePaid(String ResId,String ConfirmNo,String state,String CheckOutDate) throws Exception {
        log.info("CalculateTheAmountToBePaid()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ResId",ResId);
        map.put("ConfirmNo",ConfirmNo);
        map.put("state",state);
        map.put("CheckOutDate",CheckOutDate);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/CalculateTheAmountToBePaid";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    @RequestMapping(value = "/CheckOut", method = RequestMethod.GET)
    @ApiOperation(value = "退房不结账", httpMethod = "GET")
    public Result<JSONObject> CheckOut(String ResId,String ConfirmNo) throws Exception {
        log.info("CheckOut()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("ResId",ResId);
        map.put("ConfirmNo",ConfirmNo);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/CheckOut";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println("CheckOut："+jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        System.out.println("CheckOutdata:"+data);
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            Reservation reservations=reservationService.getOne(new QueryWrapper<Reservation>().eq("Confirm_no",ConfirmNo));
            List<Reservation> list=getOneReservation(ConfirmNo,"O");
            for (Reservation reservation : list){
                reservation.setArrivalBizDate(reservation.getArrivalBizDate().replace("T"," "));
                reservation.setDepartBizDate(reservation.getDepartBizDate().replace("T"," "));
                reservation.setArrivalTime(reservation.getArrivalTime().replace("T"," "));
                reservation.setCreateTime(reservation.getCreateTime().replace("T"," "));
                reservation.setDepartTime(reservation.getDepartTime().replace("T"," "));
                reservation.setIsActionTime(reservation.getIsActionTime().replace("T"," "));
            }
            if (reservations==null){
                reservationService.saveBatch(list);
            }else{
                list.get(0).setId(reservations.getId());
                reservationService.updateById(list.get(0));
            }
            OperationRecord operationRecord=new OperationRecord();
            operationRecord.setName(list.get(0).getGuestName());
            operationRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            operationRecord.setOperation("CHECKOUT");
            operationRecord.setOperationDes("订单确认号为:"+ConfirmNo+"，姓名为:"+operationRecord.getName()+"的客人，在"+operationRecord.getCreateTime()+",在自助机办理退房。");
            operationRecord.setResno(ConfirmNo);
            operationRecordService.save(operationRecord);
        }else{
            return SetResultUtil.setErrorMsgResult(result,"失败",jsonObj);
        }
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     *
     * @param RoomNo 房间号
     * @param ConfirmNo 确定号
     * @param LockState L 锁房 N 表示解锁
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/LockRoom", method = RequestMethod.GET)
    @ApiOperation(value = "LockRoom", httpMethod = "GET")
    public Result<JSONObject> LockRoom(String RoomNo,String ConfirmNo,String LockState) throws Exception {
        log.info("LockRoom()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("RoomNo",RoomNo);
        map.put("ConfirmNo",ConfirmNo);
        map.put("LockState",LockState);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Reservation/LockRoom";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
    }

    /**
     *  http://47.75.119.166:8066/webapinew/api/RoomRate/AddDoorsLog/
     * @param RoomNo
     * @return
     * @throws Exception
     */
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


    /**
     *  http://47.75.119.166:8066/webapinew/api/Membership/GetMemberInfoByMobileNoAndRegister/
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/GetMemberInfoByMobileNoAndRegister", method = RequestMethod.GET)
    @ApiOperation(value = "GetMemberInfoByMobileNoAndRegister", httpMethod = "GET")
    public Result<JSONObject> GetMemberInfoByMobileNoAndRegister(String MobileNo) throws Exception {
        log.info("GetMemberInfoByMobileNoAndRegister()方法");
        Result<JSONObject> result = new Result<JSONObject>();
        String nowDate=new Date().getTime()+"";
        Map<String,String> map=getMap(nowDate);
        map.put("HotelNo",HotelNo);
        map.put("MobileNo",MobileNo);
        String param= HttpUtil.getMapToString(map);
        System.out.println("param:"+param);
        String url=fuyiaddress+"/api/Membership/GetMemberInfoByMobileNoAndRegister";
        String returnResult= HttpUtil.sendGet(url,param,map,nowDate);
        JSONObject jsonObj = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        System.out.println("data:"+data);
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            return SetResultUtil.setSuccessResult(result,"成功",jsonObj);
        }else{
            return SetResultUtil.setErrorMsgResult(result,"查询会员等级失败",jsonObj);
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
