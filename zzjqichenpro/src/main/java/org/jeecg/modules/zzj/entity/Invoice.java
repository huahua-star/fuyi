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
@TableName("invoice")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="invoice对象", description="发票配置表")
public class Invoice {
    /**表id*/
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
    private String id;
    //打印小票二维码 图片生成路径
    private String qrDir;
    //由电子发票平台分配的appCode
    private String appCode;
    //#调用方纳税人识别号
    private String taxpayerCode;
    //证书路径
    private String keyStorePath;
    //证书别名
    private String keyStoreAbner;
    //证书密码
    private String keyStorePassWord;
    //发票接口
    private String facadeUrl;
}
