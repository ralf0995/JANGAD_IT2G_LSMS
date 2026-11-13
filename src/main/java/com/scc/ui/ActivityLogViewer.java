package com.scc.ui;

import com.scc.dao.ActivityLogDao;
import com.scc.Model.ActivityLog;
import java.util.List;

public class ActivityLogViewer {
    private static final ActivityLogDao logDao = new ActivityLogDao();

    public static void displayLogs() {
        List<ActivityLog> logs = logDao.getAllLogs();
        System.out.println("\n--- ACTIVITY LOGS ---");
        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }
        for (ActivityLog log : logs) {
            System.out.printf("[%s] LogID: %d | UserID: %d | Type: %s | Desc: %s%n",
                    log.getTimestamp(), log.getLogId(), log.getUserId(), log.getActionType(), log.getDescription());
        }
    }
}