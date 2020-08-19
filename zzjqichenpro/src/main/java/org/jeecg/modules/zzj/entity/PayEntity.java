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
@TableName("payentity")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="payentity对象", description="微信支付宝配置表")
public class PayEntity {
    /**表id*/
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
    private String id;
    //操作系统 0 安卓 1 IOS 2 windows 3 直连
    private String opSys;
    //字符集 默认 00 GBK
    private String characterSet;
    //机构号
    private String orgNo;
    //商户号
    private String mercId;
    //设备号
    private String trmNo;
    //签名方式
    private String signType;
    //普通支付版本号
    private String ordinaryVersion;
    //预授权支付版本号
    private String preVersion;
    //退款版本号
    private String tuikuanVersion;
    //签名秘钥
    private String signKey;
    //退款请求地址
    private String tuikuanUrl;
    //普通支付请求地址
    private String putongpayUrl;
    //预授权支付系列请求地址
    private String prepayUrl;


}
