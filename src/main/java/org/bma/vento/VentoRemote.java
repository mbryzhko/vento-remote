package org.bma.vento;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bma.vento.client.DefaultVentoClient;
import org.bma.vento.client.RetryableVentoClient;
import org.bma.vento.client.VentoClient;
import org.bma.vento.schedule.ScheduleProperties;
import org.bma.vento.schedule.ScheduleScenarioFactory;
import org.bma.vento.schedule.SchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
@EnableScheduling
public class VentoRemote {
    // VENTO_SCHEDULE
    private static final String SCHEDULE_PROP_FILE = "vento.schedule";

    @Value("${" + SCHEDULE_PROP_FILE + ":classpath:/schedule.yaml}")
    private String schedulePropertiedFileName;

    @Autowired
    private ScheduleScenarioFactory scheduleScenarioFactory;

    @Bean
    public ScheduleProperties scheduleProperties(ResourceLoader resourceLoader) {
        log.debug("Loading scheduling properties from: {}", schedulePropertiedFileName);

        InputStream propsInputStream = null;
        try {
            propsInputStream = resourceLoader.getResource(schedulePropertiedFileName).getInputStream();
            ScheduleProperties properties = ScheduleProperties.createFrom(propsInputStream);

            log.info("Loaded properties: {}", properties);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Error loading properties from: " + schedulePropertiedFileName, e);
        } finally {
            IOUtils.closeQuietly(propsInputStream);
        }
    }

    @Bean
    public ScheduleScenarioFactory scheduleScenarioFactory(ScheduleProperties scheduleProperties, VentoClient ventoClient) {
        return new ScheduleScenarioFactory(scheduleProperties, ventoClient);
    }

    @Bean
    @Profile("!test")
    public VentoClient ventoClient() {
        return new RetryableVentoClient(new DefaultVentoClient());
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler(); //single threaded by default
    }

    @Bean
    public SchedulingService schedulingService(ScheduleScenarioFactory factory, ScheduleProperties scheduleProperties) {
        return new SchedulingService(factory, scheduleProperties);
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext("org.bma.vento");
    }
}
