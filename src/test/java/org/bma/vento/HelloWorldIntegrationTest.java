package org.bma.vento;

import org.bma.vento.web.WebConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, VentoRemote.class, WebConfig.class})
@TestPropertySource(properties = {"vento.schedule=classpath:/test-schedule.yaml"})
@ExtendWith(SpringExtension.class)
class HelloWorldIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void helloPageReturns200WithMessage() throws Exception {
        MvcResult result = mockMvc.perform(get("/hello"))
               .andExpect(status().isOk())
               .andReturn();
        Assertions.assertTrue(result.getResponse().getContentAsString().contains("Hello, Vento!"));
    }
}
