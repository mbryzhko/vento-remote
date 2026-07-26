package org.bma.vento;

import lombok.extern.slf4j.Slf4j;
import org.bma.vento.client.DefaultVentoClient;
import org.bma.vento.client.RetryableVentoClient;
import org.bma.vento.client.VentoClient;
import org.bma.vento.schedule.ScheduleProperties;
import org.bma.vento.schedule.ScheduleScenarioFactory;
import org.bma.vento.schedule.SchedulingService;
import org.bma.vento.schedule.durable.FileScenarioStateStore;
import org.bma.vento.schedule.durable.NoOpScenarioStateStore;
import org.bma.vento.schedule.durable.ScenarioStateStore;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

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


        try (InputStream propsInputStream = resourceLoader.getResource(schedulePropertiedFileName).getInputStream()) {
            ScheduleProperties properties = ScheduleProperties.createFrom(propsInputStream);

            log.info("Loaded properties: {}", properties);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Error loading properties from: " + schedulePropertiedFileName, e);
        }
    }

    @Bean
    public ScheduleScenarioFactory scheduleScenarioFactory(ScheduleProperties scheduleProperties,
                                                           VentoClient ventoClient,
                                                           ScenarioStateStore scenarioStateStore) {
        return new ScheduleScenarioFactory(scheduleProperties, ventoClient, scenarioStateStore);
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

    @Bean
    public ScenarioStateStore scenarioStateStore(ScheduleProperties scheduleProperties) {
        return scheduleProperties.isDurabilityEnabled()
                ? new FileScenarioStateStore(scheduleProperties.getDurability().getStoreFolderPath())
                : new NoOpScenarioStateStore();
    }

    public static void main(String[] args) throws Exception {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.scan("org.bma.vento");

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        Context ctx = tomcat.addContext("", null);
        DispatcherServlet servlet = new DispatcherServlet(context);
        Tomcat.addServlet(ctx, "dispatcher", servlet).setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/*", "dispatcher");

        tomcat.start();
        tomcat.getServer().await();
    }
}
