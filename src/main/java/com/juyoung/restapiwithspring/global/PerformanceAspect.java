package com.juyoung.restapiwithspring.global;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
@Slf4j
public class PerformanceAspect {

    private static final String EXECUTION_TIME_MS = "{}() - Execution time: {}ms";
    private static final String ARGUMENT_INPUT_AND_OUTPUT = "{}() - Input argument: {}, Output: {}";

    @Pointcut("execution(* com.juyoung.restapiwithspring..*.*Controller.*(..))")
    public void loggingPointCut() {
    }

    @Around("loggingPointCut()")
    public Object performCheck(ProceedingJoinPoint pjp) throws Throwable {
        Logger log = LoggerFactory.getLogger(pjp.getTarget().getClass());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object proceed = pjp.proceed();
        stopWatch.stop();

        log.info(EXECUTION_TIME_MS, pjp.getSignature().getName(), stopWatch.getTotalTimeMillis());
        log.debug(ARGUMENT_INPUT_AND_OUTPUT, pjp.getSignature().getName(), pjp.getArgs(), proceed);
        return proceed;
    }
}
