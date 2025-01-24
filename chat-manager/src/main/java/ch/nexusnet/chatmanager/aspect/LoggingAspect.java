package ch.nexusnet.chatmanager.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Pointcut defining where the logger gets used
    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)" +
            " || within(@org.springframework.stereotype.Component *)")
    private void applicationPackagePointcut() {
    }

    // Advice that logs methods before they are executed
    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Enter: {} with argument[s] = {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        }
    }

    // Advice that logs methods after they are executed
    @AfterReturning(pointcut = "applicationPackagePointcut()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("Exit: {} with result = {}", joinPoint.getSignature().toShortString(), result);
        }
    }

    // Advice that logs exceptions
    @AfterThrowing(pointcut = "applicationPackagePointcut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        if (logger.isErrorEnabled()) {
            String methodName = joinPoint.getSignature().getName();
            logger.error("<< {}() - {}", methodName, exception.getMessage());
        }
    }
}