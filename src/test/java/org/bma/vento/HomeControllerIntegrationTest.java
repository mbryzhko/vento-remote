package org.bma.vento;

import org.bma.vento.web.WebConfig;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebAppConfiguration
@ContextConfiguration(classes = {TestConfig.class, VentoRemote.class, WebConfig.class})
@TestPropertySource(properties = {"vento.schedule=classpath:/test-schedule.yaml"})
@ExtendWith(SpringExtension.class)
class HomeControllerIntegrationTest {

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void rootPathReturns200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void indexHtmlPathReturns200() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void homePageContainsHeader() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Vento Remote"));
    }

    @Test
    void homePageContainsScenarioName() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Test Scenario"));
    }

    @Test
    void homePageContainsScenarioCron() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("0 0/2 * ? * *"));
    }

    @Test
    void homePageContainsCommandHost() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("192.168.1.101"));
    }

    @Test
    void homePageContainsDurabilitySection() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Durability"));
    }

    @Test
    void homePageContainsScenarioStateStoreSection() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Scenario State Store"));
    }

    @Test
    void homePageShowsDurabilityNotEnabledMessageInStateStoreSection() throws Exception {
        String body = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertTrue(body.contains("Durability is not enabled."));
    }
}
