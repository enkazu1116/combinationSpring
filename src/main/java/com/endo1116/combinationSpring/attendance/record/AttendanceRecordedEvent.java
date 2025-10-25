package com.endo1116.combinationSpring.attendance.record;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.modulith.events.Externalized;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Externalized("attendance.recorded::#{#this.employeeId}")
public class AttendanceRecordedEvent implements Serializable {

    private Long recordId;
    private String employeeId;
    private LocalDate workDate;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private Integer workedMinutes;
    private AttendanceStatus status;
    private Long managementSettingId;
    private String organizationId;
    private LocalTime standardStartTime;
    private LocalTime standardEndTime;
    private Integer breakMinutes;
    private Boolean overtimeAllowed;
    private String note;

    public static AttendanceRecordedEvent from(
        AttendanceRecord record,
        ManagementSettingSnapshot snapshot
    ) {
        return new AttendanceRecordedEvent(
            record.getId(),
            record.getEmployeeId(),
            record.getWorkDate(),
            record.getClockIn(),
            record.getClockOut(),
            record.getWorkedMinutes(),
            record.getStatus(),
            snapshot.settingId(),
            snapshot.organizationId(),
            snapshot.standardStartTime(),
            snapshot.standardEndTime(),
            snapshot.breakMinutes(),
            snapshot.overtimeAllowed(),
            record.getNote()
        );
    }
}
