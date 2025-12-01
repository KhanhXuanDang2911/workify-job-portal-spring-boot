package beworkify.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

/**
 * Utility class for logging operations. Provides standardized logging methods for requests,
 * responses, and errors.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public class LogUtils {

  private LogUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static void logRequestStart(Logger logger, HttpServletRequest request) {
    if (!logger.isInfoEnabled()) {
      return;
    }

    String method = request.getMethod();
    String uri = request.getRequestURI();
    String queryString = request.getQueryString();
    String fullUrl = queryString != null ? uri + "?" + queryString : uri;

    logger.info(
        ">>> REQUEST START: {} {} | Remote IP: {}", method, fullUrl, getClientIpAddress(request));
  }

  public static void logRequestEnd(
      Logger logger, HttpServletRequest request, int statusCode, long startTime) {
    if (!logger.isInfoEnabled()) {
      return;
    }

    long duration = System.currentTimeMillis() - startTime;
    String method = request.getMethod();
    String uri = request.getRequestURI();

    logger.info(
        "<<< REQUEST END: {} {} | Status: {} | Duration: {}ms", method, uri, statusCode, duration);
  }

  public static void logRequestParameters(Logger logger, HttpServletRequest request) {
    if (!logger.isDebugEnabled()) {
      return;
    }

    Map<String, String[]> params = request.getParameterMap();
    if (params.isEmpty()) {
      return;
    }

    StringBuilder sb = new StringBuilder("Request Parameters: ");
    params.forEach(
        (key, values) -> {
          sb.append(key).append("=");
          if (isSensitiveParameter(key)) {
            sb.append("[REDACTED]");
          } else {
            sb.append(String.join(",", values));
          }
          sb.append("; ");
        });

    logger.debug(sb.toString());
  }

  public static void logRequestHeaders(Logger logger, HttpServletRequest request) {
    if (!logger.isDebugEnabled()) {
      return;
    }

    Map<String, String> headers = new HashMap<>();
    Enumeration<String> headerNames = request.getHeaderNames();

    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      String headerValue = request.getHeader(headerName);

      if (isSensitiveHeader(headerName)) {
        headers.put(headerName, "[REDACTED]");
      } else {
        headers.put(headerName, headerValue);
      }
    }

    logger.debug("Request Headers: {}", headers);
  }

  public static void logError(Logger logger, String message, Exception exception) {
    if (logger.isErrorEnabled()) {
      logger.error("{}: {}", message, exception.getMessage(), exception);
    }
  }

  public static void logWarning(Logger logger, String message, Object... args) {
    if (logger.isWarnEnabled()) {
      logger.warn(message, args);
    }
  }

  public static void logMethodEntry(Logger logger, String methodName, Object... params) {
    if (!logger.isDebugEnabled()) {
      return;
    }

    StringBuilder sb = new StringBuilder("Entering method: ").append(methodName);
    if (params.length > 0) {
      sb.append(" with parameters: ");
      for (int i = 0; i < params.length; i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(params[i]);
      }
    }
    logger.debug(sb.toString());
  }

  public static void logMethodExit(Logger logger, String methodName, Object returnValue) {
    if (logger.isDebugEnabled()) {
      logger.debug("Exiting method: {} with return value: {}", methodName, returnValue);
    }
  }

  public static String getClientIpAddress(HttpServletRequest request) {
    String[] headerNames = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
    };

    for (String header : headerNames) {
      String ip = request.getHeader(header);
      if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
        // Get first IP if multiple IPs are present
        if (ip.contains(",")) {
          ip = ip.split(",")[0];
        }
        return ip.trim();
      }
    }

    return request.getRemoteAddr();
  }

  private static boolean isSensitiveParameter(String paramName) {
    String lowerCaseName = paramName.toLowerCase();
    return lowerCaseName.contains("password")
        || lowerCaseName.contains("token")
        || lowerCaseName.contains("secret")
        || lowerCaseName.contains("apikey")
        || lowerCaseName.contains("api_key");
  }

  private static boolean isSensitiveHeader(String headerName) {
    String lowerCaseName = headerName.toLowerCase();
    return lowerCaseName.contains("authorization")
        || lowerCaseName.contains("cookie")
        || lowerCaseName.contains("token")
        || lowerCaseName.contains("secret")
        || lowerCaseName.contains("api-key");
  }
}
