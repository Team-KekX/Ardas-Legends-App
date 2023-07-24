package com.ardaslegends.presentation.abstraction;

import com.ardaslegends.presentation.AbstractRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ControllerUnitTest extends RestTest<MvcResult>{

    private MockMvc mockMvc;

    protected void baseSetup(AbstractRestController controller, String baseUrl) {
        super.baseSetup(baseUrl);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Override
    protected MvcResult post(String path, Object data) throws Exception {
        return perform(contentRequest(MockMvcRequestBuilders.post(url + path), data));
    }

    @Override
    protected MvcResult patch(String path, Object data) throws Exception {
        return perform(contentRequest(MockMvcRequestBuilders.patch(url + path), data));

    }

    @Override
    protected MvcResult delete(String path, Object data) throws Exception {
        return perform(contentRequest(MockMvcRequestBuilders.delete(url + path), data));

    }

    @Override
    protected MvcResult get(String path) throws Exception {
        return perform(MockMvcRequestBuilders.get(url + path));
    }

    private MvcResult perform(MockHttpServletRequestBuilder builder) throws Exception {
        log.trace("Performing MockMvc request");
        return mockMvc.perform(builder)
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andReturn();
    }

    private MockHttpServletRequestBuilder contentRequest(MockHttpServletRequestBuilder builder, Object data) {
        log.trace("Building JSON from data");

        return builder.contentType(MediaType.APPLICATION_JSON)
                .content(serialize(data));
    }


}
