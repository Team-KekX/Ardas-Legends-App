package com.ardaslegends.presentation.abstraction;

import com.ardaslegends.presentation.AbstractRestController;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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

    protected void baseSetup(AbstractRestController controller, String baseUrl) {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = mapper.writer().withDefaultPrettyPrinter();
        url = "http://localhost:8080" + baseUrl;
    }

    abstract protected T post(String path, Object data) throws Exception;

    abstract protected T patch(String path, Object data) throws Exception;

    abstract protected T delete(String path, Object data) throws Exception;

    abstract protected T get(String path, Object data) throws Exception;

    protected <T> T deserialize(MockHttpServletResponse request, Class<T> clazz) throws Exception {
        request.setCharacterEncoding("UTF-8");
        return mapper.readValue(request.getContentAsString(), clazz);
    }

    protected String serialize(Object data) throws Exception {
        return mapper.writeValueAsString(data);
    }
}
