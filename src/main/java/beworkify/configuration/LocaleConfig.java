
package beworkify.configuration;

import java.util.Locale;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

@Configuration
public class LocaleConfig {

	@Bean
	public LocaleResolver localeResolver() {
		// Header: Accept-Language: vi ; Accept-Language: en
		AcceptHeaderLocaleResolver slr = new AcceptHeaderLocaleResolver();
		slr.setDefaultLocale(new Locale("vi", "VN"));
		return slr;
	}
}
