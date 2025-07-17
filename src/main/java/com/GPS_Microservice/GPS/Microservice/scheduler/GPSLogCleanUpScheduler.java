package com.GPS_Microservice.GPS.Microservice.scheduler;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.GPS_Microservice.GPS.Microservice.repository.GPSLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GPSLogCleanUpScheduler {
    private final GPSLogRepository gpsLogRepository;

    @Value("${gps.log.cleanup.days}")
    private int cleanupDays;

    // Run once a day at 3 PM
    @Scheduled(cron = "${gps.log.cleanup.cron}")
    @Transactional
     public void cleanOldLogs() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(cleanupDays);
        int deletedCount = gpsLogRepository.deleteByTimestampBefore(threshold);
        log.info("Deleted {} GPS logs older than {} days (before {}).", deletedCount, cleanupDays, threshold);
    }
}
