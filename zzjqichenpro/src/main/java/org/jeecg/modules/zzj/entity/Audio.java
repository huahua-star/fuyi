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
@TableName("audio")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="audio对象", description="音频对象")
public class Audio {
    /**表id*/
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
    private String id;
    //音频名字
    private String audio1;
    //音频名字
    private String audio2;
    private String audio3;
    private String audio4;
    private String audio5;
    private String audio6;
    private String audio7;
    private String audio8;
    private String audio9;
    private String audio10;
    private String audio11;
    private String audio12;
    private String audio13;
    private String audio14;
    private String audio15;
    private String audio16;
    private String audio17;
    private String audio18;
}
