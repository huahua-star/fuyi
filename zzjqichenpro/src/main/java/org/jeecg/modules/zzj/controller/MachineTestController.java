package org.jeecg.modules.zzj.controller;

import TTCEPackage.K7X0Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.zzj.common.umsips;
import org.jeecg.modules.zzj.util.Card.PrintUtil;
import org.jeecg.modules.zzj.util.Card.SetResultUtil;
import org.jeecg.modules.zzj.util.FaceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import static TTCEPackage.K7X0Util.check;

@Slf4j
@Api(tags = "检测接口")
@RestController
@RequestMapping("/MachineTest")
public class MachineTestController {

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

    static int iRet;

    static char[] strMemo = new char[1024];

    //检测发卡
    @ApiOperation(value = "检测发卡（不包含写卡）", notes = "检测发卡（不包含写卡）")
    @GetMapping(value = "/SendCardTest")
    public Result<Object> SendCardTest() throws Exception {
        log.info("SendCardTest()方法");
        if(!K7X0Util.open(comHandle)){
            return  Result.error("检测发卡失败，打开串口失败");
        }
        Thread.sleep(2000);//线程休眠2s
        int returnKey=K7X0Util.sendToReadToReturn(comHandle);
        switch (returnKey){
            case 0:
                log.info("检测发送到读卡位置成功");
                break;
            case 1:
                log.info("检测打开串口失败");
                return Result.error("检测发卡失败，打开串口失败");
            case 2:
                log.info("检测发送到读卡位置失败");
                return Result.error("检测失败，发送到读卡位置失败");
        }
        Thread.sleep(2000);//线程休眠2s
        K7X0Util.sendCardToTake(comHandle);
        Thread.sleep(2000);//线程休眠2s
        if (!check(3, 0x31)) {
            return Result.error("检测失败，发送到取卡位置成功");
        }
        return Result.ok("检测发卡成功");
    }
    //检测收卡
    @ApiOperation(value = "检测收卡", notes = "检测收卡")
    @GetMapping(value = "/RecoverycardTest")
    public Result<Object> RecoverycardTest() {
        log.info("RecoverycardTest()方法");
        // 回收到发卡箱
        Boolean flag = K7X0Util.regainCard(comHandle);
        if (!flag) {
            return Result.error("检测收卡失败，回收至发卡箱失败");
        }
        return  Result.ok("检测收卡成功");
    }
    //检测读卡
    @ApiOperation(value = "检测读卡", notes = "检测读卡")
    @GetMapping(value = "/ReadCardTest")
    public Result<Object> ReadCardTest() throws InterruptedException {
        Result<Object> result = new Result<>();
        log.info("ReadCardTest()方法");
        // 将卡片发送到读卡位置
        int key=K7X0Util.sendToReadToReturn(comHandle);
        //读卡
        if (0 == key){
            result=CardController.ReadCard();
            Thread.sleep(1000);
            K7X0Util.regain();
            return  result;
        }else{
            return Result.error("失败");
        }
    }
    //检测写卡
    @ApiOperation(value = "检测写卡", notes = "检测写卡")
    @GetMapping(value = "/WriteCardTest")
    public Result<Object> WriteCardTest(String CheckInDate,String CheckOutDate,String RoomNo) throws InterruptedException {
        Result<Object> result = new Result<>();
        log.info("WriteCardTest()方法");
        if (RoomNo.length()== 3) {
            RoomNo = "0" + RoomNo;
        }
        // 将卡片发送到读卡位置
        int key=K7X0Util.sendToReadToReturn(comHandle);
        //写卡
        if (0 == key){
            result=CardController.GuestCard(CheckInDate,CheckOutDate,RoomNo);
            Thread.sleep(1000);
            K7X0Util.regain();
            return  result;
        }else{
            return Result.error("失败");
        }
    }
    //检测发卡机是否 有卡及预空
    @ApiOperation(value = "检测发卡机是否有卡及预空", notes = "检测发卡机是否有卡及预空")
    @GetMapping(value = "/testMachineCardNum")
    public Result<Object> testMachineCardNum() throws InterruptedException {
        Result<Object> result = new Result<>();
        log.info("testMachineCardNum()方法");
        boolean flag=false;
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
            flag=true;
        }
        Thread.sleep(2000);
        // 检测发卡机是否有卡
        isEmpty = check(3, 0x38);
        if (isEmpty) {
            log.info("sendCard()方法结束return:{卡箱已空}");
            return SetResultUtil.setErrorMsgResult(result, "发卡机卡箱已空");
        }
        log.info("testMachineCardNum()方法结束");
        String msg="检测成功，发卡机有卡";
        if (flag){
            msg+=",发卡机预空。";
        }
        return Result.ok(msg);
    }
    //检测银联及读卡模块打开
    @ApiOperation(value = "检测银联及读卡模块打开", notes = "检测银联及读卡模块打开")
    @GetMapping(value = "/testYinLianMachine")
    public Result<Object> testYinLianMachine() throws InterruptedException {
        Result<Object> result = new Result<>();
        log.info("testYinLianMachine()方法");
        log.info("银联初始化,init");
        iRet = UMS_Init();
        log.info("进入UMS_Init()方法");
        switch (iRet){
            case 0:
                break;
            case -103:
                return Result.error("银联密码键盘初始化失败，原因：初始化密码键盘失败");
            case -104:
                return Result.error("银联密码键盘初始化失败，原因：银联卡模块初始化失败");
            default:
                return Result.error("银联密码键盘初始化失败。");
        }
        log.info("UMS_Init()结束");
        log.info("进入UMS_EnterCard()方法");
        iRet = umsips.instanceDll.UMS_EnterCard();
        log.info("UMS_EnterCard:iRet:"+iRet);
        if(iRet>2){
            log.error("UMS_EnterCard()结束进卡操作失败iRet:{}",iRet);
            return Result.error("UMS_EnterCard()结束进卡操作失败");
        }
        log.error("UMS_EnterCard()结束");
        return Result.ok("检测银联模块成功");
    }
    //初始化
    public int UMS_Init() {
        iRet = umsips.instanceDll.UMS_Init(1);
        return iRet;
    }
    //检测打印机
    @RequestMapping(value = "/testPirnt", method = RequestMethod.GET)
    @ApiOperation(value = "检测打印小票", httpMethod = "GET")
    public Result testPirnt() throws InterruptedException {
        log.info("打印小票需要的数据");
        PrintUtil pu = new PrintUtil();
        pu.print("1024", phone, wifiname, wifipass, hotelname, cityaddress, areaaddress, roadaddress, communityaddress,
                numberaddress, "0.01","支付宝",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "B7502543");
        Thread.sleep(2000);
        return  Result.ok("打印成功");
    }
    //检测摄像头
    @RequestMapping(value = "/testCamera", method = RequestMethod.GET)
    @ApiOperation(value = "检测摄像头", httpMethod = "GET")
    public Result testCamera(String dllPath,String imgFileName,int nVISCameraID,int nNIRCameraID){
        int init=FaceUtil.CLibrary.INSTANCE.Init(dllPath);
        if (1==init){
            int faceCompre=FaceUtil.CLibrary.INSTANCE.FaceCompare(imgFileName,nVISCameraID,nNIRCameraID);
            if (1==faceCompre){
                int unInit=FaceUtil.CLibrary.INSTANCE.UnInit();
                if (1==unInit){
                    return Result.ok("检测摄像头成功");
                }else{
                    return Result.error("检测摄像头失败，unit失败");
                }
            }else{
                return Result.error("检测摄像头失败，对比失败");
            }
        }else{
            return  Result.error("检测摄像头失败,初始化失败");
        }
    }
}
