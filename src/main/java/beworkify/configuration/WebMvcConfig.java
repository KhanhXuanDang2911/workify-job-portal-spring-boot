package beworkify.configuration;

import beworkify.interceptor.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration for Spring MVC interceptors.
 *
 * @author KhanhDX
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

  private final LoggingInterceptor loggingInterceptor;

  @Autowired
  public WebMvcConfig(LoggingInterceptor loggingInterceptor) {
    this.loggingInterceptor = loggingInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(loggingInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh-token",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**");
  }
}
