package org.jeecg.modules.zzj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.zzj.entity.PayEntity;
import org.jeecg.modules.zzj.mapper.PayEntityMapper;
import org.jeecg.modules.zzj.service.PayEntityService;
import org.springframework.stereotype.Service;

@Service
public class PayEntityServiceImpl  extends ServiceImpl<PayEntityMapper, PayEntity> implements PayEntityService {
}
