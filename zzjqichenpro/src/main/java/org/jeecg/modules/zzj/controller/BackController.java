package org.jeecg.modules.zzj.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.zzj.entity.Reservation;
import org.jeecg.modules.zzj.entity.TblTxnp;
import org.jeecg.modules.zzj.service.ITblTxnpService;
import org.jeecg.modules.zzj.service.ReservationService;
import org.jeecg.modules.zzj.service.impl.TblTxnpServiceImpl;
import org.jeecg.modules.zzj.util.Card.SetResultUtil;
import org.jeecg.modules.zzj.util.Http.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@Slf4j
@Api(tags = "后台")
@RestController
@RequestMapping("/zzj/back")
public class BackController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ITblTxnpService tblTxnpService;

    @ApiOperation(value = "查询订单")
    @RequestMapping(value = "/queryReservation", method = RequestMethod.GET)
    public Result<IPage<Reservation>> queryReservation(Reservation reservation, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                       @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                       HttpServletRequest req) {
        Result<IPage<Reservation>> result = new Result<IPage<Reservation>>();
        QueryWrapper<Reservation> queryWrapper = QueryGenerator.initQueryWrapper(reservation, req.getParameterMap());
        Page<Reservation> page = new Page<Reservation>(pageNo, pageSize);
        IPage<Reservation> pageList = reservationService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    @ApiOperation(value = "修改订单")
    @RequestMapping(value = "/updateReservation", method = RequestMethod.GET)
    public Result<?> updateReservation(String ConfirmNo,String state,String gonganstate) {
        log.info("updateReservation()方法");
        if (StringUtils.isEmpty(ConfirmNo)){
            return Result.error("缺少参数");
        }
        Reservation reservation=reservationService.getOne(new QueryWrapper<Reservation>().eq("Confirm_no",ConfirmNo));
        if (gonganstate!=null&&!"".equals(gonganstate)){
            reservation.setGonganstate(gonganstate);
        }
        if (state!=null&&!"".equals(state)){
            reservation.setState(state);
        }
        reservationService.updateById(reservation);
        return Result.ok("修改成功");
    }


    @ApiOperation(value = "查询流水")
    @RequestMapping(value = "/queryTblTxnp", method = RequestMethod.GET)
    public Result<IPage<TblTxnp>> queryTblTxnp(TblTxnp tblTxnp, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                               @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                               HttpServletRequest req) {
        Result<IPage<TblTxnp>> result = new Result<IPage<TblTxnp>>();
        QueryWrapper<TblTxnp> queryWrapper =  QueryGenerator.initQueryWrapper(tblTxnp, req.getParameterMap());
        Page<TblTxnp> page = new Page<TblTxnp>(pageNo, pageSize);
        IPage<TblTxnp> pageList = tblTxnpService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }
}
