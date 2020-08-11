package org.jeecg.modules.zzj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.zzj.entity.OperationRecord;
import org.jeecg.modules.zzj.mapper.OperationRecordMapper;
import org.jeecg.modules.zzj.service.OperationRecordService;
import org.springframework.stereotype.Service;

@Service
public class OperationRecordServiceImpl  extends ServiceImpl<OperationRecordMapper, OperationRecord> implements OperationRecordService {
}
