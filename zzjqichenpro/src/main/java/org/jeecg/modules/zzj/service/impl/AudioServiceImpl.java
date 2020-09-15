package org.jeecg.modules.zzj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.zzj.entity.Audio;
import org.jeecg.modules.zzj.mapper.AudioMapper;
import org.jeecg.modules.zzj.service.AudioService;
import org.springframework.stereotype.Service;

@Service
public class AudioServiceImpl extends ServiceImpl<AudioMapper, Audio> implements AudioService {
}
