package giorgiaformicola.capstone.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientsConfig {
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor(((request, body, execution) -> {
                    log.debug("{} {}", request.getMethod(), request.getURI());
                    return execution.execute(request, body);
                }));
    }
}
