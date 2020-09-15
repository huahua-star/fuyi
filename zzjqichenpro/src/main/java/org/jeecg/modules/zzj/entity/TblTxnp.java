package org.jeecg.modules.zzj.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 流水表
 * @Author: jeecg-boot
 * @Date:   2019-09-16
 * @Version: V1.0
 */
@Data
@TableName("fuyi_tbl_txn_p")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="fuyi_tbl_txn_p对象", description="流水表")
public class TblTxnp implements Serializable{
    
	/**表id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
	private java.lang.String id;
	/**商家订单号*/
	@Excel(name = "商家订单号", width = 15)
	@ApiModelProperty(value = "商家订单号")
	private java.lang.String orderid;//商家订单号
	/**预定号*/
	@Excel(name = "预订号", width = 15)
	@ApiModelProperty(value = "预订号")
	private java.lang.String preOrderid;//凯莱酒店订单号
	/**支付方式 0支付宝 1微信 2银行卡*/
	@Excel(name = "支付方式 0支付宝 1微信 2银行卡", width = 15)
    @ApiModelProperty(value = "支付方式 0支付宝 1微信 2银行卡")
	private java.lang.String paymethod;
	/**支付类型 0 普通支付 1预授权支付*/
	@Excel(name = "支付类型 0 普通支付 1预授权支付", width = 15)
    @ApiModelProperty(value = "支付类型 0 普通支付 1预授权支付")
	private java.lang.String paytype;
	/**房间号*/
	@Excel(name = "房间号", width = 15)
	@ApiModelProperty(value = "房间号")
	private java.lang.String roomnum;
	/**消费金额*/
	@Excel(name = "消费金额", width = 15)
    @ApiModelProperty(value = "消费金额")
	private java.math.BigDecimal amount;
	/**预授权金额*/
	@Excel(name = "预授权金额", width = 15)
    @ApiModelProperty(value = "预授权金额")
	private java.math.BigDecimal preamount;
	/**退款金额*/
	@Excel(name = "退款金额", width = 15)
	@ApiModelProperty(value = "退款金额")
	private java.math.BigDecimal refund;
	/**订单现在的平台订单号*/
	@Excel(name = "订单现在的平台订单号", width = 15)
	@ApiModelProperty(value = "订单现在的平台订单号")
	private String logNo;
	@Excel(name = "订单原本的平台订单号", width = 15)
	@ApiModelProperty(value = "订单原本的平台订单号")
	private String oldLogNo;
	/**银行卡号*/
	@Excel(name = "银行卡号", width = 20)
	@ApiModelProperty(value = "银行卡号")
	private java.lang.String cardno;
	/**状态 0 撤销 1 未支付 2 已支付*/
	@Excel(name = "状态 0预授权撤销/退款 1 未支付 2 已支付 3预授权完成 4预授权完成的撤销", width = 15)
    @ApiModelProperty(value = "状态 0预授权撤销/退款 1 未支付 2 已支付 3预授权完成 4预授权完成的撤销")
	private java.lang.String state;
	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private java.lang.String createBy;
	/**预订入住时间*/
	@Excel(name = "预订入住时间", width = 15)
    @ApiModelProperty(value = "预订入住时间")
	private java.lang.String createTime;
	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private java.lang.String updateBy;
	/**预订离店时间*/
	@Excel(name = "预订离店时间", width = 15)
    @ApiModelProperty(value = "预订离店时间")
	private java.lang.String updateTime;

}
