package com.jiu.wait.aspect;

/**
 * @Author Administrator
 * @create 2020/8/10 15:13
 */

import com.jiu.utils.WebUtils;
import com.jiu.wait.annoation.Waiting;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 防止表单重复提交AOP切面
 */
@Component
@Aspect
public class WaitingAspect {

    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(WaitingAspect.class);

    /**
     * Redis简单操作模板类
     */
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * Pointcut切点: 拦截所有标注Waiting注解的方法
     */
    @Pointcut("@annotation(com.jiu.wait.annoation.Waiting)")
    private void pointCut() {

    }

    /**
     * 调用目标方法前和调用后完成特定的业务
     *
     * @param point 连接点
     * @return
     * @throws Throwable
     */
    @Around("pointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //获取HttpServletRequest请求对象
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        //通过HttpServletRequest对象获取发起请求的客户端IP地址
        String ipAddress = WebUtils.retrieveClientIp(request);
        //获取MethodSignature方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        //根据签名获取对应的方法
        Method targetMethod = signature.getMethod();
        //根据方法名称获取声明类的全限类名
        String targetClassName = targetMethod.getDeclaringClass().getName();
        //目标方法名称
        String methodName = targetMethod.getName();
        // 请求的方法参数值
        Object[] args = point.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(targetMethod);
        StringBuilder params = new StringBuilder();
        if (args != null && paramNames != null) {
            for (int i = 0; i < args.length; i++) {
                params.append("  ").append(paramNames[i]).append(": ").append(args[i]);
            }

        }

        int hashCode = Math.abs(String.format("%s#%d#%f", targetClassName, methodName,params).hashCode());
        //根据IP地址生成缓存到Redis的Key
        String redisKey = String.format("%s_%d", ipAddress, hashCode);
        logger.info("请求客户端缓存Key: {}", redisKey);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", targetMethod.getAnnotation(Waiting.class).timeout(), TimeUnit.MILLISECONDS);
        if (!success) {
            logger.error("请勿重复提交.....");
            throw new RuntimeException("请勿重复提交.....");
        }
        //保存(客户端请求IP地址->UUID随机字符串)到Redis中
        //执行Controller目标方法
        return point.proceed();
    }

}
