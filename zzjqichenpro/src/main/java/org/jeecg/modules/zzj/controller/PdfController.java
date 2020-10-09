package org.jeecg.modules.zzj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.events.PdfPageEventForwarder;
import com.lowagie.text.HeaderFooter;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.zzj.entity.OperationRecord;
import org.jeecg.modules.zzj.entity.Reservation;
import org.jeecg.modules.zzj.service.OperationRecordService;
import org.jeecg.modules.zzj.service.ReservationService;
import org.jeecg.modules.zzj.util.EmailUtil;
import org.jeecg.modules.zzj.util.PageFooter;
import org.jeecg.modules.zzj.util.pdfReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

@Slf4j
@Api(tags = "pdf")
@RestController
@RequestMapping("/pdf")
public class PdfController {


    @Value("${pdfUrl}")
    private String pdfUrl;
    @Value("${reservationMonthUrl}")
    private String reservationMonthUrl;

    @Value("${operRecordMonthUrl}")
    private String operRecordMonthUrl;

    @Value("${logoimgUrl}")
    private String logoimgUrl;

    @Value("${Email.HOST}")
    private String HOST;

    @Value("${Email.FROM}")
    private String FROM;

    @Value("${Email.AFFIXNAME}")
    private String AFFIXNAME;

    @Value("${Email.USER}")
    private String USER;

    @Value("${Email.PWD}")
    private String PWD;

    @Value("${Email.SUBJECT}")
    private String SUBJECT;

    @Value("${Email.reservationEmail}")
    private String reservationEmail;



    @Autowired
    private ReservationService reservationService;

    @Autowired
    private OperationRecordService operationRecordService;


    /*
     * 生成自助机每月订单PDF
     */
    @ApiOperation(value = "生成自助机每月订单PDF")
    @RequestMapping(value = "/createReservationMonthPdf", method = RequestMethod.GET)
    public Result<?> createReservationMonthPdf() {
        //获取上月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-1);
        date=calendar.getTime();
        String month=format.format(date);
        System.out.println("month:"+month);
        try {
            String filePath=reservationMonthUrl+month+".pdf";
            System.out.println("filePath:"+filePath);
            // 1.新建document对象
            Document document = new Document(PageSize.A4);// 建立一个Document对象
            // 2.建立一个书写器(Writer)与document对象关联
            File file = new File(filePath);
            file.createNewFile();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            PageFooter pageFooter=new PageFooter();
            writer.setPageEvent(pageFooter);
            // 3.打开文档
            document.open();
            month=new SimpleDateFormat("yyyy-MM").format(date);
            String beginTime=month+"-01 00:00:00.000";
            String endTime=month+"-31 23:59:59.999";
            List<Reservation> list=reservationService.list(new QueryWrapper<Reservation>().between("Depart_time", beginTime,endTime));
            for (Reservation reservation : list){
                reservation.setResState(getStatus(reservation.getResState()));
            }
            ArrayList<Reservation> reservationList=(ArrayList<Reservation>) list;
            // 4.向文档中添加内容
            new pdfReport().generateReservationPDF(document,reservationList,month,list.size()+"",logoimgUrl);
            // 5.关闭文档
            document.close();
            return Result.ok(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("失败");
        }
    }

    /*
     * 生成自助机每月生成操作记录pdf
     */
    @ApiOperation(value = "生成自助机每月生成操作记录pdf")
    @RequestMapping(value = "/createOperRecordMonthPdf", method = RequestMethod.GET)
    public Result<?> createOperRecordMonthPdf() {
        //获取上月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-1);
        date=calendar.getTime();
        String month=format.format(date);
        System.out.println("month:"+month);
        try {
            String filePath=operRecordMonthUrl+month+".pdf";
            System.out.println("filePath:"+filePath);
            // 1.新建document对象
            Document document = new Document(PageSize.A4);// 建立一个Document对象
            // 2.建立一个书写器(Writer)与document对象关联
            File file = new File(filePath);
            file.createNewFile();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            PageFooter pageFooter=new PageFooter();
            writer.setPageEvent(pageFooter);
            // 3.打开文档
            document.open();
            month=new SimpleDateFormat("yyyy-MM").format(date);
            String beginTime=month+"-01 00:00:00";
            String endTime=month+"-31 23:59:59";
            List<OperationRecord> list=operationRecordService.list(new QueryWrapper<OperationRecord>().between("create_time", beginTime,endTime));
            ArrayList<OperationRecord> operationRecords=(ArrayList<OperationRecord>) list;
            // 4.向文档中添加内容
            new pdfReport().generateOperationRecordPDF(document,operationRecords,month,list.size()+"",logoimgUrl);
            // 5.关闭文档
            document.close();
            return Result.ok(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("失败");
        }
    }


    //每月1号月初1点执行一次
    @Scheduled(cron = "0 0 1 1 * ?")
    public void sendEmail() throws InterruptedException {
        log.info("生成订单");
        createReservationMonthPdf();
        log.info("发送邮箱");
        String[] TOS=new String[]{reservationEmail,"149331731@qq.com"};
        //获取上月
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH)-1);
        date=calendar.getTime();
        String fileName=format.format(date)+".pdf";
        String filePath=reservationMonthUrl+fileName;
        EmailUtil.send(format.format(date)+"月自助机订单统计",HOST,FROM,filePath,fileName,USER,PWD,format.format(date)+"月自助机订单统计",TOS);
        Thread.sleep(5000);
        createOperRecordMonthPdf();
        filePath=operRecordMonthUrl+fileName;
        EmailUtil.send(format.format(date)+"月自助机订单统计",HOST,FROM,filePath,fileName,USER,PWD,format.format(date)+"月自助机操作记录统计",TOS);
    }
    /*
     * 生成自助机每月生成操作记录pdf
     */
    @ApiOperation(value = "test")
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public Result<?> test() {
        //String TO="longxikefang2020@163.com";
        String TO="570148135@qq.com";
        String[] TOS=TO.split(",");
        EmailUtil.send("账号为"+"123"+"，房间号为123的客人已在自助机上办理退房。",
                HOST,FROM,"",AFFIXNAME,USER,PWD,"客人退房提醒。",TOS);
        return Result.ok("成功");
    }


    public static String getStatus(String status){
        Map<String,String> map=new HashMap<>();
        map.put("I","在住");
        map.put("R","预定");
        map.put("O","离店");
        map.put("X","取消");
        map.put("N","预定未到/NOSHOW");
        map.put(null,"无订单状态");
        map.put("","无订单状态");
        return map.get(status);
    }

}
