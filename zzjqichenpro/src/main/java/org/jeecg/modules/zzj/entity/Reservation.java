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
@TableName("reservation")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="reservation对象", description="订单")
public class Reservation {

    /**表id*/
    @TableId(type = IdType.UUID)
    @ApiModelProperty(value = "表id")
    private String id;

    private String Specials;
    private String CompanyID;
    private String ConfirmNo;
    private String CreditLine;
    private String HotelNo;
    private String DepartBizDate;
    private String ResState;
    private String ContractID;
    private String GroupConfirmNo;
    private String AdultsCount;
    private String GroupID;
    private String MessageState;
    private String IsHaveDifferentRatePlan;
    private String RoomCount;
    private String RoomType;
    private String TravelAgentID;
    private String DepartTime;
    private String Remarks;
    private String HiddenMethod;
    private String RelationID;
    private String IsActionTime;
    private String GuestName;
    private String ArrivalTime;
    private String ResSource;
    private String TransferCommand;
    private String RoomNo;
    private String DocumentNo;
    private String SourceArea;
    private String CreateTime;
    private String ResID;
    private String DocumentType;
    //private String ResGuest;
    private String GuestMebCardNo;
    private String ContractName;
    private String ArrivalBizDate;
    private String BillingInstruction;
    private String BookerTel;
    private String RateCode;
    private String SalesmanID;
    private String ReservationType;
    private String ContractDetailID;
    private String Balance;
    private String RoomRate;
    private String state;
    private String gonganstate;
}
