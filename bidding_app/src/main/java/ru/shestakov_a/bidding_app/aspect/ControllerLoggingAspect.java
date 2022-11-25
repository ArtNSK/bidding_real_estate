package ru.shestakov_a.bidding_app.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Around("execution(* getFromController*(..))")
    public Object aroundMethodsAdvice (ProceedingJoinPoint joinPoint) throws Throwable {
        logEnter(joinPoint);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;

            logExit(joinPoint, result, duration);
            return result;
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - start;
            log.warn("exception occurred, duration={} ms", duration);
            throw e;
        }
    }

    private void logEnter(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Logger logTarget = LogManager.getLogger(signature.getDeclaringType());
            String methodName = signature.getMethod().getName();
            if (logTarget.isDebugEnabled()) {
                logTarget.debug("{}(), enter: {}", methodName, argsAsString(joinPoint));
            } else if (logTarget.isInfoEnabled()) {
                logTarget.info("{}(), enter", methodName);
            }
        } catch (Exception e) {
            log.warn("logEnter(): ", e);
        }
    }

    private String argsAsString(ProceedingJoinPoint joinPoint) {
        String[] argsName = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] argsValue = joinPoint.getArgs();
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < argsName.length; i++) {
            args.append(argsName[i]).append(" = ").append(argsValue[i]).append(", ");
        }
        args.setLength(Math.max(args.length() - 2, 0));
        return args.toString();
    }

    private void logExit(ProceedingJoinPoint joinPoint, Object result, long duration) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Logger logTarget = LogManager.getLogger(signature.getDeclaringType());
            String methodName = signature.getMethod().getName();
            if (logTarget.isDebugEnabled()) {
                logTarget.debug("{}(), duration={} ms, exit: {}", methodName, duration, result);
            } else if (logTarget.isInfoEnabled()) {
                logTarget.info("{}(), duration={} ms, exit", methodName, duration);
            }
        } catch (Exception e) {
            log.warn("logExit(): ", e);
        }
    }
}