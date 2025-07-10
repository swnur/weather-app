package com.swnur.scheduler;

import com.swnur.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionCleanupScheduler {

    private final SessionService sessionService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredSessionsTask() {
        sessionService.cleanupExpiredSessions();
    }

}
