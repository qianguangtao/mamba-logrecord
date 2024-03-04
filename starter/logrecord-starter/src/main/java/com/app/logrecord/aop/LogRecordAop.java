package com.app.logrecord.aop;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.app.core.disruptor.DisruptorPublisher;
import com.app.core.mybatis.BaseEntity;
import com.app.core.security.ResourceOwnerContext;
import com.app.core.util.SpelUtil;
import com.app.kit.AopUtil;
import com.app.logrecord.annotation.LogRecord;
import com.app.logrecord.annotation.LogRecordModel;
import com.app.logrecord.enums.LogOperate;
import com.app.logrecord.pojo.ObjectDiffDTO;
import com.app.logrecord.utils.ObjectDiffUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @author qiangt
 * @date 2023/9/13
 * @apiNote
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogRecordAop {

    /** 从返回值中取业务id的spel表达式前缀 */
    private static final String RETURN_SPEL_PREFIX = "result";
    /** spel表达式工具类，用来执行spel获取结果 */
    private final SpelUtil spelUtil;
    /** 当前登录人 */
    private final ResourceOwnerContext resourceOwnerContext;

    /** 切点定义 */
    @Pointcut("@annotation(com.app.logrecord.annotation.LogRecord)")
    public void pointcut() {

    }

    /** 切面处理 */
    @Around(value = "pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        LogRecord logRecord = method.getAnnotation(LogRecord.class);
        String key = logRecord.key();
        Map<String, Object> params = null;
        Object paramObj = null;
        Object oldObject = null;
        Object businessKey = null;
        Object result = null;
        if (StrUtil.isEmpty(key)) {
            log.error("记录操作日志@LogRecord配置错误，请设置业务主键key的取值规则");
            result = joinPoint.proceed();
            return result;
        }
        try {
            params = AopUtil.getParams(joinPoint);
            paramObj = getLogRecordModelObj(method, params);
            Class<? extends BaseEntity> entityClass = logRecord.oldObjClass();

            // 如果取业务id的spel不包含result，则是更新，oldObject从数据库中取
            if (key.indexOf(RETURN_SPEL_PREFIX) < 0) {
                businessKey = spelUtil.executeExpression(key, params);
                Object oldObjectTemp = spelUtil.executeMethodExpression(logRecord.method(), Convert.toStr(businessKey));
                oldObject = BeanUtil.toBean(oldObjectTemp, entityClass);
            } else {
                // 使用反射创建个新对象
                oldObject = entityClass.newInstance();
            }
        } catch (Exception e) {
            log.error("操作记录aop，执行切面方法前异常：{}", e.getMessage(), e);
        }

        // 执行切面方法
        result = joinPoint.proceed();

        try {
            // 如果取业务id的spel包含result，则是新增，oldObject new一个空对象
            if (key.indexOf(RETURN_SPEL_PREFIX) > -1) {
                // 如果spel是#result.xxx，则从返回值中获取业务主键key，且是新增业务数据
                businessKey = spelUtil.executeExpression(key, RETURN_SPEL_PREFIX, result);
            }
            if (ObjectUtil.isEmpty(businessKey)) {
                return result;
            }
            ObjectDiffDTO objectDiffDTO = ObjectDiffUtil.objectDiff(oldObject, paramObj);
            if (ObjectUtil.isNotEmpty(objectDiffDTO) && CollectionUtil.isNotEmpty(objectDiffDTO.getFieldDiffDTOList())) {
                objectDiffDTO.setBusinessId(Convert.toStr(businessKey));
                objectDiffDTO.setOperatorName(resourceOwnerContext.getPrincipal().getName());
                objectDiffDTO.setDescription(logRecord.desc());
                objectDiffDTO.setJsonBefore(JSON.toJSONString(oldObject));
                objectDiffDTO.setJsonAfter(JSON.toJSONString(paramObj));
                objectDiffDTO.setLogOperate(LogOperate.getLogOperateByCode(logRecord.operateType()));
                log.info("发送操作记录：{}", JSON.toJSONString(objectDiffDTO));
                DisruptorPublisher.send(objectDiffDTO);
            }
        } catch (Exception e) {
            log.error("操作记录aop，执行切面方法后异常：{}", e.getMessage(), e);
        }
        return result;
    }

    /**
     * 获取切面方法中，带LogRecordModel注解的对象
     * @param method
     * @param params
     * @return
     */
    private Object getLogRecordModelObj(Method method, Map<String, Object> params) {
        Parameter[] parameters = method.getParameters();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object paramObj = null;
        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName();
            for (int j = 0; j < parameterAnnotations[i].length; j++) {
                Annotation annotation = parameterAnnotations[i][j];
                if (annotation instanceof LogRecordModel) {
                    String argName = ((LogRecordModel) annotation).value();
                    paramObj = StrUtil.isNotBlank(argName) ? params.get(argName) : params.get(paramName);
                    break;
                }
            }
        }
        return paramObj;
    }

}
