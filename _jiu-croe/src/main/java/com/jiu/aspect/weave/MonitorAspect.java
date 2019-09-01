package com.jiu.aspect.weave;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @By 九 监控Aspect
 * @Date 2019年8月20日
 * @Time 上午10:44:07
 * @Email budongilt@gmail.com
 */
@Aspect
public class MonitorAspect {
    final String controller = "execution(* com.faquir.controller..*.*(..))";

    //final String service="execution(* com.faquir.service..*.*(..))";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut(controller)
    public void monitorPointcut() {
    }

    @Around("monitorPointcut()")
    public Object monitor(ProceedingJoinPoint pjp) {
        try {
            Method method = getMethod(pjp);
            //调用的方法全路径
            String name = method.getDeclaringClass().getName() + "." + method.getName();
            ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
            if (apiOperation != null) {
                //获取方法描述
               String apiValue = apiOperation.value();
            }
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String request_url = requestMapping.value()[0];
            }
            String param = paramJson(pjp);
        } catch (Exception e) {
        }
        Object result = "";
        try {
            Object proceed = pjp.proceed();
            return proceed;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }


    /**
     * 方法形参转成JSON
     * @param pjp
     * @return
     */
    private String paramJson(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        Signature signature = pjp.getSignature();
        List<Object> o = new ArrayList<Object>();
        MethodSignature methodSignature = (MethodSignature) signature;
        String[] parameterNames = methodSignature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof HttpServletRequest) {
                continue;
            }
            Map<String, Object> str = new HashMap<>();
            str.put(parameterNames[i], args[i]);
            o.add(str);
        }
        try {
            return  objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            return  "";
        }
    }


    /**
     * 获取执行方法
     *
     * @param point
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethod(JoinPoint point) throws NoSuchMethodException {
        Signature sig = point.getSignature();
        MethodSignature msig = (MethodSignature) sig;
        return point.getTarget().getClass().getMethod(msig.getName(), msig.getParameterTypes());
    }
}