package org.jeecg.modules.zzj.util.Card;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.math.BigDecimal;
import java.math.RoundingMode;
@Slf4j
/**
 * java定位打印，把打印内容打到指定的地方。
 *
 * @author lyb
 */

public class PrintBillUtil implements Printable {
    private int PAGES = 0;
    private JSONObject json;

    public void print(JSONObject jsonObject) {
        json=jsonObject;
        if (json != null) { // 当打印内容不为空时
            PAGES = 1; // 获取打印总页数
            // 指定打印输出格式
            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
            // 定位默认的打印服务
            PrintService printService = PrintServiceLookup
                    .lookupDefaultPrintService();
            // 创建打印作业
            DocPrintJob job = printService.createPrintJob();
            // 设置打印属性
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            // 设置纸张大小,也可以新建MediaSize类来自定义大小
            MediaSize ms = new MediaSize(80, 100, Size2DSyntax.MM, MediaSizeName.ISO_A7);
            pras.add(ms.getMediaSizeName().ISO_A7);
            DocAttributeSet das = new HashDocAttributeSet();
            // 指定打印内容
            Doc doc = new SimpleDoc(this, flavor, das);
            // 不显示打印对话框，直接进行打印工作
            try {
                job.print(doc, pras); // 进行每一页的具体打印操作
            } catch (PrintException pe) {
                pe.printStackTrace();
            }
        } else {
            // 如果打印内容为空时，提示用户打印将取消
            JOptionPane.showConfirmDialog(null,
                    "Sorry, Printer Job is Empty, Print Cancelled!",
                    "Empty", JOptionPane.DEFAULT_OPTION,
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public int print(Graphics gp, PageFormat pf, int page) {
        try{
            Graphics2D g2 = (Graphics2D) gp;
            g2.setPaint(Color.black); // 设置打印颜色为黑色
            if (page >= PAGES) // 当打印页号大于需要打印的总页数时，打印工作结束
                return Printable.NO_SUCH_PAGE;
            g2.translate(pf.getImageableX(), pf.getImageableY());// 转换坐标，确定打印边界
            Font font = new Font("宋体", Font.PLAIN, 12);// 创建字体
            Paper p = new Paper();
            g2.setFont(font);
            JSONArray datas=json.getJSONArray("data");
            JSONObject mid=datas.getJSONObject(0);
            JSONArray ResTransaction=mid.getJSONArray("ResTransaction");
            log.info("ResTransaction:"+ResTransaction.toJSONString());
            JSONArray ResTransactions=mid.getJSONArray("ResTransactions");
            log.info("ResTransactions:"+ResTransactions.toJSONString());
            int sizey=ResTransaction.size()*15;
            p.setSize(235, 235+sizey);
            p.setImageableArea(10, 10, 100, 141);
            pf.setPaper(p);
            // 打印当前页文本
            g2.drawString("感谢您入住", 53, 5);
            g2.drawString("富驿时尚酒店(北京中关村店)", 8, 25);
            g2.drawString("------------------------------------------", 10, 40);
            g2.drawString("------------------------------------------", 10, 42);
            Font roomfont= new Font("宋体", Font.PLAIN, 12);//房间字体
            g2.setFont(roomfont);
            g2.drawString("" + mid.getString("RoomNo") + "号房间", 35, 55);
            font = new Font("宋体", Font.PLAIN, 12);// 创建字体
            g2.setFont(font);
            g2.drawString("明细账单 Guest Folio",35, 75);
            g2.drawString("宾客姓名:" + mid.getString("GuestName") + "",8, 90);
            g2.drawString("抵店时间:" + mid.getString("ArrivalTime").substring(0,19).replace("T"," ") + "",8,105);
            g2.drawString("离店时间:" + mid.getString("DepartTime").substring(0,19).replace("T"," ") + "", 8, 120);
            g2.drawString("房    号:"+mid.getString("RoomNo"), 8, 135);
            g2.drawString("账户编号:" + mid.getString("ConfirmNo") + "", 8, 150);
            font = new Font("宋体", Font.PLAIN, 7);// 创建字体
            g2.setFont(font);
            g2.drawString("时   间      项目      消费金额   结算金额 ",8, 165);
            for (int i=0;i<ResTransaction.size();i++){
                String write=ResTransaction.getJSONObject(i).getString("TransTime").substring(5,16).replace("T"," ")
                        +"  "+ResTransaction.getJSONObject(i).getString("TransCodeName")+"  ";
                String Charge=ResTransaction.getJSONObject(i).getString("Charge");
                if (null==Charge || Charge.equals("null")){
                    write+="   ";
                }else{
                    BigDecimal endCharge=new BigDecimal(ResTransaction.getJSONObject(i).getString("Charge"));
                    BigDecimal oldTaxOne=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxOne"));
                    endCharge=endCharge.add(oldTaxOne);
                    BigDecimal oldTaxTwo=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxTwo"));
                    endCharge=endCharge.add(oldTaxTwo);
                    BigDecimal oldTaxThree=new BigDecimal(ResTransaction.getJSONObject(i).getString("TaxThree"));
                    endCharge=endCharge.add(oldTaxThree);
                    write+=endCharge.setScale(2,RoundingMode.HALF_UP);
                }
                String Payment=ResTransaction.getJSONObject(i).getString("Payment");
                if (null ==Payment || Payment.equals("null")){
                    write+="   ";
                }else{
                    write+=ResTransaction.getJSONObject(i).getString("Payment");
                }
                g2.drawString(write,8, 165+(i+1)*15);
            }
            g2.drawString("------------------------------------------", 10, 170+sizey);
            font = new Font("宋体", Font.PLAIN, 14);// 创建字体
            g2.setFont(font);
            JSONObject charge=null;
            JSONObject payment=null;
            for (int i=0;i<ResTransactions.size();i++){
                String TransType=ResTransactions.getJSONObject(i).getString("TransType");
                if (TransType.equals("D")){
                    charge=ResTransactions.getJSONObject(i);
                }
                if (TransType.equals("C")){
                    payment=ResTransactions.getJSONObject(i);
                }
            }
            if (charge!=null){
                g2.drawString("消费合计:" + charge.getString("Charge"), 15, 185+sizey);
            }else{
                g2.drawString("消费合计: 0.00", 15, 185+sizey);
                charge=new JSONObject();
                charge.put("Charge","0.00");
            }
            if (payment!=null){
                g2.drawString("结算合计:" + payment.getString("Payment"), 15, 200+sizey);
            }else{
                g2.drawString("结算合计: 0.00", 15, 200+sizey);
                payment=new JSONObject();
                payment.put("Payment","0.00");
            }
            BigDecimal ch=new BigDecimal( charge.getString("Charge"));
            BigDecimal pay=new BigDecimal( payment.getString("Payment"));
            BigDecimal balance=ch.subtract(pay);
            g2.drawString("期末余额:" + balance.setScale(2, RoundingMode.HALF_UP), 15, 215+sizey);
            return Printable.PAGE_EXISTS; // 存在打印页时，继续打印工作
        }catch (Exception e){
            e.printStackTrace();
            return Printable.PAGE_EXISTS;
        }
    }

}
