package org.jeecg.modules.zzj.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.zzj.common.Invoiqrutil.Invoiqr;
import org.jeecg.modules.zzj.entity.HotelSetTable;
import org.jeecg.modules.zzj.entity.Invoice;
import org.jeecg.modules.zzj.entity.invoice.ResponseData;
import org.jeecg.modules.zzj.service.HotelSetTableService;
import org.jeecg.modules.zzj.service.InvoiceService;
import org.jeecg.modules.zzj.util.Card.*;
import org.jeecg.modules.zzj.util.Http.HttpUtil;
import org.jeecg.modules.zzj.util.Returned3.R;
import org.jeecg.modules.zzj.util.SignUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Api(tags="打印相关功能")
@RestController
@RequestMapping("/print")
public class PrintController {

    @Value("${qrDir}")
    private String qrDir;
    @Value("${Invoiqr.appCode}")
    private String appCode;
    @Value("${Invoiqr.taxpayerCode}")
    private String taxpayerCode;
    @Value("${Invoiqr.keyStorePath}")
    private String keyStorePath;
    @Value("${Invoiqr.keyStoreAbner}")
    private String keyStoreAbner;
    @Value("${Invoiqr.keyStorePassWord}")
    private String keyStorePassWord;
    @Value("${Invoiqr.facadeUrl}")
    private String facadeUrl;
    @Value("${cardUrl}")
    private String cardUrl;


    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private HotelSetTableService hotelSetTableService;

    @PostConstruct
    public void init() {
        Invoice invoiqr=invoiceService.getById("fuyi");
        System.out.println("invoiqr:"+invoiqr);
    }

    /**
     * 离店打印小票
     *
     * @param reservationNumber 订单号
     * @param stime             预计入住时间
     * @param ztime             预计离店时间
     * @param roomNum           房间号
     * @return
     */
    @RequestMapping(value = "/updatechckInPerson", method = RequestMethod.GET)
    @ApiOperation(value = "离店打印小票", httpMethod = "GET")
    public R updatechckInPerson(@RequestParam(name = "reservationNumber", required = true)
                                @ApiParam(name = "reservationNumber", value = "订单号")
                                        String reservationNumber, String stime,
                                String ztime, String roomNum,String money) {
        log.info("进入updatechckInPerson()方法reservationNumber:{}", reservationNumber);
        if (StringUtils.isEmpty(reservationNumber) || StringUtils.isEmpty(stime)
                || StringUtils.isEmpty(ztime) || StringUtils.isEmpty(roomNum)
                ||StringUtils.isEmpty(money)
        ) {
            return R.error("请输入必须值");
        }
        try {
            leavePrintUtil pu = new leavePrintUtil();
            leavewuPrintUtil wupu = new leavewuPrintUtil();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String orderNo = null;
            Date kstime = null;
            Date jstime = null;
            //打印小票
            kstime = sdf.parse(stime);
            jstime = sdf.parse(ztime);
            log.info("打印小票无发票");
            wupu.print(roomNum, null, kstime, jstime, new Date(),money);
            return R.ok();
        } catch (Exception e) {
            log.error("updatechckInPerson()方法出现异常error:{}", e.getMessage());
            return R.error("打印退房小票失败!");
        }
    }


    @ApiOperation(value = "检查打印机是否缺纸")
    @RequestMapping(value = "/printers", method = RequestMethod.POST)
    public R SeePrinterstate() {
        log.info("检查打印机是否缺纸方法开始");
        try {
            CLibraryUtil.INSTANCE.SetUsbportauto();
            CLibraryUtil.INSTANCE.SetInit();
            log.info("SeePrinterstate()结束");
            log.info(CLibraryUtil.INSTANCE.GetStatus() + "");
            if (CLibraryUtil.INSTANCE.GetStatus() == 7) {
                return R.error(7, "缺纸");
            } else {
                if (CLibraryUtil.INSTANCE.GetStatus() == 8) {
                    String printStr = "设备即将缺纸请检测";
                    return R.error(8, "缺纸");
                }
            }
            if (CLibraryUtil.INSTANCE.GetStatus() == 0) {
                log.info("打印机状态良好,暂时不缺纸");
                return R.ok();
            } else {
                return R.error(CLibraryUtil.INSTANCE.GetStatus(), "打印机故障或纸头没有放对位置");
            }

        } catch (UnsatisfiedLinkError e) {
            log.error("SeePrinterstate()出现异常error:{}", e.getMessage());
            return R.error("无法检测打印机状况!");
        }
    }


    /**
     * 离店打印普票
     *
     * @param reservationNumber 订单号
     * @param stime             预计入住时间
     * @param ztime             预计离店时间
     * @param amount            开普票金额
     * @param roomNum           房间号
     * @return
     */
    @ApiOperation(value = "离店打印发票", httpMethod = "GET")
    @RequestMapping(value = "/invoice", method = RequestMethod.GET)
    public R invoice(
            @RequestParam(name = "reservationNumber", required = true) @ApiParam(name = "reservationNumber", value = "订单号") String reservationNumber,
            String stime, String ztime, String amount, String roomNum) throws Exception {
        if (StringUtils.isEmpty(reservationNumber) || StringUtils.isEmpty(stime) || StringUtils.isEmpty(ztime) ||
                StringUtils.isEmpty(amount) || StringUtils.isEmpty(roomNum)) {
            return R.error("请输入开票必须值!");
        }
        if (amount.equals("0")) {
            return R.error("金额不能为零");
        }
        //获取可打印发票数量
        HotelSetTable hotelSetTable=hotelSetTableService.getById("fuyi");
        String invoice=hotelSetTable.getInvoiceNums();
        int invoiceNums=Integer.parseInt(invoice);//判断可打印数量
        if (invoiceNums<=0){
            return R.error("打印发票失败,可打印发票数量为："+invoiceNums);
        }
        leavePrintUtil pu = new leavePrintUtil();
        leavewuPrintUtil wupu = new leavewuPrintUtil();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        log.info("根据amount是否打印发票");
        String orderNo = null;
        Date kstime = null;
        Date jstime = null;
        int stamptype = 0;
        Invoiqr i = new Invoiqr();
        //打印电子发票开票
        Map s = i.getCheckInPerson(amount, qrDir, appCode, taxpayerCode, keyStorePath,
                keyStoreAbner, keyStorePassWord, facadeUrl, reservationNumber);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
        String dateString = formatter.format(new Date());
        String imgurl;
        orderNo = (String) s.get("orderNo");
        imgurl = (String) s.get("filePath");
        kstime = sdf.parse(stime);
        jstime = sdf.parse(ztime);
        stamptype = 1;
        pu.print(roomNum, imgurl, kstime, jstime, new Date(), orderNo);

        /**
         * 发票数量减1
         */
        invoiceNums--;
        hotelSetTable.setInvoiceNums(invoiceNums+"");
        hotelSetTableService.updateById(hotelSetTable);

        return R.ok();
    }

    /**
     * 查询电子发票订单状态
     *
     * @param orderNo 电子发票订单号
     * @return 状态码 以及说明
     * @throws Exception
     */
    @ApiOperation(value = "查询电子发票订单状态")
    @RequestMapping(value = "/invoiceQuery", method = RequestMethod.GET)
    @ResponseBody
    public R invoiceQuery(String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            return R.error("请输入发票订单号");
        }
        Invoiqr i = new Invoiqr();
        try {
            ResponseData responseData = i.quiry_order(orderNo);
            return R.ok("Response", responseData);
        } catch (Exception e) {
            log.error("invoiceQuery()方法异常:查询失败");
        }
        return R.error();
    }


    /**
     * 账单打印
     */
    @ApiOperation(value = "账单打印", httpMethod = "GET")
    @RequestMapping(value = "/printBill", method = RequestMethod.GET)
    public R printBill(String ConfirmNo,String ResStates) throws Exception {
        JSONObject jsonObject=getOneReservation(ConfirmNo,ResStates);
        PrintBillUtil pu = new PrintBillUtil();
        pu.print(jsonObject);
        return R.ok();
    }


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

    public JSONObject getOneReservation(String ConfirmNo,String ResStates) throws Exception {

        log.info("SSMForQueryReservation()方法");
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
        JSONObject old = JSONObject.parseObject(returnResult);
        System.out.println(jsonObj);
        String fuyiResult=jsonObj.getString("result");
        String data=jsonObj.getString("data");
        if ("true".equals(fuyiResult) && null!=data && data.length()>2){
            if(!org.jeecg.modules.zzj.util.StringUtils.isNullOrEmpty(ResStates)){
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
                            if (!org.jeecg.modules.zzj.util.StringUtils.isNullOrEmpty(ResTransaction.getJSONObject(i).getString("Charge"))
                                    && !org.jeecg.modules.zzj.util.StringUtils.isNullOrEmpty(newArray.getJSONObject(k).getString("Charge"))
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
                            if (!org.jeecg.modules.zzj.util.StringUtils.isNullOrEmpty(ResTransaction.getJSONObject(i).getString("Payment"))
                                    && !org.jeecg.modules.zzj.util.StringUtils.isNullOrEmpty(newArray.getJSONObject(k).getString("Payment"))
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
                        if (!org.jeecg.modules.zzj.util.StringUtils.isNullOrEmpty(j.getString("Charge"))){
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
                System.out.println("newArray:"+newArray);
                loddatas.set(0,oldmid);
                old.put("data",loddatas);
            }
            return old;
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
