package org.jeecg.modules.zzj.util.Publicsecurity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import javax.xml.bind.annotation.XmlRootElement;

@Data
@TableName("gongan")
public class gongan {
    private String id;//UUID
    private String flag;//国籍
    private String createtime;//创建时间(当前时间)
    private String operatetype;;// 对该旅客的操作类型  0 入住 1 退房 2 修改 3 换房
    private String roomno;//房间号
    private String exittime;//入住时间
    private String entertime;//离店时间
    private String name;//姓名
    private String sex;//性别 1 男 2 女  3 不明
    private String nation;//国家或者民族
    private String birthday;//出生年月日
    private String cardtype;// 证件类型
    private String cardno;
    private String reason;//理由
    private String occupation;//职业
    private String addrdetail;//旅客详细住址
    private String creditcardtype;
    private String creditcardno;
    private String confirmno;
}
