package org.jeecg.modules.zzj.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 发卡记录表
 * @Author: jeecg-boot
 * @Date:   2019-09-16
 * @Version: V1.0
 */
@Data
@TableName("sendrecord")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="qc_record对象", description="发卡记录表")
public class Record {
    
	/**表id*/
	@TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
	private String id;
	/**房间号*/
	@Excel(name = "房间号", width = 15)
    @ApiModelProperty(value = "房间号")
	private String roomNum;
	/**状态 0失败 1成功*/
	@Excel(name = "状态 0失败 1成功", width = 15)
    @ApiModelProperty(value = "状态 0失败 1成功")
	private String state;
	/**pms 订单号 */
	@Excel(name = "pms订单号", width = 15)
	@ApiModelProperty(value = "pms订单号")
	private String orderId;
	/*数量*/
	@Excel(name = "数量", width = 15)
	@ApiModelProperty(value = "数量")
	private String sendNum;
	/**评论时间*/
	@Excel(name = "评论时间", width = 15)
    @ApiModelProperty(value = "评论时间")
	private String sendTime;
}
