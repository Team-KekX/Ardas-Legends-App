package com.ardaslegends.presentation.abstraction;

import com.ardaslegends.presentation.AbstractRestController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ControllerUnitTest extends RestTest<MvcResult>{

    private MockMvc mockMvc;

    @Override
    protected void baseSetup(AbstractRestController controller, String baseUrl) {
        super.baseSetup(controller, baseUrl);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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


}
