package com.ardaslegends.presentation.abstraction;

import com.ardaslegends.domain.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Slf4j
public class AbstractIntegrationTest extends RestTest<ResponseEntity>{

    private TestRestTemplate restTemplate = new TestRestTemplate(new RestTemplateBuilder().requestFactory(HttpComponentsClientHttpRequestFactory.class));

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
