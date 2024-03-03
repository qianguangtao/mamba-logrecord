package com.app.core.mybatis.concurrent;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author qiangt
 * @version 1.0
 * @date 2024/1/22 17:37
 * @description: 批量数据库操作并发基类
 */
@Data
@Slf4j
public abstract class BaseCallBack implements Callable<Boolean> {

    /**
     * 批处理id，作为redis key，控制事务回滚
     */
    protected String batchId;
    /**
     * 主线程等待计数器
     */
    protected CountDownLatch mainThreadLatch;
    /**
     * 是否需要回滚
     */
    protected AtomicBoolean requiredRollback;
    /**
     * 事务
     */
    protected PlatformTransactionManager transactionManager;

    @Override
    public Boolean call() throws Exception {
        if (requiredRollback.get()) {
            // 如果其他线程已经报错，停止主线程
            mainThreadLatch.countDown();
            return false;
        }
        // 设置一个事务
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        // 事物隔离级别，开启新事务，这样会比较安全些。
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        // 获得事务状态
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            log.info("业务开始处理：{}", Thread.currentThread().getName());
            this.doWork();
            log.info("业务处理结束：{}", Thread.currentThread().getName());
            // 业务处理结束
            mainThreadLatch.countDown();
            log.info("线程内正常 mainThreadLatch.countDown()：{}", Thread.currentThread().getName());
            // 线程等待
            while (true) {
                String curBatchStatus = AsyncTaskHelper.getBatchStatus(batchId);
                if (ObjectUtil.equals(BatchStatus.SUCCESS.name(), curBatchStatus)) {
                    log.info("提交事务：{}", Thread.currentThread().getName());
                    transactionManager.commit(status);
                    break;
                } else if (ObjectUtil.equals(BatchStatus.FAILURE.name(), curBatchStatus)) {
                    log.info("回滚事务：{}", Thread.currentThread().getName());
                    transactionManager.rollback(status);
                    break;
                } else {
                    long count = mainThreadLatch.getCount();
                    log.info("事务等待：{}，{}, {}", Thread.currentThread().getName(), curBatchStatus, count);
                    // 所有批处理已经全部执行完，批处理状态设为SUCCESS，跳出死循环
                    if (count == 0) {
                        AsyncTaskHelper.changeBatchStatus(batchId, requiredRollback.get() ? BatchStatus.FAILURE : BatchStatus.SUCCESS);
                    }
                }
            }
            return true;
        } catch (Exception e) {
            log.error("线程内异常 mainThreadLatch.countDown()，{}, {}", Thread.currentThread().getName(), e.getMessage(), e);
            // 如果出错了，requiredRollback需要回滚标识设为true，主线程计数器mainThreadLatch依旧减一
            requiredRollback.set(true);
            mainThreadLatch.countDown();
            transactionManager.rollback(status);
            log.info("回滚事务：{}", Thread.currentThread().getName());
            AsyncTaskHelper.changeBatchStatus(batchId, BatchStatus.FAILURE);
            return false;
        }
    }

    /**
     * 抽象方法，具体的任务
     */
    protected abstract void doWork();
}
