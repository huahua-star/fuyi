package org.jeecg.modules.zzj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@TableName("hotelSetTable")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="hotelSetTable对象", description="酒店配置表")
public class HotelSetTable {
    /**表id*/
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
    private String id;
    //酒店名称
    private String hotelName;
    //机器编号
    private String machineNum;
    //表头规定 离店时间
    private String checkOutTime;
    // 是否支持钟点房
    private String isTrueHourRoom;
    // 是否支持迷你吧
    private String isTrueMini;
    // 是否支持选房
    private String isTrueChooseRoom;
    //自助预定
    private String selfHelpBook;
    //入住
    private String checkInName;
    //退房
    private String checkOutName;
    //续住
    private String continueLiveName;
    //团队入住
    private String teamName;
    //团队入住英文
    private String teamNameEng;

    //自助预定 英文
    private String selfHelpBookEng;
    //入住 英文
    private String checkInNameEng;
    //退房 英文
    private String checkOutNameEng;
    //续住 英文
    private String continueLiveNameEng;

    //有订单页面的按钮颜色
    private String checkInButtonColor;
    //登记表同住人按钮颜色
    private String liveWithButtonColor;
    //登记表继续的按钮颜色
    private String continueButtonColor;
    //续房的按钮颜色
    private String xufangButtonColor;
    //退房的按钮颜色
    private String tuifangButtonColor;

    //登记表页面描述字段
    private String registrationDescription;
    //登记表页面英文描述字段
    private String registrationDescriptionEng;
    //首页模块颜色
    private String homePageModuleColor;
    //团队是否续房
    private String teamWhetherXuFang;
    //可开发票数量
    private String invoiceNums;
}

