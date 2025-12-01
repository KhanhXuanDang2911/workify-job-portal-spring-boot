package beworkify.interceptor;

import beworkify.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor for logging HTTP requests and responses.
 *
 * @author KhanhDX
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
  private static final String START_TIME_ATTRIBUTE = "startTime";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    long startTime = System.currentTimeMillis();
    request.setAttribute(START_TIME_ATTRIBUTE, startTime);

    LogUtils.logRequestStart(logger, request);
    LogUtils.logRequestParameters(logger, request);
    LogUtils.logRequestHeaders(logger, request);

    return true;
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      ModelAndView modelAndView)
      throws Exception {}

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
      throws Exception {
    Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
    if (startTime != null) {
      int statusCode = response.getStatus();
      LogUtils.logRequestEnd(logger, request, statusCode, startTime);
    }

    if (ex != null) {
      LogUtils.logError(logger, "Request completed with exception", ex);
    }
  }
}
