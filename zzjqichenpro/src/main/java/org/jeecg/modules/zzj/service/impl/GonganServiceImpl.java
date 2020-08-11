package org.jeecg.modules.zzj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.zzj.mapper.GonganMapper;
import org.jeecg.modules.zzj.service.GonganService;
import org.jeecg.modules.zzj.util.Publicsecurity.gongan;
import org.springframework.stereotype.Service;

@Service
public class GonganServiceImpl  extends ServiceImpl<GonganMapper, gongan> implements GonganService {
}
