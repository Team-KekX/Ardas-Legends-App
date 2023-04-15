package com.ardaslegends.presentation.abstraction;

import com.ardaslegends.domain.Player;
import com.ardaslegends.presentation.AbstractRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {  }
)
@ActiveProfiles("test")
@Testcontainers
public class AbstractIntegrationTest extends RestTest<ResponseEntity>{

    private final TestRestTemplate restTemplate = new TestRestTemplate(new RestTemplateBuilder().requestFactory(HttpComponentsClientHttpRequestFactory.class));

    @LocalServerPort
    protected Integer port;

    protected void baseSetup(AbstractRestController controller, String baseUrl) {
        super.baseSetup(controller, baseUrl, port);
    }

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:14-alpine")
            .withReuse(false);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Override
    protected <T> ResponseEntity<T> post(String path, Object data, Class<T> responseType) {
        return doRequest(HttpMethod.POST, path, data, responseType);
    }

    @Override
    protected <T> ResponseEntity<T> patch(String path, Object data, Class<T> responseType) {
        return doRequest(HttpMethod.PATCH, path, data, responseType);

    }

    @Override
    protected <T> ResponseEntity<T> delete(String path, Object data, Class<T> responseType) {
        return doRequest(HttpMethod.DELETE, path, data, responseType);

    }

    @Override
    protected <T> ResponseEntity<T> get(String path, Object data, Class<T> responseType) {
        return doRequest(HttpMethod.GET, path, data, responseType);
    }

    private <T> ResponseEntity<T> doRequest(HttpMethod method, String path, Object data, Class<T> responseType) {
        log.trace("Building JSON from data");

        log.debug("Performing {} request to {}",method ,url + path);
        var entity = new HttpEntity<>(data);
        return restTemplate.exchange(url + path, method, entity, responseType);
    }
}
