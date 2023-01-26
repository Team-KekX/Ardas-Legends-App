package com.ardaslegends.presentation;

import com.ardaslegends.domain.RPChar;
import com.ardaslegends.presentation.api.PlayerRestController;
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
public class ControllerTest {

    private ObjectMapper mapper;
    private ObjectWriter ow;
    protected String url;
    private MockMvc mockMvc;

    protected void baseSetup(AbstractRestController controller, String baseUrl) {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ow = mapper.writer().withDefaultPrettyPrinter();
        url = "http://localhost:8080" + baseUrl;
    }

    protected MvcResult post(String path, Object data) throws Exception{
        log.trace("Building JSON from data");
        String requestJson = ow.writeValueAsString(data);

        log.debug("Performing Post request to {}", url + path);
        return mockMvc.perform(MockMvcRequestBuilders
                        .post(url + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    protected MvcResult patch(String path, Object data) throws Exception{
        log.trace("Building JSON from data");
        String requestJson = ow.writeValueAsString(data);

        log.debug("Performing Patch request to {}", url + path);
        return mockMvc.perform(MockMvcRequestBuilders
                        .patch(url + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    protected MvcResult delete(String path, Object data) throws Exception{
        log.trace("Building JSON from data");
        String requestJson = ow.writeValueAsString(data);

        log.debug("Performing Delete request to {}", url + path);
        return mockMvc.perform(MockMvcRequestBuilders
                        .delete(url + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    protected MvcResult get(String path, Object data) throws Exception{
        log.trace("Building JSON from data");
        String requestJson = ow.writeValueAsString(data);

        log.debug("Performing Get request to {}", url + path);
        return mockMvc.perform(MockMvcRequestBuilders
                        .get(url + path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    protected <T> T deserialize(MockHttpServletResponse request, Class<T> clazz) throws Exception {
        request.setCharacterEncoding("UTF-8");
        return mapper.readValue(request.getContentAsString(), clazz);
    }

}
