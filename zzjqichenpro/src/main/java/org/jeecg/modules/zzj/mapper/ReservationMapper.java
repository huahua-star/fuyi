package org.jeecg.modules.zzj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.zzj.entity.Record;
import org.jeecg.modules.zzj.entity.Reservation;

@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {
}
