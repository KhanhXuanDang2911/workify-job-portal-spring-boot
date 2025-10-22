package beworkify.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("employerDetailsService")
    private UserDetailsService employerDetailsService;

    private final String[] WHITE_LIST = { "/api/v1/auth/**" };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers("/api/v1/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/sign-up/**").permitAll()
                        .requestMatchers("/api/v1/employers/sign-up/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/employers/{id}", "api/v1/employers").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/provinces/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/v1/districts/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories-post/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories-job/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/industries/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/posts/public/**", "/api/v1/posts/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/jobs/advanced/**",
                                "/api/v1/jobs/locations/popular/**", "/api/v1/jobs/industries/popular/**", "/api/v1/jobs/openings/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(userAuthProvider())
                .authenticationProvider(employerAuthProvider())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider userAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationProvider employerAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(employerDetailsService);
        return provider;
    }

    @Bean("userAuthManager")
    public AuthenticationManager userAuthenticationManager(AuthenticationConfiguration config) throws Exception {
        return new ProviderManager(userAuthProvider());
    }

    @Bean("employerAuthManager")
    public AuthenticationManager employerAuthenticationManager(AuthenticationConfiguration config) throws Exception {
        return new ProviderManager(employerAuthProvider());
    }

    @Bean
    @Primary
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(userAuthProvider(), employerAuthProvider()));
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> webSecurity.ignoring()
                .requestMatchers("/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js",
                        "/swagger-ui*/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost:4000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
