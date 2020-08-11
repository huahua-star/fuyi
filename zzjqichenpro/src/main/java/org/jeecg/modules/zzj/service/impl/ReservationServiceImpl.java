package org.jeecg.modules.zzj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.zzj.entity.Reservation;
import org.jeecg.modules.zzj.mapper.ReservationMapper;
import org.jeecg.modules.zzj.service.ReservationService;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceImpl  extends ServiceImpl<ReservationMapper, Reservation> implements ReservationService {

}
