package beworkify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main application class for the Workify backend. Bootstraps the Spring Boot application and
 * enables necessary features like Feign clients and Async processing.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@SpringBootApplication
@EnableFeignClients
@EnableAsync
public class BeWorkifyApplication {

  public static void main(String[] args) {
    SpringApplication.run(BeWorkifyApplication.class, args);
  }
}
