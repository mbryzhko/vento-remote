package org.bma.vento;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bma.vento.client.DefaultVentoClient;
import org.bma.vento.client.RetryableVentoClient;
import org.bma.vento.client.VentoClient;
import org.bma.vento.schedule.ScheduleProperties;
import org.bma.vento.schedule.ScheduleScenarioFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.TimeZone;

@Configuration
@Slf4j
@EnableScheduling
public class VentoRemote implements SchedulingConfigurer {
    // VENTO_SCHEDULE
    private static final String SCHEDULE_PROP_FILE = "vento.schedule";

    @Value("${" + SCHEDULE_PROP_FILE + ":classpath:/schedule.yaml}")
    private String schedulePropertiedFileName;

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext("org.bma.vento");
    }

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
    public ScheduleScenarioFactory schedulerService(ScheduleProperties scheduleProperties, VentoClient ventoClient) {
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

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        scheduleScenarioFactory.getScenarioToSchedule().forEach(scenario -> {
            CronTrigger trigger = new CronTrigger(scenario.getCronExp(), TimeZone.getDefault());

            log.info("Scheduling scenario: {}, Next run at: {}", scenario, trigger.nextExecutionTime(new SimpleTriggerContext()));
            taskRegistrar.addCronTask(new CronTask(scenario, trigger));
        });
    }
}
