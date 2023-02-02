package org.bma.vento;

import org.bma.vento.client.VentoClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class TestConfig {

    @Bean
    @Profile("test")
    public VentoClient mockVentoClient() {
        return Mockito.mock(VentoClient.class);
    }
}
