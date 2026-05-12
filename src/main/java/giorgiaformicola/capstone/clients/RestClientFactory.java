package giorgiaformicola.capstone.clients;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientFactory {
    private final RestClient.Builder builder;

    public RestClientFactory(RestClient.Builder builder) {
        this.builder = builder;
    }

    public RestClient create(String baseURL) {
        return builder.baseUrl(baseURL).build();
    }
}
