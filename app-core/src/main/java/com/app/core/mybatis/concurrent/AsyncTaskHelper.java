package com.app.core.mybatis.concurrent;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import com.app.core.redis.CommonRedisKey;
import com.app.kit.SpringKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/1/22 17:44
 * @description: 异步批量数据库操作工具类
 */
@Slf4j
public class AsyncTaskHelper {

    /** 批处理状态锁时间 */
    private static final Integer TASK_LOCK_MINUTES = 3;

    public static boolean doWork(List<? extends BaseCallBack> tasks, PlatformTransactionManager transactionManager) throws InterruptedException {
        if (CollectionUtil.isEmpty(tasks)) {
            return false;
        }
        CountDownLatch mainThreadLatch = new CountDownLatch(tasks.size());
        AtomicBoolean requiredRollback = new AtomicBoolean(false);
        List<Future<Boolean>> resultList = new ArrayList<Future<Boolean>>();
        ExecutorService executorService = SpringKit.getBean(ExecutorService.class);
        String batchId = IdUtil.simpleUUID();
        for (BaseCallBack task : tasks) {
            task.setBatchId(batchId);
            task.setMainThreadLatch(mainThreadLatch);
            task.setRequiredRollback(requiredRollback);
            task.setTransactionManager(transactionManager);
        }
        changeBatchStatus(batchId, BatchStatus.WAIT);
        resultList = executorService.invokeAll(tasks);
        try {
            // 阻塞当前主线程，等待批处理线程全部执行完毕
            mainThreadLatch.await(TASK_LOCK_MINUTES, TimeUnit.MINUTES);
            if (!requiredRollback.get()) {
                // 不需要回滚，设置批处理状态为SUCCESS，提交全部事务
                changeBatchStatus(batchId, BatchStatus.SUCCESS);
                return true;
            } else {
                for (Future<Boolean> f : resultList) {
                    Boolean result = f.get();
                    if (!result) {
                        return result;
                    }
                }
                return true;
            }
        } catch (Exception e) {
            log.error("异步任务出现异常：{}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 更改批处理状态
     * @param batchId
     * @param batchStatus
     */
    public static void changeBatchStatus(String batchId, BatchStatus batchStatus) {
        StringRedisTemplate redisTemplate = SpringKit.getBean(StringRedisTemplate.class);
        String redisKey = CommonRedisKey.ConcurrentTransactionKey.key(batchId);
        redisTemplate.opsForValue().set(redisKey, batchStatus.name(), TASK_LOCK_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 根据批处理id，获取批处理状态
     * @param batchId
     * @return
     */
    public static String getBatchStatus(String batchId) {
        StringRedisTemplate redisTemplate = SpringKit.getBean(StringRedisTemplate.class);
        String redisKey = CommonRedisKey.ConcurrentTransactionKey.key(batchId);
        return redisTemplate.opsForValue().get(redisKey);
    }
}
