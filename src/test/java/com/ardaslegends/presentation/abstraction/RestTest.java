package com.ardaslegends.presentation.abstraction;

import com.ardaslegends.presentation.AbstractRestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public abstract class RestTest<T> {

    protected ObjectMapper mapper;
    protected ObjectWriter ow;
    protected String url;

    void baseSetup(String baseUrl) {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.registerModule(new JavaTimeModule());
        ow = mapper.writer().withDefaultPrettyPrinter();
        url = baseUrl;
    }

    abstract T post(String path, Object data) throws Exception;

    abstract T patch(String path, Object data) throws Exception;

    abstract T delete(String path, Object data) throws Exception;

    abstract T get(String path) throws Exception;

    protected <G> G deserialize(MockHttpServletResponse request, Class<G> responseType) throws Exception {
        request.setCharacterEncoding("UTF-8");
        return mapper.readValue(request.getContentAsString(), responseType);
    }

    protected String serialize(Object data) {
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
