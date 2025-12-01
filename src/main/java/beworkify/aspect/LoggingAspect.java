package beworkify.aspect;

import beworkify.exception.AppException;
import beworkify.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Aspect for logging controller and service method executions.
 *
 * @author KhanhDX
 */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

  @Pointcut("within(beworkify.controller..*)")
  public void controllerMethods() {}

  @Pointcut("within(beworkify.service..*)")
  public void serviceMethods() {}

  @Around("controllerMethods()")
  public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    Object[] args = joinPoint.getArgs();

    if (log.isDebugEnabled()) {
      log.debug(">>> Entering: {} with args: {}", methodName, formatArgs(args));
    }

    long startTime = System.currentTimeMillis();
    Object result = null;

    try {
      result = joinPoint.proceed();
      return result;
    } finally {
      long duration = System.currentTimeMillis() - startTime;

      if (log.isDebugEnabled()) {
        log.debug("<<< Exiting: {} | Duration: {}ms", methodName, duration);
      }
    }
  }

  @AfterThrowing(pointcut = "controllerMethods() || serviceMethods()", throwing = "exception")
  public void logException(JoinPoint joinPoint, Throwable exception) {
    String methodName = joinPoint.getSignature().toShortString();
    if (exception instanceof AppException
        || exception instanceof ResourceNotFoundException
        || exception instanceof ConstraintViolationException
        || exception instanceof HttpMessageNotReadableException
        || exception instanceof AccessDeniedException) {
      log.error("Exception in {}: {}", methodName, exception.getMessage());
    } else {
      log.error("Exception in {}: {}", methodName, exception.getMessage(), exception);
    }
  }

  private String formatArgs(Object[] args) {
    if (args == null || args.length == 0) {
      return "[]";
    }

    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < args.length; i++) {
      if (i > 0) {
        sb.append(", ");
      }

      Object arg = args[i];
      if (arg == null) {
        sb.append("null");
      } else if (arg instanceof MultipartFile) {
        MultipartFile file = (MultipartFile) arg;
        sb.append("MultipartFile(").append(file.getOriginalFilename()).append(")");
      } else if (arg.getClass().getName().startsWith("beworkify.dto.request")) {
        sb.append(arg.getClass().getSimpleName());
      } else if (arg.getClass().getName().startsWith("jakarta.servlet")
          || arg.getClass().getName().startsWith("org.springframework")) {
        sb.append(arg.getClass().getSimpleName());
      } else {
        String str = arg.toString();
        if (str.length() > 100) {
          sb.append(str, 0, 100).append("...");
        } else {
          sb.append(str);
        }
      }
    }
    sb.append("]");
    return sb.toString();
  }
}
