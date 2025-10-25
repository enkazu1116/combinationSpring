package com.endo1116.combinationSpring.attendance.setting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.modulith.events.Externalized;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Externalized("attendance.management.setting.updated::#{#this.organizationId}")
public class ManagementSettingUpdatedEvent implements Serializable {

    private Long settingId;
    private String organizationId;
    private LocalTime standardStartTime;
    private LocalTime standardEndTime;
    private Integer breakMinutes;
    private boolean overtimeAllowed;
    private LocalDate effectiveFrom;
    private String note;

    public static ManagementSettingUpdatedEvent from(AttendanceManagementSetting setting) {
        return new ManagementSettingUpdatedEvent(
            setting.getId(),
            setting.getOrganizationId(),
            setting.getStandardStartTime(),
            setting.getStandardEndTime(),
            setting.getBreakMinutes(),
            Boolean.TRUE.equals(setting.getOvertimeAllowed()),
            setting.getEffectiveFrom(),
            setting.getNote()
        );
    }
}
