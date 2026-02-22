# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

vento-remote is a Java-based automation tool for Blauberg Vento Expert WiFi ventilation units (VENTO Expert A50-1 W). It provides cron-based scheduling to control multiple ventilation units via UDP commands.

## Build and Development Commands

### Building
```bash
# Compile and run tests
mvn clean package

# Run only unit tests (excludes *IT, *IntegrationTest, *E2ETest, *ContractTest)
mvn test

# Run integration tests
mvn verify
```

### Docker
```bash
# Build Docker image for arm32v7 and amd64 platforms
mvn docker:build

# Build and push Docker image (requires GITHUB_ACTOR and GITHUB_TOKEN env vars)
mvn docker:build docker:push

# Manual Docker build for arm32v7
docker build --build-arg JAR_FILE=vento-remote-2.4-SNAPSHOT-jar-with-dependencies.jar \
  --platform linux/arm/v7 -t user/repo:arm32v7-latest --load .
```

### Release
```bash
# Create a new release (uses custom tag format: vento-remote-arm32v7-{version})
mvn release:prepare
mvn release:perform
```

## Architecture

### Core Components

**VentoRemote** (main class): Spring configuration class that bootstraps the application
- Loads schedule configuration from YAML file (via `vento.schedule` property or classpath:/schedule.yaml)
- Configures Spring beans for scheduling, client, and durability
- Entry point: `VentoRemote.main()`

**SchedulingService**: Implements `SchedulingConfigurer` to register cron-based scenarios
- Schedules scenarios using Spring's `CronTrigger`
- Handles missed executions when durability is enabled (executes missed runs on startup)
- Uses `ScheduleScenarioFactory` to create scenario instances

**VentoClient** interface: UDP communication layer for sending commands to ventilation units
- `DefaultVentoClient`: Basic UDP client implementation
- `RetryableVentoClient`: Wrapper adding retry logic for failed commands
- Protocol: UDP-based custom binary protocol for Blauberg units

**Command Pattern**: Commands are executed as part of scenarios
- `TurnOnCommand`: Sends TURN_ON command to specified host
- `TurnOffCommand`: Sends TURN_OFF command to specified host
- Commands are defined in YAML configuration with type and host

### Durability System

The durability feature persists scenario execution state to survive application restarts:

**ScenarioStateStore** interface:
- `FileScenarioStateStore`: Persists execution timestamps to disk (configured via `durability.storeFolderPath`)
- `NoOpScenarioStateStore`: No-op implementation when durability is disabled

**DurableScheduleScenario**: Wraps regular scenarios with state persistence
- Tracks last execution time
- On startup, `SchedulingService` checks for missed executions and runs them sequentially in chronological order

### Configuration

Schedule configuration (YAML format):
```yaml
durability:
  enable: true
  storeFolderPath: "/usr/share/vento-remote"
scenario:
  - name: Night
    cron: 0 0 23 ? * *  # Quartz cron format
    commands:
      - type: TURN_ON
        host: 192.168.1.101
```

Configuration is loaded via `ScheduleProperties.createFrom()` using SnakeYAML.

## Package Structure

- `org.bma.vento.client`: UDP client implementations and request/response classes
- `org.bma.vento.cmd`: Command pattern implementations (TurnOn/TurnOff)
- `org.bma.vento.schedule`: Scheduling logic and scenario management
- `org.bma.vento.schedule.durable`: Durability system for persisting execution state
- `org.bma.vento.api`: API models and interfaces
- `org.bma.vento.utils`: Utility classes

## Testing

Test naming conventions:
- `*Test.java`: Unit tests (run with `mvn test`)
- `*IntegrationTest.java`: Integration tests (run with `mvn verify`)
- Exclude patterns: `*IT`, `*E2ETest`, `*ContractTest`

## Docker Deployment

The application runs as a containerized service:
- Base image: `eclipse-temurin:17.0.13_11-jre-noble`
- JAR location: `/usr/share/vento-remote/vento-remote.jar`
- Configuration: Mount schedule.yaml and set `VENTO_SCHEDULE` environment variable
- Platforms: Built for both `linux/arm/v7` and `linux/amd64`
- Security: Uses custom seccomp profile (`custom-seccomp.json`)

## Branch Strategy

- `master`: Main development branch (used for PRs)
- `arm32v7`: Current branch for ARM32v7 builds
- `arm32v6`: Deprecated ARM32v6 support