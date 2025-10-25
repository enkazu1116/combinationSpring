package com.endo1116.combinationSpring.attendance.record;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 管理設定のスナップショット。
 * 勤怠記録作成時に参照し、イベントへ添付する。
 */
public record ManagementSettingSnapshot(
    Long settingId,
    String organizationId,
    LocalTime standardStartTime,
    LocalTime standardEndTime,
    int breakMinutes,
    boolean overtimeAllowed,
    LocalDate effectiveFrom
) {
    public static ManagementSettingSnapshot defaultSnapshot() {
        return new ManagementSettingSnapshot(
            null,
            "default",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            60,
            true,
            LocalDate.now()
        );
    }
}
