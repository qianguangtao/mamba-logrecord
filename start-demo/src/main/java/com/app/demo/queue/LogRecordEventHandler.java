package com.app.demo.queue;

import com.alibaba.fastjson.JSON;
import com.app.core.disruptor.AbstractEventHandler;
import com.app.core.disruptor.Event;
import com.app.demo.service.OperationService;
import com.app.logrecord.pojo.ObjectDiffDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qiangt
 * @version 1.0
 * @date 2023/12/28 13:51
 * @description: 操作日志消息队列处理类
 */
@Slf4j
@Component
public class LogRecordEventHandler extends AbstractEventHandler<ObjectDiffDTO> {

    @Resource
    private OperationService operationService;

    @Override
    public boolean filter(Event<ObjectDiffDTO> event) {
        return event.getValue() instanceof ObjectDiffDTO;
    }

    @Override
    public void handler(ObjectDiffDTO objectDiffDTO) {
        try {
            log.info("接收操作记录：{}", JSON.toJSONString(objectDiffDTO));
            operationService.save(objectDiffDTO);
        } catch (Exception e) {
            log.error("接收操作记录失败：{}", e.getMessage(), e);
        }
    }
}
