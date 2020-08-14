package org.jeecg.modules.zzj.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import org.jeecg.modules.zzj.entity.OperationRecord;
import org.jeecg.modules.zzj.entity.Reservation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class pdfReport {

        // 定义全局的字体静态变量
        private static Font titlefont;
        private static Font headfont;
        private static Font keyfont;
        public  static Font textfont;
        // 最大宽度
        private static int maxWidth = 520;
        // 静态代码块
        static {
            try {
                // 不同字体（这里定义为同一种字体：包含不同字号、不同style）
                BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                titlefont = new Font(bfChinese, 16, Font.BOLD);
                headfont = new Font(bfChinese, 14, Font.BOLD);
                keyfont = new Font(bfChinese, 10, Font.BOLD);
                textfont = new Font(bfChinese, 10, Font.NORMAL);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 生成订单PDF文件
        public void generateReservationPDF(Document document, ArrayList<Reservation> list, String month, String total, String imgUrl) throws Exception {
            Paragraph paragraph = new Paragraph("自助机"+month+"订单", titlefont);
            paragraph.setAlignment(1); //设置文字居中 0靠左   1，居中     2，靠右
            paragraph.setIndentationLeft(12); //设置左缩进
            paragraph.setIndentationRight(12); //设置右缩进
            paragraph.setFirstLineIndent(24); //设置首行缩进
            paragraph.setLeading(30f); //行间距
            paragraph.setSpacingAfter(15f);
            PdfPTable table2 = createTable(new float[] { 130, 160,120,120,130,120,120,120,120,120});
            //table.addCell(createCell("美好的一天", headfont, Element.ALIGN_LEFT, 6, false));
            table2.addCell(createCell("订单号", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("确认号", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("姓名", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("入住时间", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("离店时间", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("房型", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("房间号", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("证件号码", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("预定人手机号", keyfont, Element.ALIGN_CENTER));
            table2.addCell(createCell("订单状态", keyfont, Element.ALIGN_CENTER));

            for (int i=0;i<list.size();i++){
                table2.addCell( createCellBottomBoder(list.get(i).getResID(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getConfirmNo(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getGuestName(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getArrivalTime(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getDepartTime(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getRoomType(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getRoomNo(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getDocumentNo(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getBookerTel(), keyfont, Element.ALIGN_CENTER,0.5f));
                table2.addCell( createCellBottomBoder(list.get(i).getResState(), keyfont, Element.ALIGN_CENTER,0.5f));
            }

            table2.addCell( createCellBottomBoderCospan("合计", keyfont, Element.ALIGN_RIGHT,0.7f,3));
            table2.addCell( createCellBottomBoderCospan("Total", keyfont, Element.ALIGN_CENTER,0.7f,2));
            table2.addCell( createCellBottomBoder(total, keyfont, Element.ALIGN_CENTER,0.7f));
            table2.addCell( createCellBottomBoderCospan("", keyfont, Element.ALIGN_LEFT,0.7f,2));
            table2.addCell( createCellBottomBoder("", keyfont, Element.ALIGN_LEFT,0.7f));
            table2.addCell( createCellBottomBoder("", keyfont, Element.ALIGN_CENTER,0.7f));


            // 添加图片
            Image image = Image.getInstance(imgUrl);
            image.setAlignment(Image.ALIGN_CENTER);
            image.scalePercent(15); //依照比例缩放
            image.setSpacingAfter(10f);

            document.add(image);
            document.add(paragraph);
            document.add(table2);

        }


    public void generateOperationRecordPDF(Document document, ArrayList<OperationRecord> list, String month, String total, String imgUrl) throws Exception {
        Paragraph paragraph = new Paragraph("自助机"+month+"操作记录", titlefont);
        paragraph.setAlignment(1); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(24); //设置首行缩进
        paragraph.setLeading(30f); //行间距
        paragraph.setSpacingAfter(15f);
        PdfPTable table2 = createTable(new float[] { 130, 160,120,120,130});
        //table.addCell(createCell("美好的一天", headfont, Element.ALIGN_LEFT, 6, false));
        table2.addCell(createCell("订单号", keyfont, Element.ALIGN_CENTER));
        table2.addCell(createCell("姓名", keyfont, Element.ALIGN_CENTER));
        table2.addCell(createCell("操作", keyfont, Element.ALIGN_CENTER));
        table2.addCell(createCell("操作时间", keyfont, Element.ALIGN_CENTER));
        table2.addCell(createCell("操作状态", keyfont, Element.ALIGN_CENTER));
        for (int i=0;i<list.size();i++){
            table2.addCell( createCellBottomBoder(list.get(i).getResno(), keyfont, Element.ALIGN_CENTER,0.5f));
            table2.addCell( createCellBottomBoder(list.get(i).getName(), keyfont, Element.ALIGN_CENTER,0.5f));
            table2.addCell( createCellBottomBoder(list.get(i).getOperation(), keyfont, Element.ALIGN_CENTER,0.5f));
            table2.addCell( createCellBottomBoder(list.get(i).getCreateTime(), keyfont, Element.ALIGN_CENTER,0.5f));
            table2.addCell( createCellBottomBoder(list.get(i).getState().equals("1")?"成功":"失败", keyfont, Element.ALIGN_CENTER,0.5f));
        }

        table2.addCell( createCellBottomBoderCospan("合计", keyfont, Element.ALIGN_RIGHT,0.7f,3));
        table2.addCell( createCellBottomBoderCospan("Total", keyfont, Element.ALIGN_CENTER,0.7f,2));
        table2.addCell( createCellBottomBoder(total, keyfont, Element.ALIGN_CENTER,0.7f));
        table2.addCell( createCellBottomBoderCospan("", keyfont, Element.ALIGN_LEFT,0.7f,2));
        table2.addCell( createCellBottomBoder("", keyfont, Element.ALIGN_LEFT,0.7f));
        table2.addCell( createCellBottomBoder("", keyfont, Element.ALIGN_CENTER,0.7f));


        // 添加图片
        Image image = Image.getInstance(imgUrl);
        image.setAlignment(Image.ALIGN_CENTER);
        image.scalePercent(15); //依照比例缩放
        image.setSpacingAfter(10f);

        document.add(image);
        document.add(paragraph);
        document.add(table2);

    }
        /*// 直线
            Paragraph p1 = new Paragraph();
            p1.add(new Chunk(new LineSeparator()));

            // 点线
            Paragraph p2 = new Paragraph();
            p2.add(new Chunk(new DottedLineSeparator()));

            // 超链接
            Anchor anchor = new Anchor("baidu");
            anchor.setReference("www.baidu.com");

            // 定位
            Anchor gotoP = new Anchor("goto");
            gotoP.setReference("#top");*/


/**------------------------创建表格单元格的方法start----------------------------*/
        /**
         * 创建单元格(指定字体)
         * @param value
         * @param font
         * @return
         */
        public PdfPCell createCell(String value, Font font) {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPhrase(new Phrase(value, font));
            return cell;
        }
        /**
         * 创建单元格（指定字体、水平..）
         * @param value
         * @param font
         * @param align
         * @return
         */
        public PdfPCell createCell(String value, Font font, int align) {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(align);
            cell.setPhrase(new Phrase(value, font));
            return cell;
        }

    /**
     * 创建单元格（指定字体、水平..）
     * @param value
     * @param font
     * @param align
     * @return
     */
    public PdfPCell createCellNoboder(String value, Font font, int align) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthBottom(0);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }

    /**
     * 创建单元格（指定字体、水平..）
     * @param value
     * @param font
     * @param align
     * @return
     */
    public PdfPCell createCellNoboder(String value, Font font, int align, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthBottom(0);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }


    /**
     * 创建单元格（指定字体、水平..）
     * @param value
     * @param font
     * @param align
     * @return
     */
    public PdfPCell createCellBottomBoder(String value, Font font, int align, float width) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthBottom(width);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }
    /**
     * 创建单元格（指定字体、水平..）
     * @param value
     * @param font
     * @param align
     * @return
     */
    public PdfPCell createCellBottomBoderCospan(String value, Font font, int align, float width, int colspan) {
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(align);
        cell.setBorderWidthLeft(0);
        cell.setBorderWidthRight(0);
        cell.setBorderWidthTop(0);
        cell.setBorderWidthBottom(width);
        cell.setColspan(colspan);
        cell.setPhrase(new Phrase(value, font));
        return cell;
    }




        /**
         * 创建单元格（指定字体、水平居..、单元格跨x列合并）
         * @param value
         * @param font
         * @param align
         * @param colspan
         * @return
         */
        public PdfPCell createCell(String value, Font font, int align, int colspan) {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(align);
            cell.setColspan(colspan);
            cell.setPhrase(new Phrase(value, font));
            return cell;
        }
        /**
         * 创建单元格（指定字体、水平居..、单元格跨x列合并、设置单元格内边距）
         * @param value
         * @param font
         * @param align
         * @param colspan
         * @param boderFlag
         * @return
         */
        public PdfPCell createCell(String value, Font font, int align, int colspan, boolean boderFlag) {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(align);
            cell.setColspan(colspan);
            cell.setPhrase(new Phrase(value, font));
            cell.setPadding(3.0f);
            if (!boderFlag) {
                cell.setBorder(0);
                cell.setPaddingTop(15.0f);
                cell.setPaddingBottom(8.0f);
            } else if (boderFlag) {
                cell.setBorder(0);
                cell.setPaddingTop(0.0f);
                cell.setPaddingBottom(15.0f);
            }
            return cell;
        }
        /**
         * 创建单元格（指定字体、水平..、边框宽度：0表示无边框、内边距）
         * @param value
         * @param font
         * @param align
         * @param borderWidth
         * @param paddingSize
         * @param flag
         * @return
         */
        public PdfPCell createCell(String value, Font font, int align, float[] borderWidth, float[] paddingSize, boolean flag) {
            PdfPCell cell = new PdfPCell();
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(align);
            cell.setPhrase(new Phrase(value, font));
            cell.setBorderWidthLeft(borderWidth[0]);
            cell.setBorderWidthRight(borderWidth[1]);
            cell.setBorderWidthTop(borderWidth[2]);
            cell.setBorderWidthBottom(borderWidth[3]);
            cell.setPaddingTop(paddingSize[0]);
            cell.setPaddingBottom(paddingSize[1]);
            if (flag) {
                cell.setColspan(2);
            }
            return cell;
        }
/**------------------------创建表格单元格的方法end----------------------------*/


/**--------------------------创建表格的方法start------------------- ---------*/
        /**
         * 创建默认列宽，指定列数、水平(居中、右、左)的表格
         * @param colNumber
         * @param align
         * @return
         */
        public PdfPTable createTable(int colNumber, int align) {
            PdfPTable table = new PdfPTable(colNumber);
            try {
                table.setTotalWidth(maxWidth);
                table.setLockedWidth(true);
                table.setHorizontalAlignment(align);
                table.getDefaultCell().setBorder(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return table;
        }
        /**
         * 创建指定列宽、列数的表格
         * @param widths
         * @return
         */
        public PdfPTable createTable(float[] widths) {
            PdfPTable table = new PdfPTable(widths);
            try {
                table.setTotalWidth(maxWidth);
                table.setLockedWidth(true);
                table.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.getDefaultCell().setBorder(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return table;
        }
        /**
         * 创建空白的表格
         * @return
         */
        public PdfPTable createBlankTable() {
            PdfPTable table = new PdfPTable(1);
            table.getDefaultCell().setBorder(0);
            table.addCell(createCell("", keyfont));
            table.setSpacingAfter(20.0f);
            table.setSpacingBefore(20.0f);
            return table;
        }
/**--------------------------创建表格的方法end------------------- ---------*/
    }